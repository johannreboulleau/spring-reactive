package com.example.spring_reactive;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

package io.pivotal.literx.domain;

public class User {

	public static final User SKYLER = new User("swhite", "Skyler", "White");
	public static final User JESSE = new User("jpinkman", "Jesse", "Pinkman");
	public static final User WALTER = new User("wwhite", "Walter", "White");
	public static final User SAUL = new User("sgoodman", "Saul", "Goodman");

	private final String username;

	private final String firstname;

	private final String lastname;

	public User(String username, String firstname, String lastname) {
		this.username = username;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public String getUsername() {
		return username;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		User user = (User) o;

		if (!username.equals(user.username)) {
			return false;
		}
		if (!firstname.equals(user.firstname)) {
			return false;
		}
		return lastname.equals(user.lastname);

	}

	@Override
	public int hashCode() {
		int result = username.hashCode();
		result = 31 * result + firstname.hashCode();
		result = 31 * result + lastname.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "Person{" +
				"username='" + username + '\'' +
				", firstname='" + firstname + '\'' +
				", lastname='" + lastname + '\'' +
				'}';
	}
}



@SpringBootTest
class SpringReactiveApplicationTests {

	@Test
	void contextLoads() {
	}

	Flux requestAllExpectFour(Flux<String> flux) {
		System.out.println("flux = " + flux);

		return Flux.just(User.JESSE).doFirst(() -> System.out.println("Starring:"))
				.doOnNext((user) -> System.out.println(user.getFirstname() + " " + user.getLastname()))
				.doFinally((signalType) -> System.out.println("The end!"));
	}

	void expect3600Elements(Supplier<Flux<Long>> supplier) {

		StepVerifier.withVirtualTime(supplier)
				.expectSubscription()
				.thenAwait(Duration.ofMillis(10))
				.expectNextCount(3600)
				.expectComplete()
				.verify();
	}

	Mono<User> betterCallSaulForBogusMono(Mono<User> mono) {

		return mono.onErrorReturn(User.SAUL);
	}

	Flux<User> betterCallSaulAndJesseForBogusFlux(Flux<User> flux) {
		return flux.onErrorResume(throwable -> Flux.just(User.SAUL, User.JESSE));
	}

	CompletableFuture<User> fromMonoToCompletableFuture(Mono<User> mono) {
		return mono.toFuture();
	}

	// TODO Adapt Java 8+ CompletableFuture to Mono
	Mono<User> fromCompletableFutureToMono(CompletableFuture<User> future) {
		return Mono.fromFuture(future);
	}

	Mono<List<User>> fluxCollection(Flux<User> flux) {
		return flux.collectList();
	}


	Mono<User> emptyToSkyler(Mono<User> mono) {
		return mono.switchIfEmpty(Mono.just(User.SKYLER));
	}

	Iterable<User> fluxToValues(Flux<User> flux) {
		return flux.toIterable();
	}

}
