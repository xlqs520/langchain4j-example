package org.xlqs.com.example.dto;


import java.io.Serial;
import java.util.Date;
import java.io.Serializable;

import lombok.*;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;  // 导入 TableId 注解
import com.baomidou.mybatisplus.annotation.IdType;  // 导入 IdType 枚举
import com.baomidou.mybatisplus.annotation.TableField;  // 导入 TableField 注解
import io.swagger.v3.oas.annotations.media.Schema; // 导入swagger3注解

/**
 *  实体类
 *
 * @author huawei
 * @date 2026-06-10 22:52:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@EqualsAndHashCode(callSuper = false)
@TableName("user")
@Schema(name="UserDto", description="用户列表")
public class UserDto implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * id
         */
        @TableId(value = "id", type = IdType.AUTO)
        @Schema(description="id", type="Long")
        private Long id;

        /**
         * userid
         */
        @TableField("user_id")
        @Schema(description="userid", type="String")
        private String userId;

        /**
         * 用户姓名
         */
        @TableField("name")
        @Schema(description="用户姓名", type="String")
        private String name;

        /**
         * 用户邮箱
         */
        @TableField("email")
        @Schema(description="用户邮箱", type="String")
        private String email;

        /**
         * 用户手机
         */
        @TableField("phone")
        @Schema(description="用户手机", type="String")
        private String phone;

        /**
         * 用户住址
         */
        @TableField("address")
        @Schema(description="用户住址", type="String")
        private String address;

        /**
         * 创建时间
         */
        @TableField("created_at")
        @Schema(description="创建时间", type="Date")
        private Date createdAt;

        /**
         * 更新时间
         */
        @TableField("updated_at")
        @Schema(description="更新时间", type="Date")
        private Date updatedAt;

        /**
         * 爱好
         */
        @TableField("hobby")
        @Schema(description="爱好", type="String")
        private String hobby;

        /**
         * 工作
         */
        @TableField("job")
        @Schema(description="工作", type="String")
        private String job;

}
