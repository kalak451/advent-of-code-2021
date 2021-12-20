import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class Day20 {

    @Test
    fun shouldExtreme() {
        val (_, alg) = loadData("aoc-2021-20.txt")
        val image = (0..99).map {
            (0..99).map { '.' }.joinToString("")
        }

        val enhanced1 = enhance(image, alg, '.')
        val enhanced2 = enhance(enhanced1, alg, alg[0])
        val enhanced3 = enhance(enhanced2, alg, alg[511])

        assertEquals(0, enhanced2.joinToString("").count { it == '#' })
    }

    @Test
    fun part1() {
        val (testImage, testAlg) = loadData("aoc-2021-20-test.txt")
        val testEnhanced1 = enhance(testImage, testAlg, '.')
        val testEnhanced2 = enhance(testEnhanced1, testAlg, testAlg[0])

        val testCnt = testEnhanced2.joinToString("").count { it == '#' }
        assertEquals(35, testCnt)

        val (image, alg) = loadData("aoc-2021-20.txt")

        val enhanced1 = enhance(image, alg, '.')
        val enhanced2 = enhance(enhanced1, alg, alg[0])

        val cnt = enhanced2.joinToString("").count { it == '#' }
        assertEquals(5291, cnt)
    }

    @Test
    fun part2() {
        val (testImage, testAlg) = loadData("aoc-2021-20-test.txt")

        val (_,testEnhanced) = generateSequence(Pair(1, enhance(testImage, testAlg, '.'))) {
            val (flag, acc) = it
            if(flag == 1) {
                Pair(0, enhance(acc, testAlg, testAlg[0]))
            } else {
                Pair(1, enhance(acc, testAlg, testAlg[0]))
            }
        }.take(50).last()

        val testCnt = testEnhanced.joinToString("").count { it == '#' }
        assertEquals(3351, testCnt)

        val (image, alg) = loadData("aoc-2021-20.txt")

        val (_,enhanced) = generateSequence(Pair(1, enhance(image, alg, '.'))) {
            val (flag, acc) = it
            if(flag == 1) {
                Pair(0, enhance(acc, alg, alg[0]))
            } else {
                Pair(1, enhance(acc, alg, alg[511]))
            }
        }.take(50).last()

        val cnt = enhanced.joinToString("").count { it == '#' }
        assertEquals(16665, cnt)
    }

    @Test
    fun shouldEnhance() {
        val expected1 = listOf(
            ".##.##.",
            "#..#.#.",
            "##.#..#",
            "####..#",
            ".#..##.",
            "..##..#",
            "...#.#.",
        )

        val expected2 = listOf(
            ".......#.",
            ".#..#.#..",
            "#.#...###",
            "#...##.#.",
            "#.....#.#",
            ".#.#####.",
            "..#.#####",
            "...##.##.",
            "....###..",
        )

        val (testImage, testAlg) = loadData("aoc-2021-20-test.txt")


        val testEnhanced1 = enhance(testImage, testAlg, '.')
        val testEnhanced2 = enhance(testEnhanced1, testAlg, testAlg[0])

        assertEquals(expected1, testEnhanced1)
        assertEquals(expected2, testEnhanced2)
    }

    fun enhance(input: List<String>, alg: String, expansionChar: Char): List<String> {
        val image = expandImage(input, expansionChar)
        return image.mapIndexed { y, row ->
            row.indices.map { x ->
                val idx = buildAdj(listOf(x, y))
                    .map { pt -> image.at(pt, expansionChar) }
                    .map { c ->
                        when (c) {
                            '.' -> '0'
                            '#' -> '1'
                            else -> throw RuntimeException("Invalid char $c")
                        }
                    }
                    .joinToString("")
                    .toInt(2)

                alg[idx]
            }.joinToString("")
        }
    }

    fun List<String>.at(pt: List<Int>, expansionChar: Char): Char {
        if (pt.all { it in 0 until this.size }) {
            return this[pt[1]][pt[0]]
        }

        return expansionChar
    }

    fun buildAdj(pt: List<Int>): List<List<Int>> {
        return ((pt[1] - 1)..(pt[1] + 1)).flatMap { y ->
            ((pt[0] - 1)..(pt[0] + 1)).map { x ->
                listOf(x, y)
            }
        }
    }

    @Test
    fun shouldExpandImage() {
        val input = listOf(
            "#..#.",
            "#....",
            "##..#",
            "..#..",
            "..###",
        )

        val expected = listOf(
            ".......",
            ".#..#..",
            ".#.....",
            ".##..#.",
            "...#...",
            "...###.",
            "......."
        )

        assertEquals(expected, expandImage(input, '.'))
    }

    fun expandImage(input: List<String>, expansionChar: Char): List<String> {
        val emptyRow = arrayOfNulls<Char>(input[0].length).map { expansionChar }.joinToString("")

        return (listOf(emptyRow) + input + listOf(emptyRow)).map { row -> "$expansionChar$row$expansionChar" }
    }

    fun loadData(path: String): Pair<List<String>, String> {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()

        val alg = lines.take(1).first()

        val image = lines.drop(2)

        assertEquals(512, alg.length)

        return Pair(image, alg)
    }
}