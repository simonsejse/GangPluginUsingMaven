package dk.simonwinther.inventorymanaging;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public abstract class Menu implements InventoryHolder
{

    protected UUID playerUuid;

    public Menu(UUID playerUuid)  //Chaining constructors
    {
        this();
        this.playerUuid = playerUuid;
    }

    public Menu()
    {
        inventory = Bukkit.createInventory(this, getSize(), getName());
    }



    protected abstract String getName();

    protected abstract int getSize();

    protected Inventory inventory;

    protected void setItem(int index, ItemStack item)
    {
        inventory.setItem(index, item);
    }

    public abstract void onGuiClick(int slot, ItemStack item, Player whoClicked, ClickType clickType);

}
