package seamcarving

import java.awt.Color

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.sqrt

data class Pixel(val w:Int, val h: Int)

class Weight {
    var energy = 255.0
    var weight = 0.0
}

class Carv(_image: BufferedImage) {
    var image = _image
    var pixels = mutableMapOf<Pixel, Weight>()
    val height get() =  image.height
    val width get() =  image.width

    fun setEnergy() {
        pixels.clear()
        for (y in 0 until height)
            for (x in 0 until width) {
                val left = if (x == 0) 0 else if (x == width - 1) width - 3 else x - 1
                val right = if (x == width - 1) x else if (x == 0) 2 else x + 1
                val down = if (y == height - 1) y else if (y == 0) 2 else y + 1
                val up = if (y == 0) 0 else if (y == height - 1) height - 3 else y - 1
                val colorL = Color(image.getRGB(left, y))
                val colorR = Color(image.getRGB(right, y))
                val colorU = Color(image.getRGB(x, up))
                val colorD = Color(image.getRGB(x, down))
                val energy = sqrt(
                    (colorR.blue - colorL.blue).toDouble().pow(2) + (colorR.green - colorL.green).toDouble().pow(2) +
                            (colorR.red - colorL.red).toDouble().pow(2) + (colorD.blue - colorU.blue).toDouble()
                        .pow(2) +
                            (colorD.green - colorU.green).toDouble().pow(2) +
                            (colorD.red - colorU.red).toDouble().pow(2)
                )
                pixels[Pixel(x, y)] = Weight()
                pixels[Pixel(x, y)]?.energy = energy
            }
    }

    fun setWeight() {
        (0 until width).forEach { pixels[Pixel(it, 0)]?.weight = pixels[Pixel(it, 0)]?.energy!! }
        for (h in 1 until height) {
            for(w in 0 until width){
                pixels[Pixel(w, h)]?.weight = pixels[Pixel(w, h)]?.energy!! +
                        (w - 1..w + 1).filter { it in 0 until width }.minOf { pixels[Pixel(it, h-1)]?.weight!! }
            }
        }
    }

    fun findSeam(): MutableList<Pixel> {
        val seam = mutableListOf<Pixel>()
        var weightMin = (0 until width).map { Pixel(it, height-1) }.minByOrNull { pixels[it]?.weight!! }!!
        seam.add(weightMin)
        for (h in height - 1 downTo 1) {
            val w = weightMin.w
            val pixs = (w - 1..w + 1).filter { it in 0 until width }.map { Pixel(it, h-1) }
            weightMin = pixs.minByOrNull { pixels[it]?.weight!! }!!
            seam.add(weightMin)
        }
        return seam
    }

    fun carv(times: Int) {
        repeat(times) {
            setEnergy()
            setWeight()
            val seam = findSeam()
            val reduced = BufferedImage(width - 1, height, BufferedImage.TYPE_INT_RGB)
            for (y in 0 until height) {
                var w = 0
                for (x in 0 until width) {
                    if (Pixel(x,y) in seam) continue
                    reduced.setRGB(w, y, image.getRGB(x,y))
                    w++
                }
            }
            image = reduced
        }
    }

    fun rotate() {
        val rotated = BufferedImage(height, width, BufferedImage.TYPE_INT_RGB)
        for (y in 0 until height)
            for (x in 0 until width) {
                rotated.setRGB(y, x, image.getRGB(x, y))
            }
        image = rotated
    }
}

fun main( args:Array<String> ) {
    val inputFile = File(args[args.indexOf("-in") + 1])
    val outputFile = File(args[args.indexOf("-out") + 1])
    val vertiNumber = args[args.indexOf("-width") + 1].toInt()
    val horiNumber = args[args.indexOf("-height") + 1].toInt()
    val image = ImageIO.read(inputFile)
    val work = Carv(image)
    work.carv(vertiNumber)
    work.rotate()
    work.carv(horiNumber)
    work.rotate()
    ImageIO.write(work.image, "png", outputFile)
}