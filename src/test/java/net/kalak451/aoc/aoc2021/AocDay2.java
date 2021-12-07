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
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AocDay2 {

    @Test
    public void part1() throws IOException {
        var position = loadFile()
                .map(this::parseLine)
                .reduce(new Position(), this::applyMovementP1)
                .block();

        assertNotNull(position);

        System.out.println(position);
        System.out.println(position.x * position.y);
    }

    @Test
    public void part2() throws IOException {
        var position = loadFile()
                .map(this::parseLine)
                .reduce(new Position(), this::applyMovementP2)
                .block();

        assertNotNull(position);

        System.out.println(position);
        System.out.println(position.x * position.y);
    }

    private Position applyMovementP1(Position position, Tuple2<String, Integer> movement) {
        return switch (movement.getT1()) {
            case "forward" -> new Position(position.x + movement.getT2(), position.y);
            case "down" -> new Position(position.x, position.y + movement.getT2());
            case "up" -> new Position(position.x, position.y - movement.getT2());
            default -> throw new RuntimeException("Unexpected direction: " + movement.getT1());
        };
    }

    private Position applyMovementP2(Position position, Tuple2<String, Integer> movement) {
        return switch (movement.getT1()) {
            case "forward" -> new Position(position.x + movement.getT2(), position.y + (position.aim * movement.getT2()), position.aim);
            case "down" -> new Position(position.x, position.y, position.aim + movement.getT2());
            case "up" -> new Position(position.x, position.y, position.aim - movement.getT2());
            default -> throw new RuntimeException("Unexpected direction: " + movement.getT1());
        };
    }

    private Tuple2<String, Integer> parseLine(String s) {
        String[] split = s.split(" ");
        return Tuples.of(
                split[0],
                Integer.parseInt(split[1])
        );
    }

    private Flux<String> loadFile() {
        return Mono.fromCallable(() -> {
                    ClassPathResource cpr = new ClassPathResource("/aoc-2021-2.txt");
                    return Files.readAllLines(Path.of(cpr.getURI()));
                }).subscribeOn(Schedulers.boundedElastic())
                .flatMapIterable(Function.identity());
    }

    private class Position {
        public int x = 0;
        public int y = 0;
        public int aim = 0;

        public Position() {
        }

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Position(int x, int y, int aim) {
            this.x = x;
            this.y = y;
            this.aim = aim;
        }

        @Override
        public String toString() {
            return "Position{" +
                    "x=" + x +
                    ", y=" + y +
                    ", aim=" + aim +
                    '}';
        }
    }
}
