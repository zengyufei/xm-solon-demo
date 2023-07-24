package com.xunmo.jimmer.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunmo.XmPackageNameConstants;
import com.xunmo.jimmer.JimmerAdapter;
import com.xunmo.jimmer.annotation.Db;
import com.xunmo.jimmer.interceptor.XmCacheInterceptor;
import com.xunmo.jimmer.interceptor.XmCachePutInterceptor;
import com.xunmo.jimmer.interceptor.XmCacheRemoveInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.jackson.ImmutableModule;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.VarHolder;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.data.annotation.Cache;
import org.noear.solon.data.annotation.CachePut;
import org.noear.solon.data.annotation.CacheRemove;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.data.cache.CacheServiceWrapConsumer;

import javax.sql.DataSource;

@Slf4j
public class XmJimmerPluginImp implements Plugin {

	@Override
	public void start(AopContext context) {
		Solon.app().enableCaching(false);

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

		Solon.context().getBeanAsync(CacheService.class, cacheService -> {
			log.info("{} 异步订阅 CacheService, 执行初始化动作", XmPackageNameConstants.XM_JIMMER);
			Solon.context().subWrapsOfType(CacheService.class, new CacheServiceWrapConsumer());
		});

		context.beanAroundAdd(CachePut.class, new XmCachePutInterceptor(), 110);
		context.beanAroundAdd(CacheRemove.class, new XmCacheRemoveInterceptor(), 110);
		context.beanAroundAdd(Cache.class, new XmCacheInterceptor(), 111);

		if (XmPackageNameConstants.IS_CONSOLE_LOG) {
			log.info("{} 包加载完毕!", XmPackageNameConstants.XM_JIMMER);
		}
		else {
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
		}
		else {
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
		}
		else {
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
		}
		else {
			System.out.println(XmPackageNameConstants.XM_JIMMER + " 插件关闭!");
		}
	}

}
