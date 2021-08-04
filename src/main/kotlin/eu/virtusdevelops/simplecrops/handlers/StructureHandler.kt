package eu.virtusdevelops.simplecrops.handlers

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.multiblock.MultiBlockStructure
import java.io.File

class StructureHandler(private val plugin: SimpleCrops) {

    val structures: MutableMap<String, MultiBlockStructure> = mutableMapOf()

    init {
        loadStructures()
    }

    fun getStructure(name: String): MultiBlockStructure?{
        return structures[name]
    }

    fun addStructure(name: String, structure: MultiBlockStructure){
        structures[name] = structure
    }

    fun loadStructure(name: String){
        var file = File(plugin.dataFolder.path + "/structures/${name}.dat")
        if(file.isFile){
            structures[file.name.replace(".dat" , "")] = MultiBlockStructure.create(file.inputStream(), file.name, true, true)
        }
    }

    fun reload(){
        structures.clear()
        loadStructures()
    }

    fun loadStructures(){
        var files = File(plugin.dataFolder.path + "/structures")
        if(files.isDirectory){
            val files_raw = files.listFiles()
            for(file in files_raw){
                structures[file.name.replace(".dat" , "")] = MultiBlockStructure.create(file.inputStream(), file.name, true, true)
            }
        }
    }
}