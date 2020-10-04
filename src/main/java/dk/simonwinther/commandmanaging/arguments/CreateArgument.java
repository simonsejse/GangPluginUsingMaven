package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.commandmanaging.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;

public class CreateArgument implements CommandArguments
{
    private MainPlugin plugin;
    private List<String> bannedWords;

    public CreateArgument(MainPlugin plugin){
        this.plugin = plugin;
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
            p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NO_SPACE));
        } else
        {
            UUID playerUuid = p.getUniqueId();
            if (!(GangManaging.playerInGangPredicate.test(playerUuid)))
            {
                if (!(GangManaging.gangExistsPredicate.test(args[1])))
                {
                    if (plugin.getEconomy().getBalance(Bukkit.getOfflinePlayer(p.getUniqueId())) >= GangManaging.getGangCost())
                    {
                        args[1] = args[1].replace(" ","");
                        if (nameLengthFunc.apply(args[1])){
                            if(!nameContainsWordsFunc.apply(args[1])){

                                //Check player can afford, once added economy.
                                GangManaging.createNewGangBiConsumer.accept(playerUuid, args[1]);
                                p.sendMessage((plugin.getChatUtil().color(plugin.getChatUtil().GANG_CREATED.replace("{name}", args[1]))));
                                gangCreatedMsg(p, args[1]);
                                plugin.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()), GangManaging.getGangCost());
                            } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().CONTAINS_BAD_WORDS));
                        } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().MAX_MIN_GANG_NAME_LENGTH_REACHED));
                    } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().CANT_AFFORD_GANG.replace("{0}", String.valueOf(GangManaging.getGangCost()))));
                } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().GANG_EXISTS.replace("{name}", args[1])));
            } else p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().ALREADY_IN_GANG));
        }
    }

    public void gangCreatedMsg(Player p, String gang)
    {
        Bukkit.getOnlinePlayers()
                .forEach(_localPlayer ->
                {
                    //Check if player name is same as the guy who created, dont wanna send 2 messages.
                    if (!(_localPlayer.getName().equalsIgnoreCase(p.getName())))
                        _localPlayer.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().SUCCESSFULLY_CREATED_GANG_GLOBAL.replace("{player}", p.getName()).replace("{name}", gang)));
                });
        p.sendMessage(ChatColor.translateAlternateColorCodes('&',"&c&l-"+GangManaging.getGangCost()+"&f$"));
    }

    public Function<String, Boolean> nameLengthFunc = (name) -> name.length() <= plugin.getChatUtil().MAX_GANG_NAME_LENGTH && name.length() >= plugin.getChatUtil().MIN_GANG_NAME_LENGTH;
    public Function<String, Boolean> nameContainsWordsFunc = (name) -> bannedWords.stream().anyMatch(name::contains);
}
