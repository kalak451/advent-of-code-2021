import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class Day20 {

    @Test
    fun shouldExtreme() {
        val orig = loadImage("aoc-2021-20.txt")
        val data = (0..99).map {
            (0..99).map { '.' }.joinToString("")
        }

        val image = Image(orig.alg, data)

        val enhanced1 = image.enhance()
        val enhanced2 = enhanced1.enhance()
        val enhanced3 = enhanced2.enhance()

        assertEquals(0, enhanced2.data.joinToString("").count { it == '#' })
        assertEquals(0, enhanced3.data.joinToString("").count { it == '.' })
    }

    @Test
    fun part1() {
        val testImage = loadImage("aoc-2021-20-test.txt")
        val testEnhanced1 = testImage.enhance()
        val testEnhanced2 = testEnhanced1.enhance()

        val testCnt = testEnhanced2.data.joinToString("").count { it == '#' }
        assertEquals(35, testCnt)

        val image = loadImage("aoc-2021-20.txt")

        val enhanced1 = image.enhance()
        val enhanced2 = enhanced1.enhance()

        val cnt = enhanced2.data.joinToString("").count { it == '#' }
        assertEquals(5291, cnt)
    }

    @Test
    fun part2() {
        val testImage = loadImage("aoc-2021-20-test.txt")

        val testEnhanced = generateSequence(testImage.enhance()) { it.enhance() }.take(50).last()

        val testCnt = testEnhanced.data.joinToString("").count { it == '#' }
        assertEquals(3351, testCnt)

        val image = loadImage("aoc-2021-20.txt")

        val enhanced = generateSequence(image.enhance()) { it.enhance() }.take(50).last()

        val cnt = enhanced.data.joinToString("").count { it == '#' }
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

        val testImage = loadImage("aoc-2021-20-test.txt")


        val testEnhanced1 = testImage.enhance()
        val testEnhanced2 = testEnhanced1.enhance()

        assertEquals(expected1, testEnhanced1.data)
        assertEquals(expected2, testEnhanced2.data)
    }

    fun loadImage(path: String): Image {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()

        val alg = lines.take(1).first()

        val image = lines.drop(2)

        assertEquals(512, alg.length)

        return Image(alg, image)
    }


    class Image(val alg: String, val data: List<String>, private val unknown: Char = '.') {
        fun enhance(): Image {
            val newData = (-1..data.size).map { y ->
                (-1..data.size).map { x ->
                    val idx = buildAdj(listOf(x, y)).map { pt -> at(pt) }.map { c ->
                            when (c) {
                                '.' -> '0'
                                '#' -> '1'
                                else -> throw RuntimeException("Invalid char $c")
                            }
                        }.joinToString("").toInt(2)
                    alg[idx]
                }.joinToString("")
            }

            val nextUnknown = calculateNextUnknown()

            return Image(alg, newData, nextUnknown)
        }

        private fun buildAdj(pt: List<Int>): List<List<Int>> {
            return ((pt[1] - 1)..(pt[1] + 1)).flatMap { y ->
                ((pt[0] - 1)..(pt[0] + 1)).map { x ->
                    listOf(x, y)
                }
            }
        }

        private fun calculateNextUnknown(): Char {
            return if (unknown == '.' && alg[0] == '.') {
                '.'
            } else if (unknown == alg[0]) {
                alg[1]
            } else {
                alg[0]
            }
        }

        private fun at(pt: List<Int>): Char {
            if (pt.all { it in data.indices }) {
                return data[pt[1]][pt[0]]
            }

            return unknown
        }
    }
}