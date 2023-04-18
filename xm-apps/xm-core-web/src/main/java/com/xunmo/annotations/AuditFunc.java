package com.xunmo.annotations;

import org.javers.core.Changes;
import org.javers.core.diff.changetype.ValueChange;

import java.util.List;

@FunctionalInterface
public interface AuditFunc {
    /**
     * 审计
     *
     * @param changes 变化
     */
    void audit(Changes changes);
}
