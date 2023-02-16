package com.xunmo.base.move;

import cn.hutool.core.date.DateUtil;
import com.xunmo.base.XmServiceImpl;
import org.noear.solon.data.annotation.Tran;

import java.util.Objects;

public class XmSimpleMoveServiceImpl<M extends XmSimpleMoveMapper<T>, T extends XmSimpleMoveEntity> extends XmServiceImpl<M, T> implements XmSimpleMoveService<T> {

    @Override
    @Tran
    public boolean upTop(final String id) throws Exception {
        // 查询需要置顶的数据
        final T old = this.checkAndGet(id);
        // 设置更新人信息
        String userId = "1";
        String nickname = "管理员";
        old.setLastUpdateTime(DateUtil.date());
        old.setLastUpdateUser(userId);
        old.setLastUpdateUserName(nickname);

        final int plusSequence = this.baseMapper.plusSort(old);
        // 返回任意值都行
        if (plusSequence > -1) {
            old.setSort(1);
            this.updateNotNullById(old);
        }
        return true;
    }

    @Override
    @Tran
    public boolean upMove(final String id) throws Exception {
        final T old = this.checkAndGet(id);
        // 查询上一条子菜单数据
        T beforeOne = this.baseMapper.getBeforeOne(old);
        if (Objects.isNull(beforeOne)) {
            throw new RuntimeException("当前数据已是第一条,无法上移");
        }
        this.baseMapper.changeSort(beforeOne.getId(), old.getId());
        return true;
    }

    @Override
    @Tran
    public boolean downMove(final String id) throws Exception {
        final T old = this.checkAndGet(id);

        // 查询下一条子菜单数据
        T afterOne = this.baseMapper.getAfterOne(old);
        if (Objects.isNull(afterOne)) {
            throw new RuntimeException("当前数据已是最后一条");
        }
        this.baseMapper.changeSort(old.getId(), afterOne.getId());
        return true;
    }

    @Override
    @Tran
    public boolean changeSort(final String preId, final String nextId) throws Exception {
        this.checkAndGet(preId);
        this.checkAndGet(nextId);

        // 交换两条数据的 sort
        this.baseMapper.changeSort(preId, nextId);
        return true;
    }
}
