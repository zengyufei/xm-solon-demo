package com.xunmo.config.jimmer;


import com.xunmo.common.base.BaseEntityDraft;
import com.xunmo.common.base.BaseEntityProps;
import org.babyfish.jimmer.ImmutableObjects;
import org.babyfish.jimmer.sql.DraftInterceptor;
import org.noear.solon.annotation.Component;

import java.time.LocalDateTime;

@Component
public class BaseEntityDraftInterceptor implements DraftInterceptor<BaseEntityDraft> {

	@Override
	public void beforeSave(BaseEntityDraft draft, boolean isNew) {
		if (!ImmutableObjects.isLoaded(draft, BaseEntityProps.UPDATE_TIME)) {
			draft.setUpdateTime(LocalDateTime.now());
		}
		if (isNew) {
			if (!ImmutableObjects.isLoaded(draft, BaseEntityProps.CREATE_TIME)) {
				draft.setCreateTime(LocalDateTime.now());
			}
		}
	}
}
