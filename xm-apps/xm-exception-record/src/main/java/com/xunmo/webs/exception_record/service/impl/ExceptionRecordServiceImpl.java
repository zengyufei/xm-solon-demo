package com.xunmo.webs.exception_record.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xunmo.base.XmServiceImpl;
import com.xunmo.webs.exception_record.entity.ExceptionRecord;
import com.xunmo.webs.exception_record.mapper.ExceptionRecordMapper;
import com.xunmo.webs.exception_record.model.query.ExceptionRecordQuery;
import com.xunmo.webs.exception_record.service.ExceptionRecordService;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.ProxyComponent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

/**
 * @author zengyufei
 * @date 2021/9/27 20:54
 */
@Slf4j
@ProxyComponent
public class ExceptionRecordServiceImpl extends XmServiceImpl<ExceptionRecordMapper, ExceptionRecord> implements ExceptionRecordService {

    @Inject
    private ExceptionRecordMapper exceptionRecordMapper;

    /**
     * 查询异常记录
     *
     * @param query 参数
     * @return 结果
     */
    @Override
    public List<ExceptionRecord> getList(ExceptionRecordQuery query) {
        LambdaQueryWrapper<ExceptionRecord> qw = new QueryWrapper<ExceptionRecord>().lambda()
                .orderByAsc(ExceptionRecord::getHappenTime)
                .orderByAsc(ExceptionRecord::getId);
        if (StrUtil.isNotBlank(query.getUri())) {
            qw.eq(ExceptionRecord::getUri, query.getUri());
        }
        if (StrUtil.isNotBlank(query.getMethod())) {
            qw.eq(ExceptionRecord::getMethod, query.getMethod());
        }
        if (StrUtil.isNotBlank(query.getUserId())) {
            qw.eq(ExceptionRecord::getUserId, query.getUserId());
        }
        if (StrUtil.isNotBlank(query.getStartTime())) {
            Long start = this.getDateTime(query.getStartTime());
            if (Objects.nonNull(start)) {
                qw.ge(ExceptionRecord::getHappenTime, start);
            }
        }
        if (StrUtil.isNotBlank(query.getEndTime())) {
            Long end = this.getDateTime(query.getEndTime());
            if (Objects.nonNull(end)) {
                qw.le(ExceptionRecord::getHappenTime, end);
            }
        }
        return this.list(qw);
    }

    /**
     * 获取时间
     *
     * @param datetime
     * @return
     */
    public Long getDateTime(String datetime) {
        if (StrUtil.isBlank(datetime)) {
            return null;
        }

        datetime = datetime.trim();

        String format = null;
        if (datetime.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            format = "yyyy-MM-dd";
        } else if (datetime.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\{2}$")) {
            format = "yyyy-MM-dd HH:mm:ss";
        }

        if (Objects.isNull(format)) {
            return null;
        }

        try {
            return new SimpleDateFormat(format).parse(datetime).getTime();
        } catch (ParseException e) {
            return null;
        }
    }


    /**
     * 删除时间之前的数据
     *
     * @param time 时间
     * @return 结果
     */
    @Override
    public boolean delBeforeTime(Long time) {
        if (Objects.isNull(time)) {
            return true;
        }
        int i = exceptionRecordMapper.delBeforeTime(time);
        return i > 0;
    }
}
