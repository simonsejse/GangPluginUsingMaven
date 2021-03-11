package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.manager.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.entity.Player;

public class AllyArgument implements CommandArguments {
    private MainPlugin plugin;
    private MessageProvider mp;
    private final GangManaging gangManaging;

    public AllyArgument(GangManaging gangManaging, MainPlugin plugin) {
        this.plugin = plugin;
        this.mp = plugin.getMessageProvider();
        this.gangManaging = gangManaging;
    }

    @Override
    public String getAlias() {
        return "a";
    }

    @Override
    public String getArgument() {
        return "ally";
    }

    @Override
    public String usage() {
        return "/bande ally <bandename>";
    }

    @Override
    public void perform(Player p, String... args) {
        //Player is in gang
        if (gangManaging.playerInGangPredicate.test(p.getUniqueId())) {
            if (gangManaging.gangExistsPredicate.test(args[1])) {

                this.gangManaging.requestAlly(
                        this.gangManaging.getGangByUuidFunction.apply(p.getUniqueId()),
                        this.gangManaging.getGangByNameFunction.apply(args[1]),
                        p
                );

            } else p.sendMessage(this.mp.gangDoesNotExists.replace("{name}", args[1]));
        } else p.sendMessage(this.mp.notInGang);
    }


}
