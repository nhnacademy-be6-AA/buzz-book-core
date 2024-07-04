package store.buzzbook.core.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "spring.datasource")
@Getter
@Setter
@Component
@Profile("prod")
public class MySQLProperties {
	private String driverClassName;
	private Integer maxIdle;
	private Integer maxTotal;
	private Integer initialSize;
	private Integer minIdle;
}
