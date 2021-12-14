import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File


class Day14 {

    @Test
    fun part1() {
        assertEquals(1588, runPart1("aoc-2021-14-test.txt", 10))
        assertEquals(2768, runPart1("aoc-2021-14.txt", 10))
    }

    private fun runPart1(path: String, levels: Int): Long {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()
        val (template, rules) = parseInput(lines)

        val merged = generateSequence(template) { applyRules(it, rules) }
            .take(levels + 1)
            .last()

        val sorted = merged
            .groupBy { it }
            .values
            .map { it.count() }
            .sorted()

        return (sorted.last() - sorted.first()).toLong()
    }

    private fun applyRules(
        template: String,
        rules: Map<String, String>
    ): String {
        return template
            .windowed(2, 1)
            .joinToString("", postfix = template.last().toString()) { it[0] + rules[it]!! }
    }

    @Test
    fun part2() {
        assertEquals(1588, runPart2("aoc-2021-14-test.txt", 10))
        assertEquals(2768, runPart2("aoc-2021-14.txt", 10))
        assertEquals(2188189693529L, runPart2("aoc-2021-14-test.txt", 40))
        assertEquals(2914365137499, runPart2("aoc-2021-14.txt", 40))
    }

    private fun runPart2(path: String, levels: Int): Long {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()
        val (template, rules) = parseInput(lines)

        val initial = template
            .windowed(2, 1)
            .groupBy { it }
            .mapValues { (_, v) -> v.count().toLong() }

        val results = generateSequence(initial) { applyRulesPart2(it, rules) }
            .take(levels + 1)
            .last()

        val sorted = results
            .map { (k, v) -> Pair(k[0], v) }
            .groupBy { it.first }
            .toMap()
            .mapValues { (k, v) ->
                val sum = v.sumOf { it.second }
                when (k) {
                    template.last() -> sum + 1
                    else -> sum
                }
            }
            .values
            .sorted()

        return (sorted.last() - sorted.first())
    }

    private fun applyRulesPart2(
        previous: Map<String, Long>,
        rules: Map<String, String>
    ): Map<String, Long> {
        return previous
            .flatMap { (k, v) ->
                val newChar = rules[k]
                listOf(
                    Pair(k[0] + newChar.toString(), v),
                    Pair(newChar + k[1].toString(), v)
                )
            }
            .groupBy { it.first }
            .mapValues { (_, v) -> v.sumOf { it.second } }
    }

    private fun parseInput(lines: List<String>): Pair<String, Map<String, String>> {
        val template = lines.first()
        val rules = lines.asSequence()
            .drop(2)
            .map { it.split(" -> ") }
            .map { (k, v) -> Pair(k, v) }
            .toMap()
        return Pair(template, rules)
    }
}
