package com.xunmo.webs.user.input;

import com.xunmo.webs.user.entity.User;
import lombok.Data;
import org.babyfish.jimmer.Input;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

/**
 * 用户表(User)输入类
 *
 * @author zengyufei
 * @since 2023-06-28 10:14:30
 */
@Data
public class UserInput implements Input<User> {

    private static final Converter CONVERTER = Mappers.getMapper(Converter.class);

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 创建时间 - 开始区间
     */
    private LocalDateTime beginCreateTime;

    /**
     * 创建时间 - 结束区间
     */
    private LocalDateTime endCreateTime;

    /**
     * 修改时间 - 开始区间
     */
    private LocalDateTime beginUpdateTime;

    /**
     * 修改时间 - 结束区间
     */
    private LocalDateTime endUpdateTime;

    /**
     * 创建人ID
     */
    private String createId;

    /**
     * 修改人ID
     */
    private String updateId;

    /**
     * 创建人用户名
     */
    private String createName;

    /**
     * 修改人用户名
     */
    private String updateName;

    /**
     * 审批状态
     */
    private String approvalStatus;

    /**
     * 审批人id
     */
    private String approverId;

    /**
     * 审批人姓名
     */
    private String approverName;

    /**
     * 审批意见
     */
    private String approvalComment;

    /**
     * 审批时间
     */
    private LocalDateTime approvalTime;

    /**
     * 是否导入
     */
    private Integer isImported;

    /**
     * 导入时间
     */
    private LocalDateTime importTime;

    /**
     * 是否系统默认
     */
    private Integer isSystemDefault;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 乐观锁版本号
     */
    private Integer version;

    /**
     * 状态
     */
    private String status;


    // ---------- 转换方法 ----------


    @Override
    public User toEntity() {
        return CONVERTER.toUser(this);
    }

    @Mapper
    interface Converter {

        @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
        User toUser(UserInput input);
    }
}
