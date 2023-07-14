package com.xunmo.ext;

import org.noear.solon.core.handle.Context;

import java.io.IOException;

public interface XmHandlerExt {

	void before(Context ctx) throws IOException;

	void after(Context ctx);

	default boolean isOpenBefore() {
		return true;
	}

	default boolean isOpenAfter() {
		return true;
	}

}
