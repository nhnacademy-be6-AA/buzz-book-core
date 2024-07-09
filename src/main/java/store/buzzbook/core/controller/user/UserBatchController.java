package store.buzzbook.core.controller.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.user.UserRealBill;
import store.buzzbook.core.service.user.UserService;

@RestController
@RequestMapping("/api/account/bills")
@RequiredArgsConstructor
public class UserBatchController {
	private final UserService userService;

	@GetMapping("/3month")
	ResponseEntity<List<UserRealBill>> get3MonthBills() {
		return ResponseEntity.ok(userService.getUserRealBills());
	}
}
