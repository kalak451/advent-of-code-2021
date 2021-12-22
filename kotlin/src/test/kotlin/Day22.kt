import org.junit.jupiter.api.Test
import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.test.assertEquals

class Day22 {
    val regex = "(.*) x=(.*)\\.\\.(.*),y=(.*)\\.\\.(.*),z=(.*)\\.\\.(.*)".toRegex()

    @Test
    fun part1Sample() {
        val onEntries = runPart1("aoc-2021-22-sample.txt")
        assertEquals(39, onEntries)
    }

    @Test
    fun part1Test() {
        val onEntries = runPart1("aoc-2021-22-test.txt")
        assertEquals(590784, onEntries)
    }

    @Test
    fun part1() {
        val onEntries = runPart1("aoc-2021-22.txt")
        assertEquals(596598, onEntries)
    }

    private fun runPart1(path: String): Int {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()
        val cuboids = lines.mapNotNull { mapCuboidPart1(it) }

        val grid = (0..100).map { z ->
            (0..100).map { y ->
                (0..100).map { x ->
                    "off"
                }.toMutableList()
            }.toMutableList()
        }.toMutableList()

        cuboids.forEach { applyCuboidPart1(it, grid) }

        return grid.flatMap { it.flatten() }.count { it == "on" }
    }

    private fun applyCuboidPart1(cubeoid: CuboidPart1, grid: MutableList<MutableList<MutableList<String>>>) {
        cubeoid.zRange.forEach { z ->
            cubeoid.yRange.forEach { y ->
                cubeoid.xRange.forEach { x ->
                    grid[z][y][x] = cubeoid.status
                }
            }
        }
    }

    fun mapCuboidPart1(line: String): CuboidPart1? {
        val mr = regex.matchEntire(line) ?: return null

        val status = mr.groupValues[1]
        val xStart = mr.groupValues[2].toInt()
        val xEnd = mr.groupValues[3].toInt()
        val yStart = mr.groupValues[4].toInt()
        val yEnd = mr.groupValues[5].toInt()
        val zStart = mr.groupValues[6].toInt()
        val zEnd = mr.groupValues[7].toInt()

        if (xStart > 50 || xEnd < -50) {
            return null
        }

        if (yStart > 50 || yEnd < -50) {
            return null
        }

        if (zStart > 50 || zEnd < -50) {
            return null
        }

        return CuboidPart1(
            status,
            (max(xStart, -50) + 50..min(xEnd, 50) + 50),
            (max(yStart, -50) + 50..min(yEnd, 50) + 50),
            (max(zStart, -50) + 50..min(zEnd, 50) + 50)
        )
    }

    data class CuboidPart1(
        val status: String,
        val xRange: IntRange,
        val yRange: IntRange,
        val zRange: IntRange
    )

}