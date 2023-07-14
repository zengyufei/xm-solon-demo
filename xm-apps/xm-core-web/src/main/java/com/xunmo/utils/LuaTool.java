//package com.xunmo.utils;
//
//import org.noear.solon.annotation.Component;
//import org.noear.solon.annotation.Inject;
//import org.redisson.api.RScript;
//import org.redisson.api.RedissonClient;
//import org.redisson.client.codec.LongCodec;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//import java.time.temporal.ChronoUnit;
//import java.util.Collections;
//
///**
// * @Classname LuaTool
// * @Date 2022/2/21 14:28
// * @Author WangZY
// * @Description redis-lua脚本工具
// */
//@Component
//public class LuaTool {
//
//	@Inject
//	private RedissonClient redissonClient;
//
//	/**
//	 * 单号按照keyPrefix+yyyyMMdd+4位流水号的格式生成
//	 * @param keyPrefix 流水号前缀标识--用作redis key名
//	 * @return 单号
//	 */
//	public String generateOrder(String keyPrefix) {
//		RScript script = redissonClient.getScript(new LongCodec());
//		long between = ChronoUnit.SECONDS.between(LocalDateTime.now(),
//				LocalDateTime.of(LocalDate.now(), LocalTime.MAX));
//		Long eval = script.eval(RScript.Mode.READ_WRITE,
//				"local sequence = redis.call('get', KEYS[1]);if sequence then if sequence>ARGV[1] then sequence "
//						+ "= 0 else sequence = sequence+1 end else sequence = 1 end;redis.call('set', KEYS[1], "
//						+ "sequence);redis.call('expire',KEYS[1],ARGV[2]);return sequence;",
//				RScript.ReturnType.INTEGER, Collections.singletonList(keyPrefix), 9999, between);
//		DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd");
//		int len = String.valueOf(eval).length();
//		StringBuilder res = new StringBuilder();
//		for (int i = 0; i < 4 - len; i++) {
//			res.append("0");
//		}
//		res.append(eval);
//		return keyPrefix + yyyyMMdd + res;
//	}
//
//	/**
//	 * 限流器-漏桶算法思想
//	 * @param key 被限流的key
//	 * @param limit 限制次数
//	 * @return 当前时间范围内正在执行的线程数
//	 */
//	public long judgeLimit(String key, int limit) {
//		RScript script = redissonClient.getScript(new LongCodec());
//		return script.eval(RScript.Mode.READ_WRITE,
//				"local count = redis.call('get', KEYS[1]);if count then if count>=ARGV[1] then count=-1 else redis"
//						+ ".call('incr',KEYS[1]) end else count = 1 redis.call('set', KEYS[1],count) end;redis.call"
//						+ "('expire',KEYS[1],ARGV[2]);return count;",
//				RScript.ReturnType.INTEGER, Collections.singletonList(key), limit, 600);
//	}
//
//	/**
//	 * 归还次数-漏桶算法思想
//	 * @param key 被限流的key
//	 * @return 正在执行的线程数
//	 */
//	public long returnCount(String key) {
//		RScript script = redissonClient.getScript(new LongCodec());
//		return script.eval(RScript.Mode.READ_WRITE,
//				"local count = tonumber(redis.call('get', KEYS[1]));if count then if count>0 then count=count-1 redis"
//						+ ".call('set', KEYS[1],count) redis.call('expire',KEYS[1],ARGV[1]) else count = 0 end else "
//						+ "count = 0 end;return count;",
//				RScript.ReturnType.INTEGER, Collections.singletonList(key), 600);
//	}
//
//	/**
//	 * @param key 幂等性校验的key
//	 * @param expireTime 设定一个窗口时间
//	 * @return 如果不为-1说明已有请求在当前时间窗口内运行
//	 */
//	public long idempotencyCheck(String key, int expireTime) {
//		RScript script = redissonClient.getScript(new LongCodec());
//		return script.eval(RScript.Mode.READ_WRITE,
//				"local exist = redis.call('get', KEYS[1]);" + "if not exist then "
//						+ "redis.call('set', KEYS[1], ARGV[1]);" + "redis.call('expire',KEYS[1],ARGV[1]);"
//						+ "exist = -1;" + "end;" + "return exist;",
//				RScript.ReturnType.INTEGER, Collections.singletonList(key), expireTime);
//	}
//
//}
