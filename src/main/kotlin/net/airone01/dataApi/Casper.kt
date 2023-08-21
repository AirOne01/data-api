package net.airone01.dataApi

import net.airone01.dataApi.utils.HeadItem
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Display
import org.bukkit.entity.ItemDisplay
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3d
import org.joml.Vector3f

val magicPos = arrayOf(
    doubleArrayOf(0.25, 0.5001, 0.25),
    doubleArrayOf(0.75, 0.5001, 0.25),
    doubleArrayOf(0.25, 0.5001, 0.75),
    doubleArrayOf(0.75, 0.5001, 0.75),
    doubleArrayOf(0.75, 1.0001, 0.75),
    doubleArrayOf(0.25, 1.0001, 0.25),
    doubleArrayOf(0.75, 1.0001, 0.25),
    doubleArrayOf(0.25, 1.0001, 0.75),
)

class Casper {
    private val location: Location

    var texture: String = "62d8f456ab0426d375a7f8852a2c22fa7791e3385a429c373ba53d28fca0ff22"

    constructor(world: World, pos: Vector3d) {
        this.location = Location(world, pos.x, pos.y, pos.z)
    }

    constructor(world: World, x: Int, y: Int, z: Int) {
        this.location = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
    }

    constructor(location: Location) {
        this.location = location
    }

    constructor(location: Location, texture: String) {
        this.location = location
        this.texture = texture
    }

    constructor(world: World, x: Int, y: Int, z: Int, texture: String) {
        this.location = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
        this.texture = texture
    }

    constructor(world: World, pos: Vector3d, texture: String) {
        this.location = Location(world, pos.x, pos.y, pos.z)
    }

    fun getBlock(): Block {
        return location.world.getBlockAt(location)
    }

    fun spawnGhost() {
        val head = HeadItem.getHead(texture)

        magicPos.forEach {
            // rounded position
            val x = location.x.toInt().toDouble() + it[0]
            val y = location.y.toInt().toDouble() + it[1]
            val z = location.z.toInt().toDouble() + it[2]

            val itemDisplay: ItemDisplay =
                location.world.spawnEntity(Location(location.world, x, y, z), org.bukkit.entity.EntityType.ITEM_DISPLAY, false) as ItemDisplay
            itemDisplay.itemStack = head
            itemDisplay.brightness = Display.Brightness(0, 15)
            itemDisplay.transformation = Transformation(Vector3f(0f, 0f, 0f), AxisAngle4f(0f, 0f, 0f, 0f), Vector3f(1.005f, 1.0003f, 1.005f), AxisAngle4f(0f, 0f, 0f, 0f))
        }
    }
}
