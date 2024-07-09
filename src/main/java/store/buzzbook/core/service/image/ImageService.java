package store.buzzbook.core.service.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.client.image.ImageClient;

@Service
public class ImageService {

	private final ImageClient imageClient;

	@Value("${nhncloud.image.appkey}")
	private String appKey;

	@Value("${nhncloud.image.secretkey}")
	private String secretKey;

	@Autowired
	public ImageService(ImageClient imageClient) {
		this.imageClient = imageClient;
	}

	public String uploadImage(MultipartFile file, String folderPath) {
		String authorizationHeader = "Bearer " + secretKey;
		ResponseEntity<String> response = imageClient.uploadImage(authorizationHeader, appKey, folderPath, file);
		return response.getBody();
	}
}