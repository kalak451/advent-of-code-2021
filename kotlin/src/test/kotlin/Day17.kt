import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.util.regex.Pattern
import kotlin.math.max
import kotlin.test.assertTrue


class Day17 {
    @Test
    fun part1() {
        assertEquals(45, solve("aoc-2021-17-test.txt").first)
        assertEquals(13041, solve("aoc-2021-17.txt").first)
    }

    @Test
    fun part2() {
        assertEquals(112, solve("aoc-2021-17-test.txt").second)
        assertEquals(1031, solve("aoc-2021-17.txt").second)
    }

    private fun solve(path: String): Pair<Int, Int> {
        val targetRange = loadTargetRange(path)

        return generatePossibleDeltas()
            .map { runSimulation(it, targetRange) }
            .filter { it.second }
            .fold(Pair(Int.MIN_VALUE, 0)) { (max, count), (localMaxHeight, _, _) ->
                Pair(max(max, localMaxHeight), count + 1)
            }
    }

    private fun generatePossibleDeltas() = (0..200).asSequence()
        .flatMap { dx -> (-200..200).asSequence().map { dy -> Pair(dx, dy) } }

    private fun loadTargetRange(path: String): Pair<IntRange, IntRange> {
        val pattern = Pattern.compile("target area: x=(.*)\\.\\.(.*), y=(.*)\\.\\.(.*)")
        val line = File(ClassLoader.getSystemResource(path).file).readLines().joinToString("")
        val matcher = pattern.matcher(line)
        assertTrue { matcher.matches() }
        val xRange = matcher.group(1).toInt()..matcher.group(2).toInt()
        val yRange = matcher.group(3).toInt()..matcher.group(4).toInt()
        return Pair(xRange, yRange)
    }

    private fun runSimulation(
        startVelocity: Pair<Int, Int>,
        targetRange: Pair<IntRange, IntRange>
    ): Triple<Int, Boolean, List<Pair<Int, Int>>> {
        var currentPoint = Pair(0, 0)
        var currentVelocity = startVelocity

        val points = mutableListOf(currentPoint)

        while (!targetRange.contains(currentPoint) && !targetRange.isBeyond(currentPoint)) {
            val nextPt = nextStep(currentPoint, currentVelocity)
            currentPoint = nextPt.first
            currentVelocity = nextPt.second
            points.add(currentPoint)
        }

        val maxHeight = points.maxOf { it.second }
        return Triple(maxHeight, targetRange.contains(points.last()), points)
    }

    fun Pair<IntRange, IntRange>.contains(p: Pair<Int, Int>): Boolean {
        return this.first.contains(p.first) && this.second.contains(p.second)
    }

    private fun Pair<IntRange, IntRange>.isBeyond(p: Pair<Int, Int>): Boolean {
        return this.second.first > p.second
    }

    private fun nextStep(
        currentPosition: Pair<Int, Int>,
        currentVelocity: Pair<Int, Int>
    ): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        val nextPosition =
            Pair(currentPosition.first + currentVelocity.first, currentPosition.second + currentVelocity.second)

        val nextXVelocityDelta = if (currentVelocity.first == 0) {
            0
        } else {
            if (currentVelocity.first > 0) {
                -1
            } else {
                1
            }
        }

        val nextVelocity = Pair(currentVelocity.first + nextXVelocityDelta, currentVelocity.second - 1)

        return Pair(nextPosition, nextVelocity)
    }
}



