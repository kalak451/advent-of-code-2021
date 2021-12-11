import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File


class Day11Immutable {

    @Test
    fun part1() {
        assertEquals(204, runPart1("aoc-2021-11-test.txt", 10))
        assertEquals(1656, runPart1("aoc-2021-11-test.txt", 100))
        assertEquals(1673, runPart1("aoc-2021-11.txt", 100))
    }

    private fun runPart1(path: String, steps: Int): Int {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()
        val world = lines.map { row -> row.map { it.digitToInt() } }
        val contexts = generateSequence(world) { runImmutableStep(it) }
        return contexts
            .take(steps + 1)
            .map { it.countFlashes() }
            .sum()
    }

    @Test
    fun part2() {
        assertEquals(195, runPart2("aoc-2021-11-test.txt"))
        assertEquals(279, runPart2("aoc-2021-11.txt"))
    }

    private fun runPart2(path: String): Int {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()
        val world = lines.map { row -> row.map { it.digitToInt() } }
        return generateSequence(world) { runImmutableStep(it) }
            .mapIndexed { idx, w -> Pair(idx, w) }
            .find { it.second.countFlashes() == 100 }!!
            .first
    }

    private fun runImmutableStep(world: List<List<Int>>): List<List<Int>> {
        val resetWorld = world.map { row -> row.map { if (it >= 10) 0 else it } }

        return generateSequence(
            FlashContext(
                resetWorld.allCoords(),
                resetWorld
            )
        ) { (work, world) ->
            if (work.isEmpty()) {
                null
            } else {
                work.fold(FlashContext(listOf(), world)) { acc, c ->
                    val fc = processWork(c, acc.newWorld)
                    FlashContext(acc.work + fc.work, fc.newWorld)
                }
            }
        }.last().newWorld
    }

    private fun processWork(work: Coord, world: List<List<Int>>): FlashContext {
        val newEnergy = world[work.y][work.x] + 1

        if (newEnergy > 10) {
            return FlashContext(
                listOf(),
                world
            )
        }


        val newWorld = world.update(work, newEnergy)

        if (newEnergy <= 9) {
            return FlashContext(
                listOf(),
                newWorld
            )
        }

        return FlashContext(
            work.adj().filter { newWorld.has(it) },
            newWorld
        )
    }

    private fun List<List<Int>>.countFlashes() : Int {
        return this.flatten().count { it >= 10 }
    }

    private fun List<List<Int>>.update(c: Coord, v: Int): List<List<Int>> {
        return this.mapIndexed { i, row ->
            if (i == c.y) {
                row.mapIndexed { j, p -> if (j == c.x) v else p }
            } else {
                row
            }
        }
    }

    private fun List<List<Int>>.has(c: Coord): Boolean {
        return c.x >= 0
                && c.y >= 0
                && c.x < this[0].size
                && c.y < this.size
    }

    private fun List<List<Int>>.allCoords(): List<Coord> {
        return this.indices.flatMap { i -> this[i].indices.map { j -> Coord(j, i) } }
    }

    data class Coord(
        val x: Int,
        val y: Int
    ) {
        fun adj(): Set<Coord> {
            return setOf(
                Coord(this.x - 1, this.y - 1), Coord(this.x, this.y - 1), Coord(this.x + 1, this.y - 1),
                Coord(this.x - 1, this.y), Coord(this.x + 1, this.y),
                Coord(this.x - 1, this.y + 1), Coord(this.x, this.y + 1), Coord(this.x + 1, this.y + 1),
            )
        }
    }

    data class FlashContext(
        val work: List<Coord>,
        val newWorld: List<List<Int>>
    )
}