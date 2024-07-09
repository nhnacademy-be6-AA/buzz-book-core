package store.buzzbook.core.client.image;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "imageClient", url = "https://api-image.nhncloudservice.com")
public interface ImageClient {

	@PostMapping(value = "/image/v2.0/appkeys/{appKey}/folders/{folderPath}/images", consumes = "multipart/form-data")
	ResponseEntity<String> uploadImage(
		@RequestHeader("Authorization") String authorization,
		@RequestParam("appKey") String appKey,
		@PathVariable("folderPath") String folderPath,
		@RequestPart("file") MultipartFile file
	);
}