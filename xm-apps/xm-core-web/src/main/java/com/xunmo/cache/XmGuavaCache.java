package com.xunmo.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class XmGuavaCache<K, V> {

	// 缓存自动刷新周期
	protected int refreshDuration = 10;

	// 缓存刷新周期时间格式
	protected TimeUnit refreshTimeunit = TimeUnit.MINUTES;

	// 缓存过期时间（可选择）
	protected int expireDuration = -1;

	// 缓存刷新周期时间格式
	protected TimeUnit expireTimeunit = TimeUnit.HOURS;

	// 缓存最大容量
	protected int maxSize = 4;

	// 数据刷新线程池
	protected static ListeningExecutorService refreshPool = MoreExecutors
		.listeningDecorator(Executors.newFixedThreadPool(20));

	private LoadingCache<K, V> cache = null;

	/**
	 * 用于初始化缓存值（某些场景下使用，例如系统启动检测缓存加载是否征程）
	 */
	public abstract void loadValueWhenStarted();

	/**
	 * @description: 定义缓存值的计算方法
	 * @description: 新值计算失败时抛出异常，get操作时将继续返回旧的缓存
	 */
	protected abstract V getValueWhenExpired(K key) throws Exception;

	/**
	 * @description: 从cache中拿出数据操作
	 * @param key
	 */
	public V getValue(K key) throws Exception {
		try {
			return getCache().get(key);
		}
		catch (Exception e) {
			log.error("从内存缓存中获取内容时发生异常，key: " + key, e);
			throw e;
		}
	}

	public V getValueOrDefault(K key, V defaultValue) {
		try {
			return getCache().get(key);
		}
		catch (Exception e) {
			log.error("从内存缓存中获取内容时发生异常，key: " + key, e);
			return defaultValue;
		}
	}

	/**
	 * 设置基本属性
	 */
	public XmGuavaCache<K, V> setRefreshDuration(int refreshDuration) {
		this.refreshDuration = refreshDuration;
		return this;
	}

	public XmGuavaCache<K, V> setRefreshTimeUnit(TimeUnit refreshTimeunit) {
		this.refreshTimeunit = refreshTimeunit;
		return this;
	}

	public XmGuavaCache<K, V> setExpireDuration(int expireDuration) {
		this.expireDuration = expireDuration;
		return this;
	}

	public XmGuavaCache<K, V> setExpireTimeUnit(TimeUnit expireTimeunit) {
		this.expireTimeunit = expireTimeunit;
		return this;
	}

	public XmGuavaCache<K, V> setMaxSize(int maxSize) {
		this.maxSize = maxSize;
		return this;
	}

	public void clearAll() {
		this.getCache().invalidateAll();
	}

	/**
	 * @description: 获取cache实例
	 */
	private LoadingCache<K, V> getCache() {
		if (cache == null) {
			synchronized (this) {
				if (cache == null) {
					CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder().maximumSize(maxSize);
					if (refreshDuration > 0) {
						cacheBuilder = cacheBuilder.refreshAfterWrite(refreshDuration, refreshTimeunit);
					}
					if (expireDuration > 0) {
						cacheBuilder = cacheBuilder.expireAfterWrite(expireDuration, expireTimeunit);
					}
					cache = cacheBuilder.build(new CacheLoader<K, V>() {
						@Override
						public V load(K key) throws Exception {
							return getValueWhenExpired(key);
						}

						@Override
						public ListenableFuture<V> reload(final K key, V oldValue) throws Exception {
							return refreshPool.submit(() -> getValueWhenExpired(key));
						}
					});
				}
			}
		}
		return cache;
	}

	@Override
	public String toString() {
		return "GuavaCache";
	}

	public void refresh(K key) throws Exception {
		this.getCache().refresh(key);
	}

}
