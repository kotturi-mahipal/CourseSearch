package org.undoschool.coursesearch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiIntegrationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void healthEndpointWorks() {
		ResponseEntity<String> resp = restTemplate.getForEntity("http://localhost:" + port + "/api/health", String.class);
		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(resp.getBody()).contains("Course Search API is running!");
	}

	@Test
	void searchEndpointReturnsShape() {
		ResponseEntity<Map<String,Object>> resp = restTemplate.exchange(
				"http://localhost:" + port + "/api/search",
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<Map<String,Object>>() {}
		);
		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
		Map<String, Object> body = resp.getBody();
		assertThat(body).isNotNull();
		assertThat(body).containsKeys("total", "courses");
		Object total = body.get("total");
		assertThat(total).isInstanceOf(Number.class);
		Object courses = body.get("courses");
		assertThat(courses).isInstanceOf(List.class);
	}
}
