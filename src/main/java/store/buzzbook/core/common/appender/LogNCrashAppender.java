package store.buzzbook.core.common.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.AllArgsConstructor;
import store.buzzbook.core.client.log.LogNCrashAdapter;
import store.buzzbook.core.dto.log.LogNCrashRequest;

@AllArgsConstructor
public class LogNCrashAppender extends AppenderBase<ILoggingEvent> {

	private String version;
	private String host;
	private String platform;
	private String logVersion;
	private String logSource;
	private String logType;
	private String appKey;
	private LogNCrashAdapter logNCrashAdapter;

	@Override
	protected void append(ILoggingEvent iLoggingEvent) {
		LogNCrashRequest request = createLogNCrashRequest(iLoggingEvent);
		logNCrashAdapter.sendLog(request);
	}

	private LogNCrashRequest createLogNCrashRequest(ILoggingEvent iLoggingEvent) {
		return LogNCrashRequest.builder()
			.projectName(appKey)
			.projectVersion(version)
			.logVersion(logVersion)
			.body(iLoggingEvent.getFormattedMessage())
			.logSource(logSource)
			.logType(logType)
			.logLevel(iLoggingEvent.getLevel().toString())
			.host(host)
			.Platform(platform)
			.build();
	}
}
