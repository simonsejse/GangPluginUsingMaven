package dk.simonwinther.inventorymanaging;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public abstract class AbstractMenu implements InventoryHolder
{

    protected UUID playerUUID;
    protected Inventory inventory;
    protected abstract String getName();
    protected abstract int getSize();

    public AbstractMenu(UUID playerUUID)
    {
        this();
        this.playerUUID = playerUUID;
    }

    public AbstractMenu()
    {
        this.inventory = Bukkit.createInventory(this, getSize(), getName());
    }

    protected void setItem(int index, ItemStack item)
    {
        inventory.setItem(index, item);
    }

    public abstract void onGuiClick(int slot, ItemStack item, Player whoClicked, ClickType clickType);

}
