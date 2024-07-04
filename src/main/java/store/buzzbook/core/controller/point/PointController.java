package store.buzzbook.core.controller.point;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.annotation.JwtValidate;
import store.buzzbook.core.dto.point.CreatePointPolicyRequest;
import store.buzzbook.core.dto.point.DeletePointPolicyRequest;
import store.buzzbook.core.dto.point.PointPolicyResponse;
import store.buzzbook.core.dto.point.UpdatePointPolicyRequest;
import store.buzzbook.core.service.point.PointService;

@Tag(name = "회원의 쿠폰 관련 컨트롤러", description = "유저의 쿠폰 조회, 추가 관리")
@RestController
@RequestMapping("/api/account/points")
@RequiredArgsConstructor
public class PointController {

	private final PointService pointService;

	@JwtValidate
	@GetMapping
	public ResponseEntity<List<PointPolicyResponse>> getPointPolicies() {
		return ResponseEntity.ok(pointService.getPointPolicies());
	}

	@JwtValidate
	@PostMapping
	public ResponseEntity<PointPolicyResponse> createPointPolicy(@RequestBody CreatePointPolicyRequest request) {
		return ResponseEntity.ok(pointService.createPointPolicy(request));
	}

	@JwtValidate
	@PutMapping
	public ResponseEntity<Void> updatePointPolicy(@RequestBody UpdatePointPolicyRequest request) {
		pointService.updatePointPolicy(request);
		return ResponseEntity.ok().build();
	}

	@JwtValidate
	@DeleteMapping
	public ResponseEntity<Void> deletePointPolicy(@RequestBody DeletePointPolicyRequest request) {
		pointService.deletePointPolicy(request);
		return ResponseEntity.ok().build();
	}
}
