package eu.virtusdevelops.simplecrops.locale

enum class Locales(private val path: String) {
    /*
        Command stuff
     */
    NO_PERMISSION(
            "no-permission"
    ),
    INVALID_PLAYER(
            "commands.invalid-player"
    ),
    INVALID_AMOUNT(
            "commands.invalid-amount"
    ),
    INVALID_HOE(
            "commands.invalid-hoe"
    ),
    RELOAD_SUCCESS(
            "commands.reload-success"
    ),
    INVALID_CROP(
            "commands.invalid-crop"
    ),
    /*
        GUI STUFF
     */
    MAIN_GUI_TITLE(
        "gui.main.title"
    ),
    MAIN_GUI_EDIT(
        "gui.main.edit"
    ),

    /*
        Items
     */
    ITEMS_GUI_TITLE(
        "gui.items.title"
    ),
    ITEMS_GUI_ADD_NEW_TITLE(
        "gui.items.addNewTitle"
    ),
    ITEMS_GUI_ADD_NEW_LORE(
        "gui.items.addNewLore"
    ),

    /*
        Commands
     */
    COMMANDS_GUI_TITLE(
        "gui.commands.title"
    ),
    COMMANDS_GUI_ADD_INFO(
        "gui.commands.addCommandInfo"
    ),
    COMMANDS_GUI_ADD_NEW_LORE(
        "gui.commands.addNewLore"
    ),
    COMMANDS_GUI_ADD_NEW_TITLE(
        "gui.commands.addNewTitle"
    ),


    BLOCKS_GUI_TITLE(
        "gui.blocks.title"
    ),
    BLOCKS_GUI_ADD_NEW_LORE(
        "gui.blocks.addNewLore"
    ),
    BLOCKS_GUI_ADD_NEW_TITLE(
        "gui.blocks.addNewTitle"
    ),


    /*
        GLOBAL GUI
     */
    GLOBAL_GUI_EDIT(
        "gui.global.edit"
    ),
    GLOBAL_GUI_REMOVE(
        "gui.global.remove"
    ),
    GLOBAL_GUI_NEXT_PAGE(
        "gui.global.nextPage"
    ),
    GLOBAL_GUI_PREV_PAGE(
        "gui.global.prevPage"
    ),
    GLOBAL_GUI_NEXT_PAGE_TITLE(
        "gui.global.nextPageTitle"
    ),
    GLOBAL_GUI_PREV_PAGE_TITLE(
        "gui.global.prevPageTitle"
    ),
    GLOBAL_GUI_INVALID_MATERIAL(
        "gui.global.invalidMaterial"
    );


    fun getPath(): String{
        return path
    }
}