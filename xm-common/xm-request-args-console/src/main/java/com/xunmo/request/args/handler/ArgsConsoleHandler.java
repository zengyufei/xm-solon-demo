package com.xunmo.request.args.handler;

import com.xunmo.ext.XmHandlerExt;

public interface ArgsConsoleHandler extends XmHandlerExt {

    @Override
    default boolean isOpenAfter() {
        return false;
    }
}
