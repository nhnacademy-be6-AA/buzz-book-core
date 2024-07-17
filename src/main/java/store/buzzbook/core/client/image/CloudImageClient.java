package store.buzzbook.core.client.image;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import netscape.javascript.JSObject;

@FeignClient(name = "cloudImageClient", url = "https://api-image.nhncloudservice.com")
public interface CloudImageClient {

	@PostMapping(value = "/image/v2.0/appkeys/${nhncloud.image.appkey}/images", consumes = "multipart/form-data")
	ResponseEntity<JSONObject> uploadImages(
		@RequestHeader("Authorization") String authorization,
		@RequestPart("params") String params,
		@RequestPart("files") List<MultipartFile> files
	);
}
