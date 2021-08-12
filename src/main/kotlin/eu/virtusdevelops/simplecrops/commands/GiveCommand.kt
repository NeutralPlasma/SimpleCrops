package eu.virtusdevelops.simplecrops.commands

import eu.virtusdevelops.simplecrops.handlers.crophandler.CropDrops
import eu.virtusdevelops.simplecrops.locale.LocaleHandler
import eu.virtusdevelops.simplecrops.locale.Locales
import eu.virtusdevelops.virtuscore.command.AbstractCommand
import eu.virtusdevelops.virtuscore.utils.HexUtil
import eu.virtusdevelops.virtuscore.utils.PlayerUtils
import eu.virtusdevelops.virtuscore.utils.TextUtils
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class GiveCommand(private val cropDrops: CropDrops, private val locale: LocaleHandler) : AbstractCommand(CommandType.BOTH, true, "give") {

    override fun runCommand(sender: CommandSender, vararg args: String): ReturnType {

        if(args.size > 4){
            val targetPlayer = Bukkit.getPlayer(args[0])
            if(targetPlayer != null){
                val crop = args[1]
                if(cropDrops.cropConfigurations.containsKey(crop)){
                    val amount = args[2].toInt()
                    if(amount < 1){
                        sender.sendMessage(HexUtil.colorify(TextUtils.formatString(locale.getLocale(Locales.INVALID_AMOUNT), "{value}:value > 0")))

                    }
                    val item = cropDrops.createSeed(crop, args[3].toInt(), args[4].toInt())
                    if(item != null) {
                        item.amount = amount
                        PlayerUtils.giveItem(targetPlayer, item, true)
                        //targetPlayer.inventory.addItem(item)
                        return ReturnType.SUCCESS
                    }
                    return ReturnType.FAILURE
                }else{
                    sender.sendMessage(HexUtil.colorify(TextUtils.formatString(locale.getLocale(Locales.INVALID_CROP), "{crop}:$crop")))
                }
            }else{
                sender.sendMessage(HexUtil.colorify(TextUtils.formatString(locale.getLocale(Locales.INVALID_PLAYER), "{player}:${args[0]}")))
            }
        }else{
            return ReturnType.SYNTAX_ERROR
        }
        return ReturnType.SUCCESS
    }



    override fun onTab(commandSender: CommandSender, vararg args: String): List<String> {
        if(args.size == 1){
            return Bukkit.getOnlinePlayers().map { it.name }
        }else if(args.size == 2){
            return cropDrops.cropConfigurations.map { it.key }
        }else if(args.size in 3..5){
            return listOf("<NUMBER>")
        }
        return listOf()
    }

    override fun getPermissionNode(): String {
        return "simplecrops.command.give"
    }

    override fun getSyntax(): String {
        return "/simplecrops give <player> <crop> <amount:number> <gain:number> <strength:number>"
    }

    override fun getDescription(): String {
        return "Gives someone a crop."
    }

}

