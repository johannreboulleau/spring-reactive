# Spring Reactive test

* Spring 6
* Spring Reactor 3.7
* Spring Webflux 6

![](https://miro.medium.com/v2/resize:fit:592/1*OCnmZc6D1Oyr5cJUUDQgXg.png)

<!-- TOC -->
* [Spring Reactive test](#spring-reactive-test)
  * [Start](#start)
* [Reactive Streams (specifications)](#reactive-streams-specifications)
* [Back pressure](#back-pressure)
* [Reactor](#reactor)
  * [Create Flux or Mono](#create-flux-or-mono)
  * [Step verifier (test)](#step-verifier-test)
  * [Transform](#transform)
  * [Merge](#merge)
  * [Log and doOn request](#log-and-doon-request)
  * [Error](#error-)
  * [Adapt (RxJava3, Java 8)](#adapt-rxjava3-java-8)
  * [Other Operations](#other-operations)
  * [Reactive to Blocking (DANGER)](#reactive-to-blocking-danger)
  * [Blocking to Reactive](#blocking-to-reactive)
<!-- TOC -->

## Start

```shell
mvn spring-boot:run
```

# Reactive Streams (specifications)

The Reactive Streams specification is an industry-driven effort to standardize Reactive Programming libraries on the
JVM, and more importantly specify how they must behave so that they are interoperable. Implementors include Reactor 3
but also RxJava from version 2 and above, Akka Streams, Vert.x and Ratpack.

# Back pressure

Backpressure in software systems is the capability to overload the traffic communication. In other words, emitters of 
information overwhelm consumers with data they are not able to process.

In other words:

**In Reactive Streams, backpressure defines how to regulate the transmission of stream elements.**

WebFlux uses TCP flow control to regulate the backpressure in bytes. But it does not handle the logical elements the 
consumer can receive.

Good documentation: https://www.baeldung.com/spring-webflux-backpressure

# Reactor

These examples come from an official Reactor training:
* https://projectreactor.io/learn
  * https://tech.io/playgrounds/929/reactive-programming-with-reactor-3/Intro

## Create Flux or Mono

Code examples: 
* [Part01Flux.java](src/main/java/com/example/spring_reactive/createExamples/Part01Flux.java)
* [Part02Mono.java](src/main/java/com/example/spring_reactive/createExamples/Part02Mono.java)

```java
static <T> Flux<T> empty();
// Create a Flux that completes without emitting any item.

static <T> Mono<T> empty();
// Create a Mono that completes without emitting any item.
```

```java
static <T> Flux<T> fromIterable(Iterable<? extends T> it);
// Create a Flux that emits the items contained in the provided Iterable.
```

```java
static <T> Flux<T> error(Throwable error);
// Create a Flux that completes with the specified error.
```

```java
static Flux<Long> interval(Duration period);
// Create a new Flux that emits an ever incrementing long starting with 0 every period on the global timer.
```

```java
Mono.firstWithValue(
        Mono.just(1).map(integer -> "foo" + integer),
        Mono.delay(Duration.ofMillis(100)).thenReturn("bar")
    )
    .subscribe(System.out::println);
```

```java
// Return a Mono that never emits any signal
Mono<String> monoWithNoSignal() {
    return Mono.never();
}
```

## Step verifier (test)

Code examples:
* [Part03StepVerifier.java](src/test/java/com/example/spring_reactive/stepVerifier/Part03StepVerifier.java)

Note that **you must always call the verify()** method or one of the shortcuts that combine the terminal expectation and
verify, like .verifyErrorMessage(String). Otherwise the StepVerifier won't subscribe to your sequence and nothing will be asserted.

```java
StepVerifier.create(T<Publisher>).{expectations...}.verify();
```

Fortunately, StepVerifier comes with a **virtual time** option: by using **StepVerifier.withVirtualTime(Supplier<Publisher>)**, 
the verifier will temporarily replace default core Schedulers (the component that define the execution context in Reactor). 
All these default Scheduler are replaced by a single instance of a VirtualTimeScheduler, which has a virtual clock that 
can be manipulated.

## Transform

Code examples:
* [Part04Transform.java](src/main/java/com/example/spring_reactive/transform/Part04Transform.java)

* `map()`
* `flatMap()`

## Merge

Code examples:
* [Part05Merge.java](src/main/java/com/example/spring_reactive/merge/Part05Merge.java)

* `concat()`
* `merge()`

## Log and doOn request

* `Flux.log()`
* `doFirst`
* `doOnNext`
* `doFinally`

## Back pressure

Natively support by Reactor, but we can add more controls.

* `request(n)`
* `onBackpressureBuffer()`
  * bufferize the events (risk to increase the memory)
* `onBackpressureDrop()`
  * give up (abandon) the additional events
* `onBackpressureLatest`
  * keep only last elements

No need to have back pressure on Mono.

## Error 

Code examples:
* [Part07Errors.java](src/main/java/com/example/spring_reactive/error/Part07Errors.java)

* `Mono#onErrorReturn`
* `Flux#onErrorResume`
* `Exceptions#propagate` in custom lambda

## Adapt (RxJava3, Java 8)

Code examples:
* [Part09Adapt.java](src/main/java/com/example/spring_reactive/adapt/Part09Adapt.java)
You can make RxJava3 and Reactor 3 types interact without a single external library.

* `Flowable.fromPublisher(flux);`
* `Flux.from(flowable)`
* `Observable.fromPublisher(flux)`
* `Flux.from(observable.toFlowable(BackpressureStrategy.BUFFER))`

From Reactor To Java 8:
* `mono.toFuture()`
* `Mono.fromFuture(future)`

## Other Operations

Code examples:
* [Part08OtherOperations.java](src/main/java/com/example/spring_reactive/otheroperations/Part08OtherOperations.java)

* `zip()`
* `firstWithValue()`
* `ignoreElements()`
* `then()` return `Mono<Void>`
* `justOrEmpty()`
* `switchIfEmpty()`
* `collectList()`

## Reactive to Blocking (DANGER)

Note that you should avoid this by favoring having reactive code end-to-end, as much as possible. You **MUST avoid this at 
all cost in the middle of other reactive code**, as this has the potential to lock your whole reactive pipeline.

Code examples:
* [Part10ReactiveToBlocking.java](src/main/java/com/example/spring_reactive/reactivetoblocking/Part10ReactiveToBlocking.java)

* `mono.block()`
* `flux.toIterable()`

## Blocking to Reactive

The big question is "How to deal with legacy, non-reactive code?".

Say you have blocking code (eg. a JDBC connection to a database), and you want to integrate that into your reactive 
pipelines while avoiding too much of an impact on performance.

The best course of action is to isolate such intrinsically blocking parts of your code into their own execution context 
via a Scheduler, keeping the efficiency of the rest of the pipeline high and only creating extra threads when absolutely 
needed.

Code examples:
* [Part11BlockingToReactive.java](src/main/java/com/example/spring_reactive/blockingtoreactivity/Part11BlockingToReactive.java)

* `defer()`
* `Schedulers.boundedElastic()`
* `subscribeOn()`
* `publishOn()`
