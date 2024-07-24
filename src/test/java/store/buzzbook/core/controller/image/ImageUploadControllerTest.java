package store.buzzbook.core.controller.image;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import store.buzzbook.core.service.image.ImageService;

@WebMvcTest(ImageUploadController.class)
class ImageUploadControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ImageService imageService;

	@Mock
	private MockMultipartFile multipartFile1, multipartFile2, multipartFile3;

	@Test
	@DisplayName("upload image controller")
	void testSaveReview() throws Exception {
		List<MockMultipartFile> files = List.of(multipartFile1, multipartFile2, multipartFile3);

		String folderPath = "/test-folder";

		when(imageService.uploadImagesToCloud(anyList(), eq(folderPath))).thenReturn("Images uploaded successfully");

		mockMvc.perform(MockMvcRequestBuilders.multipart("/api/image/upload")
				.file("files", files.get(0).getBytes())
				.file("files", files.get(1).getBytes())
				.file("files", files.get(2).getBytes())
				.param("folderPath", folderPath)
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(MockMvcResultMatchers.status().isOk());
	}

}
