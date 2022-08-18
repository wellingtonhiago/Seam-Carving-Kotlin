package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.sqrt

fun main(args: Array<String>) {
    val image = ImageIO.read(File(args[args.indexOf("-in") + 1]))

    val energies = mutableListOf<Double>()
    for (x in 0 until image.width) {
        for (y in 0 until image.height) {
            energies.add(image.getEnergy(x, y))
        }
    }
    val maxEnergy = energies.maxOf { it }

    val newImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
    for (x in 0 until image.width) {
        for (y in 0 until image.height) {
            val intensity = (255.0 * image.getEnergy(x, y) / maxEnergy).toInt()
            newImage.setRGB(x, y, Color(intensity, intensity, intensity).rgb)
        }
    }

    ImageIO.write(newImage, "png", File(args[args.indexOf("-out") + 1]))
}

fun BufferedImage.getEnergy(x: Int, y: Int): Double {
    val normalizedX = x.coerceAtLeast(1).coerceAtMost(this.width - 2)
    val normalizedY = y.coerceAtLeast(1).coerceAtMost(this.height - 2)
    val u = Color(this.getRGB(x, normalizedY - 1))
    val r = Color(this.getRGB(normalizedX + 1, y))
    val d = Color(this.getRGB(x, normalizedY + 1))
    val l = Color(this.getRGB(normalizedX - 1, y))
    val xGradient = (.0 + l.red - r.red).pow(2) + (.0 + l.green - r.green).pow(2) + (.0 + l.blue - r.blue).pow(2)
    val yGradient = (.0 + u.red - d.red).pow(2) + (.0 + u.green - d.green).pow(2) + (.0 + u.blue - d.blue).pow(2)
    return sqrt(xGradient + yGradient)
}
