package org.xlqs.com.example.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/8 下午9:41
 * @description RagResultDto 类说明：TODO
 * @since 2026/6/8
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RagResultDto {

    /**
     *  id, UUID
     */
    private String id;

    /**
     *  分数
     */
    private float score;

    /**
     *  内容
     */
    private Map<String, Object>  content;

    /**
     *  来源
     */
    private String source;
}
