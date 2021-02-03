package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EnemyArgument implements CommandArguments
{
    private MessageProvider mp;
    private final GangManaging gangManaging;

    public EnemyArgument(GangManaging gangManaging, MainPlugin plugin){
        this.gangManaging = gangManaging;
        this.mp = plugin.getMessageProvider();
    }

    @Override
    public String getAlias()
    {
        return "e";
    }

    @Override
    public String getArgument()
    {
        return "enemy";
    }

    @Override
    public String usage()
    {
        return "/bande enemy <bandenavn>";
    }

    @Override
    public void perform(Player p, String... args)
    {
        UUID playerUuid = p.getUniqueId();
        if (gangManaging.playerInGangPredicate.test(playerUuid))
        {
            Gang playerGang = gangManaging.getGangByUuidFunction.apply(playerUuid);
            if (gangManaging.isRankMinimumPredicate.test(playerUuid, playerGang.gangPermissions.accessToEnemy))
            {
                if (gangManaging.gangExistsPredicate.test(args[1]))
                {
                    Gang argsGang = gangManaging.getGangByNameFunction.apply(args[1]);
                    if (playerGang.getEnemies().size() < playerGang.getMaxEnemies())
                    {

                        if (!playerGang.equals(argsGang))
                        {
                            if (!playerGang.getEnemies().values().contains(args[1].toLowerCase()))
                            {
                                gangManaging.addEnemyGang.accept(playerGang, argsGang);
                            } else p.sendMessage(this.mp.alreadyEnemies);
                        } else p.sendMessage(this.mp.cantEnemyOwnGang);
                    } else p.sendMessage(this.mp.playerGangMaxEnemies);
                } else p.sendMessage(this.mp.gangDoesNotExists.replace("{name}", args[1]));
            } else p.sendMessage(this.mp.notHighRankEnough);
        } else p.sendMessage(this.mp.notInGang);
    }
}
