package dk.simonwinther.commandmanaging.arguments;

import dk.simonwinther.commandmanaging.CommandArguments;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.concurrent.*;

public class TestCommandArgument implements CommandArguments
{

    @Override
    public String getAlias()
    {
        return "test";
    }

    @Override
    public String getArgument()
    {
        return "testing";
    }

    @Override
    public String usage()
    {
        return "/bande test";
    }

    @Override
    public void perform(Player p, String... args)
    {
        /*
        CompletableFuture<Inventory> completableFuture = CompletableFuture.supplyAsync(() -> new TestMenu().getInventory());
        try {
            p.sendMessage("Creating inventory..");
            p.openInventory(completableFuture.get());
            p.sendMessage("Opening inventory..");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
         */
    }
}
