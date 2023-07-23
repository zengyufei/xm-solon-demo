package com.xunmo.jimmer.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunmo.XmPackageNameConstants;
import com.xunmo.jimmer.JimmerAdapter;
import com.xunmo.jimmer.annotation.Db;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.jackson.ImmutableModule;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.VarHolder;
import org.noear.solon.core.event.EventBus;

import javax.sql.DataSource;

@Slf4j
public class XmJimmerPluginImp implements Plugin {

	@Override
	public void start(AopContext context) {

		context.getBeanAsync(ObjectMapper.class, bean -> {
			// bean 获取后，可以做些后续处理。。。
			log.info("{} 异步订阅 ObjectMapper, 执行 jimmer 初始化动作", XmPackageNameConstants.XM_JIMMER);
			final ImmutableModule immutableModule = new ImmutableModule();
			bean.registerModule(immutableModule);
			EventBus.push(immutableModule);
		});

		context.subWrapsOfType(DataSource.class, bw -> {
			JimmerAdapterManager.register(bw);
		});

		// for new
		context.beanBuilderAdd(Db.class, (clz, wrap, anno) -> {
			builderAddDo(clz, wrap, anno.value());
		});

		context.beanInjectorAdd(Db.class, (varH, anno) -> {
			injectorAddDo(varH, anno.value());
		});

		if (XmPackageNameConstants.IS_CONSOLE_LOG) {
			log.info("{} 包加载完毕!", XmPackageNameConstants.XM_JIMMER);
		} else {
			System.out.println(XmPackageNameConstants.XM_JIMMER + " 包加载完毕!");
		}
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
		// 进入容器，用于 @Inject 注入
		dsBw.context().wrapAndPut(clz, raw.getRepository(clz));
	}

	private void inject0(VarHolder varH, BeanWrap dsBw) {
		JimmerAdapter jimmerAdapter = JimmerAdapterManager.get(dsBw);

		if (jimmerAdapter != null) {
			jimmerAdapter.injectTo(varH, dsBw);
		}
	}

	@Override
	public void stop() throws Throwable {
		if (XmPackageNameConstants.IS_CONSOLE_LOG) {
			log.info("{} 插件关闭!", XmPackageNameConstants.XM_JIMMER);
		} else {
			System.out.println(XmPackageNameConstants.XM_JIMMER + " 插件关闭!");
		}
	}
}
