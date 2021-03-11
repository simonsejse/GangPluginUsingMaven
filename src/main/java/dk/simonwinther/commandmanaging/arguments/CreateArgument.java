package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.manager.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.entity.Player;

public class CreateArgument implements CommandArguments
{
    private final MainPlugin plugin;
    private final GangManaging gangManager;
    private final MessageProvider mp;

    public CreateArgument(GangManaging gangManaging, MainPlugin plugin){
        this.gangManager = gangManaging;
        this.plugin = plugin;
        this.mp = this.plugin.getMessageProvider();
    }


    @Override
    public String getAlias()
    {
        return "opret";
    }

    @Override
    public String getArgument()
    {
        return "create";
    }

    @Override
    public String usage()
    {
        return "Forkert brug: /bande create <navn>";
    }

    @Override
    public void perform(Player p, String... args)
    {
        if (args.length == 2){
            this.gangManager.createNewGang(p.getPlayer(), args[1]);
        }else if (args.length > 2){
            p.sendMessage(this.mp.noSpace);
            p.sendMessage(usage());
        }else{
            p.sendMessage(this.mp.gangNameDoesNotMeetRequirements);
            p.sendMessage(usage());
        }
    }




}
