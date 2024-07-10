package store.buzzbook.core.client.auth;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import store.buzzbook.core.dto.user.DoorayMessagePayload;

@FeignClient(name = "doorayClient", url = "https://hook.dooray.com/services/3204376758577275363/3844281503041287963/rhI2AlZaT-SjIHz-Zu-BiQ")
public interface DoorayClient {
	@PostMapping
	ResponseEntity<String> sendMessage(@RequestBody DoorayMessagePayload messagePayload);

}
