package net.kalak451.aoc.aoc2021;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AocDay5 {
    private static final Pattern COORD_PATTERN = Pattern.compile("^(.*),(.*) -> (.*),(.*)$");

    @Test
    public void part1() throws IOException {
        List<Line> inputTest = loadInput("/aoc-2021-5-test.txt");
        assertEquals(10, inputTest.size());
        var testResult = countOverlap(inputTest.stream()
                .filter(l -> l.isVertical() || l.isHorizontal())
                .flatMap(l -> l.rasterize().stream())
                .collect(Collectors.toList()));

        assertEquals(5, testResult);

        List<Line> input = loadInput("/aoc-2021-5.txt");
        assertEquals(500, input.size());
        var result = countOverlap(input.stream()
                .filter(l -> l.isVertical() || l.isHorizontal())
                .flatMap(l -> l.rasterize().stream())
                .collect(Collectors.toList()));

        assertEquals(8111, result);
    }

    @Test
    public void part2() throws IOException {
        List<Line> inputTest = loadInput("/aoc-2021-5-test.txt");
        var testResult = countOverlap(inputTest.stream()
                .flatMap(l -> l.rasterize().stream())
                .collect(Collectors.toList()));

        assertEquals(12, testResult);

        List<Line> input = loadInput("/aoc-2021-5.txt");
        var result = countOverlap(input.stream()
                .flatMap(l -> l.rasterize().stream())
                .collect(Collectors.toList()));

        assertEquals(22088, result);
    }

    public long countOverlap(List<Point> points) {
        return points.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        x -> 1L,
                        Long::sum
                ))
                .values()
                .stream()
                .filter(x -> x > 1)
                .count();
    }

    private List<Line> loadInput(String path) throws IOException {
        ClassPathResource cpr = new ClassPathResource(path);
        List<String> textLines = Files.readAllLines(Path.of(cpr.getURI()));
        return textLines.stream()
                .map(COORD_PATTERN::matcher)
                .filter(Matcher::matches)
                .map(m ->
                        new Line(
                                new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))),
                                new Point(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4))))
                )
                .toList();
    }

    private static class Point {
        public int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private static class Line {
        public Point start;
        public Point end;

        public Line(Point start, Point end) {
            this.start = start;
            this.end = end;
        }

        public boolean isHorizontal() {
            return start.y == end.y;
        }

        public boolean isVertical() {
            return start.x == end.x;
        }

        public List<Point> rasterize() {
            int steps;
            int xDistance = start.x - end.x;
            int yDistance = start.y - end.y;

            if (isHorizontal()) {
                steps = Math.abs(xDistance) + 1;
            } else {
                steps = Math.abs(yDistance) + 1;
            }

            int xStepSize = determineStepSize(xDistance);
            int yStepSize = determineStepSize(yDistance);

            return IntStream.range(0, steps)
                    .mapToObj(i -> new Point(
                            start.x + (i * xStepSize),
                            start.y + (i * yStepSize)
                    ))
                    .collect(Collectors.toList());

        }

        private int determineStepSize(int distance) {
            return Integer.compare(0, distance);
        }
    }
}
