package com.CampusRoomStatus;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.CampusRoomStatus.service.BuildingsService;

@SpringBootTest
class CampusRoomStatusApplicationTests {

	@Autowired
	private BuildingsService buildingsService;

	@Test
	void contextLoads() {
        assertThat(buildingsService).isNotNull();
	}

	@Test
	void testCacheEviction() {
		buildingsService.getAll();
		buildingsService.syncFromGoogle();
		buildingsService.getAll();
	}
}
