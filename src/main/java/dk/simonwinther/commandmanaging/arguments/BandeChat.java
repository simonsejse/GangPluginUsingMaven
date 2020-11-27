package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.Consumer;

public class BandeChat implements CommandArguments
{
    private MainPlugin plugin;
    private final GangManaging gangManaging;

    public BandeChat(GangManaging gangManaging, MainPlugin plugin){
        this.gangManaging = gangManaging;
        this.plugin = plugin;
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

        UUID playerUuid = p.getUniqueId();
        if (args.length >= 2){
            if (gangManaging.playerInGangPredicate.test(playerUuid)){
                Gang gang = gangManaging.getGangByUuidFunction.apply(playerUuid);
                if (gangManaging.isRankMinimumPredicate.test(playerUuid, gang.gangPermissions.accessToGangChat)){
                    StringBuilder message = new StringBuilder();
                    final Consumer<String> messageConsumer = line -> message.append(line).append(" ");
                    Arrays.stream(args).skip(1).forEach(messageConsumer);

                    gang.getMembersSorted().keySet()
                            .stream()
                            .filter(uuid -> Bukkit.getPlayer(uuid) != null)
                            .map(Bukkit::getPlayer)
                            .forEach(teamMember -> teamMember.sendMessage("§8§l| §b§lBANDECHAT §8§l|§e "+p.getName()+" §8§l|§f "+message.toString()));

                }else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_HIGH_RANK_ENOUGH));
            }else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_IN_GANG));
        } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().MISSING_ARGUMENTS));

    }
}
