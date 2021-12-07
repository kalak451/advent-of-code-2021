package net.kalak451.aoc.aoc2021;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AocDay4 {

    @Test
    public void part1() {
        Game gameTest = parseGame("/aoc-2021-4-test.txt").block();
        assertEquals(4512, gameTest.play().block());

        Game game = parseGame("/aoc-2021-4.txt").block();
        assertEquals(71708, game.play().block());
    }

    @Test
    public void part2() {
        Game gameTest = parseGame("/aoc-2021-4-test.txt").block();
        assertEquals(1924, gameTest.playToLose().block());

        Game game = parseGame("/aoc-2021-4.txt").block();
        assertEquals(34726, game.playToLose().block());
    }

    private Mono<Game> parseGame(String fileName) {
        List<String> lines = loadFile(fileName);

        var picks = Flux.fromArray(lines.get(0).split(",")).map(Integer::parseInt).collectList();
        var boards = Flux.fromIterable(lines)
                .skip(1)
                .buffer(6)
                .flatMap(ls -> Flux.fromIterable(ls)
                        .skip(1)
                        .flatMap(row -> Flux.fromArray(row.split(" "))
                                .filter(StringUtils::hasLength)
                                .map(n -> new Space(Integer.parseInt(n)))
                                .collectList()
                        )
                        .collectList()
                        .map(Board::new)
                )
                .collectList();

        return Mono.zip(
                picks,
                boards
        ).map(t -> new Game(t.getT1(), t.getT2()));
    }

    private List<String> loadFile(String fileName) {
        try {
            return Files.readAllLines(Path.of(new ClassPathResource(fileName).getURI()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static class Game {
        private final List<Integer> picks;
        private final List<Board> boards;

        public Game(List<Integer> picks, List<Board> boards) {
            this.picks = picks;
            this.boards = boards;
        }

        public Mono<Integer> play() {
            return Flux.fromIterable(picks)
                    .flatMap(pick -> Flux.fromIterable(boards)
                            .flatMap(board -> board.play(pick))
                    )
                    .next();
        }

        public Mono<Integer> playToLose() {
            return Flux.fromIterable(picks)
                    .takeUntil(x -> boards.stream().allMatch(b -> b.complete))
                    .flatMap(pick -> Flux.fromIterable(boards)
                            .flatMap(board -> board.play(pick))
                    )
                    .last();
        }
    }

    private static class Board {
        public boolean complete;
        public List<List<Space>> spaces;

        public Board(List<List<Space>> spaces) {
            this.spaces = spaces;
            this.complete = false;
        }

        public Mono<Integer> play(int pick) {
            return Mono.fromCallable(() -> {
                if (complete) {
                    return null;
                }

                boolean[] rowWins = {true, true, true, true, true};
                boolean[] colWins = {true, true, true, true, true};
                int score = 0;
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 5; j++) {
                        Space space = spaces.get(i).get(j);

                        if (!space.played) {
                            if (space.number == pick) {
                                space.played = true;
                            } else {
                                score += space.number;
                            }
                        }

                        colWins[j] = space.played && colWins[j];
                        rowWins[i] = space.played && rowWins[i];
                    }
                }

                for (int i = 0; i < 5; i++) {
                    if (rowWins[i] || colWins[i]) {
                        this.complete = true;
                        return score * pick;
                    }
                }
                return null;
            });
        }

    }

    private static class Space {
        public boolean played;
        public int number;

        public Space(int number) {
            this.played = false;
            this.number = number;
        }

        @Override
        public String toString() {
            return "Space{" +
                    "played=" + played +
                    ", number=" + number +
                    '}';
        }
    }
}
