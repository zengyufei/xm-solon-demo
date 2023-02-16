package com.xunmo.request.args.handler;

import com.xunmo.ext.XmHandlerExt;

public interface ArgsConsoleHandlerExt extends XmHandlerExt {

    @Override
    default boolean isOpenAfter() {
        return false;
    }
}
