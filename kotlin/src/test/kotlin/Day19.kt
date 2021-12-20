import org.junit.jupiter.api.Test
import java.io.File
import kotlin.math.abs
import kotlin.test.assertEquals

class Day19 {

    @Test
    fun part1() {
        val scanners = loadFile("aoc-2021-19-test.txt")
        val (beacons, scannerPositions) = runPart1(
            scanners[0]!!,
            (1 until scanners.size).map { scanners[it]!! }
        )


        assertEquals(79, beacons.size)

        val max = scannerPositions.indices.flatMap { s1 ->
            (s1 until scannerPositions.size).map { s2 ->
                Pair(scannerPositions[s1], scannerPositions[s2])
            }
        }
            .map { diffCoords(it.first, it.second) }
            .map { abs(it.first) + abs(it.second) + abs(it.third) }
            .maxOf { it }

        assertEquals(3621, max)
    }

    private fun runPart1(
        scan0: List<Triple<Int, Int, Int>>,
        otherScans: List<List<Triple<Int, Int, Int>>>
    ): Pair<List<Triple<Int, Int, Int>>, List<Triple<Int, Int, Int>>> {
        var scans = otherScans
        var starter = scan0
        var scannerPositions = listOf(Triple(0, 0, 0))

        while (scans.isNotEmpty()) {
            val (result, scanners, remaining) = scans
                .fold(
                    Triple(
                        starter,
                        scannerPositions,
                        listOf<List<Triple<Int, Int, Int>>>()
                    )
                ) { (acc, knownScanners, notFound), scan ->
                    val alignedResult = alignScans(acc, scan)

                    if (alignedResult == null) {
                        Triple(acc, knownScanners, notFound + listOf(scan))
                    } else {
                        val (scanner, aligned) = alignedResult
                        Triple(aligned, knownScanners + listOf(scanner), notFound)
                    }
                }
            scans = remaining
            starter = result
            scannerPositions = scanners
        }
        return Pair(starter, scannerPositions)
    }

    private fun alignScans(
        reference: List<Triple<Int, Int, Int>>,
        scan: List<Triple<Int, Int, Int>>
    ): Pair<Triple<Int, Int, Int>, List<Triple<Int, Int, Int>>>? {
        val ops = orientationPermutations(scan)

        val compareResults = ops.mapValues { (_, v) -> compareCoords(reference, v) }
        val result = compareResults.filter { (_, v) -> v.isNotEmpty() }

        if (result.isEmpty()) {
            return null
        }

        val rot = result.toList()[0].first
        val (offset, _) = result.toList()[0].second.first()

        val correctedB = scan
            .map { applyRotation(it, rot) }
            .map { applyOffset(it, offset) }

        val merged = (reference + correctedB).distinct()
        return Pair(offset, merged)

    }

    private fun applyOffset(item: Triple<Int, Int, Int>, offset: Triple<Int, Int, Int>): Triple<Int, Int, Int> {
        return Triple(item.first + offset.first, item.second + offset.second, item.third + offset.third)
    }

    fun orientationPermutations(input: List<Triple<Int, Int, Int>>): Map<Triple<Int, Int, Int>, List<Triple<Int, Int, Int>>> {
        return rotationPermutations()
            .associateWith { rot -> input.map { i -> applyRotation(i, rot) } }

    }

    fun rotationPermutations(): List<Triple<Int, Int, Int>> {
        return (0..3).flatMap { x ->
            (0..3).flatMap { y ->
                (0..3).map { z ->
                    Triple(x, y, z)
                }
            }
        }
    }

    fun applyRotation(beacon: Triple<Int, Int, Int>, rotation: Triple<Int, Int, Int>): Triple<Int, Int, Int> {
        var (x, y, z) = beacon

        //X rot
        repeat(rotation.first) {
            val tmp = y
            y = z
            z = -tmp
        }

        //y rot
        repeat(rotation.second) {
            val tmp = x
            x = z
            z = -tmp
        }

        //y rot
        repeat(rotation.third) {
            val tmp = x
            x = y
            y = -tmp
        }

        return Triple(x, y, z)
    }

    fun compareCoords(
        a: List<Triple<Int, Int, Int>>,
        b: List<Triple<Int, Int, Int>>
    ): List<Pair<Triple<Int, Int, Int>, List<Pair<Int, Int>>>> {
        val diffs = a.map { ap -> b.map { bp -> diffCoords(ap, bp) } }

        val possibleOffsets = (0 until diffs.size).flatMap { i ->
            (i + 1 until diffs.size).flatMap { j ->
                diffs[i].intersect(diffs[j].toSet())
            }
        }.distinct()

        val offsetResults = possibleOffsets.map { po ->
            val d = diffs
                .flatMapIndexed { ai, aDiffs -> aDiffs.mapIndexed { bi, bDiff -> Triple(ai, bi, bDiff) } }
                .filter { it.third == po }
                .map { Pair(it.first, it.second) }

            Pair(po, d)
        }

        return offsetResults.filter { it.second.size >= 12 }
    }

    fun diffCoords(a: Triple<Int, Int, Int>, b: Triple<Int, Int, Int>): Triple<Int, Int, Int> {
        return Triple(a.first - b.first, a.second - b.second, a.third - b.third)
    }

    fun loadFile(path: String): Map<Int, List<Triple<Int, Int, Int>>> {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()
        val loadedSensors = lines.fold(Pair(mutableMapOf<String, MutableList<String>>(), "")) { (res, currentKey), l ->
            var newCurrentKey = currentKey
            if (l.startsWith("---")) {
                newCurrentKey = l
                res[newCurrentKey] = mutableListOf()
            } else {
                res[newCurrentKey]!!.add(l)
            }

            Pair(res, newCurrentKey)
        }.first

        val keyRegex = "--- scanner (.*) ---".toRegex()
        val coordRegx = "(.*),(.*),(.*)".toRegex()

        return loadedSensors
            .mapKeys { (k, _) -> keyRegex.matchEntire(k)!!.groupValues[1].toInt() }
            .mapValues { (_, v) ->
                v.mapNotNull { coordRegx.matchEntire(it) }
                    .map { m ->
                        Triple(
                            m.groupValues[1].toInt(),
                            m.groupValues[2].toInt(),
                            m.groupValues[3].toInt()
                        )
                    }
            }
    }
}