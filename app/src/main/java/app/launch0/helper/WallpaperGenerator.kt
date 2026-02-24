package app.launch0.helper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import androidx.core.graphics.createBitmap
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Generates random abstract wallpapers using gradients and mathematical patterns.
 * Uses a date-based seed so the wallpaper changes daily but stays consistent within a day.
 */
object WallpaperGenerator {

    private val darkPalette = intArrayOf(
        0xFF1e3a5f.toInt(), // steel blue
        0xFF4a1942.toInt(), // plum
        0xFF0f5257.toInt(), // deep teal
        0xFF7b2d8e.toInt(), // vivid purple
        0xFF1f4037.toInt(), // dark emerald
        0xFF6b2fa0.toInt(), // bright violet
        0xFF0d4f8b.toInt(), // ocean blue
        0xFF8b1a4a.toInt(), // raspberry
        0xFF2e4057.toInt(), // blue-grey
        0xFFb02a5e.toInt(), // magenta-rose
        0xFF1a6b4f.toInt(), // jewel green
        0xFF4834d4.toInt(), // indigo
        0xFFc44569.toInt(), // coral-pink
        0xFF0c7b93.toInt(), // bright teal
        0xFF8854d0.toInt(), // medium purple
        0xFF2d6a4f.toInt(), // forest green
    )

    private val lightPalette = intArrayOf(
        0xFF7eb8da.toInt(), // sky blue
        0xFFe8a87c.toInt(), // peach
        0xFFd4a5e5.toInt(), // orchid
        0xFF85c7a2.toInt(), // mint green
        0xFFf0a1b7.toInt(), // rose pink
        0xFF8fd3e2.toInt(), // aqua
        0xFFf5c16c.toInt(), // warm amber
        0xFFb5a8d5.toInt(), // soft violet
        0xFFf09ea7.toInt(), // salmon
        0xFF82c4b5.toInt(), // sea foam
        0xFFe0a3d0.toInt(), // mauve
        0xFFa8d5ba.toInt(), // sage green
        0xFFf4b886.toInt(), // tangerine
        0xFF92b4d8.toInt(), // periwinkle
        0xFFe8c170.toInt(), // gold
        0xFFc49bc4.toInt(), // plum pink
    )

    fun generate(width: Int, height: Int, isDark: Boolean, seed: Long): android.graphics.Bitmap {
        val random = Random(seed)
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)

        canvas.drawColor(if (isDark) 0xFF0a0a14.toInt() else 0xFFF5F0EB.toInt())

        val palette = if (isDark) darkPalette else lightPalette
        val patternType = random.nextInt(5)

        when (patternType) {
            0 -> drawLayeredLinearGradients(canvas, width, height, palette, random)
            1 -> drawRadialOrbs(canvas, width, height, palette, isDark, random)
            2 -> drawDiagonalFlow(canvas, width, height, palette, random)
            3 -> drawAuroraWaves(canvas, width, height, palette, isDark, random)
            4 -> drawMeshGlow(canvas, width, height, palette, isDark, random)
        }

        return bitmap
    }

    private fun pickColors(palette: IntArray, count: Int, random: Random): IntArray {
        val indices = palette.indices.shuffled(random)
        return IntArray(count) { palette[indices[it]] }
    }

    /**
     * Pattern 0: Multiple overlapping linear gradients at random angles.
     */
    private fun drawLayeredLinearGradients(
        canvas: Canvas, w: Int, h: Int, palette: IntArray, random: Random
    ) {
        val layerCount = 4 + random.nextInt(3)
        for (i in 0 until layerCount) {
            val colorCount = 2 + random.nextInt(2)
            val colors = pickColors(palette, colorCount, random)
            val angle = random.nextDouble() * 2 * Math.PI
            val cx = w / 2f
            val cy = h / 2f
            val len = maxOf(w, h).toFloat()
            val x0 = cx - (len * cos(angle) / 2).toFloat()
            val y0 = cy - (len * sin(angle) / 2).toFloat()
            val x1 = cx + (len * cos(angle) / 2).toFloat()
            val y1 = cy + (len * sin(angle) / 2).toFloat()

            val shader = LinearGradient(x0, y0, x1, y1, colors, null, Shader.TileMode.CLAMP)
            val paint = Paint().apply {
                this.shader = shader
                alpha = 160 + random.nextInt(80)
            }
            canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        }
    }

    /**
     * Pattern 1: Vivid glowing radial circles at random positions.
     */
    private fun drawRadialOrbs(
        canvas: Canvas, w: Int, h: Int, palette: IntArray, isDark: Boolean, random: Random
    ) {
        // First lay down a base gradient so it's not just plain background
        val baseColors = pickColors(palette, 2, random)
        val baseShader = LinearGradient(
            0f, 0f, w.toFloat(), h.toFloat(),
            baseColors, null, Shader.TileMode.CLAMP
        )
        val basePaint = Paint().apply {
            shader = baseShader
            alpha = if (isDark) 100 else 120
        }
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), basePaint)

        val orbCount = 5 + random.nextInt(4)
        for (i in 0 until orbCount) {
            val cx = random.nextFloat() * w
            val cy = random.nextFloat() * h
            val radius = (minOf(w, h) * (0.3f + random.nextFloat() * 0.5f))
            val color = pickColors(palette, 1, random)[0]
            val bgColor = if (isDark) 0x000A0A14 else 0x00F5F0EB

            val shader = RadialGradient(
                cx, cy, radius,
                intArrayOf(color, blendColor(color, bgColor, 0.4f), bgColor),
                floatArrayOf(0f, 0.55f, 1f),
                Shader.TileMode.CLAMP
            )
            val paint = Paint().apply {
                this.shader = shader
                alpha = 180 + random.nextInt(60)
            }
            canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        }
    }

    /**
     * Pattern 2: Bold diagonal color flow using offset linear gradients.
     */
    private fun drawDiagonalFlow(
        canvas: Canvas, w: Int, h: Int, palette: IntArray, random: Random
    ) {
        val colors = pickColors(palette, 4 + random.nextInt(3), random)
        val shader = LinearGradient(
            0f, 0f, w.toFloat(), h.toFloat(),
            colors, null, Shader.TileMode.CLAMP
        )
        val paint = Paint().apply { this.shader = shader }
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)

        // Stronger cross-gradient for richer color mixing
        val crossColors = pickColors(palette, 3 + random.nextInt(2), random)
        val crossShader = LinearGradient(
            w.toFloat(), 0f, 0f, h.toFloat(),
            crossColors, null, Shader.TileMode.CLAMP
        )
        val crossPaint = Paint().apply {
            this.shader = crossShader
            alpha = 130 + random.nextInt(60)
        }
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), crossPaint)

        // Third layer for even more mixing
        val thirdColors = pickColors(palette, 2, random)
        val angle = random.nextDouble() * 2 * Math.PI
        val len = maxOf(w, h).toFloat()
        val cx = w / 2f
        val cy = h / 2f
        val thirdShader = LinearGradient(
            cx - (len * cos(angle) / 2).toFloat(),
            cy - (len * sin(angle) / 2).toFloat(),
            cx + (len * cos(angle) / 2).toFloat(),
            cy + (len * sin(angle) / 2).toFloat(),
            thirdColors, null, Shader.TileMode.CLAMP
        )
        val thirdPaint = Paint().apply {
            this.shader = thirdShader
            alpha = 100 + random.nextInt(50)
        }
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), thirdPaint)
    }

    /**
     * Pattern 3: Vibrant horizontal color bands inspired by aurora borealis.
     */
    private fun drawAuroraWaves(
        canvas: Canvas, w: Int, h: Int, palette: IntArray, isDark: Boolean, random: Random
    ) {
        // Base gradient instead of flat color
        val bgColors = pickColors(palette, 2, random)
        val bgShader = LinearGradient(
            0f, 0f, 0f, h.toFloat(), bgColors, null, Shader.TileMode.CLAMP
        )
        val bgPaint = Paint().apply {
            shader = bgShader
            alpha = if (isDark) 80 else 100
        }
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bgPaint)

        val bandCount = 4 + random.nextInt(3)
        val baseColor = if (isDark) 0xFF0a0a14.toInt() else 0xFFF5F0EB.toInt()

        for (i in 0 until bandCount) {
            val color = pickColors(palette, 1, random)[0]
            val yCenter = h * (0.05f + random.nextFloat() * 0.9f)
            val bandHeight = h * (0.12f + random.nextFloat() * 0.22f)

            val shader = LinearGradient(
                0f, yCenter - bandHeight, 0f, yCenter + bandHeight,
                intArrayOf(baseColor, color, color, baseColor),
                floatArrayOf(0f, 0.3f, 0.7f, 1f),
                Shader.TileMode.CLAMP
            )
            val paint = Paint().apply {
                this.shader = shader
                alpha = 170 + random.nextInt(70)
            }
            canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)

            // Horizontal color shift overlay
            val hColor = pickColors(palette, 1, random)[0]
            val xOffset = random.nextFloat() * w
            val hShader = LinearGradient(
                xOffset - w * 0.5f, yCenter, xOffset + w * 0.5f, yCenter,
                intArrayOf(baseColor, hColor, baseColor),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
            val hPaint = Paint().apply {
                this.shader = hShader
                alpha = 100 + random.nextInt(60)
            }
            canvas.drawRect(0f, yCenter - bandHeight, w.toFloat(), yCenter + bandHeight, hPaint)
        }
    }

    /**
     * Pattern 4: Rich mesh of radial glows over a gradient base.
     */
    private fun drawMeshGlow(
        canvas: Canvas, w: Int, h: Int, palette: IntArray, isDark: Boolean, random: Random
    ) {
        // Base gradient
        val baseColors = pickColors(palette, 3, random)
        val baseAngle = random.nextDouble() * 2 * Math.PI
        val baseShader = LinearGradient(
            (w / 2 - w * cos(baseAngle) / 2).toFloat(),
            (h / 2 - h * sin(baseAngle) / 2).toFloat(),
            (w / 2 + w * cos(baseAngle) / 2).toFloat(),
            (h / 2 + h * sin(baseAngle) / 2).toFloat(),
            baseColors, null, Shader.TileMode.CLAMP
        )
        val basePaint = Paint().apply {
            shader = baseShader
            alpha = 220
        }
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), basePaint)

        // Scattered radial glows
        val glowCount = 6 + random.nextInt(5)
        val bgColor = if (isDark) 0xFF0a0a14.toInt() else 0xFFF5F0EB.toInt()
        for (i in 0 until glowCount) {
            val cx = random.nextFloat() * w
            val cy = random.nextFloat() * h
            val radius = minOf(w, h) * (0.25f + random.nextFloat() * 0.35f)
            val color = pickColors(palette, 1, random)[0]

            val shader = RadialGradient(
                cx, cy, radius,
                intArrayOf(color, blendColor(color, bgColor, 0.6f), bgColor),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
            val paint = Paint().apply {
                this.shader = shader
                alpha = 140 + random.nextInt(80)
            }
            canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        }
    }

    private fun blendColor(color1: Int, color2: Int, ratio: Float): Int {
        val r = ((Color.red(color1) * (1 - ratio) + Color.red(color2) * ratio)).toInt()
        val g = ((Color.green(color1) * (1 - ratio) + Color.green(color2) * ratio)).toInt()
        val b = ((Color.blue(color1) * (1 - ratio) + Color.blue(color2) * ratio)).toInt()
        val a = ((Color.alpha(color1) * (1 - ratio) + Color.alpha(color2) * ratio)).toInt()
        return Color.argb(a, r, g, b)
    }
}
