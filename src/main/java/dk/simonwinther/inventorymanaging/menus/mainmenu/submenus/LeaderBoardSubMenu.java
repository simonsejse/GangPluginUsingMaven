package dk.simonwinther.inventorymanaging.menus.mainmenu.submenus;

import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.enums.ColorDataEnum;
import dk.simonwinther.enums.ColorIndexEnum;
import dk.simonwinther.inventorymanaging.Menu;
import dk.simonwinther.utility.InventoryUtility;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;

public class LeaderBoardSubMenu extends Menu
{
    private OtherSubMenu otherSubMenu;
    private int count = 0;
    private StringBuilder stringBuilder;
    private final GangManaging gangManaging;

    public LeaderBoardSubMenu(GangManaging gangManaging, OtherSubMenu otherSubMenu)
    {
        this.otherSubMenu = otherSubMenu;
        this.gangManaging = gangManaging;
    }

    @Override
    protected String getName()
    {
        return "Topliste";
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
                whoClicked.openInventory(otherSubMenu.getInventory());
                break;
        }
    }

    @Override
    public Inventory getInventory()
    {
        InventoryUtility.decorate(super.inventory, InventoryUtility.MENU_PREDICATE_SIX, new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.RED.value[ColorIndexEnum.STAINED_GLASS.index]), true);

        super.setItem(20, new ItemBuilder(Material.IRON_SWORD).setItemName("&a&lFangedrab").setLore(getMostPrisonerKills()).addFlags(ItemFlag.HIDE_ATTRIBUTES).buildItem());
        super.setItem(22, new ItemBuilder(Material.GOLD_SWORD).setItemName("&a&lVagtdrab").setLore(getMostGuardKills()).addFlags(ItemFlag.HIDE_ATTRIBUTES).buildItem());
        super.setItem(24, new ItemBuilder(Material.DIAMOND_SWORD).setItemName("&a&lOfficerdrab").setLore(getMostOfficerKills()).addFlags(ItemFlag.HIDE_ATTRIBUTES).buildItem());
        super.setItem(30, new ItemBuilder(Material.GOLD_INGOT).setItemName("&a&lFlest penge").setLore(getMostMoney()).buildItem());
        super.setItem(32, new ItemBuilder(Material.SKULL_ITEM, 1, SkullType.SKELETON).setItemName("&a&lFlest døde").setLore(getMostDeaths()).buildItem());
        return super.inventory;
    }

    public String getMostDeaths(){
        count = 0;
        stringBuilder = new StringBuilder();
        this.gangManaging.getGangMap()
                .values()
                .stream()
                .sorted(Comparator.comparingInt(Gang::getDeaths))
                .forEach(gang -> {
                    String color = getColor();
                    stringBuilder.append("&8[").append(color).append(count += 1).append(".&8] ").append(color).append(gang.getGangName()).append(" &8[&f").append(gang.getDeaths()).append(" døde&8]\n");
                });
        return stringBuilder.toString();
    }

    public String getMostMoney(){
        count = 0;
        stringBuilder = new StringBuilder();
        this.gangManaging.getGangMap()
                .values()
                .stream()
                .sorted(Comparator.comparing(Gang::getGangBalance).reversed())
                .forEach(gang -> {
                    String color = getColor();
                    stringBuilder.append("&8[" + color+(count += 1) + ".&8] " + color+gang.getGangName() + " &8[&f$" + gang.getGangBalance() + "&8]\n");
                });
        return stringBuilder.toString();
    }

    public String getMostOfficerKills(){
        count = 0;
        stringBuilder = new StringBuilder();
        this.gangManaging.getGangMap()
                .values()
                .stream()
                .sorted(Comparator.comparing(Gang::getOfficerKills).reversed())
                .forEach(gang -> {
                    String color = getColor();
                    stringBuilder.append("&8[" + color+(count += 1) + ".&8] " + color+gang.getGangName() + " &8[&f" + gang.getOfficerKills() + " officer drab&8]\n");
                });
        return stringBuilder.toString();
    }


    public String getMostGuardKills(){
        count = 0;
        stringBuilder = new StringBuilder();
        this.gangManaging.getGangMap()
                .values()
                .stream()
                .sorted(Comparator.comparing(Gang::getGuardKills).reversed())
                .forEach(gang -> {
                    String color = getColor();
                    stringBuilder.append("&8[" + color+(count += 1) + ".&8] " + color+gang.getGangName() + " &8[&f" + gang.getGuardKills() + " vagt drab&8]\n");
                });

        return stringBuilder.toString();
    }

    public String getMostPrisonerKills()
    {
        count = 0;
        stringBuilder = new StringBuilder();
        this.gangManaging
                .getGangMap()
                .values()
                .stream()
                .sorted(Comparator.comparing(Gang::getPrisonerKills).reversed())
                .forEach(gang ->
                {
                    String color = getColor();
                    stringBuilder.append("&8[" + color+(count += 1) + ".&8] " + color+gang.getGangName() + " &8[&f" + gang.getPrisonerKills() + " drab&8]\n");
                });


        return stringBuilder.toString();
    }

    public String getColor(){
        switch (count)
        {
            case 0:
                return "&a";
            case 1:
                return "&e";
            case 2:
                return "&c";
            default:
                return "&f";
        }
    }
}
