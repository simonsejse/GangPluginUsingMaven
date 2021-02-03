package dk.simonwinther.inventorymanaging.menus.infomenu.submenus;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.Gang;
import dk.simonwinther.enums.ColorDataEnum;
import dk.simonwinther.enums.ColorIndexEnum;
import dk.simonwinther.inventorymanaging.Menu;
import dk.simonwinther.utility.InventoryUtility;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ListInvitationsSubMenu extends Menu
{
    private Gang gang;
    private final MemberSubMenu memberSubMenu;

    private final MessageProvider mp;

    public ListInvitationsSubMenu(MainPlugin plugin, Gang gang, MemberSubMenu memberSubMenu){
        this.mp = plugin.getMessageProvider();
        this.gang = gang;
        this.memberSubMenu = memberSubMenu;
    }

    @Override
    protected String getName()
    {
        return "Inviteret til bande";
    }

    @Override
    protected int getSize()
    {
        return 9*6;
    }

    @Override
    public void onGuiClick(int slot, ItemStack item, Player whoClicked, ClickType clickType)
    {
        Material type = item.getType();
        if (type == Material.BED) whoClicked.openInventory(memberSubMenu.getInventory());
        else if (type == Material.SKULL_ITEM)
        {
            if (gang.getMemberInvitations().contains(ChatColor.stripColor(item.getItemMeta().getDisplayName())))
            {
                gang.getMemberInvitations().remove(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                super.inventory.clear();
                whoClicked.openInventory(this.getInventory());

            }else whoClicked.sendMessage(this.mp.notInSameGang);
        }
    }


    private int slot;
    @Override
    public Inventory getInventory()
    {
        slot = 10;
        InventoryUtility.decorate(super.inventory, InventoryUtility.MENU_PREDICATE_SIX, new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.MAGENTA.value[ColorIndexEnum.STAINED_GLASS.index]), true);

        gang.getMemberInvitations()
                .stream()
                .sorted()
                .forEach(s -> {
                    if (slot > 43) return;
                    if (slot == 17 | slot == 26 | slot == 35) slot+=2;
                    super.setItem(slot++, new ItemBuilder(Material.SKULL_ITEM, 1, SkullType.PLAYER).setItemName("&a&l"+s).setLore("&fKlik for at fjerne", "&fspillerens invitation").buildItem());
                });

        super.setItem(49, new ItemBuilder(Material.BED).setItemName("&c&lTilbage").setLore("&fKlik her for at", "&fgå tilbage til forrige menu").buildItem());
        return super.inventory;
    }
}
