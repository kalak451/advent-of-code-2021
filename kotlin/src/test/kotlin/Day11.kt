import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File


class Day11 {

    @Test
    fun part1() {
        assertEquals(204, runPart1("aoc-2021-11-test.txt", 10))
        assertEquals(1656, runPart1("aoc-2021-11-test.txt", 100))
        assertEquals(1673, runPart1("aoc-2021-11.txt", 100))
    }

    private fun runPart1(path: String, steps: Int): Long {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()
        val nodeMap = buildNodeMap(lines)

        var flashes = 0L
        for (i in 1..steps) {
            val alreadyVisited = mutableSetOf<Coord>()
            flashes += nodeMap.values.sumOf { visitPt(it, alreadyVisited, nodeMap) }

            nodeMap.values.forEach {
                if (it.energy > 9) {
                    it.energy = 0
                }
            }
        }

        return flashes
    }

    @Test
    fun part2() {
        assertEquals(195, runPart2("aoc-2021-11-test.txt"))
        assertEquals(279, runPart2("aoc-2021-11.txt"))
    }

    private fun runPart2(path: String): Long {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()
        val nodeMap = buildNodeMap(lines)

        var step = 0L
        for (i in 1..999999999) {
            step++
            val alreadyVisited = mutableSetOf<Coord>()
            val stepFlashes = nodeMap.values.sumOf { visitPt(it, alreadyVisited, nodeMap) }

            if (stepFlashes == 100L) {
                return step
            }

            nodeMap.values.forEach {
                if (it.energy > 9) {
                    it.energy = 0
                }
            }
        }

        return -1
    }

    private fun visitPt(
        n: Node,
        alreadyVisited: MutableSet<Coord>,
        nodeMap: Map<Coord, Node>
    ): Long {
        if (alreadyVisited.contains(n.pt)) {
            return 0
        }


        n.energy++

        if (n.energy <= 9) {
            return 0
        }

        alreadyVisited.add(n.pt)
        val adjacentFlashes = n.adj.sumOf { visitPt(nodeMap[it]!!, alreadyVisited, nodeMap) }

        return adjacentFlashes + 1
    }

    private fun buildNodeMap(lines: List<String>): Map<Coord, Node> {
        val rowIndices = lines.indices
        return rowIndices.flatMap { y ->
            val columnIndices = lines[y].indices
            columnIndices.map { x ->
                val adjacent = mutableSetOf<Coord>()

                if (x != columnIndices.first) {
                    adjacent.add(Coord(x - 1, y))

                    if (y != rowIndices.first) {
                        adjacent.add(Coord(x - 1, y - 1))
                    }

                    if (y != rowIndices.last) {
                        adjacent.add(Coord(x - 1, y + 1))
                    }
                }

                if (x != columnIndices.last) {
                    adjacent.add(Coord(x + 1, y))

                    if (y != rowIndices.first) {
                        adjacent.add(Coord(x + 1, y - 1))
                    }

                    if (y != rowIndices.last) {
                        adjacent.add(Coord(x + 1, y + 1))
                    }
                }

                if (y != rowIndices.first) {
                    adjacent.add(Coord(x, y - 1))
                }

                if (y != rowIndices.last) {
                    adjacent.add(Coord(x, y + 1))
                }

                Node(
                    lines[y][x].digitToInt(),
                    Coord(x, y),
                    adjacent
                )
            }
        }.associateBy { it.pt }
    }

    data class Node(
        var energy: Int,
        val pt: Coord,
        val adj: Set<Coord>
    )

    data class Coord(
        val x: Int,
        val y: Int
    )
}