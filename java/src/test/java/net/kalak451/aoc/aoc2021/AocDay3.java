package net.kalak451.aoc.aoc2021;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AocDay3 {

    @Test
    public void part1() throws IOException {
        var testResult = runPart1("/aoc-2021-3-test.txt", 5).block();
        assertEquals(198, testResult.getT1() * testResult.getT2());

        var result = runPart1("/aoc-2021-3.txt", 12).block();
        assertEquals(3882564, result.getT1() * result.getT2());
    }

    private Mono<Tuple2<Integer, Integer>> runPart1(String path, int size) {
        return loadFile(path)
                .collectList()
                .flatMap(lines -> Flux.range(0, size)
                        .flatMap(idx -> common(lines, idx))
                        .collectList()
                )
                .flatMap(gammaChars -> Mono.zip(
                                Flux.fromIterable(gammaChars).as(join()).transform(parse()),
                                Flux.fromIterable(gammaChars).map(this::flip).as(join()).transform(parse())
                        )
                );
    }

    @Test
    public void part2() {
        var testResult = runPart2("/aoc-2021-3-test.txt", 5).block();
        assertEquals(230, testResult.getT1() * testResult.getT2());

        var result = runPart2("/aoc-2021-3.txt", 12).block();
        assertEquals(3385170, result.getT1() * result.getT2());
    }

    private Mono<Tuple2<Integer, Integer>> runPart2(String path, int size) {
        return loadFile(path)
                .collectList()
                .map(l -> Tuples.of(0, l, l))
                .expand(t -> Mono.zip(
                                        Mono.just(t.getT1() + 1),
                                        common(t.getT2(), t.getT1()).transform(filterMatches(t.getT1(), t.getT2())),
                                        common(t.getT3(), t.getT1()).map(this::flip).transform(filterMatches(t.getT1(), t.getT3()))
                                )
                                .filter(tt -> tt.getT1() <= size)
                )
                .last()
                .map(t -> Tuples.of(t.getT2().get(0), t.getT3().get(0)))
                .map(t -> t
                        .mapT1(v -> Integer.parseInt(v, 2))
                        .mapT2(v -> Integer.parseInt(v, 2))
                );

    }

    private Function<Mono<Character>, Mono<List<String>>> filterMatches(int idx, List<String> t2) {
        if (t2.size() == 1) {
            return m -> Mono.just(t2);
        }

        return mc -> mc.flatMap(c -> Flux.fromIterable(t2)
                .filter(l -> l.charAt(idx) == c)
                .collectList()
        );
    }


    private Mono<Character> common(List<String> input, int idx) {
        return Flux.fromIterable(input)
                .map(s -> s.charAt(idx))
                .filter(c -> c == '1')
                .count()
                .map(ones -> ones >= input.size() / 2.0 ? '1' : '0');
    }

    private char flip(char c) {
        return c == '1' ? '0' : '1';
    }

    private Function<Flux<Character>, Mono<String>> join() {
        return f -> f.reduce("", (acc, c) -> acc + c);
    }

    private Function<Mono<String>, Mono<Integer>> parse() {
        return m -> m.map(s -> Integer.parseInt(s, 2));
    }

    private Flux<String> loadFile(String path) {
        return Mono.fromCallable(() -> {
                    ClassPathResource cpr = new ClassPathResource(path);
                    return Files.readAllLines(Path.of(cpr.getURI()));
                }).subscribeOn(Schedulers.boundedElastic())
                .flatMapIterable(Function.identity());
    }
}
