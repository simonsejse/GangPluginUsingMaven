package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.manager.Gang;
import dk.simonwinther.manager.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class AllianceChat implements CommandArguments
{

    private final MainPlugin plugin;
    private final GangManaging gangManaging;
    private final MessageProvider mp;

    public AllianceChat(GangManaging gangManaging, MainPlugin plugin){
        this.gangManaging = gangManaging;
        this.plugin = plugin;
        this.mp = this.plugin.getMessageProvider();
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
    TODO: understa
    public <K, V extends Comparable<? super V>> Map<K, V> getLowestToHighest(@NotNull Map<K, V> map, @Nullable Integer limit){

    }
     */

    @Override
    public void perform(Player p, String... args)
    {
        UUID playerUUID = p.getUniqueId();
        if (args.length >= 2){
            if (gangManaging.playerInGangPredicate.test(playerUUID))
            {
                Gang gang = gangManaging.getGangByUuidFunction.apply(playerUUID);
                if (gangManaging.isRankMinimumPredicate.test(playerUUID, gang.gangPermissions.accessToAllyChat)){
                    //Check if has permissions
                    final StringBuilder message = new StringBuilder();
                    Arrays.stream(args).skip(1).forEach(line -> message.append(line).append(" "));
                    
                    p.sendMessage("§8§l| §a§lALLIANCECHAT §8§l|§e "+p.getName()+" §8§l|§f "+message.toString());

                    gang.getAllies()
                            .values()
                            .stream()
                            .map(gangManaging.getGangByNameFunction::apply)
                            .map(Gang::getMembersSorted)
                            .map(Map::keySet)
                            .flatMap(Collection::stream)
                            .filter(uuid -> Bukkit.getPlayer(uuid) != null)
                            .map(Bukkit::getPlayer)
                            .forEach(alliancePlayer -> alliancePlayer.sendMessage("§8§l| §a§lALLIANCECHAT §8§l|§e "+p.getName()+" §8§l|§f "+message.toString()));


                } else p.sendMessage(this.mp.notHighRankEnough);
            }else p.sendMessage(this.mp.notInGang);
        } else p.sendMessage(this.mp.missingArguments);

    }
}
