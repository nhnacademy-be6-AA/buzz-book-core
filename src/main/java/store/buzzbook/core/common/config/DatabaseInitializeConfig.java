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
		int[] standard = new int[] {0, 100000, 200000, 300000};
		double[] benfit = new double[] {1.0d, 2.0d, 2.0d, 3.0d};

		return args -> {
			for (int i = 0; i < GradeName.values().length; i++) {
				GradeName gradeName = GradeName.values()[i];
				log.info("Initializing grade name : {}", gradeName);

				Grade grade = Grade.builder()
					.name(gradeName)
					.standard(standard[i])
					.benefit(benfit[i])
					.build();

				gradeService.save(grade);
			}

		};

	}
}
