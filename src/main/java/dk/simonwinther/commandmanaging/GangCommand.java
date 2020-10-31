package dk.simonwinther.commandmanaging;

import dk.simonwinther.Gang;
import dk.simonwinther.MainPlugin;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.commandmanaging.arguments.*;
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

    private final CommandArguments[] commandArgumentsList;
    private final MainPlugin plugin;
    private final GangManaging gangManaging;

    public GangCommand(GangManaging gangManaging, MainPlugin plugin)
    {
        this.gangManaging = gangManaging;
        this.plugin = plugin;
        commandArgumentsList = new CommandArguments[]
        {
            new CreateArgument(gangManaging, plugin),
            new DeleteArgument(gangManaging, plugin),
            new InfoArgument(gangManaging, plugin),
            new KickArgument(gangManaging, plugin),
            new InviteArgument(gangManaging, plugin),
            new JoinArgument(gangManaging, plugin),
            new RankArgument(gangManaging, plugin),
            new AllyArgument(gangManaging, plugin),
            new EnemyArgument(gangManaging, plugin),
            new LeaveArgument(gangManaging, plugin),
            new BankArgument(gangManaging, plugin),
            new AllianceChat(gangManaging, plugin),
            new BandeChat(gangManaging, plugin),
            new AdminArgument(gangManaging, plugin),
            new SpawnBotArgument(),
            new GangDamageArgument(gangManaging, plugin)
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
            if (gangManaging.gangMap.containsKey(args[0]))
            {
                p.openInventory(new InfoMenu(gangManaging, plugin, gangManaging.getGangByNameFunction.apply(args[0]), false).getInventory());
                return true;
            }

            //Search for Player Gang
            if (Bukkit.getPlayer(args[0]) != null){
                UUID argsUuid = Bukkit.getPlayer(args[0]).getUniqueId();
                if (gangManaging.namesOfGang.containsKey(argsUuid))
                {
                    p.openInventory(new InfoMenu(gangManaging, plugin, gangManaging.getGangByUuidFunction.apply(argsUuid), false).getInventory());
                    return true;
                }
            }
        }
        if (gangManaging.playerInGangPredicate.test(p.getUniqueId()))
            p.openInventory(new MainMenu(gangManaging, plugin, p.getUniqueId(), true).getInventory());
        else p.openInventory(new MainMenu(gangManaging, plugin, p.getUniqueId(), false).getInventory());
        return false;
    }

}
