package com.xunmo.config.mybatisplus;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.AbstractSqlInjector;
import com.baomidou.mybatisplus.core.injector.methods.*;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.xunmo.config.mybatisplus.methods.InsertBatch;
import com.xunmo.config.mybatisplus.methods.UpdateAllColumnById;
import com.xunmo.config.mybatisplus.methods.UpdateBatchMethod;
import com.xunmo.config.mybatisplus.methods.UpdateNotNullById;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 自定义 SqlInjector
 *
 * @author miemie
 * @since 2018-08-13
 */
public class XmLogicSqlInjector extends AbstractSqlInjector {

    /**
     * 如果只需增加方法，保留MP自带方法
     * 可以super.getMethodList() 再add
     *
     * @return
     */
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        final Stream.Builder<AbstractMethod> builder = Stream.<AbstractMethod>builder()
                .add(new Insert())
                .add(new InsertBatch(t -> true))
                .add(new Delete())
                .add(new DeleteByMap())
                .add(new Update())
                .add(new SelectByMap())
                .add(new SelectCount())
                .add(new SelectMaps())
                .add(new SelectMapsPage())
                .add(new SelectObjs())
                .add(new SelectList())
                .add(new SelectPage());
        if (tableInfo.havePK()) {
            builder.add(new DeleteById())
                    .add(new DeleteBatchByIds())
                    .add(new UpdateById())
                    .add(new UpdateNotNullById())
                    .add(new UpdateBatchMethod())
                    .add(new UpdateAllColumnById())
                    .add(new SelectById())
                    .add(new SelectBatchByIds());
        } else {
            this.logger.warn(String.format("%s ,Not found @TableId annotation, Cannot use Mybatis-Plus 'xxById' Method.", tableInfo.getEntityType()));
        }
        return builder.build().collect(Collectors.toList());
    }
}
