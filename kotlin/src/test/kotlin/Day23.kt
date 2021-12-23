import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class Day23 {
    val completeBoard = listOf(
        "#############".toList(),
        "#...........#".toList(),
        "###A#B#C#D###".toList(),
        "  #A#B#C#D#".toList(),
        "  #########".toList()
    )

    val priorityDests = mapOf(
        'A' to listOf(Pair(3, 3), Pair(3, 2)),
        'B' to listOf(Pair(5, 3), Pair(5, 2)),
        'C' to listOf(Pair(7, 3), Pair(7, 2)),
        'D' to listOf(Pair(9, 3), Pair(9, 2))
    )

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

    private val validPos = listOf(
        hallways,
        priorityDests.values.flatten()
    ).flatten()

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

    fun playGame(
        board: List<List<Char>>,
        prevCost: Long = 0L,
        previousBoards: List<List<List<Char>>> = listOf(),
        cache: MutableMap<Pair<List<List<Char>>?, List<List<Char>>>, Long?> = mutableMapOf()
    ): Long? {
        if (cache.containsKey(Pair(previousBoards.lastOrNull(), board))) {
            return cache[Pair(previousBoards.lastOrNull(), board)]
        }

        if(board.isComplete()) {
            return prevCost
        }

        val newPrevBoards = previousBoards + setOf(board)
        val moves = determineMoves(board)

        val min = moves
            .asSequence()
            .map { (points, cost) -> Pair(board.move(points.first, points.second), cost) }
            .filter { !previousBoards.contains(it.first) }
            .mapNotNull { (newBoard, cost) ->
                playGame(newBoard, prevCost + cost, newPrevBoards, cache)
            }
            .minOrNull()

        cache[Pair(previousBoards.lastOrNull(), board)] = min
        return min
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
        val c = board[pos.second][pos.first]
        if (!c.isLetter()) {
            return listOf()
        }

        val firstPriorityForC = priorityDests[c]!![0]
        if (firstPriorityForC == pos) {
            return listOf()
        }

        if (board.at(firstPriorityForC) == c && priorityDests[c]!![1] == pos) {
            return listOf()
        }

        val visited = mutableMapOf<Pair<Int, Int>, Int>()
        walkTree(pos, board, 0, visited)


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

    fun walkTree(pos: Pair<Int, Int>, board: List<List<Char>>, depth: Int, visited: MutableMap<Pair<Int, Int>, Int>) {
        if (visited.containsKey(pos)) {
            return
        }

        visited[pos] = depth

        board.adj(pos).forEach { walkTree(it, board, depth + 1, visited) }
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
        if (x < 0 || x > 12) {
            return ' '
        }

        if (y < 0 || y > 4) {
            return ' '
        }

        return this[y][x]
    }

    fun List<List<Char>>.adj(pos: Pair<Int, Int>): List<Pair<Int, Int>> {
        return pos.adj().filter { (x, y) ->
            this[y][x] == '.'
        }
    }

    private fun List<List<Char>>.isComplete(): Boolean {
        return this == completeBoard
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


