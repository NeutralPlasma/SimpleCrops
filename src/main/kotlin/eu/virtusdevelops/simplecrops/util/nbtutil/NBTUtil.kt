package eu.virtusdevelops.simplecrops.util.nbtutil

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.util.nbtutil.*
import eu.virtusdevelops.virtuscore.compatibility.ServerVersion

class NBTUtil(plugin: SimpleCrops){
    var nbt: NBTVer = if(ServerVersion.isServerVersionAbove(ServerVersion.V1_13)) {
        NBTCurrent(plugin)
    }else{
        NBTLegacy(plugin)
    }

}