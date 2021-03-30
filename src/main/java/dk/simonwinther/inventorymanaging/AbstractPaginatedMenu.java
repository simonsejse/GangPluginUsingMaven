package dk.simonwinther.inventorymanaging;

import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.utility.InventoryUtility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

public abstract class   AbstractPaginatedMenu implements InventoryHolder {

    protected int indexItem = 0;

    protected int currentPageIndex = 0;

    private final ItemBuilder previousPageItem = new ItemBuilder();
    private final ItemBuilder nextPageItem = new ItemBuilder();

    public void initializeInventories(){
        if (itemStacks == null){
            Bukkit.getLogger().warning("ItemStacks is null, perhaps code hasn't made the ItemStacks for the inventory.");
            return;
        }
        int amountOfItems = itemStacks.length;
        int amountOfInventoriesNeeded = (int) Math.ceil(((double) amountOfItems/availableSlots()));
        if (amountOfInventoriesNeeded == 0) amountOfInventoriesNeeded++;
        for(int page = 1; page <= amountOfInventoriesNeeded; page++){
            final Inventory currentInventory = Bukkit.createInventory(this, getSizeOfInventory(), getName()+" "+page+"/"+amountOfInventoriesNeeded);
            InventoryUtility.decorate(currentInventory, menuDecoration(), itemDecoration(), false); //True if you want go back slot
            final int offsetStart = offsetStart();

            for(int slot = 0+offsetStart;  slot < getSizeOfInventory() && this.indexItem < itemStacks.length; slot++){
                if (menuDecoration().negate().test(slot) && slot != backPageSlot() && slot != frontPageSlot()) currentInventory.setItem(slot, itemStacks[this.indexItem++]);
                if(this.indexItem >= (availableSlots() * page)){
                    break;
                }
            }
            setupPageArrows(currentInventory, page, amountOfInventoriesNeeded);
            inventories.add(currentInventory);
        }
        this.addSeparateItems();
    }


    protected void setupPageArrows(Inventory inventory, int page, int amountOfInventoriesNeeded){
        if (page == amountOfInventoriesNeeded) this.nextPageItem.setMaterial(Material.BARRIER).setItemName("&cIkke flere sider...").setLore("&7Der er ikke flere sider til menuen.");
        else this.nextPageItem.setMaterial(Material.ARROW).setItemName("&aNæste side &2&l»").setLore("&7Klik her for at gå frem til næste side.");
        if (page == 1) this.previousPageItem.setMaterial(Material.BARRIER).setItemName("&4Du kan ikke gå længere tilbage").setLore("&cDu er allerede på den første side", "&cdu kan ikke komme længere tilbage!");
        else this.previousPageItem.setMaterial(Material.BED).setItemName("&4&l« &4Forrige side").setLore("&cKlik her for at gå til forrige side.");
        inventory.setItem(backPageSlot(), this.previousPageItem.buildItem());
        inventory.setItem(frontPageSlot(), this.nextPageItem.buildItem());
    }

    protected abstract String getName();
    protected abstract int backPageSlot();
    protected abstract int frontPageSlot();
    protected abstract IntPredicate menuDecoration();
    protected abstract ItemStack itemDecoration();

    protected ItemStack[] itemStacks;
    protected abstract int offsetStart();
    protected abstract int availableSlots();
    protected abstract int getSizeOfInventory();
    protected final List<Inventory> inventories = new ArrayList<>();
    public abstract void onGuiClick(int slot, ItemStack item, Player whoClicked, ClickType clickType);
    protected abstract void addSeparateItems();

    /**
     *
     * @param itemStacks the items for the total of inventories, whereas after the initializeInventories() method is invoked and the addSeparateItems() is invoked after
     * This method is the only method you have to call to start the paginated menu
     */
    public void setItemStacksForInventory(ItemStack[] itemStacks) {
        if (itemStacks == null) this.itemStacks = new ItemStack[0];
        else this.itemStacks = itemStacks;
        this.initializeInventories();
    }

    /**
     *
     * Uses in our EventHandling to automatically go back and forth pages in all menus!
     */
    public int getBackPageSlot(){
        return backPageSlot();
    }
    public int getNextPageSlot(){
        return frontPageSlot();
    }
    public int getCurrentPageIndex() {
        return currentPageIndex;
    }
    public List<Inventory> getInventories() {
        return inventories;
    }
    public void setCurrentPageIndex(int currentPageIndex) {
        this.currentPageIndex = currentPageIndex;
    }

}
