import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File


class Day12 {

    @Test
    fun part1() {
        assertEquals(10, runPart1("aoc-2021-12-test-1.txt"))
        assertEquals(19, runPart1("aoc-2021-12-test-2.txt"))
        assertEquals(226, runPart1("aoc-2021-12-test-3.txt"))
        assertEquals(4167, runPart1("aoc-2021-12.txt"))
    }

    private fun runPart1(path: String): Int {
        val caveGraph = buildGraph(path)

        val results = visitCavePart1(caveGraph["start"]!!, listOf())

        return results.size
    }

    fun visitCavePart1(c: Cave, currentPath: List<Cave>): List<List<Cave>> {
        val newPath = currentPath + c

        if (c.isEnd()) {
            return listOf(newPath)
        }

        return c.connectedTo
            .filter { newPath.isAllowedOnPathP1(it) }
            .flatMap { visitCavePart1(it, newPath) }
    }

    fun List<Cave>.isAllowedOnPathP1(c: Cave): Boolean {
        if (c.isBig()) {
            return true
        }

        if(!this.contains(c)) {
            return true
        }

        return false
    }

    @Test
    fun part2() {
        assertEquals(36, runPart2("aoc-2021-12-test-1.txt"))
        assertEquals(103, runPart2("aoc-2021-12-test-2.txt"))
        assertEquals(3509, runPart2("aoc-2021-12-test-3.txt"))
        assertEquals(98441, runPart2("aoc-2021-12.txt"))
    }

    private fun runPart2(path: String): Int {
        val caveGraph = buildGraph(path)
        val results = visitCavePart2(caveGraph["start"]!!, listOf())

        return results.size
    }

    fun visitCavePart2(c: Cave, currentPath: List<Cave>): List<List<Cave>> {
        val newPath = currentPath + c

        if (c.isEnd()) {
            return listOf(newPath)
        }

        return c.connectedTo
            .filter { newPath.isAllowedOnPathP2(it) }
            .flatMap { visitCavePart2(it, newPath) }
    }

    fun List<Cave>.isAllowedOnPathP2(c: Cave): Boolean {
        if (c.isBig()) {
            return true
        }

        if (c.isStart()) {
            return false
        }

        if (c.isEnd()) {
            return true
        }

        if(!this.contains(c)) {
            return true
        }

        return this
            .filter { !it.isStart() }
            .filter { it.isSmall() }
            .groupBy { it }
            .mapValues { (_, v) -> v.size }
            .values
            .all{it < 2}
    }

    private fun buildGraph(path: String): MutableMap<String, Cave> {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()

        val caveGraph = mutableMapOf<String, Cave>()
        lines
            .map { it.split("-") }
            .forEach { (n1, n2) ->
                if (!caveGraph.containsKey(n1)) {
                    caveGraph[n1] = Cave(n1, mutableSetOf())
                }

                if (!caveGraph.containsKey(n2)) {
                    caveGraph[n2] = Cave(n2, mutableSetOf())
                }

                caveGraph[n1]!!.connectedTo.add(caveGraph[n2]!!)
                caveGraph[n2]!!.connectedTo.add(caveGraph[n1]!!)
            }
        return caveGraph
    }

    class Cave(
        val name: String,
        val connectedTo: MutableSet<Cave>
    ) {
        fun isBig(): Boolean {
            return name.all { it.isUpperCase() }
        }

        fun isSmall(): Boolean {
            return name.all { it.isLowerCase() }
        }

        fun isStart(): Boolean {
            return name == "start"
        }

        fun isEnd(): Boolean {
            return name == "end"
        }

        override fun toString(): String {
            return "Cave(name='$name'"
        }
    }


}