package store.buzzbook.core.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeName;
import store.buzzbook.core.service.user.GradeService;

@Configuration
@RequiredArgsConstructor
public class DatabaseInitializeConfig {
	private static final Logger log = LoggerFactory.getLogger(DatabaseInitializeConfig.class);
	public final GradeService gradeService;

	@Bean
	public ApplicationRunner initDatabase() {
		return args -> {

			for (GradeName name : GradeName.values()) {
				log.info("Initializing grade name : {}", name);
				Grade grade = Grade.builder()
					.name(name)
					.standard(100000)
					.benefit(2d)
					.build();

				gradeService.save(grade);
			}

		};

	}
}
