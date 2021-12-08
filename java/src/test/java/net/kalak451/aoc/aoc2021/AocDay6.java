package net.kalak451.aoc.aoc2021;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AocDay6 {
    @Test
    public void part1() throws IOException {
        List<Integer> testInput = loadInput("/aoc-2021-6-test.txt");
        assertEquals(5, testInput.size());
        assertEquals(26, crunch(testInput, 18));
        assertEquals(5934, crunch(testInput, 80));

        List<Integer> input = loadInput("/aoc-2021-6.txt");
        assertEquals(300, input.size());
        assertEquals(385391, crunch(input, 80));
    }

    @Test
    public void part2() throws IOException {
        List<Integer> testInput = loadInput("/aoc-2021-6-test.txt");
        assertEquals(26, smartCrunch(testInput, 18));
        assertEquals(5934, smartCrunch(testInput, 80));
        assertEquals(26984457539L, smartCrunch(testInput, 256));

        List<Integer> input = loadInput("/aoc-2021-6.txt");
        assertEquals(385391, smartCrunch(input, 80));
        assertEquals(1728611055389L, smartCrunch(input, 256));
    }

    public long smartCrunch(List<Integer> input, int days) {
        long[] slots = new long[9];
        input.forEach(i -> slots[i]++);

        for (int i = 0; i < days; i++) {
            var newFish = slots[0];
            for (int j = 0; j < slots.length - 1; j++) {
                slots[j] = slots[j + 1];
            }
            slots[8] = newFish;
            slots[6] += newFish;
        }

        return LongStream.of(slots)
                .sum();
    }

    public long crunch(List<Integer> input, int days) {
        var working = input;
        for (int i = 0; i < days; i++) {
            working = processDay(working);
        }

        return working.size();
    }

    public List<Integer> processDay(List<Integer> input) {
        return input.stream()
                .flatMap(i -> {
                    if (i == 0) {
                        return Stream.of(6, 8);
                    } else {
                        return Stream.of(i - 1);
                    }
                })
                .collect(Collectors.toList());
    }

    private List<Integer> loadInput(String path) throws IOException {
        ClassPathResource cpr = new ClassPathResource(path);
        List<String> textLines = Files.readAllLines(Path.of(cpr.getURI()));
        return textLines.stream()
                .flatMap(l -> Stream.of(l.split(",")))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
