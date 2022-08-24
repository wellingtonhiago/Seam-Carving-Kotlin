package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.sqrt

fun main(args: Array<String>) {
    val mainImage: BufferedImage = ImageIO.read(File(args[args.indexOf("-in") + 1]))
    val transposedImg = transpose(mainImage)
    drawVerticalSeam(transposedImg)
    val horizontalSeam = transpose(transposedImg)
    ImageIO.write(horizontalSeam, "png", File(args[args.indexOf("-out") + 1]))
}

fun transpose(image: BufferedImage): BufferedImage {
    val newImage = BufferedImage(image.height, image.width, BufferedImage.TYPE_INT_RGB)
    with (image) {
        for (x in 0 until width)
            for (y in 0 until height) {
                val color = image.getRGB(x, y)
                newImage.setRGB(y, x, color)
            }
    }
    return newImage
}

fun drawVerticalSeam(image: BufferedImage, color: Color = Color.red) {
    fun pow2(n: Int) = n * n
    with (image) {
        val energies = Array(height) { Array(width) { 0.0 } }
        for (y in 0 until height)
            for (x in 0 until width){
                val cx = x.coerceIn(1..width - 2)
                val cy = y.coerceIn(1..height - 2)
                val west = Color(getRGB(cx - 1, y))
                val east = Color(getRGB(cx + 1, y))
                val north = Color(getRGB(x, cy - 1))
                val south = Color(getRGB(x, cy + 1))
                val gradientX = pow2(west.red - east.red) + pow2(west.green - east.green) + pow2(west.blue - east.blue)
                val gradientY = pow2(north.red - south.red) + pow2(north.green - south.green) + pow2(north.blue - south.blue)
                energies[y][x] = sqrt((gradientX + gradientY).toDouble())
            }

        val weights = Array(height) { Array(width) { 0.0 } }
        weights[0] = energies[0].copyOf()
        for (y in 1 until height) {
            for (x in 0 until width) {
                var minimo = weights[y - 1][x]
                if (x > 0 && minimo > weights[y - 1][x - 1]) minimo = weights[y - 1][x - 1]
                if (x < width - 1 && minimo > weights[y - 1][x + 1]) minimo = weights[y - 1][x + 1]
                weights[y][x] = energies[y][x] + minimo
            }
        }
        var idx = 0
        for (x in 1 until width - 1) if (weights[height - 1][idx] > weights[height - 1][x]) idx = x
        setRGB(idx, height - 1, color.rgb)
        for (y in height - 2 downTo 0) {
            for (x in (idx - 1).coerceAtLeast(0)..(idx + 1).coerceAtMost(width - 1))
                if (weights[y + 1][idx] == energies[y + 1][idx] + weights[y][x]) idx = x
            setRGB(idx, y, color.rgb)
        }

    }
}