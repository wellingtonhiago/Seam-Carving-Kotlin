package seamcarving

import java.awt.Color
import java.io.File
import javax.imageio.ImageIO

fun main(args:Array<String>) {
    val inFile = File(args[1])
    val outFile = File(args[3])
    val inImage = ImageIO.read(inFile)

    for (x in 0 until inImage.width) {
        for (y in 0 until inImage.height) {
            val color = Color(inImage.getRGB(x, y))
            val newColor = Color(255 - color.red, 255 - color.green, 255 - color.blue)
            inImage.setRGB(x, y, newColor.rgb)
        }
    }

    ImageIO.write(inImage, "png", outFile)
}
