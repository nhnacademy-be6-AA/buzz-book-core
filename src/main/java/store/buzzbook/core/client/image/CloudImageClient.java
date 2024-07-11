package store.buzzbook.core.client.image;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "cloudImageClient", url = "https://api-image.nhncloudservice.com")
public interface CloudImageClient {

	@PostMapping(value = "/image/v2.0/appkeys/{appkey}/images", consumes = "multipart/form-data")
	ResponseEntity<String> uploadImages(
		@PathVariable("appkey") String appKey,
		@RequestHeader("Authorization") String authorization,
		@RequestPart("params") String params,
		@RequestPart("files") List<MultipartFile> files
	);

	@GetMapping(value = "/image/v2.0/appkeys/{appkey}/folders")
	ResponseEntity<String> getFolderFiles(
		@PathVariable("appkey") String appKey,
		@RequestHeader("Authorization") String authorization,
		@RequestParam("basepath") String basepath
	);

}