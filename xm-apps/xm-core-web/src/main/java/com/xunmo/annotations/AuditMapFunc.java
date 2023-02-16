package com.xunmo.annotations;

import com.xunmo.core.utils.XmMap;
import org.javers.core.diff.changetype.ValueChange;

@FunctionalInterface
public interface AuditMapFunc {
    void audit(XmMap<String, ValueChange> changes);
}
