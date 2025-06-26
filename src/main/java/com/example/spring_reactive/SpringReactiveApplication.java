package com.example.spring_reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@SpringBootApplication
@RestController
public class SpringReactiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringReactiveApplication.class, args);
	}

	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {


		return String.format("Hello %s!", name);
	}

	void expectSkylerJesseComplete(Flux<User> flux) {
		StepVerifier.create(flux)
				.assertNext(user -> assertThat(user.getUsername()).isEqualTo("swhite"))
				.assertNext(user -> assertThat(user.getUsername()).isEqualTo("jpinkman"))
				.verify();
	}
}
