package store.buzzbook.core.controller.image;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.service.image.ImageService;

@RestController
@RequiredArgsConstructor
public class ImageController {

	private final ImageService imageService;

	@PostMapping("api/product/upload-image")
	public ResponseEntity<String> uploadImage(
		@RequestPart("file") MultipartFile file,
		@RequestParam("folderPath") String folderPath
	) {
		String response = imageService.uploadImage(file, folderPath);
		return ResponseEntity.ok(response);
	}
}
