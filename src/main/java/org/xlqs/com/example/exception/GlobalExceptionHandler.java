//package org.xlqs.com.example.exception;
//
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.xlqs.com.example.vo.Result;
//
///**
// * @author xlqs
// * @version 1.0
// * @date 2026/6/6 下午10:41
// * @description GlobalExceptionHandler 类说明：TODO
// * @since 2026/6/6
// */
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(Exception.class)
//    public Result<?> handleException(Exception e) {
//        return Result.error(e.getMessage());
//    }
//
//    @ExceptionHandler(RuntimeException.class)
//    public Result<?> handleRuntime(RuntimeException e) {
//        return Result.error(500, e.getMessage());
//    }
//}