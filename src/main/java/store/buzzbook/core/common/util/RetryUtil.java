package store.buzzbook.core.common.util;

import java.util.concurrent.Callable;

public class RetryUtil {
	public static <T> T executeWithRetry(Callable<T> callable, int maxRetries, long retryDelayMs) throws Exception {
		int attempt = 0;
		while (true) {
			try {
				return callable.call();
			} catch (Exception e) {
				attempt++;
				if (attempt >= maxRetries) {
					throw e;
				}
				Thread.sleep(retryDelayMs);
			}
		}
	}
}
