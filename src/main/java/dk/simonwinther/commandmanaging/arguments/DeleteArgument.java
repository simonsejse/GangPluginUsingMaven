package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DeleteArgument implements CommandArguments
{
    private MainPlugin plugin;
    private final GangManaging gangManaging;

    public DeleteArgument(GangManaging gangManaging, MainPlugin plugin){
        this.gangManaging = gangManaging;
        this.plugin = plugin;
    }

    @Override
    public String getAlias()
    {
        return "slet";
    }

    @Override
    public String getArgument()
    {
        return "delete";
    }

    @Override
    public String usage()
    {
        return "Forkert brug: /bande leave";
    }

    @Override
    public void perform(Player p, String... args)
    {
        if (args.length > 1) return;
        UUID playerUuid = p.getUniqueId();

        if (gangManaging.playerInGangPredicate.test(playerUuid))
        {
            if (gangManaging.ownerOfGangPredicate.test(playerUuid))
            {
                if (gangManaging.getGangByUuidFunction.apply(playerUuid).getMembersSorted().size() <= 1)
                {
                    //Last player inside gang, delete
                    Gang gang = gangManaging.getGangByUuidFunction.apply(playerUuid);
                    String gangName = gang.getGangName();

                    //@Removes the gang from all other gang allies!
                    gang.getAllies()
                            .values()
                            .stream()
                            .map(gangManaging.getGangByNameFunction)
                            .forEach(g -> g.getAllies().remove(gangName.toLowerCase()));

                    //@Removes the gang from all other gang enemies!

                    gangManaging.enemyGangListFunction.apply(gangName)
                            .stream()
                            .map(Gang::getEnemies)
                            .forEach(list -> list.remove(gangName));

                    gangManaging.deleteGangConsumer.accept(playerUuid);
                    p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().SUCCESSFULLY_LEFT_GANG.replace("{name}", gangName)));
                } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().KICK_PLAYERS_TO_LEAVE_GANG));

            }
        } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_IN_GANG));

    }
}
