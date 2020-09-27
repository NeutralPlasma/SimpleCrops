package eu.virtusdevelops.simplecrops.commands

import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.handlers.crophandler.CropDrops
import eu.virtusdevelops.simplecrops.handlers.hoehandler.HoeHandler
import eu.virtusdevelops.simplecrops.locale.LocaleHandler
import eu.virtusdevelops.simplecrops.locale.Locales
import eu.virtusdevelops.virtuscore.command.AbstractCommand
import eu.virtusdevelops.virtuscore.utils.HexUtil
import eu.virtusdevelops.virtuscore.utils.PlayerUtils
import eu.virtusdevelops.virtuscore.utils.TextUtils
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class GiveHoeCommand(private val hoeHandler: HoeHandler, private val locale: LocaleHandler) : AbstractCommand(CommandType.BOTH, false, "givehoe") {

    override fun runCommand(sender: CommandSender, vararg args: String): ReturnType {

        if(args.size > 2){
            val targetPlayer = Bukkit.getPlayer(args[0])
            if(targetPlayer != null){
                val hoe = args[1]
                if(hoeHandler.hoeConfigurations.containsKey(hoe)){
                    val amount = args[2].toInt()
                    if(amount < 1){
                        sender.sendMessage(HexUtil.colorify(TextUtils.formatString(locale.getLocale(Locales.INVALID_AMOUNT), "{value}:value > 0")))

                    }
                    val item = hoeHandler.createHoe(hoe)

                    if(item != null){
                        item.amount = amount
                        PlayerUtils.giveItem(targetPlayer, item, true)
                        //targetPlayer.inventory.addItem(item)
                        return ReturnType.SUCCESS
                    }

                    return ReturnType.FAILURE
                }else{
                    sender.sendMessage(HexUtil.colorify(TextUtils.formatString(locale.getLocale(Locales.INVALID_HOE), "{hoe}:$hoe")))
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
            return hoeHandler.hoeConfigurations.map { it.key }
        }else if(args.size == 3){
            return mutableListOf("1", "2", "3", "4", "5", "6", "7", "8", "9")
        }
        return listOf()
    }

    override fun getPermissionNode(): String {
        return "simplecrops.command.givehoe"
    }

    override fun getSyntax(): String {
        return "/simplecrops givehoe <player> <hoe> <amount:number>"
    }

    override fun getDescription(): String {
        return "Gives someone a hoe."
    }

}

