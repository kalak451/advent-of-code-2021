package net.kalak451.aoc.aoc2021;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AocDay7 {

    @Test
    public void part1() throws IOException {
        List<Long> testPositions = loadInput("/aoc-2021-7-test.txt");
        Tuple2<Long, Long> testMin = run(testPositions, this::calculateCostPart1);
        assertEquals(37, testMin.getT2());

        List<Long> positions = loadInput("/aoc-2021-7.txt");
        Tuple2<Long, Long> min = run(positions, this::calculateCostPart1);
        assertEquals(359648, min.getT2());
    }

    @Test
    public void part2() throws IOException {
        List<Long> testPositions = loadInput("/aoc-2021-7-test.txt");
        Tuple2<Long, Long> testMin = run(testPositions, this::calculateCostPart2);
        assertEquals(5, testMin.getT1());
        assertEquals(168, testMin.getT2());

        List<Long> positions = loadInput("/aoc-2021-7.txt");
        Tuple2<Long, Long> min = run(positions, this::calculateCostPart2);
        assertEquals(100727924, min.getT2());
    }

    private Tuple2<Long, Long> run(List<Long> positions, BiFunction<List<Long>, Long, Long> costFunction) {
        Long minPosition = positions.stream().min(Long::compare).get();
        Long maxPosition = positions.stream().max(Long::compare).get();
        return LongStream.rangeClosed(minPosition, maxPosition)
                .mapToObj(desired -> Tuples.of(
                        desired,
                        costFunction.apply(positions, desired)
                ))
                .min(Comparator.comparing(Tuple2::getT2))
                .get();
    }

    private long calculateCostPart1(List<Long> positions, long desired) {
        return positions.stream()
                .map(p -> Math.abs(p - desired))
                .reduce(Long::sum)
                .get();
    }

    private long calculateCostPart2(List<Long> positions, long desired) {
        return positions.stream()
                .map(p -> Math.abs(p - desired))
                .map(d -> d * (1 + d) / 2)
                .reduce(Long::sum)
                .get();
    }

    private List<Long> loadInput(String path) throws IOException {
        ClassPathResource cpr = new ClassPathResource(path);
        List<String> textLines = Files.readAllLines(Path.of(cpr.getURI()));
        return textLines.stream()
                .flatMap(l -> Stream.of(l.split(",")))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}
