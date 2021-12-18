import org.junit.jupiter.api.Test
import java.io.File
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.test.assertEquals

class Day18 {

    @Test
    fun part2() {
        assertEquals(3993, runPart2(loadFile("aoc-2021-18-test.txt")))
        assertEquals(4807, runPart2(loadFile("aoc-2021-18.txt")))
    }

    private fun runPart2(numbers: List<List<Token>>): Long {
        return numbers.indices.flatMap { a ->
            numbers.slice(a + 1 until numbers.size).indices.flatMap { b ->
                listOf(
                    calculateMagnitude(add(numbers[a], numbers[b])),
                    calculateMagnitude(add(numbers[b], numbers[a]))
                )
            }
        }.maxOf { it }
    }

    @Test
    fun part1() {
        assertEquals(4140, calculateMagnitude(add(loadFile("aoc-2021-18-test.txt"))))
        assertEquals(4289, calculateMagnitude(add(loadFile("aoc-2021-18.txt"))))
    }

    fun loadFile(path: String): List<List<Token>> {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()

        return lines.map { parse(it) }
    }

    @Test
    fun shouldMagnitude() {
        assertEquals(143, calculateMagnitude(parse("[[1,2],[[3,4],5]]")))
        assertEquals(1384, calculateMagnitude(parse("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]")))
        assertEquals(445, calculateMagnitude(parse("[[[[1,1],[2,2]],[3,3]],[4,4]]")))
        assertEquals(791, calculateMagnitude(parse("[[[[3,0],[5,3]],[4,4]],[5,5]]")))
        assertEquals(1137, calculateMagnitude(parse("[[[[5,0],[7,4]],[5,5]],[6,6]]")))
        assertEquals(3488, calculateMagnitude(parse("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]")))
    }

    fun calculateMagnitude(tokens: List<Token>): Long {
        val stack = Stack<MutableList<Long>>()
        var current = mutableListOf<Long>()
        tokens.forEach { t ->
            when (t) {
                Open -> {
                    stack.push(current)
                    current = mutableListOf()
                }
                Close -> {
                    val mag = current[0] * 3 + current[1] * 2
                    current = stack.pop()
                    current.add(mag)
                }
                else -> {
                    val value = t as Value
                    current.add(value.v)
                }
            }
        }

        return current[0]
    }

    @Test
    fun shouldAdd() {
        assertEquals(
            parse("[[[[1,1],[2,2]],[3,3]],[4,4]]"), add(
                listOf(
                    parse("[1,1]"),
                    parse("[2,2]"),
                    parse("[3,3]"),
                    parse("[4,4]")
                )
            )
        )

        assertEquals(
            parse("[[[[5,0],[7,4]],[5,5]],[6,6]]"), add(
                listOf(
                    parse("[1,1]"),
                    parse("[2,2]"),
                    parse("[3,3]"),
                    parse("[4,4]"),
                    parse("[5,5]"),
                    parse("[6,6]")
                )
            )
        )

        assertEquals(
            parse("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]"), add(
                listOf(
                    parse("[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]"),
                    parse("[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]"),
                    parse("[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]"),
                    parse("[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]"),
                    parse("[7,[5,[[3,8],[1,4]]]]"),
                    parse("[[2,[2,2]],[8,[8,1]]]"),
                    parse("[2,9]"),
                    parse("[1,[[[9,3],9],[[9,0],[0,7]]]]"),
                    parse("[[[5,[7,4]],7],1]"),
                    parse("[[[[4,2],2],6],[8,7]]")
                )
            )
        )
    }

    fun add(snailNumbers: List<List<Token>>): List<Token> {
        return snailNumbers.reduce { a, b -> add(a, b) }
    }

    @Test
    fun shouldBasicAdd() {
        assertEquals(parse("[[1,1],[2,2]]"), add(parse("[1,1]"), parse("[2,2]")))
    }

    fun add(a: List<Token>, b: List<Token>): List<Token> {
        return reduce(listOf(Open) + a + b + listOf(Close))
    }

    @Test
    fun shouldReduce() {
        assertEquals(
            parse("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]"),
            reduce(parse("[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]"))
        )
    }

    fun reduce(tokens: List<Token>): List<Token> {
        var a = tokens
        var b = reduceStep(tokens)

        while (a != b) {
            a = b
            b = reduceStep(a)
        }

        return b
    }

    @Test
    fun shouldHandleReduceStep() {
        assertEquals(
            parse("[[[[0,7],4],[7,[[8,4],9]]],[1,1]]"),
            reduceStep(parse("[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]"))
        )
        assertEquals(parse("[[[[0,7],4],[15,[0,13]]],[1,1]]"), reduceStep(parse("[[[[0,7],4],[7,[[8,4],9]]],[1,1]]")))
        assertEquals(parse("[[[[0,7],4],[[7,8],[0,13]]],[1,1]]"), reduceStep(parse("[[[[0,7],4],[15,[0,13]]],[1,1]]")))
        assertEquals(
            parse("[[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]"),
            reduceStep(parse("[[[[0,7],4],[[7,8],[0,13]]],[1,1]]"))
        )
        assertEquals(
            parse("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]"),
            reduceStep(parse("[[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]"))
        )
        assertEquals(parse("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]"), reduceStep(parse("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]")))
    }

    fun reduceStep(tokens: List<Token>): List<Token> {
        val afterExplode = explode(tokens)
        if (afterExplode != tokens) {
            return afterExplode
        }

        return split(tokens)
    }

    @Test
    fun shouldHandleExplodes() {
        assertEquals(parse("[[[[0,9],2],3],4]"), explode(parse("[[[[[9,8],1],2],3],4]")))
        assertEquals(parse("[7,[6,[5,[7,0]]]]"), explode(parse("[7,[6,[5,[4,[3,2]]]]]")))
        assertEquals(parse("[[6,[5,[7,0]]],3]"), explode(parse("[[6,[5,[4,[3,2]]]],1]")))
        assertEquals(
            parse("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]"),
            explode(parse("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]"))
        )
        assertEquals(parse("[[3,[2,[8,0]]],[9,[5,[7,0]]]]"), explode(parse("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]")))
    }

    fun explode(tokens: List<Token>): List<Token> {
        val soe = findStartOfExplode(tokens) ?: return tokens

        val eoe = soe + 3
        val explodeLeft = tokens[soe + 1] as Value
        val explodeRight = tokens[soe + 2] as Value

        val before = tokens.slice(0 until soe).toMutableList()
        val after = tokens.slice(eoe + 1 until tokens.size).toMutableList()

        val beforeToUpdate = before.indices.reversed().firstOrNull { before[it] is Value }
        if (beforeToUpdate != null) {
            before[beforeToUpdate] = Value(explodeLeft.v + (before[beforeToUpdate] as Value).v)
        }

        val afterToUpdate = after.indices.firstOrNull { after[it] is Value }
        if (afterToUpdate != null) {
            after[afterToUpdate] = Value(explodeRight.v + (after[afterToUpdate] as Value).v)
        }

        return before + mutableListOf(Value(0)) + after
    }

    fun findStartOfExplode(tokens: List<Token>): Int? {
        val (_, startOfExplode) = tokens.withIndex().fold(Pair(0, -1)) { (depth, soe), c ->
            if (soe != -1) {
                Pair(depth, soe)
            } else {
                if (c.value == Open) {
                    val newDepth = depth + 1
                    if (newDepth == 5) {
                        Pair(newDepth, c.index)
                    } else {
                        Pair(newDepth, soe)
                    }
                } else if (c.value == Close) {
                    Pair(depth - 1, soe)
                } else {
                    Pair(depth, soe)
                }
            }
        }

        return if (startOfExplode == -1) null else startOfExplode
    }

    @Test
    fun shouldHandleComplexSplits() {
        assertEquals(parse("[[[[0,7],4],[[7,8],[0,13]]],[1,1]]"), split(parse("[[[[0,7],4],[15,[0,13]]],[1,1]]")))
        assertEquals(parse("[[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]"), split(parse("[[[[0,7],4],[[7,8],[0,13]]],[1,1]]")))
    }

    fun split(tokens: List<Token>): List<Token> {
        val indexToSplit =
            tokens.indices.firstOrNull { tokens[it] is Value && (tokens[it] as Value).v >= 10 } ?: return tokens

        val split = split(tokens[indexToSplit] as Value)

        return tokens.slice(0 until indexToSplit) + split + tokens.slice(indexToSplit + 1 until tokens.size)
    }

    @Test
    fun shouldHandleBasicSplits() {
        assertEquals(listOf(Open, Value(5), Value(5), Close), split(Value(10)))
        assertEquals(listOf(Open, Value(5), Value(6), Close), split(Value(11)))
        assertEquals(listOf(Open, Value(6), Value(6), Close), split(Value(12)))
    }

    fun split(value: Value): List<Token> {
        val i = value.v
        val left = floor(i / 2.0).toLong()
        val right = ceil(i / 2.0).toLong()

        return listOf(Open, Value(left), Value(right), Close)
    }

    @Test
    fun shouldParse() {
        assertEquals(mutableListOf(Open, Value(1), Value(2), Close), parse("[1,2]"))
        assertEquals(mutableListOf(Open, Open, Value(1), Value(2), Close, Value(3), Close), parse("[[1,2],3]"))
    }

    fun parse(s: String): List<Token> {
        val (res, _) = s.fold(Pair(listOf<Token>(), "")) { (res, acc), c ->
            when (c) {
                '[' -> Pair(res + listOf(Open), acc)
                ',' -> if (acc == "") Pair(res, "") else Pair(res + listOf(Value(acc.toLong())), "")
                ']' -> if (acc == "") Pair(res + listOf(Close), "") else Pair(
                    res + listOf(Value(acc.toLong()), Close),
                    ""
                )
                else -> Pair(res, acc + c)
            }
        }

        return res
    }

    sealed class Token
    object Open : Token()
    object Close : Token()
    data class Value(val v: Long) : Token()
}