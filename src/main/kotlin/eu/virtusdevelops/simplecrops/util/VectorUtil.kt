/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Slikey
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package eu.virtusdevelops.simplecrops.util

import eu.virtusdevelops.virtuscore.utils.MathL
import org.bukkit.Location
import org.bukkit.util.Vector
import kotlin.math.abs

object VectorUtils {
    /**
     * Rotates a vector around the X axis at an angle
     *
     * @param v Starting vector
     * @param angle How much to rotate
     * @return The starting vector rotated
     */
    fun rotateAroundAxisX(v: Vector, angle: Double): Vector {
        val y: Double
        val z: Double
        val cos: Double = MathL.cos(angle)
        val sin: Double = MathL.sin(angle)
        y = v.y * cos - v.z * sin
        z = v.y * sin + v.z * cos
        return v.setY(y).setZ(z)
    }

    /**
     * Rotates a vector around the Y axis at an angle
     *
     * @param v Starting vector
     * @param angle How much to rotate
     * @return The starting vector rotated
     */
    fun rotateAroundAxisY(v: Vector, angle: Double): Vector {
        val x: Double
        val z: Double
        val cos: Double
        val sin: Double
        cos = MathL.cos(angle)
        sin = MathL.sin(angle)
        x = v.x * cos + v.z * sin
        z = v.x * -sin + v.z * cos
        return v.setX(x).setZ(z)
    }

    /**
     * Rotates a vector around the Z axis at an angle
     *
     * @param v Starting vector
     * @param angle How much to rotate
     * @return The starting vector rotated
     */
    fun rotateAroundAxisZ(v: Vector, angle: Double): Vector {
        val x: Double
        val y: Double
        val cos: Double
        val sin: Double
        cos = MathL.cos(angle)
        sin = MathL.sin(angle)
        x = v.x * cos - v.y * sin
        y = v.x * sin + v.y * cos
        return v.setX(x).setY(y)
    }

    /**
     * Rotates a vector around the X, Y, and Z axes
     *
     * @param v The starting vector
     * @param angleX The change angle on X
     * @param angleY The change angle on Y
     * @param angleZ The change angle on Z
     * @return The starting vector rotated
     */
    fun rotateVector(v: Vector, angleX: Double, angleY: Double, angleZ: Double): Vector {
        rotateAroundAxisX(v, angleX)
        rotateAroundAxisY(v, angleY)
        rotateAroundAxisZ(v, angleZ)
        return v
    }

    /**
     * Rotate a vector about a location using that location's direction
     *
     * @param v The starting vector
     * @param location The location to rotate around
     * @return The starting vector rotated
     */
    fun rotateVector(v: Vector, location: Location): Vector {
        return rotateVector(v, location.yaw, location.pitch)
    }

    /**
     * This handles non-unit vectors, with yaw and pitch instead of X,Y,Z angles.
     *
     * Thanks to SexyToad!
     *
     * @param v The starting vector
     * @param yawDegrees The yaw offset in degrees
     * @param pitchDegrees The pitch offset in degrees
     * @return The starting vector rotated
     */
    fun rotateVector(v: Vector, yawDegrees: Float, pitchDegrees: Float): Vector {
        val yaw = Math.toRadians((-1 * (yawDegrees + 90)).toDouble())
        val pitch = Math.toRadians(-pitchDegrees.toDouble())
        val cosYaw = MathL.cos(yaw)
        val cosPitch = MathL.cos(pitch)
        val sinYaw = MathL.sin(yaw)
        val sinPitch = MathL.sin(pitch)
        var initialX: Double
        val initialY: Double
        val initialZ: Double
        var x: Double
        val y: Double
        val z: Double

        // Z_Axis rotation (Pitch)
        initialX = v.x
        initialY = v.y
        x = initialX * cosPitch - initialY * sinPitch
        y = initialX * sinPitch + initialY * cosPitch

        // Y_Axis rotation (Yaw)
        initialZ = v.z
        initialX = x
        z = initialZ * cosYaw - initialX * sinYaw
        x = initialZ * sinYaw + initialX * cosYaw
        return Vector(x, y, z)
    }

    /**
     * Gets the angle toward the X axis
     *
     * @param vector The vector to check
     * @return The angle toward the X axis
     */
    fun angleToXAxis(vector: Vector): Double {
        return Math.atan2(vector.x, vector.y)
    }


    fun calculateOffSet(first: Vector, second: Vector, third: Vector): Vector{
        val y = third.y
        // x - z offsets
        if(first.y > second.y){
            third.subtract(first)
        }else{
            third.subtract(second)
        }
        // y offset
        if(first.y <= y){
            third.y = y - first.y
        }else if(second.y <= y){
            third.y = y - second.y
        }else{
            third.y = 0.0
        }

        // get positive numbers since coordinates can be negative.
        third.x = abs(third.x)
        third.y = abs(third.y)
        third.z = abs(third.z)
        return third
    }
}