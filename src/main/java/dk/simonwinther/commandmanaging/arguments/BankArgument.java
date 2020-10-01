package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BankArgument implements CommandArguments
{
    private MainPlugin plugin;

    public BankArgument(MainPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public String getAlias()
    {
        return "b";
    }

    @Override
    public String getArgument()
    {
        return "bank";
    }

    @Override
    public String usage()
    {
        return "/bande bank <amount>";
    }

    @Override
    public void perform(Player p, String... args)
    {
        if (args.length != 2)
        {
            p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().MISSING_ARGUMENTS));
            return;
        }

        UUID playerUuid = p.getUniqueId();
        if (GangManaging.playerInGangPredicate.test(playerUuid))
        {
            if (GangManaging.isRankMinimumPredicate.test(playerUuid, GangManaging.getGangByUuidFunction.apply(playerUuid).gangPermissions.accessToDeposit)){
                int amount;
                try
                {
                    amount = Integer.parseInt(args[1]);
                    Gang gang = GangManaging.getGangByUuidFunction.apply(playerUuid);
                    gang.depositMoney(amount);
                    p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().INSERT_BANK.replace("{0}", String.valueOf(amount))));

                } catch (NumberFormatException nfe)
                {
                    p.sendMessage(args[1] + "er ikke et nummer.");
                }

            } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_HIGH_RANK_ENOUGH));
        } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_IN_GANG));

    }
}
