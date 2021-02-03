package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AllyArgument implements CommandArguments
{
    private MainPlugin plugin;
    private MessageProvider mp;
    private final GangManaging gangManaging;

    public AllyArgument(GangManaging gangManaging, MainPlugin plugin)
    {
        this.plugin = plugin;
        this.mp = plugin.getMessageProvider();
        this.gangManaging = gangManaging;
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
                                        playerGang.getAllies().put(argsGang.getGangId(), argsGang.getGangName().toLowerCase());
                                        argsGang.getAllies().put(playerGang.getGangId(), playerGang.getGangName().toLowerCase());

                                        playerGang.getAllyInvitation().remove(argsGang.getGangName().toLowerCase());
                                        argsGang.getAllyInvitation().remove(playerGang.getGangName().toLowerCase());

                                        this.gangManaging.sendTeamMessage.accept(playerGang, this.mp.allySuccessful.replace("{name}", argsGang.getGangName()));
                                        this.gangManaging.sendTeamMessage.accept(argsGang, this.mp.allySuccessful.replace("{name}", playerGang.getGangName()));

                                    } else
                                    {
                                        if (gangManaging.gangContainsAllyInvitationPredicate.test(playerGang, argsGang.getGangName().toLowerCase()))
                                        {
                                            p.sendMessage(this.mp.unAlly.replace("{name}", args[1]));
                                            playerGang.getAllyInvitation().remove(argsGang.getGangName().toLowerCase());
                                            this.gangManaging.sendTeamMessage.accept(argsGang, this.mp.askAlly.replace("{name}", playerGang.getGangName()));

                                        } else
                                        {
                                            p.sendMessage(this.mp.askAlly.replace("{name}", args[1]));
                                            if (playerGang.getEnemies().values().contains(args[1]))
                                                playerGang.getEnemies().remove(args[1]);
                                            playerGang.getAllyInvitation().add(argsGang.getGangName().toLowerCase());
                                            this.gangManaging.sendTeamMessage.accept(argsGang, this.mp.wishesToBeAlly.replace("{name}", playerGang.getGangName()));
                                        }
                                    }
                                } else p.sendMessage(this.mp.cantAllyOwnGang);
                            } else p.sendMessage(this.mp.playerGangMaxAllies);
                        } else p.sendMessage(this.mp.otherGangMaxAllys);
                    } else p.sendMessage(this.mp.alreadyAllies);
                } else p.sendMessage(this.mp.gangDoesNotExists.replace("{name}", args[1]));
            } else p.sendMessage(this.mp.notHighRankEnough);
        } else p.sendMessage(this.mp.notInGang);
    }

    private Function<Gang, Boolean> gangHasAllyRoomFunction = gang -> gang.getAllies().size() < gang.getMaxAllies();
    private BiFunction<Gang, Gang, Boolean> gangEqualsFunction = Gang::equals;
    private BiConsumer<Gang, Gang> gangDeleteEnemiesConsumer = (gang1, gang2) ->
    {
        if (gang1.getEnemies().values().contains(gang2.getGangName().toLowerCase()))
            gang1.getEnemies().remove(gang2.getGangName().toLowerCase());
    };
    private BiFunction<Gang, Gang, Boolean> gangIsAllyFunction = (gang1, gang2) -> gang1.getAllies().values().contains(gang2.getGangName().toLowerCase()) || gang2.getAllies().values().contains(gang1.getGangName().toLowerCase());




}
