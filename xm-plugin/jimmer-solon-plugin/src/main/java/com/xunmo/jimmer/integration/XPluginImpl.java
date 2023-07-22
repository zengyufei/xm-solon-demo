package com.xunmo.jimmer.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunmo.jimmer.JimmerAdapter;
import com.xunmo.jimmer.Repository;
import com.xunmo.jimmer.annotation.Db;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.jackson.ImmutableModule;
import org.babyfish.jimmer.sql.JSqlClient;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.VarHolder;

import javax.sql.DataSource;

@Slf4j
public class XPluginImpl implements Plugin {
	@Override
	public void start(AopContext context) {

		context.getBeanAsync(ObjectMapper.class, bean -> {
			// bean 获取后，可以做些后续处理。。。
			log.info("异步订阅 ObjectMapper, 执行 jimmer 初始化动作");
			bean.registerModule(new ImmutableModule());
		});


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
		JimmerAdapter raw = JimmerAdapterManager.get(dsBw);
		dsBw.context().wrapAndPut(clz, raw.getRepository(clz));
	}

	private void inject0(VarHolder varH, BeanWrap dsBw) {
		JimmerAdapter jimmerAdapter = JimmerAdapterManager.get(dsBw);

		final Class<?> varHolderType = varH.getType();
		if (JSqlClient.class.isAssignableFrom(varHolderType)) {
			final JSqlClient sqlClient = jimmerAdapter.sqlClient();
			if (sqlClient != null) {
				varH.setValue(sqlClient);
			}
		}
		if (Repository.class.isAssignableFrom(varHolderType)) {
			varH.setValue(jimmerAdapter.getRepository(varHolderType));
		}
	}
}
