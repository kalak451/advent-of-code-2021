import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.util.*


class Day15 {

    @Test
    fun part1() {
        assertEquals(40, runPart1("aoc-2021-15-test.txt"))
        assertEquals(398, runPart1("aoc-2021-15.txt"))
    }

    private fun runPart1(path: String): Int {
        val cave = File(ClassLoader.getSystemResource(path).file).readLines()
        return calcCostIter(cave)
    }

    private fun calcCostIter(cave: List<String>): Int {
        val start = Pair(0, 0)
        val end = Pair(cave[0].length - 1, cave.size - 1)

        val dist = mutableMapOf(Pair(start, 0))
        val queue = PriorityQueue<Pair<Pair<Int, Int>, Int>>(compareBy { it.second })
        queue.add(Pair(start, 0))

        while (!queue.isEmpty()) {
            val (c, _) = queue.poll()
                cave.adj(c).forEach { p ->
                    val newDist = cave.at(p) + dist[c]!!

                    if (newDist < (dist[p] ?: Int.MAX_VALUE)) {
                        dist[p] = newDist
                        queue.add(Pair(p, newDist))
                    }
                }
        }

        return dist[end]!!
    }

    private fun List<String>.at(c: Pair<Int, Int>): Int {
        return this[c.second][c.first].digitToInt()
    }

    private fun List<String>.adj(
        c: Pair<Int, Int>,
    ): Set<Pair<Int, Int>> {
        val (x, y) = c

        return sequenceOf(
            Pair(x - 1, y),
            Pair(x + 1, y),
            Pair(x, y - 1),
            Pair(x, y + 1)
        )
            .filter { it.first >= 0 }
            .filter { it.second >= 0 }
            .filter { it.first < this[0].length }
            .filter { it.second < this.size }
            .toSet()
    }

    @Test
    fun part2() {
        assertEquals(315, runPart2("aoc-2021-15-test.txt"))
        assertEquals(2817, runPart2("aoc-2021-15.txt"))
    }

    private fun runPart2(path: String): Int {
        val tile = File(ClassLoader.getSystemResource(path).file).readLines()
        val cave = generateTiledCave(tile, 5, 5)
        return calcCostIter(cave)
    }

    private fun generateTiledCave(tile: List<String>, columns: Int, rows: Int): List<String> {
        val tileRow = generateSequence(tile) { incrementTile(it) }
            .take(columns)
            .fold(List(tile.size) { "" }) { acc, t ->
                acc.zip(t).map { (a, b) -> a + b }
            }

        return generateSequence(tileRow) { incrementTile(it) }
            .take(rows)
            .flatten()
            .toList()
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



