package dk.simonwinther.inventorymanaging.menus.mainmenu.submenus;

import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.enums.ColorDataEnum;
import dk.simonwinther.enums.ColorIndexEnum;
import dk.simonwinther.inventorymanaging.Menu;
import dk.simonwinther.inventorymanaging.menus.mainmenu.MainMenu;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.utility.InventoryUtility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class OtherSubMenu extends Menu
{
    private MainMenu mainMenu;
    private GangManaging gangManaging;

    public OtherSubMenu(GangManaging gangManaging, MainMenu mainMenu)
    {
        this.mainMenu = mainMenu;
        this.gangManaging = gangManaging;
    }

    @Override
    protected String getName()
    {
        return "Andet";
    }

    @Override
    protected int getSize()
    {
        return 9 * 6;
    }

    @Override
    public void onGuiClick(int slot, ItemStack item, Player whoClicked, ClickType clickType)
    {
        switch (item.getType())
        {
            case BED:
                //whoClicked.getOpenInventory().close();
                whoClicked.openInventory(mainMenu.getInventory());
                break;
            case DIAMOND_SWORD:
                //whoClicked.getOpenInventory().close();
                whoClicked.openInventory(new LeaderBoardSubMenu(this.gangManaging, this).getInventory());
                break;
        }

    }


    @Override
    public Inventory getInventory()
    {
        InventoryUtility.decorate(super.inventory, InventoryUtility.MENU_PREDICATE_TWO, new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.LIME.value[ColorIndexEnum.STAINED_GLASS.index]), true);
        super.setItem(20, new ItemBuilder(Material.DIAMOND_SWORD).setItemName("&a&lToplister").setLore("&fSe top 10 over", "&a - &fFlest drab", "&a - &fFlest vagtdrab", "&a - &fFlest penge", "&a - &fFlest døde").addFlags(ItemFlag.HIDE_ATTRIBUTES).buildItem());
        super.setItem(24, new ItemBuilder(Material.BOOK).setItemName("&a&lKommandoer").setLore("&a/bande <bande/spiller>", "&a/bandechat&f, &a/bac&f og &a/bchat &8- &fBandechat", "&a/allychat&f, &a/ac&f og &a/achat&8 - &fAlliance chat", "&a/bc damage &8-&f Slår bande skade til/fra", "&a/bc bank <antal> &8- &fIndsætter penge i bande banken", "&a/bc invite <spiller> &8- &fGenvej til at invitere til banden.", "&a/bc ally <bande> &8- &fGenvej til at spørge en bande om alliance", "&a/bc enemy <bande> &8- &fGenvej til at gøre en bande til rival").buildItem());
        return super.inventory;
    }
}
