package eu.virtusdevelops.simplecrops.handlers

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.multiblock.MultiBlockStructure
import java.io.File
import java.io.IOException
import java.nio.file.Files

class StructureHandler(private val plugin: SimpleCrops) {

    val structures: MutableMap<String, MultiBlockStructure> = mutableMapOf()

    init {
        createDefault()
        loadStructures()
    }

    fun getStructure(name: String): MultiBlockStructure?{
        return structures[name]
    }

    fun addStructure(name: String, structure: MultiBlockStructure){
        structures[name] = structure
    }

    fun loadStructure(name: String){
        val file = File(plugin.dataFolder.path + "/structures/${name}.dat")
        if(file.isFile){
            structures[file.name.replace(".dat" , "")] = MultiBlockStructure.create(file.inputStream(), file.name, true, true)
        }
    }

    private fun createDefault(){
        val filesToCreate = listOf<String>("copper1.dat", "copper2.dat", "copper3.dat")

        val files = File(plugin.dataFolder.path + "/structures")
        if(!files.isDirectory) {
            files.mkdir()
        }

        for(file in filesToCreate){
            if(!File(plugin.dataFolder.path + "/structures/${file}").isFile){
                val file1 = File(plugin.dataFolder.path + "/structures", file)
                try {
                    plugin.getResource("structures/copper1.dat").use { `in` -> Files.copy(`in`, file1.toPath()) }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun reload(){
        structures.clear()
        loadStructures()
    }

    fun loadStructures(){
        val files = File(plugin.dataFolder.path + "/structures")
        if(files.isDirectory){
            val files_raw = files.listFiles()
            for(file in files_raw){
                structures[file.name.replace(".dat" , "")] = MultiBlockStructure.create(file.inputStream(), file.name, true, true)
            }
        }else{
            files.mkdir()
        }
    }
}