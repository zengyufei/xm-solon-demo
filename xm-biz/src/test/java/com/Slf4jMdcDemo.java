package com;

import com.alibaba.ttl.TtlRunnable;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.slf4j.TtlMDCAdapter;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Slf4jMdcDemo {

	public static void main(String[] args) throws Exception {
		TtlMDCAdapter.getInstance();
		// Log in Main Thread
		log.info("Log in main!");

		final ExecutorService executorService = Executors.newFixedThreadPool(2);

		// Run task in thread pool
		executorService.submit(createTask()).get();

		// Init Log Context, set TTL
		// More KV if needed
		final String TRACE_ID = "reqId";
		final String TRACE_ID_VALUE = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
		MDC.put(TRACE_ID, TRACE_ID_VALUE);

		// Log in Main Thread
		log.info("Log in main!");

		executorService.submit(createTask()).get();

		log.info("Exit main");

		executorService.shutdown();
	}

	private static Runnable createTask() {
		final Runnable task = () -> {
			// Log in thread pool
			MDC.put("task", new Date().toString());
			log.info("Log in Runnable!");
		};
		return TtlRunnable.get(task);
	}

}
