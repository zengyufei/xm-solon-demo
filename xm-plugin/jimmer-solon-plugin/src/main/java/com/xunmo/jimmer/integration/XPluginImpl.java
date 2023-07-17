package com.xunmo.jimmer.integration;

import com.xunmo.jimmer.annotation.Db;
import org.babyfish.jimmer.sql.JSqlClient;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.VarHolder;

import javax.sql.DataSource;

public class XPluginImpl implements Plugin {
	@Override
	public void start(AopContext context) {

		context.subWrapsOfType(DataSource.class, bw -> {
			JimmerAdapterManager.register(bw);
		});

		//for new
		context.beanBuilderAdd(Db.class, (clz, wrap, anno) -> {
			builderAddDo(clz, wrap, anno.value());
		});

		context.beanInjectorAdd(Db.class, (varH, anno) -> {
			injectorAddDo(varH, anno.value());
		});

	}

	private void builderAddDo(Class<?> clz, BeanWrap wrap, String annoValue) {
		if (!clz.isInterface()) {
			return;
		}

		if (Utils.isEmpty(annoValue)) {
			wrap.context().getWrapAsync(DataSource.class, (dsBw) -> {
				create0(clz, dsBw);
			});
		} else {
			wrap.context().getWrapAsync(annoValue, (dsBw) -> {
				if (dsBw.raw() instanceof DataSource) {
					create0(clz, dsBw);
				}
			});
		}
	}

	private void injectorAddDo(VarHolder varH, String annoValue) {
		if (Utils.isEmpty(annoValue)) {
			varH.context().getWrapAsync(DataSource.class, (dsBw) -> {
				inject0(varH, dsBw);
			});
		} else {
			varH.context().getWrapAsync(annoValue, (dsBw) -> {
				if (dsBw.raw() instanceof DataSource) {
					inject0(varH, dsBw);
				}
			});
		}
	}


	private void create0(Class<?> clz, BeanWrap dsBw) {
		Object raw = JimmerAdapterManager.get(dsBw);
		dsBw.context().wrapAndPut(clz, raw);
	}

	private void inject0(VarHolder varH, BeanWrap dsBw) {
		JSqlClient sqlClient = JimmerAdapterManager.get(dsBw);

		if (sqlClient != null) {

			if (JSqlClient.class.isAssignableFrom(varH.getType())) {
				varH.setValue(sqlClient);
			}
		}
	}
}