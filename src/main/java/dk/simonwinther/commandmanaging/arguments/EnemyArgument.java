package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.manager.Gang;
import dk.simonwinther.manager.GangManaging;
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
        UUID playerUUID = p.getUniqueId();
        if (gangManaging.playerInGangPredicate.test(playerUUID))
        {
            Gang playerGang = this.gangManaging.getGangByUuidFunction.apply(playerUUID);
            this.gangManaging.requestEnemy(playerGang, args[1], p);
        } else p.sendMessage(this.mp.notInGang);
    }
}
