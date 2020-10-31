package dk.simonwinther.utility;

import dk.simonwinther.Builders.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.IntPredicate;
import java.util.stream.IntStream;

public class InventoryUtility
{

    public static IntPredicate MENU_PREDICATE_ONE = t -> t < 10 && t != 4 || t > 16 && t < 19 || t > 25 && t < 28 || t > 34 && t < 37 || t > 43 && t != 47 && t != 49 && t != 51;
    public static IntPredicate MENU_PREDICATE_TWO = t -> t < 10 || t > 16 && t < 19 || t > 25 && t < 28 || t > 34 && t != 49;
    public static IntPredicate MENU_PREDICATE_THREE = t -> t < 10 || t > 16 && t < 19 || t > 25 && t < 28 || t > 34 && t != 47 && t != 49 && t != 51;
    public static IntPredicate MENU_PREDICATE_FOUR = t -> t != 2 && t != 4 && t != 6 && t < 19 || t > 25 && t < 28 || t > 34 && t < 37 || t > 43 && t != 49;
    //public IntPredicate menuLookFivePredicate = t -> t < 10 | t > 16 && t < 19 | t > 25 && t < 28 | t > 34 && t != 40;
    public static IntPredicate MENU_PREDICATE_FIVE = t -> t < 10 || t > 16 && t < 19 || t > 25 && t < 28 || t > 34 && t < 37 || t > 43 && t != 47 && t != 49 && t != 51;
    public static IntPredicate MENU_PREDICATE_SIX = t -> t < 10 || t > 16 && t < 19 || t > 25 && t < 28 || t > 34 && t < 37 || t > 43 && t != 49;

    public static void decorate(Inventory inv, IntPredicate intPredicate, ItemStack item, boolean setBed){
        IntStream.range(0, 9 * 6)
                .filter(intPredicate)
                .forEach(t -> inv.setItem(t, new ItemBuilder(item).setItemName(" ").buildItem()));
        if (setBed) inv.setItem(49, new ItemBuilder(Material.BED).setItemName("&c&lTilbage").setLore("&fKlik her for at", "&fgå tilbage til forrige menu").buildItem());
    }


    public static final int BACK_SLOT = 49;
    //TODO: Use GangAccess instead
    public static final int ECONOMY_SLOT = 10, MEMBERS_SLOT = 12, GANG_INFORMATION_SLOT = 14, LIMITS_SLOT = 16, LEVEL_SLOT = 28, ALLY_SLOT = 30, ENEMY_SLOT = 32, GANG_SHOP_SLOT = 34, PERMISSION_SLOT = 47, DELETE_SLOT = 51;
    public static final int OTHERS_SLOT = 24, MEMBER_AND_OPEN_SHOP_SLOT = 20, GANG_OR_CREATION_SLOT = 22;


}
