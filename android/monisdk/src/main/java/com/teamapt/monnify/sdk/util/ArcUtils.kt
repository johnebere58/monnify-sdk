package com.teamapt.monnify.sdk.util

import android.graphics.Path
import android.graphics.PointF

import java.lang.Math.sqrt

internal object ArcUtils {

    fun addBezierArcToPath(
        path: Path, center: PointF,
        start: PointF, end: PointF, moveToStart: Boolean
    ) {
        if (moveToStart) {
            path.moveTo(start.x, start.y)
        }
        if (start == end) {
            return
        }

        val ax = (start.x - center.x).toDouble()
        val ay = (start.y - center.y).toDouble()
        val bx = (end.x - center.x).toDouble()
        val by = (end.y - center.y).toDouble()
        val q1 = ax * ax + ay * ay
        val q2 = q1 + ax * bx + ay * by
        val k2 = 4.0 / 3.0 * (sqrt(2.0 * q1 * q2) - q2) / (ax * by - ay * bx)
        val x2 = (center.x + ax - k2 * ay).toFloat()
        val y2 = (center.y.toDouble() + ay + k2 * ax).toFloat()
        val x3 = (center.x.toDouble() + bx + k2 * by).toFloat()
        val y3 = (center.y + by - k2 * bx).toFloat()

        path.cubicTo(x2, y2, x3, y3, end.x, end.y)
    }

}