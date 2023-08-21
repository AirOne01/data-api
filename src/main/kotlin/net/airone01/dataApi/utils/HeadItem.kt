package net.airone01.dataApi.utils

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.mojang.authlib.properties.PropertyMap
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*
import java.util.regex.Pattern
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
abstract class HeadItem : ItemStack() {
    companion object {
        fun getHead(key: String): ItemStack {
            val url = "https://textures.minecraft.net/texture/$key"
            val profile = GameProfile(UUID.randomUUID(), null)
            val propertyMap: PropertyMap = profile.properties
                ?: throw IllegalStateException("Profile doesn't contain a property map")
            val encodedData: ByteArray =
                Base64.encodeToByteArray(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).toByteArray())
            propertyMap.put("textures", Property("textures", String(encodedData)))
            val head = ItemStack(Material.PLAYER_HEAD, 1)
            val headMeta = head.itemMeta as SkullMeta
            val headMetaClass: Class<*> = headMeta.javaClass
            Reflections.getField(headMetaClass, "profile", GameProfile::class.java)[headMeta] = profile
            head.setItemMeta(headMeta)
            return head
        }
    }
}

object Reflections {
    // Deduce the net.minecraft.server.v* package
    private val OBC_PREFIX = Bukkit.getServer().javaClass.getPackage().name
    private val NMS_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server")
    private val VERSION = OBC_PREFIX.replace("org.bukkit.craftbukkit", "").replace(".", "")

    // Variable replacement
    private val MATCH_VARIABLE = Pattern.compile("\\{([^\\}]+)\\}")

    /**
     * Retrieve a field accessor for a specific field type and name.
     *
     * @param target    the target type
     * @param name      the name of the field, or NULL to ignore
     * @param fieldType a compatible field type
     * @return the field accessor
     */
    fun <T> getField(target: Class<*>, name: String?, fieldType: Class<T>): FieldAccessor<T> {
        return getField(target, name, fieldType, 0)
    }

    // Common method
    private fun <T> getField(target: Class<*>, name: String?, fieldType: Class<T>, index: Int): FieldAccessor<T> {
        var index = index
        for (field in target.getDeclaredFields()) {
            if ((name == null || field.name == name) && fieldType.isAssignableFrom(field.type) && index-- <= 0) {
                field.setAccessible(true)

                // A function for retrieving a specific field value
                return object : FieldAccessor<T> {
                    override operator fun get(target: Any?): T {
                        return try {
                            field[target] as T
                        } catch (e: IllegalAccessException) {
                            throw RuntimeException("Cannot access reflection.", e)
                        }
                    }

                    override operator fun set(target: Any?, value: Any?) {
                        try {
                            field[target] = value
                        } catch (e: IllegalAccessException) {
                            throw RuntimeException("Cannot access reflection.", e)
                        }
                    }

                    override fun hasField(target: Any?): Boolean {
                        // target instanceof DeclaringClass
                        return field.declaringClass.isAssignableFrom(target?.javaClass)
                    }
                }
            }
        }

        // Search in parent classes
        if (target.superclass != null) return getField(target.superclass, name, fieldType, index)
        throw IllegalArgumentException("Cannot find field with type $fieldType")
    }

    /**
     * An interface for retrieving the field content.
     *
     * @param <T> field type
    </T> */
    interface FieldAccessor<T> {
        /**
         * Retrieve the content of a field.
         *
         * @param target the target object, or NULL for a static field
         * @return the value of the field
         */
        operator fun get(target: Any?): T

        /**
         * Set the content of a field.
         *
         * @param target the target object, or NULL for a static field
         * @param value  the new value of the field
         */
        operator fun set(target: Any?, value: Any?)

        /**
         * Determine if the given object has this field.
         *
         * @param target the object to test
         * @return TRUE if it does, FALSE otherwise
         */
        fun hasField(target: Any?): Boolean
    }
}