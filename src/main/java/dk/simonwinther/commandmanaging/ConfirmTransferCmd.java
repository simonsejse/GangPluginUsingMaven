package dk.simonwinther.commandmanaging;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.enums.Level;
import dk.simonwinther.enums.QuestPayEnum;
import dk.simonwinther.utility.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ConfirmTransferCmd implements CommandExecutor
{

    private MainPlugin plugin;

    public ConfirmTransferCmd(MainPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        if (plugin.getEventHandling().containsActiveMoneyPlayer.apply(player.getUniqueId())){
            plugin.getEventHandling().removeActiveMoneyPlayers.accept(player.getUniqueId());
            if (args[0].equalsIgnoreCase("yes")){
                Gang gang = GangManaging.getGangByUuidFunction.apply(uuid);
                Level level = Level.valueOf(ChatUtil.numbers[gang.getGangLevel()]);
                if (gang.getLevelSystem().getPaidForQuest() >= level.getAmountToPay()[QuestPayEnum.AMOUNT_PAY_INDEX.value]){
                    player.sendMessage("Du har allerede betalt det du skulle i dette level!");
                }else{
                    int restToPay = level.getAmountToPay()[QuestPayEnum.AMOUNT_PAY_INDEX.value] - gang.getLevelSystem().getPaidForQuest();
                    int amountToPay = Math.min(gang.getGangBalance(), restToPay);

                    gang.setGangBalance(gang.getGangBalance() - amountToPay);
                    gang.getLevelSystem().setPaidForQuest(gang.getLevelSystem().getPaidForQuest() - amountToPay);
                }
            }else{
                player.sendMessage("§cDu har annulleret overførslen!");
            }
        }else player.sendMessage("Denne kommando kan ikke bruges manuelt!");
        return false;
    }
}
