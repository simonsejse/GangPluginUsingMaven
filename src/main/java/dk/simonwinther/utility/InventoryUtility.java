package dk.simonwinther.utility;

import dk.simonwinther.Builders.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.IntPredicate;
import java.util.stream.IntStream;

public class InventoryUtility
{

    public static IntPredicate menuLookOnePredicate = t -> t < 10 && t != 4 || t > 16 && t < 19 || t > 25 && t < 28 || t > 34 && t < 37 || t > 43 && t != 47 && t != 49 && t != 51;
    public static IntPredicate menuLookTwoPredicate = t -> t < 10 || t > 16 && t < 19 || t > 25 && t < 28 || t > 34 && t != 49;
    public static IntPredicate menuLookThreePredicate = t -> t < 10 || t > 16 && t < 19 || t > 25 && t < 28 || t > 34 && t != 47 && t != 49 && t != 51;
    public static IntPredicate menuLookFourPredicate = t -> t != 2 && t != 4 && t != 6 && t < 19 || t > 25 && t < 28 || t > 34 && t < 37 || t > 43 && t != 49;
    //public static IntPredicate menuLookFivePredicate = t -> t < 10 | t > 16 && t < 19 | t > 25 && t < 28 | t > 34 && t != 40;
    public static IntPredicate menuLookSixPredicate = t -> t < 10 || t > 16 && t < 19 || t > 25 && t < 28 || t > 34 && t < 37 || t > 43 && t != 47 && t != 49 && t != 51;
    public static IntPredicate menuLookSevenPredicate = t -> t < 10 || t > 16 && t < 19 || t > 25 && t < 28 || t > 34 && t < 37 || t > 43 && t != 49;

    public static void decorate(Inventory inv, IntPredicate intPredicate, ItemStack item, boolean setBed){
        IntStream.range(0, 9 * 6)
                .filter(intPredicate)
                .forEach(t -> inv.setItem(t, new ItemBuilder(item).setItemName(" ").buildItem()));
        if (setBed) inv.setItem(49, new ItemBuilder(Material.BED).setItemName("&c&lTilbage").setLore("&fKlik her for at", "&fgå tilbage til forrige menu").buildItem());
    }


    public static final int backSlot = 49;
    public static final int economySlot = 10, membersSlot = 12, gangInformationSlot = 14, limitsSlot = 16, levelSlot = 28, allySlot = 30, enemySlot = 32, gangShopSlot = 34, permissionSlot = 47, deleteSlot = 51;
    public static final int othersSlot = 24, memberAndOpenShopSlot = 20, gangOrCreationSlot = 22;


}
