package store.buzzbook.core.controller.point;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;

import store.buzzbook.core.dto.point.CreatePointLogRequest;
import store.buzzbook.core.dto.point.CreatePointPolicyRequest;
import store.buzzbook.core.dto.point.DeletePointPolicyRequest;
import store.buzzbook.core.dto.point.PointPolicyResponse;
import store.buzzbook.core.dto.point.UpdatePointPolicyRequest;
import store.buzzbook.core.entity.point.PointLog;
import store.buzzbook.core.service.auth.AuthService;
import store.buzzbook.core.service.point.PointService;

@WebMvcTest(PointController.class)
class PointControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private PointService pointService;

	@Test
	@DisplayName("get point policies")
	void getPointPolicies() throws Exception {
		// given
		PointPolicyResponse response = new PointPolicyResponse(1L, "test", 1000, 0, false);
		when(pointService.getPointPolicies()).thenReturn(List.of(response));

		// when & then
		mockMvc.perform(get("/api/account/points"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(response.id()))
			.andExpect(jsonPath("$[0].name").value(response.name()))
			.andExpect(jsonPath("$[0].point").value(response.point()))
			.andExpect(jsonPath("$[0].rate").value(response.rate()))
			.andExpect(jsonPath("$[0].deleted").value(response.deleted()));
	}

	@Test
	@DisplayName("get user point")
	void getUserPoint() throws Exception {
		// given
		when(pointService.getUserPoint(anyLong())).thenReturn(1000);

		// when & then
		mockMvc.perform(get("/api/account/points/logs/last-point"))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("create point log")
	void createPointLog() throws Exception {
		// given
		long userId = 1L;
		CreatePointLogRequest request = new CreatePointLogRequest("test", 1000);
		PointLog testPointLog = PointLog.builder()
			.id(1L)
			.inquiry(request.inquiry())
			.delta(request.deltaPoint())
			.build();
		when(pointService.createPointLogWithDelta(anyLong(), anyString(), anyInt())).thenReturn(testPointLog);

		// when & then
		mockMvc.perform(post("/api/account/points/logs")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(userId(userId)))
			.andExpect(status().isOk());
	}

	private RequestPostProcessor userId(Long userId) {
		return request -> {
			request.setAttribute(AuthService.USER_ID, userId);
			return request;
		};
	}

	@Test
	@DisplayName("create point policy")
	void createPointPolicy() throws Exception {
		// given
		CreatePointPolicyRequest request = new CreatePointPolicyRequest("test", 1000, 0);
		PointPolicyResponse response = new PointPolicyResponse(1L, "test", 1000, 0, false);
		when(pointService.createPointPolicy(any())).thenReturn(response);

		// when & then
		mockMvc.perform(post("/api/account/points")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(response.id()))
			.andExpect(jsonPath("$.name").value(response.name()))
			.andExpect(jsonPath("$.point").value(response.point()))
			.andExpect(jsonPath("$.rate").value(response.rate()))
			.andExpect(jsonPath("$.deleted").value(response.deleted()));
	}

	@Test
	@DisplayName("update point policy")
	void updatePointPolicy() throws Exception {
		// given
		UpdatePointPolicyRequest request = new UpdatePointPolicyRequest(1L, 1000, 0);
		doNothing().when(pointService).updatePointPolicy(any());

		// when & then
		mockMvc.perform(put("/api/account/points")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("delete point policy")
	void deletePointPolicy() throws Exception {
		// given
		DeletePointPolicyRequest request = new DeletePointPolicyRequest(1L);
		doNothing().when(pointService).deletePointPolicy(any());

		// when & then
		mockMvc.perform(delete("/api/account/points")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
	}
}
