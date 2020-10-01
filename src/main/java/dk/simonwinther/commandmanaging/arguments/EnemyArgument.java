package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EnemyArgument implements CommandArguments
{
    private MainPlugin plugin;

    public EnemyArgument(MainPlugin plugin){
        this.plugin = plugin;
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
        if (GangManaging.playerInGangPredicate.test(playerUuid))
        {
            Gang playerGang = GangManaging.getGangByUuidFunction.apply(playerUuid);
            if (GangManaging.isRankMinimumPredicate.test(playerUuid, playerGang.gangPermissions.accessToEnemy))
            {
                if (GangManaging.gangExistsPredicate.test(args[1]))
                {
                    Gang argsGang = GangManaging.getGangByNameFunction.apply(args[1]);
                    if (argsGang.getEnemies().size() < playerGang.getMaxEnemies())
                    {
                        if (playerGang.getEnemies().size() < playerGang.getMaxEnemies())
                        {

                            if (!playerGang.equals(argsGang))
                            {
                                if (!playerGang.getEnemies().contains(args[1].toLowerCase()))
                                {
                                    GangManaging.addEnemyGang.accept(playerGang, argsGang);
                                } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().ALREADY_ENEMIES));
                            } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().CANT_ENEMY_OWN_GANG));
                        } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().PLAYER_GANG_MAX_ENEMIES));
                    }else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().OTHER_GANG_MAX_ENEMIES));
                } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().GANG_DOES_NOT_EXISTS.replace("{name}", args[1])));
            } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_HIGH_RANK_ENOUGH));
        } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_IN_GANG));
    }
}