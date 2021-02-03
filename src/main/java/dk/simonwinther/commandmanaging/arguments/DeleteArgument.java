package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DeleteArgument implements CommandArguments
{
    private MainPlugin plugin;
    private final GangManaging gangManaging;
    private MessageProvider mp;

    public DeleteArgument(GangManaging gangManaging, MainPlugin plugin){
        this.gangManaging = gangManaging;
        this.plugin = plugin;
        this.mp = plugin.getMessageProvider();
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
                    p.sendMessage(this.mp.successfullyLeftGang.replace("{name}", gangName));
                } else p.sendMessage(this.mp.passOwnership);

            }
        } else p.sendMessage(this.mp.notInGang);

    }
}
