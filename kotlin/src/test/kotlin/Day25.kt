import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class Day25 {
    @Test
    fun part1Test1() {
        val b = parse("...>>>>>...")

        val b1 = b.tick(CucumberType.RIGHT)
        assertEquals(parse("...>>>>.>.."), b1)

        val b2 = b1.tick(CucumberType.RIGHT)
        assertEquals(parse("...>>>.>.>."), b2)
    }

    @Test
    fun part1Test2() {
        val b = parse(
            """
            .
            .
            .
            v
            v
            v
            v
            v
            .
            .
            .
        """.trimIndent()
        )

        val b1 = parse(
            """
            .
            .
            .
            v
            v
            v
            v
            .
            v
            .
            .
        """.trimIndent()
        )

        assertEquals(b1, b.tick(CucumberType.DOWN))

        val b2 = parse(
            """
            .
            .
            .
            v
            v
            v
            .
            v
            .
            v
            .
        """.trimIndent()
        )

        assertEquals(b2, b.tick(CucumberType.DOWN).tick(CucumberType.DOWN))
    }

    @Test
    fun part1Test3() {
        val b = parse(
            """
            ..........
            .>v....v..
            .......>..
            ..........
            """.trimIndent()
        )

        val expected = parse(
            """
                ..........
                .>........
                ..v....v>.
                ..........
                """.trimIndent()
        )

        assertEquals(
            expected,
            b.tick()
        )
    }

    @Test
    fun part1Test4() {
        val init = parse(
            """
            ...>...
            .......
            ......>
            v.....>
            ......>
            .......
            ..vvv..            
        """.trimIndent()
        )

        val ticks = init.ticks()

        assertEquals(
            parse(
                """
            ..vv>..
            .......
            >......
            v.....>
            >......
            .......
            ....v..
        """.trimIndent()
            ),
            ticks.take(2).last()
        )

        assertEquals(
            parse(
                """
            ....v>.
            ..vv...
            .>.....
            ......>
            v>.....
            .......
            .......
        """.trimIndent()
            ),
            ticks.take(3).last()
        )

        assertEquals(
            parse(
                """
            ......>
            ..v.v..
            ..>v...
            >......
            ..>....
            v......
            .......
        """.trimIndent()
            ),
            ticks.take(4).last()
        )

        assertEquals(
            parse(
                """
            >......
            ..v....
            ..>.v..
            .>.v...
            ...>...
            .......
            v......
        """.trimIndent()
            ),
            ticks.take(5).last()
        )
    }

    @Test
    fun part1Test5() {
        val b = parse(
            """
            v...>>.vv>
            .vv>>.vv..
            >>.>v>...v
            >>v>>.>.v.
            v>v.vv.v..
            >.>>..v...
            .vv..>.>v.
            v.v..>>v.v
            ....v..v.>
        """.trimIndent()
        )

        val last = b.ticks().take(59).last()

        assertEquals(
            parse(
                """
                ..>>v>vv..
                ..v.>>vv..
                ..>>v>>vv.
                ..>>>>>vv.
                v......>vv
                v>v....>>v
                vvv.....>>
                >vv......>
                .>v.vv.v..            
            """.trimIndent()
            ),
            last
        )
    }

    @Test
    fun part1Test6() {
        val b = parse(
            """
            v...>>.vv>
            .vv>>.vv..
            >>.>v>...v
            >>v>>.>.v.
            v>v.vv.v..
            >.>>..v...
            .vv..>.>v.
            v.v..>>v.v
            ....v..v.>
        """.trimIndent()
        )

        assertEquals(58, b.tickUntilStable())
    }

    @Test
    fun part1() {
        val lines = File(ClassLoader.getSystemResource("aoc-2021-25.txt").file).readLines()
        val b = parse(lines.joinToString("\n"))

        assertEquals(598, b.tickUntilStable())
    }

    private fun parse(input: String): Board {
        val map = input
            .split("\n")
            .map { it.trim().toList() }

        return Board(map)
    }

    enum class CucumberType(val symbol: Char) {
        RIGHT('>'),
        DOWN('v')
    }

    data class Board(val map: List<List<Char>>) {
        private val minX = 0
        private val minY = 0
        private val maxX = map[0].size - 1
        private val maxY = map.size - 1

        fun tickUntilStable(): Int {
            return ticks()
                .withIndex()
                .windowed(2, 1)
                .takeWhile { (a, b) -> a.value != b.value }
                .count() + 1
        }

        fun ticks(): Sequence<Board> {
            return generateSequence(this) { b -> b.tick() }
        }

        fun tick(): Board {
            return this
                .tick(CucumberType.RIGHT)
                .tick(CucumberType.DOWN)
        }

        fun tick(ct: CucumberType): Board {
            val newMap = map.mapIndexed { y, row ->
                row.mapIndexed { x, v ->
                    when (ct) {
                        CucumberType.RIGHT -> {
                            if (v == '.') {
                                val prevX = if (x == minX) maxX else x - 1
                                val sym = map[y][prevX]
                                if (sym == ct.symbol) ct.symbol else v
                            } else if (v == ct.symbol) {
                                val nextX = if (x == maxX) minX else x + 1
                                val sym = map[y][nextX]
                                if (sym == '.') '.' else v
                            } else {
                                v
                            }
                        }
                        CucumberType.DOWN -> {
                            if (v == '.') {
                                val prevY = if (y == minY) maxY else y - 1
                                val sym = map[prevY][x]
                                if (sym == ct.symbol) ct.symbol else v
                            } else if (v == ct.symbol) {
                                val nextY = if (y == maxY) minY else y + 1
                                val sym = map[nextY][x]
                                if (sym == '.') '.' else v
                            } else {
                                v
                            }
                        }
                    }

                }
            }

            return Board(newMap)
        }
    }


}