package eu.virtusdevelops.simplecrops.locale

import eu.virtusdevelops.virtuscore.managers.FileManager

class LocaleHandler(private val fileManager: FileManager){

    fun getLocale(locale: Locales): String{
        return fileManager.getConfiguration("language").getString(locale.getPath()) ?: locale.getPath()
    }
    fun getList(locale: Locales): MutableList<String>{
        return  fileManager.getConfiguration("language").getStringList(locale.getPath().ifEmpty { locale.getPath() })
    }
}