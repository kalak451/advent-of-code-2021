import org.junit.jupiter.api.Test
import java.io.File
import java.util.*
import kotlin.test.assertEquals

class Day23 {
    private val completeBoardP1 = listOf(
        "#############".toList(),
        "#...........#".toList(),
        "###A#B#C#D###".toList(),
        "  #A#B#C#D#".toList(),
        "  #########".toList()
    )

    private val completeBoardP2 = listOf(
        "#############".toList(),
        "#...........#".toList(),
        "###A#B#C#D###".toList(),
        "  #A#B#C#D#".toList(),
        "  #A#B#C#D#".toList(),
        "  #A#B#C#D#".toList(),
        "  #########".toList()
    )

    private var completeBoard = completeBoardP1

    private val priorityDestsP1 = mapOf(
        'A' to listOf(Pair(3, 3), Pair(3, 2)),
        'B' to listOf(Pair(5, 3), Pair(5, 2)),
        'C' to listOf(Pair(7, 3), Pair(7, 2)),
        'D' to listOf(Pair(9, 3), Pair(9, 2))
    )

    private val priorityDestsP2 = mapOf(
        'A' to listOf(Pair(3, 5), Pair(3, 4), Pair(3, 3), Pair(3, 2)),
        'B' to listOf(Pair(5, 5), Pair(5, 4), Pair(5, 3), Pair(5, 2)),
        'C' to listOf(Pair(7, 5), Pair(7, 4), Pair(7, 3), Pair(7, 2)),
        'D' to listOf(Pair(9, 5), Pair(9, 4), Pair(9, 3), Pair(9, 2))
    )

    private var priorityDests = priorityDestsP1

    private val costs = mapOf(
        'A' to 1, 'B' to 10, 'C' to 100, 'D' to 1000
    )

    private val invalidDest = listOf(Pair(3, 1), Pair(5, 1), Pair(7, 1), Pair(9, 1))

    private val hallways = listOf(
        Pair(1, 1),
        Pair(2, 1),
        Pair(4, 1),
        Pair(6, 1),
        Pair(8, 1),
        Pair(10, 1),
        Pair(11, 1)
    )

    private var validPos = listOf(
        hallways,
        priorityDests.values.flatten()
    ).flatten()

    @Test
    fun part2Test() {
        val board = loadBoard("aoc-2021-23-test.txt")
        val newStuff = listOf(
            "  #D#C#B#A#".toList(),
            "  #D#B#A#C#".toList()
        )
        val biggerBoard = board.take(3) + newStuff + board.drop(3)

        priorityDests = priorityDestsP2
        completeBoard = completeBoardP2
        validPos = listOf(
            hallways,
            priorityDests.values.flatten()
        ).flatten()

        val r = playGame(biggerBoard)

        assertEquals(44169, r)
    }

    @Test
    fun part2() {
        val board = loadBoard("aoc-2021-23.txt")
        val newStuff = listOf(
            "  #D#C#B#A#".toList(),
            "  #D#B#A#C#".toList()
        )
        val biggerBoard = board.take(3) + newStuff + board.drop(3)

        priorityDests = priorityDestsP2
        completeBoard = completeBoardP2
        validPos = listOf(
            hallways,
            priorityDests.values.flatten()
        ).flatten()

        val r = playGame(biggerBoard)

        assertEquals(43226, r)
    }

    @Test
    fun part1() {
        val board = loadBoard("aoc-2021-23.txt")

        val r = playGame(board)

        assertEquals(16244, r)
    }

    @Test
    fun part1Test() {
        val board = loadBoard("aoc-2021-23-test.txt")

        val r = playGame(board)

        assertEquals(12521, r)
    }

    @Test
    fun part1Test2() {
        val board = listOf(
            "#############".toList(),
            "#.........A.#".toList(),
            "###.#B#C#D###".toList(),
            "  #A#B#C#D#".toList(),
            "  #########".toList()
        )
        val r = playGame(board)

        assertEquals(8, r)
    }

    @Test
    fun part1Test3() {
        val board = listOf(
            "#############".toList(),
            "#.....D.D.A.#".toList(),
            "###.#B#C#.###".toList(),
            "  #A#B#C#.#".toList(),
            "  #########".toList()
        )
        val r = playGame(board)

        assertEquals(7008, r)
    }

    @Test
    fun part1Test4() {
        val board = listOf(
            "#############".toList(),
            "#.....D.....#".toList(),
            "###.#B#C#D###".toList(),
            "  #A#B#C#A#".toList(),
            "  #########".toList()
        )
        val r = playGame(board)

        assertEquals(9011, r)
    }

    @Test
    fun part1Test5() {
        val board = listOf(
            "#############".toList(),
            "#.....D.....#".toList(),
            "###B#.#C#D###".toList(),
            "  #A#B#C#A#".toList(),
            "  #########".toList()
        )
        val r = playGame(board)

        assertEquals(9051, r)
    }

    @Test
    fun part1Test6() {
        val board = listOf(
            "#############".toList(),
            "#...B.......#".toList(),
            "###B#.#C#D###".toList(),
            "  #A#D#C#A#".toList(),
            "  #########".toList()
        )
        val r = playGame(board)

        assertEquals(12081, r)
    }

    @Test
    fun part1Test7() {
        val board = listOf(
            "#############".toList(),
            "#...B.......#".toList(),
            "###B#C#.#D###".toList(),
            "  #A#D#C#A#".toList(),
            "  #########".toList()
        )
        val r = playGame(board)

        assertEquals(12481, r)
    }

    @Test
    fun part1Test8() {
        val board = listOf(
            "#############".toList(),
            "#...B.......#".toList(),
            "###B#C#.#D###".toList(),
            "  #A#D#C#A#".toList(),
            "  #########".toList()
        )
        val r = playGame(board)

        assertEquals(12481, r)
    }

    private fun playGame(board: List<List<Char>>): Long {
        val paths = shortestPath(board) { b -> determineMoves(b) }
        return paths[completeBoard]!!
    }

    private fun determineMoves(board: List<List<Char>>): List<Pair<List<List<Char>>, Long>> {
        return validPos
            .flatMap { determineMoves(it, board) }
            .map { Pair(board.move(it.first.first, it.first.second), it.second) }
    }

    private fun determineMoves(
        pos: Pair<Int, Int>,
        board: List<List<Char>>
    ): List<Pair<Pair<Pair<Int, Int>, Pair<Int, Int>>, Long>> {
        val c = board.at(pos)
        if (!c.isLetter()) {
            return listOf()
        }

        val destinations = priorityDests[c]!!
        if (destinations.contains(pos)) {
            val firstBadIndex = destinations.map { board.at(it) }.indexOfFirst { it != c }
            if (firstBadIndex < 0 || destinations.indexOf(pos) <= firstBadIndex) {
                return listOf()
            }
        }

        val visited = shortestPath(pos) { p -> board.adj(p).map { Pair(it, 1) } }

        return visited
            .keys
            .asSequence()
            .filter { it != pos }
            .filter { !invalidDest.contains(it) }
            .filter { canMoveIntoRoom(c, it, board) }
            .filter { hallwayToRoomRule(pos, c, it) }
            .map { Pair(Pair(pos, it), costs[c]!! * visited[it]!!) }
            .toList()
    }

    private fun <T> shortestPath(start: T, adj: (T) -> List<Pair<T, Long>>): Map<T, Long> {
        val dist: MutableMap<T, Long> = mutableMapOf()
        dist[start] = 0

        val queue = PriorityQueue<Pair<T, Long>>(compareBy { it.second })
        queue.add(Pair(start, 0))

        while (queue.isNotEmpty()) {
            val (t, _) = queue.poll()
            adj(t).forEach { (nt, cost) ->
                val newDist = dist[t]!! + cost
                if (newDist < (dist[nt] ?: Long.MAX_VALUE)) {
                    dist[nt] = newDist
                    queue.add(Pair(nt, newDist))
                }
            }
        }

        return dist.toMap()
    }

    private fun hallwayToRoomRule(
        pos: Pair<Int, Int>,
        c: Char,
        it: Pair<Int, Int>
    ): Boolean {
        return if (hallways.contains(pos)) {
            priorityDests[c]!!.contains(it)
        } else {
            true
        }

    }

    private fun canMoveIntoRoom(c: Char, dest: Pair<Int, Int>, board: List<List<Char>>): Boolean {
        if (hallways.contains(dest)) {
            return true
        }

        if (!priorityDests[c]!!.contains(dest)) {
            return false
        }

        return priorityDests[c]!!.map { board.at(it) }.all { it == '.' || it == c }
    }

    private fun List<List<Char>>.move(start: Pair<Int, Int>, end: Pair<Int, Int>): List<List<Char>> {
        return this.mapIndexed { y, row ->
            row.mapIndexed { x, col ->
                if (x == start.first && y == start.second) {
                    '.'
                } else if (x == end.first && y == end.second) {
                    this.at(start)
                } else {
                    col
                }
            }
        }
    }

    private fun List<List<Char>>.at(pos: Pair<Int, Int>): Char {
        val (x, y) = pos
        return this[y][x]
    }

    private fun List<List<Char>>.adj(pos: Pair<Int, Int>): List<Pair<Int, Int>> {
        return pos.adj().filter { (x, y) ->
            this[y][x] == '.'
        }
    }

    private fun Pair<Int, Int>.adj(): List<Pair<Int, Int>> {
        return listOf(
            Pair(first - 1, second), Pair(first, second + 1), Pair(first + 1, second), Pair(first, second - 1)
        )
    }

    private fun loadBoard(path: String): List<List<Char>> {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()
        return lines.map { it.toList() }
    }
}


