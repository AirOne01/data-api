package net.airone01.dataApi

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class DataApi: JavaPlugin(), Listener {
    companion object {
        lateinit var plugin: DataApi
    }

    override fun onEnable() {
        plugin = this

        logger.info("Data-api enabled!")
    }
}