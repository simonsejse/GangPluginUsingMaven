package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AllyArgument implements CommandArguments
{
    private MainPlugin plugin;
    private GangManaging gangManaging;

    public AllyArgument(GangManaging gangManaging, MainPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public String getAlias()
    {
        return "a";
    }

    @Override
    public String getArgument()
    {
        return "ally";
    }

    @Override
    public String usage()
    {
        return "/bande ally <bandename>";
    }

    @Override
    public void perform(Player p, String... args)
    {
        //Player is in gang
        //Player 

        if (gangManaging.playerInGangPredicate.test(p.getUniqueId()))
        {
            Gang playerGang = gangManaging.getGangByUuidFunction.apply(p.getUniqueId());
            if (gangManaging.isRankMinimumPredicate.test(p.getUniqueId(), playerGang.gangPermissions.accessToAlly))
            {
                if (gangManaging.gangExistsPredicate.test(args[1]))
                {
                    Gang argsGang = gangManaging.getGangByNameFunction.apply(args[1]);
                    if (gangIsAllyFunction.apply(playerGang, argsGang))
                    {
                        if (gangHasAllyRoomFunction.apply(argsGang))
                        {
                            if (gangHasAllyRoomFunction.apply(playerGang))
                            {
                                if (!gangEqualsFunction.apply(playerGang, argsGang))
                                {
                                    gangDeleteEnemiesConsumer.accept(playerGang, argsGang);
                                    if (gangManaging.gangContainsAllyInvitationPredicate.test(argsGang, playerGang.getGangName().toLowerCase()))
                                    {
                                        //@ALLY SUCCESS
                                        playerGang.getAllies().add(argsGang.getGangName().toLowerCase());
                                        argsGang.getAllies().add(playerGang.getGangName().toLowerCase());

                                        playerGang.getAllyInvitation().remove(argsGang.getGangName().toLowerCase());
                                        argsGang.getAllyInvitation().remove(playerGang.getGangName().toLowerCase());

                                        playerGang.getMembersSorted().keySet()
                                                .stream()
                                                .filter(teamMember -> Bukkit.getPlayer(teamMember) != null)
                                                .map(Bukkit::getPlayer)
                                                .forEach(teamMember ->
                                                {
                                                    teamMember.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().ALLY_SUCCESSFUL.replace("{name}", argsGang.getGangName())));
                                                });

                                        argsGang.getMembersSorted().keySet()
                                                .stream()
                                                .filter(teamMember -> Bukkit.getPlayer(teamMember) != null)
                                                .map(Bukkit::getPlayer)
                                                .forEach(teamMember ->
                                                {
                                                    teamMember.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().ALLY_SUCCESSFUL.replace("{name}", playerGang.getGangName())));
                                                });

                                    } else
                                    {
                                        if (gangManaging.gangContainsAllyInvitationPredicate.test(playerGang, argsGang.getGangName().toLowerCase()))
                                        {
                                            p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().UN_ALLY.replace("{name}", args[1])));
                                            playerGang.getAllyInvitation().remove(argsGang.getGangName().toLowerCase());
                                            argsGang.getMembersSorted().keySet()
                                                    .stream()
                                                    .filter(teamMember -> Bukkit.getPlayer(teamMember) != null)
                                                    .map(Bukkit::getPlayer)
                                                    .forEach(teamMember ->
                                                    {
                                                        teamMember.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().REGRET_TO_BE_ALLY.replace("{name}", playerGang.getGangName())));
                                                    });
                                        } else
                                        {
                                            p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().ASK_ALLY.replace("{name}", args[1])));
                                            if (playerGang.getEnemies().contains(args[1]))
                                                playerGang.getEnemies().remove(args[1]);
                                            playerGang.getAllyInvitation().add(argsGang.getGangName().toLowerCase());
                                            argsGang.getMembersSorted().keySet()
                                                    .stream()
                                                    .filter(teamMember -> Bukkit.getPlayer(teamMember) != null)
                                                    .map(Bukkit::getPlayer)
                                                    .forEach(teamMember ->
                                                    {
                                                        teamMember.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().WISHES_TO_BE_ALLY.replace("{name}", playerGang.getGangName())));
                                                    });
                                        }
                                    }
                                } else
                                    p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().CANT_ALLY_OWN_GANG));
                            } else
                                p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().PLAYER_GANG_MAX_ALLYS));
                        } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().OTHER_GANG_MAX_ALLYS));
                    } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().ALREADY_ALLYS));
                } else
                    p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().GANG_DOES_NOT_EXISTS.replace("{name}", args[1])));
            } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_HIGH_RANK_ENOUGH));
        } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_IN_GANG));
    }

    private Function<Gang, Boolean> gangHasAllyRoomFunction = gang -> gang.getAllies().size() < gang.getMaxAllies();
    private BiFunction<Gang, Gang, Boolean> gangEqualsFunction = Gang::equals;
    private BiConsumer<Gang, Gang> gangDeleteEnemiesConsumer = (gang1, gang2) ->
    {
        if (gang1.getEnemies().contains(gang2.getGangName().toLowerCase()))
            gang1.getEnemies().remove(gang2.getGangName().toLowerCase());
    };
    private BiFunction<Gang, Gang, Boolean> gangIsAllyFunction = (gang1, gang2) -> gang1.getAllies().contains(gang2.getGangName().toLowerCase()) || gang2.getAllies().contains(gang1.getGangName().toLowerCase());


}
