package org.xlqs.com.example.util;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/6 下午6:06
 * @description ChatService 类说明：TODO
 * @since 2026/6/6
 */
@Slf4j
public class ChatService {

    // 默认超时时间：5 分钟（防止复杂大模型/长文本思考时意外中断）
    private static final Long DEFAULT_TIMEOUT = 300_000L;

    /**
     * 【极简快捷入口】只传一个参数，使用默认的 5 分钟超时时间
     * 满足你直接调用的需求：ChatService.chat(chatStream);
     *
     * @param flux 大模型吐字的响应式流
     * @return SseEmitter 供 Controller 直接返回
     */
    public static SseEmitter chat(Flux<String> flux) {
        return chat(flux, DEFAULT_TIMEOUT);
    }

    /**
     * 将 AI 的 Flux 响应流推送给前端浏览器
     *
     * @param flux    大模型吐字的响应式流
     * @param timeout 超时时间（毫秒）。
     * @return SseEmitter 直接在 Controller 层接口作为返回值返回即可
     */
    public static SseEmitter chat(Flux<String> flux, Long timeout) {
        // 1. 初始化 SseEmitter。默认通常是 30 秒。
        SseEmitter emitter = (timeout != null && timeout > 0)
                ? new SseEmitter(timeout)
                : new SseEmitter(180_000L); // 默认给 3 分钟

        StringBuilder fullResponse = new StringBuilder();

        // 2. 异步订阅 AI 的 Flux 数据流
        flux.subscribe(
                token -> {
                    try {
                        // 拼接长文本
                        fullResponse.append(token);
                        // 发送标准 SSE 数据。Spring 底层会自动将其包装为 "data: xxx\n\n" 并隐式执行 flush
                        emitter.send(SseEmitter.event().data(token));
//                        emitter.send(token, MediaType.TEXT_PLAIN);
                    } catch (IOException e) {
                        // 生产环境极常见：用户看了一半直接把网页关了、或者点了解除生成，会导致管道破裂
                        log.warn("检测到前端主动断开长连接，后端及时止损，终止 AI 发送。");
                        emitter.completeWithError(e); // 终止当前 emitter
                    }
                },
                error -> {
                    log.error("AI 传输过程中发生异常: ", error);
                    emitter.completeWithError(error); // 将错误异常通知前端并关闭
                },
                () -> {
                    log.info("AI 回复全部发送完毕，优雅关闭网络长连接。");
                    log.info("fullResponse: {}", fullResponse);
                    emitter.complete(); // 正常结束流程，释放连接
                }
        );

        // 3. 注册清理回调（防止因异常、超时引发的服务器内存泄漏）
        emitter.onTimeout(() -> {
            log.warn("SSE 连接超时");
            emitter.complete();
        });

        emitter.onError(e -> {
            log.debug("SSE 传输触发异常清理: {}", e.getMessage());
            emitter.complete();
        });

        return emitter;
    }

    public static void chatStream(Flux<String> flux) {
        // 1. 【核心黑科技】：凭空获取当前请求的上下文属性
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        // 2. 顺藤摸瓜，反向提取出隐藏的 request 和 response
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();

        // 3. 基础 Header 配置（纯文本模式，无 data: 前缀，无乱码）
        response.setContentType("text/markdown;charset=UTF-8");
        // 告诉 Apifox：我这是一个“活的”服务器推送事件流，别攒着，来一个字就给我吐一个字！
//        response.setContentType("text/event-stream;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("X-Accel-Buffering", "no");  // 禁用 Nginx 缓存
        response.setHeader("Cache-Control", "no-cache"); // 禁用浏览器缓存

        // 4. 从 request 开启异步模式
        final AsyncContext asyncContext = request.isAsyncStarted()
                ? request.getAsyncContext()
                : request.startAsync();
        asyncContext.setTimeout(300_000L); // 5分钟超时

        try {
            final PrintWriter writer = response.getWriter();

            // 6. 异步订阅 AI 响应流
            flux.subscribe(
                    token -> {
                        // 每次吐字检测用户是否关闭了网页
                        if (writer.checkError()) {
                            safeComplete(asyncContext);
                            throw new RuntimeException("客户端断开，后端自动终止");
                        }
                        writer.write(token);
                        writer.flush(); // 实现打字机效果
                    },
                    error -> safeComplete(asyncContext),
                    () -> safeComplete(asyncContext)
            );
        } catch (Exception e) {
            safeComplete(asyncContext);
        }
    }

    private static void safeComplete(AsyncContext asyncContext) {
        try {
            if (asyncContext.getRequest().isAsyncStarted()) {
                asyncContext.complete();
            }
        } catch (IllegalStateException e) {
            // 静默忽略已关闭的上下文
        }
    }

}
