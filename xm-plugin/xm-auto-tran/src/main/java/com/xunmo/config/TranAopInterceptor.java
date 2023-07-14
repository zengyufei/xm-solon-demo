package com.xunmo.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.xunmo.utils.TranAfterUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.SolonProps;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.data.annotation.Tran;
import org.noear.solon.data.annotation.TranAnno;
import org.noear.solon.data.tran.TranUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 事务aop拦截器
 *
 * @author zengyufei
 * @date 2022/11/19
 */
@Slf4j
public class TranAopInterceptor implements Interceptor {

	private static String appName;

	private static List<String> prefixNameList = new ArrayList<>();

	private static List<String> suffixNameList = new ArrayList<>();

	public TranAopInterceptor() {
		init();
	}

	public void init() {
		final SolonProps cfg = Solon.cfg();
		appName = cfg.get("solon.app.name");
		prefixNameList = cfg.getBean("xm.tran.method.prefix", List.class);
		suffixNameList = cfg.getBean("xm.tran.method.suffix", List.class);
	}

	@Override
	public Object doIntercept(Invocation inv) throws Throwable {
		final Method method = inv.method().getMethod();
		final String methodName = method.getName();
		boolean isHasTran = getIsHasTran(method);
		if (isHasTran) {
			log.info(appName + "应用, 调用 " + methodName + " 已存在事务");
			final Object o = inv.invoke();
			final List<TranAfterUtil.AfterFunction> afterMethods = TranAfterUtil.getAfterMethods();
			if (CollUtil.isNotEmpty(afterMethods)) {
				for (TranAfterUtil.AfterFunction consumer : afterMethods) {
					consumer.test();
				}
			}
			final List<TranAfterUtil.AfterFunction> afterSyncMethods = TranAfterUtil.getAfterSyncMethods();
			if (CollUtil.isNotEmpty(afterSyncMethods)) {
				for (TranAfterUtil.AfterFunction consumer : afterSyncMethods) {
					ThreadUtil.execute(consumer::test);
				}
			}
			return o;
		}

		boolean isAddTran = getIsAddTran(methodName);
		if (isAddTran) {
			// 自动给符合的方法名自动进入事务;
			log.info(appName + "应用, 调用 " + methodName + " 方法满足 xm.tran.method 匹配, 自动加事务");
			AtomicReference<Object> rst = new AtomicReference<>();
			TranUtils.execute(new TranAnno(), () -> rst.set(inv.invoke()));
			final Object o = rst.get();
			final List<TranAfterUtil.AfterFunction> afterMethods = TranAfterUtil.getAfterMethods();
			if (CollUtil.isNotEmpty(afterMethods)) {
				for (TranAfterUtil.AfterFunction consumer : afterMethods) {
					consumer.test();
				}
			}
			final List<TranAfterUtil.AfterFunction> afterSyncMethods = TranAfterUtil.getAfterSyncMethods();
			if (CollUtil.isNotEmpty(afterSyncMethods)) {
				for (TranAfterUtil.AfterFunction consumer : afterSyncMethods) {
					ThreadUtil.execute(consumer::test);
				}
			}
			return o;
		}
		else {
			// 非事务
			return inv.invoke();
		}
	}

	private boolean getIsHasTran(Method method) {
		boolean isHasTran = false;
		final Annotation[] annotations = method.getAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation instanceof Tran) {
				isHasTran = true;
				break;
			}
		}
		return isHasTran;
	}

	/**
	 * 判断方法是否需要添加事务
	 * @param methodName 方法名称
	 * @return boolean
	 */
	private boolean getIsAddTran(String methodName) {
		boolean isAddTran = false;
		if (CollUtil.isNotEmpty(prefixNameList)) {
			for (String name : prefixNameList) {
				if (methodName.startsWith(name)) {
					isAddTran = true;
					break;
				}
			}
		}
		if (CollUtil.isNotEmpty(suffixNameList)) {
			for (String name : suffixNameList) {
				if (methodName.startsWith(name)) {
					isAddTran = true;
					break;
				}
			}
		}
		return isAddTran;
	}

}
