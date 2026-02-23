package app.launch0.helper

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
import android.os.Build
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Random

object WallpaperGenerator {

    /**
     * Generates a daily seed based on the current date.
     * Same day always produces the same seed, ensuring consistency across worker runs.
     */
    fun getTodaySeed(): Long {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val day = cal.get(Calendar.DAY_OF_YEAR)
        return (year * 1000L + day)
    }

    /**
     * Generates and sets an abstract wallpaper based on the given seed and theme.
     */
    suspend fun generateAndSetWallpaper(context: Context, seed: Long, isDark: Boolean): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val (width, height) = getScreenDimensions(context)
                val bitmap = generateAbstractWallpaper(width, height, seed, isDark)

                val wallpaperManager = WallpaperManager.getInstance(context)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    wallpaperManager.setBitmap(bitmap, null, false, WallpaperManager.FLAG_SYSTEM)
                    wallpaperManager.setBitmap(bitmap, null, false, WallpaperManager.FLAG_LOCK)
                } else {
                    wallpaperManager.setBitmap(bitmap)
                }
                bitmap.recycle()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun generateAbstractWallpaper(width: Int, height: Int, seed: Long, isDark: Boolean): Bitmap {
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)
        val random = Random(seed)

        // Pick a pattern type based on seed
        val patternType = random.nextInt(5)

        when (patternType) {
            0 -> drawMultiGradient(canvas, width, height, random, isDark)
            1 -> drawConcentricCircles(canvas, width, height, random, isDark)
            2 -> drawDiagonalStripes(canvas, width, height, random, isDark)
            3 -> drawMeshGradient(canvas, width, height, random, isDark)
            4 -> drawGeometricShapes(canvas, width, height, random, isDark)
        }

        return bitmap
    }

    /**
     * Multi-layered linear gradients at varying angles.
     */
    private fun drawMultiGradient(canvas: Canvas, w: Int, h: Int, random: Random, isDark: Boolean) {
        val paint = Paint()
        val baseColor = randomBaseColor(random, isDark)
        val accentColor = randomAccentColor(random, isDark)
        val midColor = blendColors(baseColor, accentColor, 0.5f)

        // Background gradient
        paint.shader = LinearGradient(
            0f, 0f, w.toFloat(), h.toFloat(),
            baseColor, midColor,
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)

        // Overlay gradient at different angle
        val alpha = 100 + random.nextInt(80)
        val overlayColor1 = withAlpha(accentColor, alpha)
        val overlayColor2 = withAlpha(randomAccentColor(random, isDark), alpha)

        paint.shader = LinearGradient(
            w.toFloat(), 0f, 0f, h.toFloat(),
            overlayColor1, overlayColor2,
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)

        // Subtle radial highlight
        val cx = w * (0.2f + random.nextFloat() * 0.6f)
        val cy = h * (0.2f + random.nextFloat() * 0.6f)
        val radius = maxOf(w, h) * (0.3f + random.nextFloat() * 0.4f)
        val highlightColor = withAlpha(if (isDark) Color.WHITE else accentColor, 30 + random.nextInt(40))

        paint.shader = RadialGradient(
            cx, cy, radius,
            highlightColor, Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
    }

    /**
     * Concentric circles / rings radiating outward.
     */
    private fun drawConcentricCircles(canvas: Canvas, w: Int, h: Int, random: Random, isDark: Boolean) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val baseColor = randomBaseColor(random, isDark)
        val accentColor = randomAccentColor(random, isDark)

        // Fill background
        canvas.drawColor(baseColor)

        val cx = w * (0.3f + random.nextFloat() * 0.4f)
        val cy = h * (0.3f + random.nextFloat() * 0.4f)
        val maxRadius = maxOf(w, h) * 1.2f
        val ringCount = 8 + random.nextInt(12)

        for (i in ringCount downTo 0) {
            val fraction = i.toFloat() / ringCount
            val radius = maxRadius * fraction
            val color = blendColors(baseColor, accentColor, fraction)
            paint.color = color
            paint.style = Paint.Style.FILL
            canvas.drawCircle(cx, cy, radius, paint)
        }

        // Add a secondary radial glow
        val cx2 = w * (0.1f + random.nextFloat() * 0.8f)
        val cy2 = h * (0.1f + random.nextFloat() * 0.8f)
        val glowColor = withAlpha(randomAccentColor(random, isDark), 40 + random.nextInt(50))
        paint.shader = RadialGradient(
            cx2, cy2, maxOf(w, h) * 0.6f,
            glowColor, Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        paint.shader = null
    }

    /**
     * Diagonal stripes with gradient fills.
     */
    private fun drawDiagonalStripes(canvas: Canvas, w: Int, h: Int, random: Random, isDark: Boolean) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val baseColor = randomBaseColor(random, isDark)
        val accentColor = randomAccentColor(random, isDark)

        // Background gradient
        paint.shader = LinearGradient(
            0f, 0f, w.toFloat(), h.toFloat(),
            baseColor, blendColors(baseColor, accentColor, 0.3f),
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        paint.shader = null

        // Draw diagonal stripes
        val stripeCount = 10 + random.nextInt(15)
        val totalDiag = w + h
        val stripeWidth = totalDiag.toFloat() / stripeCount

        for (i in 0..stripeCount) {
            val offset = i * stripeWidth
            val alpha = 20 + random.nextInt(60)
            val stripeColor = if (i % 2 == 0) {
                withAlpha(accentColor, alpha)
            } else {
                withAlpha(blendColors(baseColor, accentColor, 0.7f), alpha)
            }
            paint.color = stripeColor
            paint.style = Paint.Style.FILL

            val path = Path()
            val sw = stripeWidth * 0.7f
            path.moveTo(offset - sw, 0f)
            path.lineTo(offset, 0f)
            path.lineTo(offset - h, h.toFloat())
            path.lineTo(offset - h - sw, h.toFloat())
            path.close()
            canvas.drawPath(path, paint)
        }
    }

    /**
     * Mesh-style gradient using overlapping radial gradients.
     */
    private fun drawMeshGradient(canvas: Canvas, w: Int, h: Int, random: Random, isDark: Boolean) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val baseColor = randomBaseColor(random, isDark)

        // Fill background
        canvas.drawColor(baseColor)

        // Place several radial gradient blobs
        val blobCount = 3 + random.nextInt(4)
        for (i in 0 until blobCount) {
            val cx = random.nextFloat() * w
            val cy = random.nextFloat() * h
            val radius = maxOf(w, h) * (0.3f + random.nextFloat() * 0.5f)
            val blobColor = withAlpha(randomAccentColor(random, isDark), 60 + random.nextInt(100))

            paint.shader = RadialGradient(
                cx, cy, radius,
                blobColor, Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            )
            canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        }
        paint.shader = null
    }

    /**
     * Overlapping geometric shapes (triangles and circles) on a gradient background.
     */
    private fun drawGeometricShapes(canvas: Canvas, w: Int, h: Int, random: Random, isDark: Boolean) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val baseColor = randomBaseColor(random, isDark)
        val accentColor = randomAccentColor(random, isDark)

        // Background gradient
        paint.shader = LinearGradient(
            0f, 0f, 0f, h.toFloat(),
            baseColor, blendColors(baseColor, accentColor, 0.4f),
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        paint.shader = null

        // Draw random shapes
        val shapeCount = 5 + random.nextInt(8)
        for (i in 0 until shapeCount) {
            val alpha = 15 + random.nextInt(55)
            val color = withAlpha(
                if (random.nextBoolean()) accentColor else randomAccentColor(random, isDark),
                alpha
            )
            paint.color = color
            paint.style = Paint.Style.FILL

            if (random.nextBoolean()) {
                // Circle
                val cx = random.nextFloat() * w
                val cy = random.nextFloat() * h
                val radius = minOf(w, h) * (0.05f + random.nextFloat() * 0.3f)
                canvas.drawCircle(cx, cy, radius, paint)
            } else {
                // Triangle
                val path = Path()
                val x1 = random.nextFloat() * w
                val y1 = random.nextFloat() * h
                val size = minOf(w, h) * (0.1f + random.nextFloat() * 0.35f)
                path.moveTo(x1, y1)
                path.lineTo(x1 + size, y1 + size * (0.5f + random.nextFloat()))
                path.lineTo(x1 - size * 0.5f, y1 + size)
                path.close()
                canvas.drawPath(path, paint)
            }
        }
    }

    // --- Color utilities ---

    private fun randomBaseColor(random: Random, isDark: Boolean): Int {
        return if (isDark) {
            // Dark theme: deep, dark tones
            val h = random.nextFloat() * 360f
            val s = 0.3f + random.nextFloat() * 0.5f
            val v = 0.05f + random.nextFloat() * 0.15f
            Color.HSVToColor(floatArrayOf(h, s, v))
        } else {
            // Light theme: soft, light tones
            val h = random.nextFloat() * 360f
            val s = 0.05f + random.nextFloat() * 0.2f
            val v = 0.85f + random.nextFloat() * 0.15f
            Color.HSVToColor(floatArrayOf(h, s, v))
        }
    }

    private fun randomAccentColor(random: Random, isDark: Boolean): Int {
        return if (isDark) {
            val h = random.nextFloat() * 360f
            val s = 0.4f + random.nextFloat() * 0.5f
            val v = 0.15f + random.nextFloat() * 0.35f
            Color.HSVToColor(floatArrayOf(h, s, v))
        } else {
            val h = random.nextFloat() * 360f
            val s = 0.15f + random.nextFloat() * 0.35f
            val v = 0.7f + random.nextFloat() * 0.3f
            Color.HSVToColor(floatArrayOf(h, s, v))
        }
    }

    private fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
        val inverseRatio = 1f - ratio
        val r = Color.red(color1) * inverseRatio + Color.red(color2) * ratio
        val g = Color.green(color1) * inverseRatio + Color.green(color2) * ratio
        val b = Color.blue(color1) * inverseRatio + Color.blue(color2) * ratio
        return Color.rgb(r.toInt(), g.toInt(), b.toInt())
    }

    private fun withAlpha(color: Int, alpha: Int): Int {
        return Color.argb(
            alpha.coerceIn(0, 255),
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
    }
}
