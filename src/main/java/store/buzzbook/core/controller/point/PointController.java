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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.annotation.JwtValidate;
import store.buzzbook.core.dto.point.CreatePointPolicyRequest;
import store.buzzbook.core.dto.point.DeletePointPolicyRequest;
import store.buzzbook.core.dto.point.PointPolicyResponse;
import store.buzzbook.core.dto.point.UpdatePointPolicyRequest;
import store.buzzbook.core.service.point.PointService;

@Tag(name = "회원의 포인트 관련 컨트롤러", description = "유저의 포인트 조회, 추가 관리")
@RestController
@RequestMapping("/api/account/points")
@RequiredArgsConstructor
public class PointController {

	private final PointService pointService;

	@JwtValidate
	@GetMapping
	@Operation(summary = "포인트 정책 조회 요청", description = "포인트 정책을 조회 합니다.")
	public ResponseEntity<List<PointPolicyResponse>> getPointPolicies() {
		return ResponseEntity.ok(pointService.getPointPolicies());
	}

	@JwtValidate
	@PostMapping
	@Operation(summary = "포인트 정책 생성 요청", description = "포인트 정책을 생성 합니다.")
	public ResponseEntity<PointPolicyResponse> createPointPolicy(@RequestBody CreatePointPolicyRequest request) {
		return ResponseEntity.ok(pointService.createPointPolicy(request));
	}

	@JwtValidate
	@PutMapping
	@Operation(summary = "포인트 정책 수정 요청", description = "포인트 정책을 수정 합니다.")
	public ResponseEntity<Void> updatePointPolicy(@RequestBody UpdatePointPolicyRequest request) {
		pointService.updatePointPolicy(request);
		return ResponseEntity.ok().build();
	}

	@JwtValidate
	@DeleteMapping
	@Operation(summary = "포인트 정책 삭제 요청", description = "포인트 정책을 삭제 합니다.")
	public ResponseEntity<Void> deletePointPolicy(@RequestBody DeletePointPolicyRequest request) {
		pointService.deletePointPolicy(request);
		return ResponseEntity.ok().build();
	}
}
