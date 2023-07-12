package com.xunmo.request.xss.handler;

import com.xunmo.ext.XmHandlerExt;

public interface RequestXssHandler extends XmHandlerExt {
    @Override
    default boolean isOpenAfter() {
        return false;
    }
}
