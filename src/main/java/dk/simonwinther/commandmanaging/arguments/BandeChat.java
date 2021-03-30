package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.manager.Gang;
import dk.simonwinther.manager.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.Consumer;

public class BandeChat implements CommandArguments
{
    private final MessageProvider mp;
    private final GangManaging gangManaging;

    public BandeChat(GangManaging gangManaging, MainPlugin plugin){
        this.gangManaging = gangManaging;
        this.mp = plugin.getMessageProvider();
    }

    @Override
    public String getAlias()
    {
        return "bc";
    }

    @Override
    public String getArgument()
    {
        return "bandechat";
    }

    @Override
    public String usage()
    {
        return "/bande bandechat/bc <besked>";
    }

    @Override
    public void perform(Player p, String... args)
    {

        UUID playerUUID = p.getUniqueId();
        if (args.length >= 2){
            if (gangManaging.playerInGangPredicate.test(playerUUID)){
                Gang gang = gangManaging.getGangByUuidFunction.apply(playerUUID);
                if (gangManaging.isRankMinimumPredicate.test(playerUUID, gang.gangPermissions.accessToGangChat)){
                    StringBuilder message = new StringBuilder();
                    final Consumer<String> messageConsumer = line -> message.append(line).append(" ");
                    Arrays.stream(args).skip(1).forEach(messageConsumer);

                    gang.getMembersSorted().keySet()
                            .stream()
                            .filter(uuid -> Bukkit.getPlayer(uuid) != null)
                            .map(Bukkit::getPlayer)
                            .forEach(teamMember -> teamMember.sendMessage("§8§l| §b§lBANDECHAT §8§l|§e "+p.getName()+" §8§l|§f "+message.toString()));

                }else p.sendMessage(this.mp.notHighRankEnough);
            }else p.sendMessage(this.mp.notInGang);
        } else p.sendMessage(this.mp.missingArguments);

    }
}
