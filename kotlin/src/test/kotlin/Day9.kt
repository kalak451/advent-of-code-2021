import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

typealias Coord = Pair<Int, Int>
typealias Node = Triple<Int, Coord, Set<Coord>>

class Day9 {

    @Test
    fun part1() {
        assertEquals(15, runPart1("aoc-2021-9-test.txt"))
        assertEquals(591, runPart1("aoc-2021-9.txt"))
    }

    private fun runPart1(path: String): Int {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()

        val nodeMap = buildNodeMap(lines)

        return nodeMap.values
            .filter { isLowPoint(it, nodeMap) }
            .map { (c) -> c }
            .sumOf { it + 1 }
    }

    @Test
    fun part2() {
        assertEquals(1134, runPart2("aoc-2021-9-test.txt"))
        assertEquals(1113424, runPart2("aoc-2021-9.txt"))
    }

    private fun runPart2(path: String): Int {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()
        val adjacencyMap = buildNodeMap(lines)

        val alreadyVisited = mutableSetOf<Coord>()

        return adjacencyMap.values
            .asSequence()
            .map { visitPt(it, alreadyVisited, adjacencyMap) }
            .filter { it.isNotEmpty() }
            .map { it.size }
            .sortedDescending()
            .take(3)
            .fold(1) { acc, i -> acc * i }
    }

    private fun visitPt(
        n: Node,
        alreadyVisited: MutableSet<Coord>,
        nodeMap: Map<Coord, Node>
    ): Set<Coord> {
        val (v, c, adj) = n

        if (alreadyVisited.contains(c)) {
            return setOf()
        }
        alreadyVisited.add(c)
        if (v == 9) {
            return setOf()
        }

        val adjacent = adj
            .map { visitPt(nodeMap[it]!!, alreadyVisited, nodeMap) }
            .fold(setOf<Coord>()) { acc, s -> acc + s }

        return setOf(c) + adjacent
    }


    private fun isLowPoint(node: Node, nodes: Map<Coord, Node>): Boolean {
        val (v, _, al) = node

        return al.map { nodes[it]!! }
            .all { (adjVal) -> v < adjVal }
    }

    private fun buildNodeMap(lines: List<String>): Map<Coord, Node> {
        val rowIndices = lines.indices
        return rowIndices.flatMap { y ->
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

                Triple(
                    lines[y][x].digitToInt(),
                    Pair(x, y),
                    adjacent
                )
            }
        }.associateBy { it.second }
    }
}