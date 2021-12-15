import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.lang.RuntimeException
import java.util.*


class Day15 {

    @Test
    fun part1() {
        assertEquals(40, runPart1("aoc-2021-15-test.txt"))
        assertEquals(398, runPart1("aoc-2021-15.txt"))
    }

    private fun runPart1(path: String): Int {
        val cave = File(ClassLoader.getSystemResource(path).file).readLines()
        return calcCostIter(cave)!!
    }

    private fun calcCostIter(cave: List<String>): Int? {
        val xMax = cave[0].length - 1
        val yMax = cave.size - 1
        val dest = Pair(xMax, yMax)

        val knownMinCosts = mutableMapOf(Pair(Pair(0, 0), 0))
        val stack = PriorityQueue<Pair<Pair<Int, Int>, Int>>(compareBy { it.second })
        stack.add(Pair(Pair(0, 0), 0))

        while (!stack.isEmpty()) {
            val (c, _) = stack.poll()

            if (c == dest) {
                return knownMinCosts[c]
            } else {

                val adj = buildAdj(c, xMax, yMax)
                adj.forEach { p ->
                    val nextVal = cave.at(p)
                    val nextCost = nextVal + knownMinCosts[c]!!

                    if (nextCost < knownMinCosts.getOrDefault(p, Int.MAX_VALUE)) {
                        knownMinCosts[p] = nextCost
                        stack.add(Pair(p, nextCost))
                    }
                }
            }
        }
        throw RuntimeException("test")
//        return knownMinCosts[dest]
    }

    private fun List<String>.at(c: Pair<Int, Int>): Int {
        return this[c.second][c.first].digitToInt()
    }

    private fun buildAdj(
        c: Pair<Int, Int>,
        xMax: Int,
        yMax: Int
    ): Set<Coord> {
        val (x, y) = c

        val adjacent = mutableSetOf<Coord>()

        if (x != 0) {
            adjacent.add(Coord(x - 1, y))
        }

        if (x != xMax) {
            adjacent.add(Coord(x + 1, y))
        }

        if (y != 0) {
            adjacent.add(Coord(x, y - 1))
        }

        if (y != yMax) {
            adjacent.add(Coord(x, y + 1))
        }

        return adjacent.toSet()
    }

    @Test
    fun part2() {
        assertEquals(315, runPart2("aoc-2021-15-test.txt"))
        assertEquals(2817, runPart2("aoc-2021-15.txt"))
    }

    private fun runPart2(path: String): Int {
        val tile = File(ClassLoader.getSystemResource(path).file).readLines()

        val tileRow = generateSequence(tile){incrementTile(it)}
            .take(5)
            .fold(List(tile.size){""}){ acc, t ->
                acc.zip(t).map { (a,b) -> a + b }
            }

        val cave = generateSequence(tileRow) { incrementTile(it) }
            .take(5)
            .flatten()
            .toList()

        return calcCostIter(cave)!!
    }

    private fun incrementTile(tile: List<String>): List<String> {
        return tile.map { row ->
            row.map { incrementCell(it) }.joinToString("")
        }
    }

    private fun incrementCell(i: Char): Char {
        val o = i.digitToInt() + 1

        return if (o >= 10) {
            (o - 9).digitToChar()
        } else {
            o.digitToChar()
        }
    }
}



