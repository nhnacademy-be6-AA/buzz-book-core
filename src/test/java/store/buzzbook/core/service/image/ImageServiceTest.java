package store.buzzbook.core.service.image;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import store.buzzbook.core.client.image.CloudImageClient;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

	@Mock
	private CloudImageClient cloudImageClient;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private ImageService imageService;


	private final String mockFile1Path = "http://mock-url/image1.jpg";
	private final String mockFile2Path = "http://mock-url/image2.png";

	@Mock
	MultipartFile mockFile1, mockFile2, mockFile3;

	@Test
	void testUploadImagesToCloud() throws ParseException {
		// Given
		List<MultipartFile> mockFiles = List.of(mockFile1, mockFile2, mockFile3);

		String mockResponseJsonString =
			"{\"successes\":[{\"url\": \"" + mockFile1Path + "\"},{\"url\":\"" + mockFile2Path + "\"}]}";
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(mockResponseJsonString);
		ResponseEntity<JSONObject> mockResponseEntity = new ResponseEntity<>(jsonObject, HttpStatus.OK);
		when(cloudImageClient.uploadImages(any(), anyString(), eq(mockFiles))).thenReturn(mockResponseEntity);

		// When
		String result = imageService.uploadImagesToCloud(mockFiles, "/test-folder");

		// Then
		assertEquals(jsonObject.toString(), result);
	}

	@Test
	void testMultiImageUpload() throws JsonProcessingException {
		// Given
		List<MultipartFile> mockFiles = List.of(mockFile1, mockFile2);
		ObjectMapper om = new ObjectMapper();

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("successes", List.of(
			new JSONObject(Map.of("url", mockFile1Path)),
			new JSONObject(Map.of("url", mockFile2Path))
		));
		ResponseEntity<JSONObject> mockResponseEntity = ResponseEntity.ok(jsonObject);
		when(cloudImageClient.uploadImages(any(), anyString(), eq(mockFiles))).thenReturn(mockResponseEntity);
		when(objectMapper.readTree(jsonObject.toString())).thenReturn(om.readTree(jsonObject.toString()));

		// When
		List<String> result = imageService.multiImageUpload(mockFiles);

		// Then
		assertEquals(2, result.size());
		assertEquals(mockFile1Path, result.get(0));
		assertEquals(mockFile2Path, result.get(1));
	}
}
