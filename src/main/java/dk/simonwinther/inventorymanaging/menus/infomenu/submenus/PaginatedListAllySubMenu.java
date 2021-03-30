package dk.simonwinther.inventorymanaging.menus.infomenu.submenus;

import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.MainPlugin;
import dk.simonwinther.constants.ColorDataEnum;
import dk.simonwinther.constants.ColorIndexEnum;
import dk.simonwinther.inventorymanaging.AbstractPaginatedMenu;
import dk.simonwinther.inventorymanaging.menus.infomenu.InfoMenu;
import dk.simonwinther.manager.Gang;
import dk.simonwinther.manager.GangManaging;
import dk.simonwinther.utility.InventoryUtility;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.IntPredicate;

public class PaginatedListAllySubMenu extends AbstractPaginatedMenu
{
    private final MainPlugin plugin;
    private final MessageProvider mp;
    private final Gang gang;
    private final GangManaging gangManaging;
    private final InfoMenu infoMenu;

    private final ItemBuilder skullBuilder = new ItemBuilder(Material.SKULL_ITEM, 1, SkullType.PLAYER);

    public PaginatedListAllySubMenu(MainPlugin plugin, Gang gang, InfoMenu infoMenu)
    {
        this.plugin = plugin;
        this.mp = this.plugin.getMessageProvider();
        this.gang = gang;
        this.gangManaging = this.plugin.getGangManaging();
        this.infoMenu = infoMenu;
        this.setItemStacksForInventory(gang.getAllies()
                .values()
                .stream()
                .map(gangManaging.getGangByNameFunction::apply)
                .map(allyGang -> skullBuilder.setPlayerSkull(allyGang.getOwnerName()).setItemName("&a&l" + allyGang.getGangName()).setLore("&7Leder: &f" + allyGang.getOwner(), "&7Co-Leader: &f" + allyGang.getCoOwner(), "&7Level: &f" + allyGang.getGangLevel(), "", "&7Klik her for at fjerne", "&7denne alliance").buildItem())
                .toArray(ItemStack[]::new)
        );
    }

    @Override
    protected String getName() {
        return "Dine allierede";
    }

    @Override
    protected int backPageSlot() {
        return 37;
    }

    @Override
    protected int frontPageSlot() {
        return 43;
    }

    @Override
    protected IntPredicate menuDecoration() {
        return InventoryUtility.MENU_PREDICATE_ONE;

    }

    @Override
    protected ItemStack itemDecoration() {
        return new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.LIME.value[ColorIndexEnum.STAINED_GLASS.index]);
    }

    @Override
    protected int offsetStart() {
        return 10;
    }

    @Override
    protected int availableSlots() {
        return 26;
    }

    @Override
    protected int getSizeOfInventory() {
        return 9*6;
    }

    @Override
    public void onGuiClick(int slot, ItemStack item, Player whoClicked, ClickType clickType) {
        UUID uuid = whoClicked.getUniqueId();
        if (slot == InventoryUtility.BACK_SLOT) whoClicked.openInventory(this.infoMenu.getInventory());
        else if (slot == 4)
        {
            plugin.getEventHandling().addPlayerToAwaitAllyRequest.accept(uuid);
            whoClicked.getOpenInventory().close();
            whoClicked.sendMessage(this.mp.whoToAllyChat);
        } else if (slot == 47) whoClicked.openInventory(new PaginatedAllyInvitations(this.gang, this.gangManaging, this, this.plugin).getInventory());
        else if (slot == 51) whoClicked.openInventory(new AllyRequestsPaginated(this.plugin, this.gang, this.gangManaging, this).getInventory());
        else if (item.getType() == Material.SKULL_ITEM)
        {
            //remove gang as allies
            String gangName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            Gang otherGang = this.gangManaging.getGangByNameFunction.apply(gangName);
            Gang playerGang = this.gangManaging.getGangByUuidFunction.apply(uuid);

            if (!otherGang.getAllies().values().contains(playerGang.getGangName()) || !playerGang.getAllies().values().contains(otherGang.getGangName())) {
                whoClicked.sendMessage(ChatColor.translateAlternateColorCodes('&',"&8&l| &e&lVENT &8&l| &eVent venligst, vær tålmodig.."));
                return;
            }

            playerGang.getAllies().remove(gangName.toLowerCase());
            otherGang.getAllies().remove(playerGang.getGangName().toLowerCase());

            this.gangManaging.sendTeamMessage.accept(otherGang, this.mp.noLongerAllies.replace("{name}", playerGang.getGangName()));
            this.gangManaging.sendTeamMessage.accept(playerGang, this.mp.noLongerAllies.replace("{name}", gangName));
        }
    }

    @Override
    protected void addSeparateItems() {
        inventories.stream().forEach(inventory -> {
            inventory.setItem(4, skullBuilder.setPlayerSkull(gang.getOwnerName()).setItemName("&a&lAlliance").setLore("&7Klik her for at spørge", "&7en bande om alliance").buildItem());
            inventory.setItem(47, skullBuilder.setPlayerSkull(gang.getOwnerName()).setItemName("&a&lInvitationer").setLore("&7Klik her for at se hvilken bander der", "&7har spurgt din bande om alliance").buildItem());
            inventory.setItem(InventoryUtility.BACK_SLOT, InventoryUtility.BACK_ITEM);
            inventory.setItem(51, skullBuilder.setPlayerSkull(gang.getOwnerName()).setItemName("&a&lAlliance anmodninger").setLore("&7Klik her for at se en liste over", "&7bander som er blevet spurgt om alliance").buildItem());
        });
    }

    @Override
    public Inventory getInventory() {
        return inventories.get(0);
    }

}
