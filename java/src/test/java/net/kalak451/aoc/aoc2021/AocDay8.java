package net.kalak451.aoc.aoc2021;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AocDay8 {

    public static final Set<Integer> UNIQUE_SIZES = Set.of(2, 4, 3, 7);

    @Test
    public void part1() throws IOException {
        var testData = loadData("/aoc-2021-8-test.txt");
        assertEquals(10, testData.size());
        assertTrue(testData.stream().allMatch(x -> x.getT1().size() == 10));
        assertTrue(testData.stream().allMatch(x -> x.getT2().size() == 4));
        var testResult = runPart1(testData);

        assertEquals(26, testResult);

        var data = loadData("/aoc-2021-8.txt");
        assertEquals(200, data.size());
        assertTrue(data.stream().allMatch(x -> x.getT1().size() == 10));
        assertTrue(data.stream().allMatch(x -> x.getT2().size() == 4));
        var result = runPart1(data);

        assertEquals(367, result);
    }

    private long runPart1(List<Tuple2<List<Set<Character>>, List<Set<Character>>>> data) {
        return data.stream()
                .map(Tuple2::getT2)
                .flatMap(Collection::stream)
                .filter(x -> UNIQUE_SIZES.contains(x.size()))
                .count();
    }

    @Test
    public void part2() throws IOException {
        var sampleData = loadData("/aoc-2021-8-sample.txt");
        var sampleResult = runPart2(sampleData);

        assertEquals(5353, sampleResult);

        var testData = loadData("/aoc-2021-8-test.txt");
        var testResult = runPart2(testData);

        assertEquals(61229, testResult);

        var data = loadData("/aoc-2021-8.txt");
        var result = runPart2(data);

        assertEquals(974512, result);
    }

    private long runPart2(List<Tuple2<List<Set<Character>>, List<Set<Character>>>> data) {
        return data.stream()
                .mapToLong(this::processLine)
                .sum();
    }

    private long processLine(Tuple2<List<Set<Character>>, List<Set<Character>>> input) {
        var key = buildKey(input.getT1());
        return decode(key, input.getT2());
    }

    private Set<Character>[] buildKey(List<Set<Character>> values) {
        Set<Character>[] output = (Set<Character>[]) new Set[10];
        output[1] = values.stream().filter(x -> x.size() == 2).findFirst().get();
        output[4] = values.stream().filter(x -> x.size() == 4).findFirst().get();
        output[7] = values.stream().filter(x -> x.size() == 3).findFirst().get();
        output[8] = values.stream().filter(x -> x.size() == 7).findFirst().get();

        var valuesWith6Segments = values.stream().filter(x -> x.size() == 6).toList();
        output[6] = valuesWith6Segments.stream().filter(x -> !x.containsAll(output[1])).findFirst().get();
        output[9] = valuesWith6Segments.stream().filter(x -> same(combine(x, output[4]), x)).findFirst().get();
        output[0] = valuesWith6Segments.stream().filter(x -> !same(x, output[6]) && !same(x, output[9])).findFirst().get();

        var valuesWith5Segments = values.stream().filter(x -> x.size() == 5).toList();
        output[3] = valuesWith5Segments.stream().filter(x -> same(combine(x, output[1]), x)).findFirst().get();
        output[5] = valuesWith5Segments.stream().filter(x -> same(combine(x, output[3]), output[9])).findFirst().get();
        output[2] = valuesWith5Segments.stream().filter(x -> !same(x, output[3]) && !same(x, output[5])).findFirst().get();
        return output;
    }

    private long decode(Set<Character>[] key, List<Set<Character>> digits) {
        return decode(key, digits.get(0)) * 1000L
                + decode(key, digits.get(1)) * 100L
                + decode(key, digits.get(2)) * 10L
                + decode(key, digits.get(3));
    }

    private int decode(Set<Character>[] key, Set<Character> digit) {
        for (int i = 0; i < key.length; i++) {
            var v = key[i];

            if (same(digit, v)) {
                return i;
            }
        }

        throw new RuntimeException("Invalid digit");
    }

    private Set<Character> combine(Set<Character> a, Set<Character> b) {
        var temp = new HashSet<>(a);
        temp.addAll(b);
        return temp;
    }

    private boolean same(Set<Character> a, Set<Character> b) {
        return b.size() == a.size() && b.containsAll(a);
    }

    private List<Tuple2<List<Set<Character>>, List<Set<Character>>>> loadData(String path) throws IOException {
        ClassPathResource cpr = new ClassPathResource(path);
        List<String> lines = Files.readAllLines(Path.of(cpr.getURI()));
        return lines.stream()
                .map(l -> splitBy(l, "\\|"))
                .map(ls -> Tuples.of(
                        splitBy(ls.get(0), " ").stream().map(x -> x.chars().mapToObj(i -> (char) i).collect(Collectors.toSet())).toList(),
                        splitBy(ls.get(1), " ").stream().map(x -> x.chars().mapToObj(i -> (char) i).collect(Collectors.toSet())).toList()
                ))
                .toList();

    }

    private List<String> splitBy(String input, String splitChar) {
        return Stream.of(input.split(splitChar))
                .map(String::trim)
                .toList();
    }
}
