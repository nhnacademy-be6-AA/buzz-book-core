package store.buzzbook.core.service.image;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.client.image.CloudImageClient;

@RequiredArgsConstructor
@Service
public class ImageService {

	private final CloudImageClient cloudImageClient;

	@Value("${nhncloud.image.appkey}")
	private String appKey;

	@Value("${nhncloud.image.secretkey}")
	private String secretKey;

	public String uploadImagesToCloud(List<MultipartFile> files, String folderPath) {
		String authorizationHeader = secretKey; // secretKey를 Authorization 헤더로 사용
		String basepath = folderPath;
		boolean overwrite = true;

		Map<String, Object> paramsMap = Map.of(
			"basepath", basepath,
			"overwrite", overwrite
		);

		try {
			String paramsJson = new ObjectMapper().writeValueAsString(paramsMap);
			ResponseEntity<String> response = cloudImageClient.uploadImages(secretKey, paramsJson, files);
			return response.getBody();
		} catch (Exception e) {
			throw new RuntimeException("Failed to upload images", e);
		}
	}
}