package dk.simonwinther.commandmanaging;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.utility.GangManaging;
import com.simonsejse.bande.commandmanaging.arguments.*;
import dk.simonwinther.inventorymanaging.menus.infomenu.InfoMenu;
import dk.simonwinther.inventorymanaging.menus.mainmenu.MainMenu;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class GangCommand implements CommandExecutor
{

    private CommandArguments[] commandArgumentsList;
    private MainPlugin plugin;

    public GangCommand(MainPlugin plugin)
    {
        this.plugin = plugin;
        commandArgumentsList = new CommandArguments[]
        {
            new CreateArgument(plugin),
            new DeleteArgument(plugin),
            new InfoArgument(plugin),
            new KickArgument(plugin),
            new InviteArgument(plugin),
            new JoinArgument(plugin),
            new RankArgument(plugin),
            new AllyArgument(plugin),
            new EnemyArgument(plugin),
            new LeaveArgument(plugin),
            new BankArgument(plugin),
            new AllianceChat(plugin),
            new BandeChat(plugin),
            new AdminArgument(plugin),
            new SpawnBotArgument(),
            new GangDamageArgument(plugin)
        };

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            return true;
        }
        Player p = (Player) sender;
        if (p.getGameMode() == GameMode.CREATIVE)
        {
            p.sendMessage("Ud af gamemode! - ændres i config");
            return true;
        }
        if (args.length > 0)
        {
            Optional<CommandArguments> commandArgumentsOptional = Arrays.stream(commandArgumentsList)
                    .filter(commandArguments -> commandArguments.getAlias().equalsIgnoreCase(args[0])
                            || commandArguments.getArgument().equalsIgnoreCase(args[0]))
                    .findAny();

            commandArgumentsOptional.ifPresent(commandArguments ->
            {
                try
                {
                    commandArguments.perform(p, args);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            });
            if (commandArgumentsOptional.isPresent()) return false;
            //Search for Gang Name
            if (GangManaging.gangMap.containsKey(args[0]))
            {
                p.openInventory(new InfoMenu(plugin, GangManaging.getGangByNameFunction.apply(args[0]), false).getInventory());
                return true;
            }
            //Search for Player Gang
            if (Bukkit.getPlayer(args[0]) != null){
                UUID argsUuid = Bukkit.getPlayer(args[0]).getUniqueId();
                if (GangManaging.namesOfGang.containsKey(argsUuid))
                {
                    p.openInventory(new InfoMenu(plugin, GangManaging.getGangByUuidFunction.apply(argsUuid), false).getInventory());
                    return true;
                }
            }
        }
        if (GangManaging.playerInGangPredicate.test(p.getUniqueId()))
            p.openInventory(new MainMenu(plugin, p.getUniqueId(), true).getInventory());
        else p.openInventory(new MainMenu(plugin, p.getUniqueId(), false).getInventory());
        return false;
    }

}
