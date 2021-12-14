import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File


class Day13 {

    @Test
    fun part1() {
        assertEquals(17, runPart1("aoc-2021-13-test.txt"))
        assertEquals(850, runPart1("aoc-2021-13.txt"))
    }

    private fun runPart1(path: String): Int {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()
        val paper = buildPaper(lines)
        val newPaper = paper.fold()

        return newPaper.sheet.map { it.filter { it == '#' }.count() }.sum()
    }

    @Test
    fun part2() {
        assertEquals(16, runPart2("aoc-2021-13-test.txt"))
        assertEquals(102, runPart2("aoc-2021-13.txt"))
    }

    private fun runPart2(path: String): Int {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()
        var paper = buildPaper(lines)

        while (paper.folds.isNotEmpty()) {
            paper = paper.fold()
        }

        println(paper.sheet.joinToString("\n") { row -> row.joinToString("") })

        return paper.sheet.sumOf { it.count { p -> p == '#' } }
    }

    private fun buildPaper(lines: List<String>): Paper {
        val coordSet = lines.asSequence()
            .takeWhile { it.isNotBlank() }
            .map { it.split(",") }
            .map { (x, y) -> Pair(x.toInt(), y.toInt()) }
            .toSet()

        val maxX = coordSet
            .maxOf { it.first }
        val maxY = coordSet
            .maxOf { it.second }

        val sheet = (0..maxY)
            .map { y ->
                (0..maxX)
                    .map { x -> if (coordSet.contains(Pair(x, y))) '#' else ' ' }
                    .toList()
            }
            .toList()

        val folds = lines.asSequence()
            .dropWhile { !it.startsWith("fold along") }
            .map {
                val groups = "fold along (.*)=(.*)".toRegex().matchEntire(it)!!.groups
                Pair(groups[1]!!.value[0], groups[2]!!.value.toInt())
            }
            .toList()

        return Paper(sheet, folds)
    }

    class Paper(
        val sheet: List<List<Char>>,
        val folds: List<Pair<Char, Int>>
    ) {
        fun fold(): Paper {
            val (axis, n) = folds.first()
            if (axis == 'x') {
                return foldX(n)
            }

            if (axis == 'y') {
                return foldY(n)
            }

            return this
        }

        private fun foldX(n: Int): Paper {
            val newSheet = (sheet.indices).map { y ->
                (0..n - 1).map { x ->
                    if (sheet[y][x] == '#' || sheet[y][sheet[0].size - 1 - x] == '#') {
                        '#'
                    } else {
                        ' '
                    }
                }
            }
            return Paper(newSheet, folds.drop(1))
        }

        private fun foldY(n: Int): Paper {
            val newSheet = (0..n - 1).map { y ->
                sheet[0].indices.map { x ->
                    val y2 = sheet.size - y - 1
                    if (sheet[y][x] == '#' || sheet[y2][x] == '#') {
                        '#'
                    } else {
                        ' '
                    }
                }
            }
            return Paper(newSheet, folds.drop(1))
        }
    }

}