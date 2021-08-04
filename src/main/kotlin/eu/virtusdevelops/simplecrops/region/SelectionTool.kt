package eu.virtusdevelops.simplecrops.region

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.virtuscore.utils.ItemUtils
import eu.virtusdevelops.virtuscore.utils.TextUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap

/**
 * A tool which can be given to players and used to select a Region, or just any two points
 * @author Redempt
 * https://github.com/Redempt/RedLib/blob/master/src/redempt/redlib/region/SelectionTool.java
 */
class SelectionTool(private val item: ItemStack, private val plugin: SimpleCrops): Listener {

    init {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    private val selections: HashMap<UUID, Array<Location?>> = HashMap<UUID, Array<Location?>>()

    @EventHandler
    fun onPluginDisable(e: PluginDisableEvent) {
        if (e.plugin == plugin) {
            HandlerList.unregisterAll(this)
        }
    }


    @EventHandler
    fun onClick(e: PlayerInteractEvent) {
        if(e.action == Action.RIGHT_CLICK_BLOCK && e.hand == EquipmentSlot.HAND){
            if(!e.player.hasPermission("simplecrops.structuretool")){
                return
            }
            if (!ItemUtils.compare(e.item, item)) {
                return
            }
            val locations: Array<Location?> = selections.getOrDefault(e.player.uniqueId, arrayOfNulls(3))
            if(e.player.isSneaking){
                if (locations[2] != null) {
                    locations[2] = null
                }
                if (locations[2] == null) {
                    locations[2] = e.clickedBlock!!.location
                    e.player.sendMessage(TextUtils.colorFormat("&8[&6SC&8] &7Selected offset."))
                }
                selections[e.player.uniqueId] = locations
            }else {
                if (locations[0] != null) {
                    locations[0] = null
                }
                if (locations[0] == null) {
                    locations[0] = e.clickedBlock!!.location
                    e.player.sendMessage(TextUtils.colorFormat("&8[&6SC&8] &7Selected position 1."))
                }
                selections[e.player.uniqueId] = locations
            }
            e.isCancelled = true
        }else if(e.action == Action.LEFT_CLICK_BLOCK){
            if (!ItemUtils.compare(e.item, item)) {
                return
            }
            val locations: Array<Location?> = selections.getOrDefault(e.player.uniqueId, arrayOfNulls(3))
            if(e.player.isSneaking) {
                if (locations[2] != null) {
                    locations[2] = null
                }
                if (locations[2] == null) {
                    locations[2] = e.clickedBlock!!.location
                    e.player.sendMessage(TextUtils.colorFormat("&8[&6SC&8] &7Selected offset."))
                }
                selections[e.player.uniqueId] = locations
            }else{
                if (locations[1] != null ) {
                    locations[1] = null
                }
                if (locations[1] == null) {
                    locations[1] = e.clickedBlock!!.location
                    e.player.sendMessage(TextUtils.colorFormat("&8[&6SC&8] &7Selected position 2."))
                }
                selections[e.player.uniqueId] = locations
            }
            e.isCancelled = true
        }
        return
    }

    /**
     * Gets the item used by this SelectionTool
     * @return The item
     */
    fun getItem(): ItemStack {
        return item.clone()
    }

    /**
     * Get the locations selected by the given player
     * @param uuid The UUID of the player
     * @return The locations selected by the given player
     */
    fun getLocations(uuid: UUID?): Array<Location?> {
        return selections.getOrDefault(uuid,  arrayOfNulls(2))
    }

    /**
     * Creates and returns a Region based on the locations selected by the player
     * @param uuid The UUID of the player
     * @return The Region selected by the player, or null if the player has not selected 2 locations
     */
    fun getRegion(uuid: UUID?): CuboidRegion? {
        val locations: Array<Location?>? = selections.getOrDefault(uuid,  arrayOfNulls(2))
        if (locations == null || locations[0] == null || locations[1] == null) {
            return null
        }
        val region = CuboidRegion(locations[0], locations[1])
        region.expand(1.0, 0.0, 1.0, 0.0, 1.0, 0.0)
        return region
    }


}