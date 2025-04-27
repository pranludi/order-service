package com.polarbookshop.orderservice.book;

import java.time.Duration;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
public class BookClient {

    static final String BOOK_ROOT_API = "/books/";
    final WebClient webClient;

    public BookClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Book> getBookByIsbn(String isbn) {
        return webClient
            .get()
            .uri(BOOK_ROOT_API + isbn)
            .retrieve()
            .bodyToMono(Book.class)
            // todo timeout 시간은 ClientProperties 으로 빼기
            .timeout(Duration.ofSeconds(3), Mono.empty()) // 3초의 타임아웃을 가지고, 3초 이상이되면 빈 모노 객체를 반환
            .onErrorResume(
                WebClientResponseException.NotFound.class,
                exception -> Mono.empty()
            )
            .retryWhen(
                // 지수 백오프를 재시도 전략으로 사용한다.
                // 100 밀리초의 초기 백오프로 총 3회까지 시도한다.
                Retry.backoff(3, Duration.ofMillis(8))
            )
            .onErrorResume(
                Exception.class,
                exception -> Mono.empty()
            );

    }
}
