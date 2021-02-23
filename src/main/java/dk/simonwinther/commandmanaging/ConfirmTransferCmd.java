package dk.simonwinther.commandmanaging;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.constants.Level;
import dk.simonwinther.constants.QuestPayEnum;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ConfirmTransferCmd implements CommandExecutor
{

    private MainPlugin plugin;
    private final GangManaging gangManaging;

    public ConfirmTransferCmd(GangManaging gangManaging, MainPlugin plugin){
        this.plugin = plugin;
        this.gangManaging = gangManaging;
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
                Gang gang = this.gangManaging.getGangByUuidFunction.apply(uuid);
                Level level = Level.valueOf(MessageProvider.numbers[gang.getGangLevel()]);
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
