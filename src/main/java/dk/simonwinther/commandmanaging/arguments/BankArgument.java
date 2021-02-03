package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BankArgument implements CommandArguments
{
    private final MainPlugin plugin;
    private final MessageProvider mp;

    private final GangManaging gangManaging;

    public BankArgument(GangManaging gangManaging, MainPlugin plugin){
        this.gangManaging = gangManaging;
        this.plugin = plugin;
        this.mp = this.plugin.getMessageProvider();
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
            p.sendMessage(this.mp.missingArguments);
            return;
        }

        UUID playerUuid = p.getUniqueId();
        if (gangManaging.playerInGangPredicate.test(playerUuid))
        {
            if (gangManaging.isRankMinimumPredicate.test(playerUuid, gangManaging.getGangByUuidFunction.apply(playerUuid).gangPermissions.accessToDeposit)){
                int amount;
                try
                {
                    amount = Integer.parseInt(args[1]);
                    Gang gang = gangManaging.getGangByUuidFunction.apply(playerUuid);
                    gang.depositMoney(amount);
                    p.sendMessage(this.mp.insertBank.replace("{0}", String.valueOf(amount)));

                } catch (NumberFormatException nfe)
                {
                    p.sendMessage(args[1] + "er ikke et nummer.");
                }

            } else p.sendMessage(this.mp.notHighRankEnough);
        } else p.sendMessage(this.mp.notInGang);

    }
}
