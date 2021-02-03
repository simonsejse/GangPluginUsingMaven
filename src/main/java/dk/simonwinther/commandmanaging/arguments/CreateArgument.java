package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;

public class CreateArgument implements CommandArguments
{
    private final MainPlugin plugin;
    private final GangManaging gangManager;
    private final MessageProvider mp;

    private List<String> bannedWords;

    public CreateArgument(GangManaging gangManaging, MainPlugin plugin){
        this.gangManager = gangManaging;
        this.plugin = plugin;
        this.mp = this.plugin.getMessageProvider();
        bannedWords = plugin.getCustomSettingsProvider().getNpcProvider().getBannedWords();
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
        if (args.length != 2)
        {
            p.sendMessage(this.mp.noSpace);
        } else
        {
            UUID playerUuid = p.getUniqueId();
            if (!(gangManager.playerInGangPredicate.test(playerUuid)))
            {
                if (!(gangManager.gangExistsPredicate.test(args[1])))
                {
                    if (plugin.getEconomy().getBalance(Bukkit.getOfflinePlayer(p.getUniqueId())) >= gangManager.GANG_COST)
                    {
                        args[1] = args[1].replace(" ","");
                        if (doesGangNameFollowRequirements(args[1])){
                            //Check player can afford, once added economy.
                            gangManager.createNewGangBiConsumer.accept(playerUuid, args[1]);
                            p.sendMessage(this.mp.gangCreated.replace("{name}", args[1]));
                            gangCreatedMsg(p, args[1]);
                            plugin.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()), gangManager.GANG_COST);
                        } else p.sendMessage(this.mp.gangNameDoesNotMeetRequirements);
                    } else p.sendMessage(this.mp.cantAffordGang.replace("{0}", String.valueOf(gangManager.GANG_COST)));
                } else p.sendMessage(this.mp.gangExists.replace("{name}", args[1]));
            } else p.sendMessage(this.mp.alreadyInGang);
        }
    }

    public void gangCreatedMsg(Player p, String gang)
    {
        Bukkit.getOnlinePlayers()
                .forEach(_localPlayer ->
                {
                    //Check if player name is same as the guy who created, dont wanna send 2 messages.
                    if (!(_localPlayer.getName().equalsIgnoreCase(p.getName())))
                        _localPlayer.sendMessage(this.mp.successfullyCreatedGangGlobal.replace("{player}", p.getName()).replace("{name}", gang));
                });
        p.sendMessage(ChatColor.translateAlternateColorCodes('&',"&c&l-"+gangManager.GANG_COST+"&f$"));
    }

    public boolean doesGangNameFollowRequirements(String gangName){
        return gangName.length() <= mp.maxGangNameLength
                && gangName.length() >= mp.minGangNameLength
                && bannedWords.stream().anyMatch(gangName::contains);
    }

}
