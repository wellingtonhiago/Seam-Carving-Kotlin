package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main() {
    println("Enter rectangle width:")
    val width = readln().toInt()
    println("Enter rectangle height:")
    val height = readln().toInt()
    println("Enter output image name:")
    val filename = File(readln())

    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val graphics = image.graphics
    graphics.color = Color.RED
    graphics.drawLine(0, 0, width - 1, height - 1)
    graphics.drawLine(0, height - 1, width - 1, 0)

    ImageIO.write(image, "png", filename)
}
