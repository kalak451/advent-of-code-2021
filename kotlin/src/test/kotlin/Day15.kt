import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.util.*


class Day15 {

    @Test
    fun part1() {
        assertEquals(40, runPart1("aoc-2021-15-test.txt"))
//        assertEquals(398, runPart1("aoc-2021-15.txt"))
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
        val stack = Stack<Pair<Pair<Int, Int>, Int>>()
        stack.push(Pair(Pair(0, 0), 0))

        while (!stack.empty()) {
            val (c, currentCost) = stack.pop()

            if (c == Pair(xMax, yMax)) {
                if (!knownMinCosts.containsKey(c) || knownMinCosts[c]!! > currentCost) {
                    knownMinCosts[c] = currentCost
                }
            } else {

                val adj = buildAdj(c, xMax, yMax)
                adj.forEach { p ->
                    val nextVal = cave.at(p)
                    val nextCost = nextVal + currentCost

                    if (!knownMinCosts.containsKey(p) || knownMinCosts[p]!! > nextCost) {
                        knownMinCosts[p] = nextCost
                        stack.push(Pair(p, nextCost))
                    }
                }
            }
        }

        return knownMinCosts[dest]
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
//        assertEquals(-1, runPart2("aoc-2021-15.txt"))
    }

    private fun runPart2(path: String): Int {
        val tile = File(ClassLoader.getSystemResource(path).file).readLines()
        val row = mutableListOf(tile, tile, tile, tile, tile)
        (1 until 5).forEach {
            row[it] = incrementTile(row[it - 1])
        }
        val firstRow = row[0].indices.map { i -> row.joinToString("") { t -> t[i] } }

        val rows = mutableListOf(firstRow, firstRow, firstRow, firstRow, firstRow)
        (1 until 5).forEach {
            rows[it] = incrementTile(rows[it - 1])
        }
        val cave = rows.flatten()
        return calcCostIter(cave)!!
    }

    private fun incrementTile(tile: List<String>): List<String> {
        return tile.map { row ->
            row.map { incrementCell(it) }.joinToString("")
        }
    }

    private fun incrementCell(i: Char): Char {
        val o = i.digitToInt() + 1

        return if (o == 10) '1' else o.digitToChar()
    }
}



