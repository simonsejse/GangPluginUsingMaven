package dk.simonwinther.commandmanaging;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.manager.GangManaging;
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
            new TestCommandArgument(),
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
        /**
         * TODO:
         *
         * /bande ally - java.lang.ArrayIndexOutOfBoundsException: 1
         * [05:41:15 WARN]: java.lang.ArrayIndexOutOfBoundsException: 1
         * [05:41:15 WARN]:        at dk.simonwinther.commandmanaging.arguments.AllyArgument.perform(AllyArgument.java:52)
         * [05:41:15 WARN]:        at dk.simonwinther.commandmanaging.GangCommand.lambda$onCommand$1(GangCommand.java:76)
         * [05:41:15 WARN]:        at java.util.Optional.ifPresent(Optional.java:159)
         * [05:41:15 WARN]:        at dk.simonwinther.commandmanaging.GangCommand.onCommand(GangCommand.java:72)
         * [05:41:15 WARN]:        at org.bukkit.command.PluginCommand.execute(PluginCommand.java:44)
         * [05:41:15 WARN]:        at org.bukkit.command.SimpleCommandMap.dispatch(SimpleCommandMap.java:141)
         * [05:41:15 WARN]:        at org.bukkit.craftbukkit.v1_8_R3.CraftServer.dispatchCommand(CraftServer.java:641)
         * [05:41:15 WARN]:        at net.minecraft.server.v1_8_R3.PlayerConnection.handleCommand(PlayerConnection.java:1162)
         * [05:41:15 WARN]:        at net.minecraft.server.v1_8_R3.PlayerConnection.a(PlayerConnection.java:997)
         * [05:41:15 WARN]:        at net.minecraft.server.v1_8_R3.PacketPlayInChat.a(PacketPlayInChat.java:45)
         * [05:41:15 WARN]:        at net.minecraft.server.v1_8_R3.PacketPlayInChat.a(PacketPlayInChat.java:1)
         * [05:41:15 WARN]:        at net.minecraft.server.v1_8_R3.PlayerConnectionUtils$1.run(SourceFile:13)
         * [05:41:15 WARN]:        at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
         * [05:41:15 WARN]:        at java.util.concurrent.FutureTask.run(FutureTask.java:266)
         * [05:41:15 WARN]:        at net.minecraft.server.v1_8_R3.SystemUtils.a(SourceFile:44)
         * [05:41:15 WARN]:        at net.minecraft.server.v1_8_R3.MinecraftServer.B(MinecraftServer.java:715)
         * [05:41:15 WARN]:        at net.minecraft.server.v1_8_R3.DedicatedServer.B(DedicatedServer.java:374)
         * [05:41:15 WARN]:        at net.minecraft.server.v1_8_R3.MinecraftServer.A(MinecraftServer.java:654)
         * [05:41:15 WARN]:        at net.minecraft.server.v1_8_R3.MinecraftServer.run(MinecraftServer.java:557)
         * [05:41:15 WARN]:        at java.lang.Thread.run(Thread.java:748)
         */

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
                p.openInventory(new InfoMenu(plugin, gangManaging.getGangByNameFunction.apply(args[0]), false, null).getInventory());
                return true;
            }

            //Search for Player Gang
            if (Bukkit.getPlayer(args[0]) != null){
                UUID argsUuid = Bukkit.getPlayer(args[0]).getUniqueId();
                if (gangManaging.userGangMap.containsKey(argsUuid))
                {
                    p.openInventory(new InfoMenu(plugin, gangManaging.getGangByUuidFunction.apply(argsUuid), false, null).getInventory());
                    return true;
                }
            }
        }
        if (gangManaging.playerInGangPredicate.test(p.getUniqueId()))
            p.openInventory(new MainMenu(plugin, p.getUniqueId(), true).getInventory());
        else p.openInventory(new MainMenu(plugin, p.getUniqueId(), false).getInventory());
        return false;
    }

}
