package store.buzzbook.core.controller.image;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.service.image.ImageService;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageUploadController {

	private final ImageService imageService;

	@PostMapping("/upload")
	public ResponseEntity<String> uploadImages(
		@RequestPart("files") List<MultipartFile> files,
		@RequestParam("folderPath") String folderPath
	) {
		String response = imageService.uploadImagesToCloud(files, folderPath);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/list")
	public ResponseEntity<Map<String, Object>> getFolderFiles(@RequestParam("folderPath") String folderPath) {
		Map<String, Object> response = imageService.getFolderImages(folderPath);
		return ResponseEntity.ok(response);
	}
}