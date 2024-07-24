package store.buzzbook.core.service.image;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.client.image.CloudImageClient;

@RequiredArgsConstructor
@Service
public class ImageService {

	private static final String REVIEW_FOLDER_PATH = "/aa-image/review";

	private final CloudImageClient cloudImageClient;
	private final ObjectMapper objectMapper;

	@Value("${nhncloud.image.secretkey}")
	private String secretKey;

	public String uploadImagesToCloud(List<MultipartFile> files, String folderPath) {
		boolean overwrite = true;

		Map<String, Object> paramsMap = Map.of(
			"basepath", folderPath,
			"overwrite", overwrite
		);

		String paramsJson = null;

		try {
			paramsJson = new ObjectMapper().writeValueAsString(paramsMap);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

		ResponseEntity<JSONObject> response = cloudImageClient.uploadImages(secretKey, paramsJson, files);

		return Objects.requireNonNull(response.getBody()).toJSONString();

	}

	public List<String> multiImageUpload(List<MultipartFile> files) {

		Map<String, Object> paramsMap = Map.of(
			"basepath", REVIEW_FOLDER_PATH,
			"overwrite", false,
			"autorename", true
		);
		try {
			String paramsJson = new ObjectMapper().writeValueAsString(paramsMap);
			ResponseEntity<JSONObject> response = cloudImageClient.uploadImages(secretKey, paramsJson, files);


			JsonNode successesNode = objectMapper.readTree(Objects.requireNonNull(response.getBody()).toString()).get("successes");
			List<String> imageUrls = new ArrayList<>();
			if (successesNode != null && successesNode.isArray()) {
				for (JsonNode success : successesNode) {
					String imageUrl = success.get("url").asText();
					imageUrls.add(imageUrl);
				}
			}
			return imageUrls;
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
