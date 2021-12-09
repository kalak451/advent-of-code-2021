import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

typealias Coord = Pair<Int, Int>

class Day9 {

    @Test
    fun part1() {
        assertEquals(15, runPart1("aoc-2021-9-test.txt"))
        assertEquals(591, runPart1("aoc-2021-9.txt"))
    }

    private fun runPart1(path: String): Int {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()

        val adjacencyList = buildAdjacencyList(lines)

        val lowSum = adjacencyList.asSequence()
            .filter { isLowPoint(it, lines) }
            .map { (c, _) -> c }
            .map { (x, y) -> lines[y][x].digitToInt() }
            .map { it + 1 }
            .sum()
        return lowSum
    }

    @Test
    fun part2() {
        assertEquals(1134, runPart2("aoc-2021-9-test.txt"))
        assertEquals(1113424, runPart2("aoc-2021-9.txt"))
    }

    private fun runPart2(path: String): Int {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()
        val adjacencyMap = buildAdjacencyList(lines).toMap()

        val alreadyVisited = mutableSetOf<Coord>()

        return adjacencyMap.keys
            .asSequence()
            .map { c -> visitPt(c, lines, alreadyVisited, adjacencyMap) }
            .filter { it.isNotEmpty() }
            .map { it.size }
            .sortedDescending()
            .take(3)
            .fold(1) { acc, i -> acc * i }
    }

    private fun visitPt(
        c: Coord,
        lines: List<String>,
        alreadyVisited: MutableSet<Coord>,
        adjacencyMap: Map<Coord, Set<Coord>>
    ): Set<Coord> {
        if (alreadyVisited.contains(c)) {
            return setOf()
        }
        alreadyVisited.add(c)
        val (x, y) = c

        if (lines[y][x].digitToInt() == 9) {
            return setOf()
        }

        val adjacent = adjacencyMap[c]!!
            .map { visitPt(it, lines, alreadyVisited, adjacencyMap) }
            .fold<Set<Coord>, Set<Coord>>(setOf()) { acc, s -> acc + s }

        return setOf(c) + adjacent
    }


    private fun isLowPoint(location: Pair<Coord, Set<Coord>>, lines: List<String>): Boolean {
        val (pt, al) = location
        val (ptx, pty) = pt

        val currentValue = lines[pty][ptx].digitToInt()

        return al.map { (alx, aly) -> lines[aly][alx].digitToInt() }
            .all { currentValue < it }
    }

    private fun buildAdjacencyList(lines: List<String>): List<Pair<Coord, Set<Coord>>> {
        val rowIndices = lines.indices
        val adjacencyList = rowIndices.flatMap { y ->
            val columnIndices = lines[y].indices
            columnIndices.map { x ->
                val adjacent = mutableSetOf<Coord>()

                if (x != columnIndices.first) {
                    adjacent.add(Coord(x - 1, y))
                }

                if (x != columnIndices.last) {
                    adjacent.add(Coord(x + 1, y))
                }

                if (y != rowIndices.first) {
                    adjacent.add(Coord(x, y - 1))
                }

                if (y != rowIndices.last) {
                    adjacent.add(Coord(x, y + 1))
                }

                Pair<Coord, Set<Coord>>(
                    Pair(x, y),
                    adjacent
                )
            }
        }
        return adjacencyList
    }
}