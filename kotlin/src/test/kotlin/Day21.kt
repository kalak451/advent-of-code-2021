import org.junit.jupiter.api.Test
import kotlin.math.min
import kotlin.test.assertEquals

class Day21 {
    @Test
    fun part1() {
        var dieValue = 1
        var gameState = GameState(9, 4)

        while (gameState.p1Score < 1000 && gameState.p2Score < 1000) {
            val r1 = 1 + (dieValue++ - 1) % 100
            val r2 = 1 + (dieValue++ - 1) % 100
            val r3 = 1 + (dieValue++ - 1) % 100

            gameState = gameState.playRound(r1, r2, r3)
        }

        val minScore = min(gameState.p1Score, gameState.p2Score)
        val num = minScore * (dieValue - 1)

        assertEquals(998088, num)
    }

    @Test
    fun part2() {
        val gameState = GameState(9, 4)

        val (p1, p2) = play(gameState, mutableMapOf())

        assertEquals(306621346123766UL, p1)
        assertEquals(166105651528183UL, p2)
    }

    fun play(gameState: GameState, cache: MutableMap<GameState, Pair<ULong, ULong>>): Pair<ULong, ULong> {
        if (cache.containsKey(gameState)) {
            return cache[gameState]!!
        }

        if (gameState.p1Score >= 21) {
            return Pair(1UL, 0UL)
        }

        if (gameState.p2Score >= 21) {
            return Pair(0UL, 1UL)
        }

        var p1Score = 0UL
        var p2Score = 0UL

        (1..3).forEach { r1 ->
            (1..3).forEach { r2 ->
                (1..3).forEach { r3 ->
                    val (p1, p2) = play(gameState.playRound(r1, r2, r3), cache)
                    p1Score += p1
                    p2Score += p2
                }
            }
        }

        val score = Pair(p1Score, p2Score)
        cache[gameState] = score

        return score
    }


    data class GameState(
        val p1Pos: Int, val p2Pos: Int, val p1Score: Int = 0, val p2Score: Int = 0, val currentPlayer: Int = 1
    ) {
        fun playRound(d1: Int, d2: Int, d3: Int): GameState {
            val mv = d1 + d2 + d3

            return if (currentPlayer == 1) {
                val pos = 1 + (p1Pos + mv - 1) % 10
                GameState(pos, p2Pos, p1Score + pos, p2Score, 2)
            } else {
                val pos = 1 + (p2Pos + mv - 1) % 10
                GameState(p1Pos, pos, p1Score, p2Score + pos, 1)
            }
        }
    }
}