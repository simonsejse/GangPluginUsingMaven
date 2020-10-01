package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;

public class AllianceChat implements CommandArguments
{

    private MainPlugin plugin;

    public AllianceChat(MainPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public String getAlias()
    {
        return "ac";
    }

    @Override
    public String getArgument()
    {
        return "alliancechat";
    }

    @Override
    public String usage()
    {
        return "/bande alliancechat/ac <besked>";
    }

    /*
    public static <K, V extends Comparable<? super V>> Map<K, V> getLowestToHighest(@NotNull Map<K, V> map, @Nullable Integer limit){

    }
     */

    @Override
    public void perform(Player p, String... args)
    {
        UUID playerUuid = p.getUniqueId();
        if (args.length >= 2){
            if (GangManaging.playerInGangPredicate.test(playerUuid))
            {
                Gang gang = GangManaging.getGangByUuidFunction.apply(playerUuid);
                if (GangManaging.isRankMinimumPredicate.test(playerUuid, gang.gangPermissions.accessToAllyChat)){
                    //Check if has permissions
                    StringBuilder message = new StringBuilder();
                    final Consumer<String> messageConsumer = line -> message.append(line).append(" ");
                    Arrays.stream(args).skip(1).forEach(messageConsumer);
                    p.sendMessage("§8§l| §a§lALLIANCECHAT §8§l|§e "+p.getName()+" §8§l|§f "+message.toString());
                    gang.getAllies()
                            .stream()
                            .map(GangManaging.getGangByNameFunction::apply)
                            .map(Gang::getMembersSorted)
                            .map(Map::keySet)
                            .flatMap(Collection::stream)
                            .filter(uuid -> Bukkit.getPlayer(uuid) != null)
                            .map(Bukkit::getPlayer)
                            .forEach(alliancePlayer -> alliancePlayer.sendMessage("§8§l| §a§lALLIANCECHAT §8§l|§e "+p.getName()+" §8§l|§f "+message.toString()));


                } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_HIGH_RANK_ENOUGH));
            }else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_IN_GANG));
        } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().MISSING_ARGUMENTS));

    }
}