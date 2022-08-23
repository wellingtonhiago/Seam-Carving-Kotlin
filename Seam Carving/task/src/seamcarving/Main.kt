package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.sqrt
import kotlin.math.pow


data class SeamEnergy(val energy: Double, val xCoordinateInPreviousRow: Int = -1)

fun main(args: Array<String>) {
    val image = ImageIO.read(File(args[args.indexOf("-in") + 1]))

    val energies = mutableListOf<List<Double>>()
    for (y in 0 until image.height) {
        val energyRow = mutableListOf<Double>()
        for (x in 0 until image.width) {
            energyRow.add(image.getEnergy(x, y))
        }
        energies.add(energyRow)
    }

    val seamEnergies = mutableListOf(energies[0].map { SeamEnergy(it) })
    for (y in 1 until energies.size) {
        val row = energies[y]
        val seamEnergyRow = mutableListOf<SeamEnergy>()
        for (x in row.indices) {
            val xLeft = (x - 1).coerceAtLeast(0)
            val xRight = (x + 1).coerceAtMost(row.size - 1)
            val minAbovePos = (xLeft..xRight).minByOrNull { seamEnergies[y - 1][it].energy }!!
            val minSeamEnergy = SeamEnergy(row[x] + seamEnergies[y - 1][minAbovePos].energy, minAbovePos)
            seamEnergyRow.add(minSeamEnergy)
        }
        seamEnergies.add(seamEnergyRow)
    }

    var x = (0 until seamEnergies.last().size - 1).minByOrNull { seamEnergies.last()[it].energy }!!
    for (y in seamEnergies.size - 1 downTo 0) {
        image.setRGB(x, y, Color.RED.rgb)
        x = seamEnergies[y][x].xCoordinateInPreviousRow
    }

    ImageIO.write(image, "png", File(args[args.indexOf("-out") + 1]))
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