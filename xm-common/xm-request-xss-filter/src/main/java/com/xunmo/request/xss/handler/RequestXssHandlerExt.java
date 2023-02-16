package com.xunmo.request.xss.handler;

import com.xunmo.ext.XmHandlerExt;

public interface RequestXssHandlerExt extends XmHandlerExt {
    @Override
    default boolean isOpenAfter() {
        return false;
    }
}
