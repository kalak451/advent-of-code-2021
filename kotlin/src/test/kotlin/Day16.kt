import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File


class Day16 {

    @Test
    fun part1() {
        assertEquals(949, runPart1("aoc-2021-16.txt"))
    }

    private fun runPart1(path: String): Int {
        val hex = File(ClassLoader.getSystemResource(path).file).readLines().joinToString("")
        val bits = parseHexString(hex)
        val (p, _) = parsePacket(bits)

        val packets: List<Packet> = extractPackets(p)

        return packets.sumOf { it.version.toInt() }
    }

    private fun extractPackets(p: Packet): List<Packet> {
        if (p.subPackets.isEmpty()) {
            return listOf(p)
        }

        return listOf(p) + p.subPackets.map { extractPackets(it) }.flatten()
    }

    @Test
    fun part2() {
        assertEquals(3UL, evalPacket(parsePacket(parseHexString("C200B40A82")).first))
        assertEquals(54UL, evalPacket(parsePacket(parseHexString("04005AC33890")).first))
        assertEquals(7UL, evalPacket(parsePacket(parseHexString("880086C3E88112")).first))
        assertEquals(9UL, evalPacket(parsePacket(parseHexString("CE00C43D881120")).first))
        assertEquals(1UL, evalPacket(parsePacket(parseHexString("D8005AC2A8F0")).first))
        assertEquals(0UL, evalPacket(parsePacket(parseHexString("F600BC2D8F")).first))
        assertEquals(0UL, evalPacket(parsePacket(parseHexString("9C005AC2F8F0")).first))
        assertEquals(1UL, evalPacket(parsePacket(parseHexString("9C0141080250320F1802104A08")).first))
        assertEquals(1114600142730UL, runPart2("aoc-2021-16.txt"))
    }

    private fun runPart2(path: String): ULong {
        val hex = File(ClassLoader.getSystemResource(path).file).readLines().joinToString("")
        val bits = parseHexString(hex)
        val (p, _) = parsePacket(bits)

        return evalPacket(p)
    }

    private fun evalPacket(p: Packet): ULong {
        return when (p.type) {
            0UL -> p.subPackets.sumOf { evalPacket(it) }
            1UL -> p.subPackets.fold(1u) { acc, i -> acc * evalPacket(i) }
            2UL -> p.subPackets.minOf { evalPacket(it) }
            3UL -> p.subPackets.maxOf { evalPacket(it) }
            4UL -> p.value!!
            5UL -> if (evalPacket(p.subPackets[0]) > evalPacket(p.subPackets[1])) 1u else 0u
            6UL -> if (evalPacket(p.subPackets[0]) < evalPacket(p.subPackets[1])) 1u else 0u
            7UL -> if (evalPacket(p.subPackets[0]) == evalPacket(p.subPackets[1])) 1u else 0u
            else -> throw RuntimeException("Unsupported packet type: ${p.type}")
        }
    }

    @Test
    fun shouldParseTypeOneOperator() {
        assertEquals(
            Pair(
                Packet(
                    7u, 3u, null, listOf(
                        Packet(2u, 4u, 1u, listOf()),
                        Packet(4u, 4u, 2u, listOf()),
                        Packet(1u, 4u, 3u, listOf()),
                    )
                ),
                "00000"
            ),
            parsePacket(parseHexString("EE00D40C823060"))
        )
    }

    @Test
    fun shouldParseTypeZeroOperator() {
        assertEquals(
            Pair(
                Packet(
                    1u, 6u, null, listOf(
                        Packet(6u, 4u, 10u, listOf()),
                        Packet(2u, 4u, 20u, listOf()),
                    )
                ),
                "0000000"
            ),
            parsePacket(parseHexString("38006F45291200"))
        )
    }


    @Test
    fun shouldParseLiteral() {
        assertEquals(
            Pair(
                Packet(6u, 4u, 2021u, listOf()),
                "000"
            ),
            parsePacket(parseHexString("D2FE28"))
        )
    }

    private fun parsePacket(bits: String): Pair<Packet, String> {
        val version = bitsToInt(bits.slice(0 until 3))

        return when (val type = bitsToInt(bits.slice(3 until 6))) {
            4UL -> {
                val (v, remaining) = parsesLiteralPacket(bits.slice(6 until bits.length))
                Pair(Packet(version, type, v, listOf()), remaining)
            }
            else -> {
                val (ps, remaining) = parseOperator(bits.slice(6 until bits.length))
                Pair(Packet(version, type, null, ps), remaining)
            }
        }
    }

    private fun parseOperator(bits: String): Pair<List<Packet>, String> {
        return when (val lengthType = bits[0]) {
            '0' -> parseTypeZeroOperator(bits.slice(1 until bits.length))
            '1' -> parseTypeOneOperator(bits.slice(1 until bits.length))
            else -> throw RuntimeException("Invalid length type bit $lengthType")
        }
    }

    private fun parseTypeZeroOperator(bits: String): Pair<List<Packet>, String> {
        val subPacketSize = bitsToInt(bits.slice(0 until 15)).toInt()
        var subPacketBits = bits.slice(15 until 15 + subPacketSize)
        val packets = mutableListOf<Packet>()

        while (subPacketBits.any { it != '0' }) {
            val (packet, remaining) = parsePacket(subPacketBits)
            subPacketBits = remaining
            packets.add(packet)
        }

        return Pair(packets, bits.slice((subPacketSize + 15) until bits.length))
    }

    private fun parseTypeOneOperator(bits: String): Pair<List<Packet>, String> {
        val packetCount = bitsToInt(bits.slice(0 until 11)).toInt()
        val packets = mutableListOf<Packet>()
        var subPacketBits = bits.slice(11 until bits.length)

        (1..packetCount).forEach { _ ->
            val (packet, remaining) = parsePacket(subPacketBits)
            subPacketBits = remaining
            packets.add(packet)
        }


        return Pair(packets, subPacketBits)
    }

    private fun parsesLiteralPacket(bits: String): Pair<ULong, String> {
        var consumedBits = 0
        var valueBits = ""

        var wasLast = false

        while (!wasLast) {
            val chunk = bits.slice(consumedBits until consumedBits + 5)
            consumedBits += 5

            if (chunk[0] == '0') {
                wasLast = true
            }

            valueBits += chunk.slice(1..4)
        }

        return Pair(bitsToInt(valueBits), bits.slice(consumedBits until bits.length))
    }

    data class Packet(
        val version: ULong,
        val type: ULong,
        val value: ULong?,
        val subPackets: List<Packet>
    )

    private fun parseHexString(hex: String): String {
        return hex.map { hexToBits(it) }.joinToString("")
    }

    private fun hexToBits(c: Char): String {
        return when (c) {
            '0' -> "0000"
            '1' -> "0001"
            '2' -> "0010"
            '3' -> "0011"
            '4' -> "0100"
            '5' -> "0101"
            '6' -> "0110"
            '7' -> "0111"
            '8' -> "1000"
            '9' -> "1001"
            'A' -> "1010"
            'B' -> "1011"
            'C' -> "1100"
            'D' -> "1101"
            'E' -> "1110"
            'F' -> "1111"
            else -> throw RuntimeException("Invalid hex char: $c")
        }
    }

    @Test
    fun shouldBitToInt() {
        assertEquals(15UL, bitsToInt("1111"))
        assertEquals(0UL, bitsToInt("0000"))
        assertEquals(1UL, bitsToInt("0001"))
        assertEquals(9UL, bitsToInt("1001"))
        assertEquals(2021UL, bitsToInt("011111100101"))
    }

    private fun bitsToInt(bits: String): ULong {
        var result = 0UL

        bits.forEach { bit ->
            result = when (bit) {
                '0' -> (result shl 1)
                '1' -> (result shl 1) or 0x00000001UL
                else -> throw RuntimeException("Invalid bit char $bit")
            }
        }

        return result
    }
}



