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

    @Test
    fun part2Test() {
        val onEntries = runPart2("aoc-2021-22-test2.txt")
        assertEquals(2758514936282235L, onEntries)
    }

    @Test
    fun part2() {
        val onEntries = runPart2("aoc-2021-22.txt")
        assertEquals(1199121349148621L, onEntries)
    }

    private fun runPart2(path: String): Long {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()
        val cuboids = lines.map { mapCuboidPart2(it) }

        return cuboids.fold<CuboidPart2, List<CuboidPart2>>(listOf()) { acc, cube ->
            val generatedCubes = acc.mapNotNull { cube.intersection(it) }
            acc + generatedCubes + if (cube.status == "on") listOf(cube) else listOf()
        }.sumOf { it.size() }
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

    fun mapCuboidPart2(line: String): CuboidPart2 {
        val mr = regex.matchEntire(line)!!

        val status = mr.groupValues[1]
        val xStart = mr.groupValues[2].toInt()
        val xEnd = mr.groupValues[3].toInt()
        val yStart = mr.groupValues[4].toInt()
        val yEnd = mr.groupValues[5].toInt()
        val zStart = mr.groupValues[6].toInt()
        val zEnd = mr.groupValues[7].toInt()

        return CuboidPart2(
            status,
            xStart,
            xEnd,
            yStart,
            yEnd,
            zStart,
            zEnd,
        )
    }

    @Test
    fun shouldSize() {
        val a = CuboidPart2(
            "on",
            0, 0,
            0, 0,
            0, 0
        )

        assertEquals(1, a.size())

        val b = CuboidPart2(
            "off",
            0, 0,
            0, 0,
            0, 0
        )

        assertEquals(-1, b.size())
    }

    data class CuboidPart2(
        val status: String,
        val xStart: Int,
        val xEnd: Int,
        val yStart: Int,
        val yEnd: Int,
        val zStart: Int,
        val zEnd: Int,
    ) {
        fun size(): Long {
            return (xEnd - xStart + 1).toLong() *
                    (yEnd - yStart + 1).toLong() *
                    (zEnd - zStart + 1).toLong() *
                    if (status == "on") 1L else -1L
        }

        fun intersection(other: CuboidPart2): CuboidPart2? {
            if (
                xStart > other.xEnd
                || xEnd < other.xStart
                || yStart > other.yEnd
                || yEnd < other.yStart
                || zStart > other.zEnd
                || zEnd < other.zStart
            ) {
                return null
            }

            val overlapXStart = max(xStart, other.xStart)
            val overlapXEnd = min(xEnd, other.xEnd)

            val overlapYStart = max(yStart, other.yStart)
            val overlapYEnd = min(yEnd, other.yEnd)

            val overlapZStart = max(zStart, other.zStart)
            val overlapZEnd = min(zEnd, other.zEnd)

            return CuboidPart2(
                if (other.status == "on") "off" else "on",
                overlapXStart,
                overlapXEnd,
                overlapYStart,
                overlapYEnd,
                overlapZStart,
                overlapZEnd
            )
        }
    }
}