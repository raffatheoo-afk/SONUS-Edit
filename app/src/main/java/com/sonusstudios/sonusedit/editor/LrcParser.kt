package com.sonusstudios.sonusedit.editor

import kotlin.math.roundToLong

data class LrcLine(
    val timeMs: Long,
    val text: String,
)

object LrcParser {
    private val timestampRegex = Regex("\\[(\\d{1,3}):(\\d{2})(?:[.:](\\d{1,3}))?]")

    fun parse(raw: String): List<LrcLine> = buildList {
        raw.lineSequence().forEach { sourceLine ->
            val timestamps = timestampRegex.findAll(sourceLine).toList()
            if (timestamps.isEmpty()) return@forEach
            val text = sourceLine.replace(timestampRegex, "").trim()
            timestamps.forEach timestamp@ { match ->
                val minutes = match.groupValues[1].toLongOrNull() ?: return@timestamp
                val seconds = match.groupValues[2].toLongOrNull() ?: return@timestamp
                val fraction = match.groupValues[3]
                val millis = when (fraction.length) {
                    0 -> 0L
                    1 -> fraction.toLong() * 100L
                    2 -> fraction.toLong() * 10L
                    else -> fraction.take(3).padEnd(3, '0').toLong()
                }
                add(LrcLine(minutes * 60_000L + seconds * 1_000L + millis, text))
            }
        }
    }.sortedBy { it.timeMs }

    /**
     * Keeps only lyrics belonging to the selected audio segment and shifts timestamps
     * so the exported video starts at zero.
     */
    fun sliceAndShift(
        lines: List<LrcLine>,
        segmentStartMs: Long,
        segmentEndMs: Long,
        preRollMs: Long = 250L,
    ): List<LrcLine> {
        if (segmentEndMs <= segmentStartMs) return emptyList()
        val from = (segmentStartMs - preRollMs).coerceAtLeast(0L)
        return lines.asSequence()
            .filter { it.timeMs in from until segmentEndMs }
            .map { it.copy(timeMs = (it.timeMs - segmentStartMs).coerceAtLeast(0L)) }
            .toList()
    }

    fun toLrc(lines: List<LrcLine>): String = lines.joinToString("\n") { line ->
        val minutes = line.timeMs / 60_000L
        val seconds = (line.timeMs % 60_000L) / 1_000L
        val centiseconds = ((line.timeMs % 1_000L) / 10.0).roundToLong().coerceAtMost(99)
        "[%02d:%02d.%02d] %s".format(minutes, seconds, centiseconds, line.text)
    }
}
