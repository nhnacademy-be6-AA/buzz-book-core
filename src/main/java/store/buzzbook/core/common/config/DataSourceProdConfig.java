package store.buzzbook.core.common.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.dto.SecretResponse;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Slf4j
@Getter
@Setter
@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {
	private final MySQLProperties mySQLProperties;

	@Value("${nhncloud.keymanager.appkey}")
	private String appKey;

	@Value("${nhncloud.keymanager.mysql.url}")
	private String mysqlUrl;

	@Value("${nhncloud.keymanager.mysql.username}")
	private String mysqlUsername;

	@Value("${nhncloud.keymanager.mysql.password}")
	private String mysqlPassword;

	@Bean
	public DataSource dataSource() {
		BasicDataSource basicDataSource = new BasicDataSource();

		RestTemplate restTemplate = new RestTemplate();

		SecretResponse url = restTemplate.getForObject(
			String.format("https://api-keymanager.nhncloudservice.com/keymanager/v1.0/appkey/%s/secrets/%s", appKey, mysqlUrl), SecretResponse.class);

		SecretResponse username = restTemplate.getForObject(
			String.format("https://api-keymanager.nhncloudservice.com/keymanager/v1.0/appkey/%s/secrets/%s", appKey, mysqlUsername), SecretResponse.class);

		SecretResponse password = restTemplate.getForObject(
			String.format("https://api-keymanager.nhncloudservice.com/keymanager/v1.0/appkey/%s/secrets/%s", appKey, mysqlPassword), SecretResponse.class);

		basicDataSource.setDriverClassName(mySQLProperties.getDriverClassName());
		basicDataSource.setUrl(url.getBody().getSecret());
		basicDataSource.setUsername(username.getBody().getSecret());
		basicDataSource.setPassword(password.getBody().getSecret());
		basicDataSource.setMaxIdle(mySQLProperties.getMaxIdle());
		basicDataSource.setMaxTotal(mySQLProperties.getMaxTotal());
		basicDataSource.setInitialSize(mySQLProperties.getInitialSize());
		basicDataSource.setMinIdle(mySQLProperties.getMinIdle());

		basicDataSource.setValidationQuery("SELECT 1");
		basicDataSource.setTestOnReturn(false);
		basicDataSource.setTestOnBorrow(false);
		basicDataSource.setTestWhileIdle(false);

		return basicDataSource;
	}
}
