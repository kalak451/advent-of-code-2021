import org.junit.jupiter.api.Test
import java.io.File
import java.util.*
import kotlin.test.assertEquals

class Day23 {
    val completeBoardP1 = listOf(
        "#############".toList(),
        "#...........#".toList(),
        "###A#B#C#D###".toList(),
        "  #A#B#C#D#".toList(),
        "  #########".toList()
    )

    val completeBoardP2 = listOf(
        "#############".toList(),
        "#...........#".toList(),
        "###A#B#C#D###".toList(),
        "  #A#B#C#D#".toList(),
        "  #A#B#C#D#".toList(),
        "  #A#B#C#D#".toList(),
        "  #########".toList()
    )

    var completeBoard = completeBoardP1

    val priorityDestsP1 = mapOf(
        'A' to listOf(Pair(3, 3), Pair(3, 2)),
        'B' to listOf(Pair(5, 3), Pair(5, 2)),
        'C' to listOf(Pair(7, 3), Pair(7, 2)),
        'D' to listOf(Pair(9, 3), Pair(9, 2))
    )

    val priorityDestsP2 = mapOf(
        'A' to listOf(Pair(3, 5), Pair(3, 4), Pair(3, 3), Pair(3, 2)),
        'B' to listOf(Pair(5, 5), Pair(5, 4), Pair(5, 3), Pair(5, 2)),
        'C' to listOf(Pair(7, 5), Pair(7, 4), Pair(7, 3), Pair(7, 2)),
        'D' to listOf(Pair(9, 5), Pair(9, 4), Pair(9, 3), Pair(9, 2))
    )

    var priorityDests = priorityDestsP1

    val costs = mapOf(
        'A' to 1, 'B' to 10, 'C' to 100, 'D' to 1000
    )

    val invalidDest = listOf(Pair(3, 1), Pair(5, 1), Pair(7, 1), Pair(9, 1))

    val hallways = listOf(
        Pair(1, 1),
        Pair(2, 1),
        Pair(4, 1),
        Pair(6, 1),
        Pair(8, 1),
        Pair(10, 1),
        Pair(11, 1)
    )

    var validPos = listOf(
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

        assertEquals(44169, r!!)
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

        assertEquals(43226, r!!)
    }

    @Test
    fun part1() {
        val board = loadBoard("aoc-2021-23.txt")

        val r = playGame(board)

        assertEquals(16244, r!!)
    }

    @Test
    fun part1Test() {
        val board = loadBoard("aoc-2021-23-test.txt")

        val r = playGame(board)

        assertEquals(12521, r!!)
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

        assertEquals(8, r!!)
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

        assertEquals(7008, r!!)
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

        assertEquals(9011, r!!)
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

        assertEquals(9051, r!!)
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

        assertEquals(12081, r!!)
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

        assertEquals(12481, r!!)
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

        assertEquals(12481, r!!)
    }

    fun playGame(board: List<List<Char>>): Long? {
        val dist = mutableMapOf(Pair(board, 0L))
        val queue = PriorityQueue<Pair<List<List<Char>>, Long>>(compareBy { it.second })
        queue.add(Pair(board, 0))

        while (!queue.isEmpty()) {
            val (b, _) = queue.poll()
            val moves = determineMoves(b)
            moves.forEach { (points, cost) ->
                val nb = b.move(points.first, points.second)
                val newCost = cost + dist[b]!!

                if (newCost < (dist[nb] ?: Long.MAX_VALUE)) {
                    dist[nb] = newCost
                    queue.add(Pair(nb, newCost))
                }
            }
        }

        return dist[completeBoard]!!
    }

    fun determineMoves(board: List<List<Char>>): List<Pair<Pair<Pair<Int, Int>, Pair<Int, Int>>, Int>> {
        return validPos
            .flatMap { determineMoves(it, board) }
            .sortedBy { it.second }
    }

    fun determineMoves(
        pos: Pair<Int, Int>,
        board: List<List<Char>>
    ): List<Pair<Pair<Pair<Int, Int>, Pair<Int, Int>>, Int>> {
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

        val visited = walkTree(pos, board)


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

    fun walkTree(pos: Pair<Int, Int>, board: List<List<Char>>): Map<Pair<Int, Int>, Int> {
        val visited: MutableMap<Pair<Int, Int>, Int> = mutableMapOf()
        visited[pos] = 0

        val queue = PriorityQueue<Pair<Pair<Int, Int>, Int>>(compareBy { it.second })
        queue.add(Pair(pos, 0))

        while (queue.isNotEmpty()) {
            val (p, _) = queue.poll()
            board.adj(p).forEach { a ->
                val newDist = visited[p]!! + 1
                if (newDist < visited.getOrDefault(a, Int.MAX_VALUE)) {
                    visited[a] = newDist
                    queue.add(Pair(a, newDist))
                }
            }
        }
        return visited.toMap()
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

    fun List<List<Char>>.move(start: Pair<Int, Int>, end: Pair<Int, Int>): List<List<Char>> {
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

    fun List<List<Char>>.at(pos: Pair<Int, Int>): Char {
        val (x, y) = pos
        return this[y][x]
    }

    fun List<List<Char>>.adj(pos: Pair<Int, Int>): List<Pair<Int, Int>> {
        return pos.adj().filter { (x, y) ->
            this[y][x] == '.'
        }
    }

    fun Pair<Int, Int>.adj(): List<Pair<Int, Int>> {
        return listOf(
            Pair(first - 1, second), Pair(first, second + 1), Pair(first + 1, second), Pair(first, second - 1)
        )
    }

    private fun loadBoard(path: String): List<List<Char>> {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()
        return lines.map { it.toList() }
    }
}


