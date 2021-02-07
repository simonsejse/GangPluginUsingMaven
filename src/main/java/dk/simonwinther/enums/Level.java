package dk.simonwinther.enums;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Gang;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public enum Level
{

    TWO(Arrays.asList(
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.BREAD) >= 15,
            gang -> gang.getAllies().size() >= 1,
            gang -> gang.getEnemies().size() >= 1,
            gang -> gang.getMembersSorted().size() >= 2
    ), new ItemStack[]{
            new ItemStack(Material.BREAD, 15)
    }, " - Aflever {value} brød til Ali Mustafa\n" +
            " - Have mindst 1 alliance\n" +
            " - Have mindst 1 rival\n" +
            " - Have mindst 2 medlemmer", null),
    THREE(Arrays.asList(
            gang -> gang.getPrisonerKills() >= 25,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.IRON_SWORD) >= 1,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.IRON_HELMET) >= 1,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.IRON_CHESTPLATE) >= 1,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.IRON_LEGGINGS) >= 1,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.IRON_BOOTS) >= 1,
            gang -> gang.getMembersSorted().size() >= 3,
            gang -> gang.getLevelSystem().getPaidForQuest() >= 25000
    ), new ItemStack[]{
            new ItemStack(Material.IRON_SWORD, 1),
            new ItemStack(Material.IRON_HELMET, 1),
            new ItemStack(Material.IRON_CHESTPLATE, 1),
            new ItemStack(Material.IRON_LEGGINGS, 1),
            new ItemStack(Material.IRON_BOOTS,1)
    }, " - Dræb 25 fanger\n" +
            " - Aflever {value} iron sword til Ali Mustafa\n" +
            " - Aflever {value} iron helmet til Ali Mustafa\n" +
            " - Aflever {value} iron chestplate til Ali Mustafa\n" +
            " - Aflever {value} iron leggings til Ali Mustafa\n" +
            " - Aflever {value} iron boots til Ali Mustafa\n" +
            " - Have mindst 3 medlemmer\n" +
            " - Aflever ${moneyVal} til Ali Mustafa", new Integer[]{25000, 25000}), //25000
    FOUR(Arrays.asList(
            gang -> gang.getPrisonerKills() >= 75,
            gang -> gang.getGangDamage() <= 90,
            gang -> gang.gangPermissions.accessToToilets,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.BREAD) >= 47,
            gang -> gang.getLevelSystem().getPaidForQuest() >= 75000,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.IRON_SWORD) >= 6,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.IRON_HELMET) >= 6,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.IRON_CHESTPLATE) >= 6,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.IRON_LEGGINGS) >= 6,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.IRON_BOOTS) >= 6
            ), new ItemStack[]{
                    new ItemStack(Material.BREAD, 47),
                    new ItemStack(Material.IRON_SWORD, 6),
                    new ItemStack(Material.IRON_HELMET, 6),
                    new ItemStack(Material.IRON_CHESTPLATE, 6),
                    new ItemStack(Material.IRON_LEGGINGS, 6),
                    new ItemStack(Material.IRON_BOOTS, 6)
    }," - Dræb 50 fanger\n" +
            " - Køb bandeskaden ned til minimum 90%\n" +
            " - Køb adgang til toiletterne i C\n" +
            " - Aflever {value} brød til Ali Mustafa\n" +
            " - Aflever ${moneyVal} til Ali Mustafa\n" +
            " - Aflever {value} iron sword til Ali Mustafa\n" +
            " - Aflever {value} iron helmet til Ali Mustafa\n" +
            " - Aflever {value} iron chestplade til Ali Mustafa\n" +
            " - Aflever {value} iron leggings til Ali Mustafa\n" +
            " - Aflever {value} iron boots til Ali Mustafa", new Integer[]{75000, 50000}),
    FIVE(Arrays.asList(
            gang -> MainPlugin.getPlugin(MainPlugin.class).getBlockAtPlayerLoc(gang.getOwnerUuid()).equalsIgnoreCase("b"),
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.SUGAR) >= 10,
            gang -> gang.getLevelSystem().getPaidForQuest() >= 175000,
            gang -> gang.getAllyDamage() <= 90,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.DIAMOND_SWORD) >= 1,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.DIAMOND_HELMET) >= 1,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.DIAMOND_CHESTPLATE) >= 1,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.DIAMOND_LEGGINGS) >= 1,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.DIAMOND_BOOTS) >= 1

    ), new ItemStack[] {
            new ItemStack(Material.SUGAR, 10),
            new ItemStack(Material.DIAMOND_SWORD, 1),
            new ItemStack(Material.DIAMOND_HELMET, 1),
            new ItemStack(Material.DIAMOND_CHESTPLATE, 1),
            new ItemStack(Material.DIAMOND_LEGGINGS, 1),
            new ItemStack(Material.DIAMOND_BOOTS, 1)
    }," - Bandelederen skal være i B\n" +
            " - Aflever {value} sugar til Ali Mustafa\n" +
            " - Aflever ${moneyVal} til Ali Mustafa\n" +
            " - Køb allianceskaden ned til minimum 90%\n" +
            " - Aflever {value} diamond sword til Ali Mustafa\n" +
            " - Aflever {value} diamond helmet til Ali Mustafa\n" +
            " - Aflever {value} diamond chestplate til Ali Mustafa\n" +
            " - Aflever {value} diamond leggings til Ali Mustafa\n" +
            " - Aflever {value} diamond boots til Ali Mustafa", new Integer[]{175000, 100000}),
    SIX(Arrays.asList(
            gang -> gang.gangPermissions.accessToFarm,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.SUGAR) >= 35,
            gang -> gang.getGangDamage() <= 80,
            gang -> gang.getAllyDamage() <= 80,
            gang -> gang.getLevelSystem().getPaidForQuest() >= 425000,
            gang -> MainPlugin.getPermissions().playerHas("world", Bukkit.getOfflinePlayer(gang.getOwnerUuid()), "jackpot.iron") //Might not be efficient,
    ), new ItemStack[]{
            new ItemStack(Material.SUGAR, 35)
    }, " - Køb adgang til gården i B\n" +
            " - Aflever {value} sugar til Ali Mustafa\n" +
            " - Køb bandeskaden ned til minimum 80%\n" +
            " - Køb allianceskaden ned til minimum 80%\n" +
            " - Aflever ${moneyVal} til Ali Mustafa\n" +
            " - Lederen skal vinde iron jackpotten", new Integer[]{425000, 250000}),
    SEVEN(Arrays.asList(
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.DIAMOND_SWORD) >= 2,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.DIAMOND_HELMET) >= 2,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.DIAMOND_CHESTPLATE) >= 2,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.DIAMOND_LEGGINGS) >= 2,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.DIAMOND_BOOTS) >= 2,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.BREAD) >= 547,
            gang -> gang.getGangDamage() <= 70,
            gang -> gang.getAllyDamage() <= 70,
            gang -> gang.getLevelSystem().getPaidForQuest() >= 925000
    ), new ItemStack[]{
            new ItemStack(Material.DIAMOND_SWORD,2),
            new ItemStack(Material.DIAMOND_HELMET,2),
            new ItemStack(Material.DIAMOND_CHESTPLATE,2),
            new ItemStack(Material.DIAMOND_LEGGINGS,2),
            new ItemStack(Material.DIAMOND_BOOTS,2),
            new ItemStack(Material.BREAD,547)
    }," - Aflever {value} diamond sword til Ali Mustafa\n" +
            " - Aflever {value} diamond helmet til Ali Mustafa\n" +
            " - Aflever {value} diamond chestplade til Ali Mustafa\n" +
            " - Aflever {value} diamond leggings til Ali Mustafa\n" +
            " - Aflever {value} diamond boots til Ali Mustafa\n" +
            " - Aflever {value} brød til Ali Mustafa\n" +
            " - Køb bandeskaden ned til minimum 70%\n" +
            " - Køb allianceskaden ned til minimum 70%\n" +
            " - Aflever ${moneyVal} til Ali Mustafa", new Integer[]{925000, 500000}),
    EIGHT(Arrays.asList(
            gang -> gang.getGuardKills() >= 1,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.IRON_SWORD) >= 26,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.IRON_HELMET) >= 26,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.IRON_CHESTPLATE) >= 26,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.IRON_LEGGINGS)>= 26,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.IRON_BOOTS) >= 26,
            gang -> gang.getGangDamage() <= 60,
            gang -> gang.getAllyDamage() <= 60,
            gang -> gang.getLevelSystem().getPaidForQuest() >= 1675000,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.SUGAR) >= 67
    ), new ItemStack[]{
            new ItemStack(Material.IRON_SWORD,26),
            new ItemStack(Material.IRON_HELMET,26),
            new ItemStack(Material.IRON_CHESTPLATE,26),
            new ItemStack(Material.IRON_LEGGINGS,26),
            new ItemStack(Material.IRON_BOOTS,26),
            new ItemStack(Material.SUGAR,67)
    }," - Dræb 1 vagt\n" +
            " - Aflever {value} iron sword til Ali Mustafa\n" +
            " - Aflever {value} iron helmet til Ali Mustafa\n" +
            " - Aflever {value} iron chestplate til Ali Mustafa\n" +
            " - Aflever {value} iron leggings til Ali Mustafa\n" +
            " - Aflever {value} iron boots til Ali Mustafa\n" +
            " - Køb bandeskaden ned til minimum 60%\n" +
            " - Køb allianceskaden ned til minimum 60%\n" +
            " - Aflever ${moneyVal} til Ali Mustafa\n" +
            " - Aflever {value} sugar til Ali Mustafa", new Integer[]{1675000, 750000}),
    NINE(Arrays.asList(
            gang -> gang.getGuardKills() >= 3,
            gang -> gang.getGangDamage() <= 50,
            gang -> gang.getAllyDamage() <= 50,
            gang -> MainPlugin.getPermissions().playerHas("world", Bukkit.getOfflinePlayer(gang.getOwnerUuid()), "jackpot.gold"), //Might not be efficient
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.SUGAR) >= 112,
            gang -> gang.getLevelSystem().getPaidForQuest() >= 2675000
    ), new ItemStack[]{
            new ItemStack(Material.SUGAR, 112)
    }," - Dræb 2 vagter\n" +
            " - Køb bandeskaden ned til minimum 50%\n" +
            " - Køb allianceskaden ned til minimum 50%\n" +
            " - Lederen skal vinde guld jackpotten\n" +
            " - Aflever {value} sugar til Ali Mustafa\n" +
            " - Aflever ${moneyVal} til Ali Mustafa", new Integer[]{2675000, 1000000}),
    TEN(Arrays.asList(
            gang -> gang.getGuardKills() >= 6,
            gang -> gang.getOfficerKills() >= 1,
            gang -> gang.getGangDamage() <= 40,
            gang -> gang.getAllyDamage() <= 40,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.DIAMOND_SWORD) >= 4,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.DIAMOND_HELMET) >= 4,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.DIAMOND_CHESTPLATE) >= 4,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.DIAMOND_LEGGINGS) >= 4,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.DIAMOND_BOOTS) >= 4,
            gang -> MainPlugin.getPermissions().playerHas("world", Bukkit.getOfflinePlayer(gang.getOwnerUuid()), "jackpot.diamond") //Might not be efficient
    ), new ItemStack[]{
            new ItemStack(Material.DIAMOND_SWORD, 4),
            new ItemStack(Material.DIAMOND_HELMET,4),
            new ItemStack(Material.DIAMOND_CHESTPLATE,4),
            new ItemStack(Material.DIAMOND_LEGGINGS,4),
            new ItemStack(Material.DIAMOND_BOOTS,4),
    }," - Dræb 3 vagter\n" +
            " - Dræb 1 officer\n" +
            " - Køb bandeskaden ned til minimum 40%\n" +
            " - Køb allianceskaden ned til minimum 40%\n" +
            " - Aflever {value} diamond swords til Ali Mustafa\n" +
            " - Aflever {value} diamond helmets til Ali Mustafa\n" +
            " - Aflever {value} diamond chestplades til Ali Mustafa\n" +
            " - Aflever {value} diamond leggings til Ali Mustafa \n" +
            " - Aflever {value} diamond boots til Ali Mustafa\n" +
            " - Lederen skal vinde diamond jackpotten", null),
    ELEVEN(Arrays.asList(
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.IRON_SWORD) >= 76,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.IRON_HELMET) >= 76,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.IRON_CHESTPLATE)>= 76,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.IRON_LEGGINGS) >= 76,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.IRON_BOOTS) >= 76,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.SUGAR) >= 187,
            gang -> gang.getLevelSystem().getPaidForQuest() >= 4175000,
            gang -> gang.getGangDamage() <= 30,
            gang -> gang.getAllyDamage() <= 30
    ), new ItemStack[]{
            new ItemStack(Material.IRON_SWORD, 76),
            new ItemStack(Material.IRON_HELMET, 76),
            new ItemStack(Material.IRON_CHESTPLATE, 76),
            new ItemStack(Material.IRON_LEGGINGS, 76),
            new ItemStack(Material.IRON_BOOTS, 76),
            new ItemStack(Material.SUGAR, 187)
    }," - Aflever 50 iron sword til Ali Mustafa\n" +
            " - Aflever {value} iron helmet til Ali Mustafa\n" +
            " - Aflever {value} iron chestplate til Ali Mustafa\n" +
            " - Aflever {value} iron leggings til Ali Mustafa\n" +
            " - Aflever {value} iron boots til Ali Mustafa\n" +
            " - Aflever {value} sugar til Ali Mustafa\n" +
            " - Aflever ${moneyVal} til Ali Mustafa\n" +
            " - Køb bandeskaden ned til minimum 30%\n" +
            " - Køb allianceskaden ned til minimum 30%", new Integer[]{4175000, 1500000}),
    TWELVE(Arrays.asList(
            gang -> gang.getPrisonerKills() >= 275,
            gang -> gang.getGangDamage() <= 20,
            gang -> gang.getAllyDamage() <= 20,
            gang -> gang.getLevelSystem().getPaidForQuest() >= 6175000
    ), null, " - Dræb 200 fanger\n" +
            " - Køb bandeskaden ned til minimum 20%\n" +
            " - Køb allianceskaden ned til minimum 20%\n" +
            " - Aflever ${moneyVal} til Ali Mustafa", new Integer[]{6175000, 2000000}),
    THIRTEEN(Arrays.asList(
            gang -> gang.getPrisonerKills() >= 575,
            gang -> gang.getLevelSystem().getValueOfMaterial(Material.SUGAR) >= 287,
            gang -> gang.getGangDamage() <= 0,
            gang -> gang.getAllyDamage() <= 0,
            gang -> gang.getGuardKills() >= 11,
            gang -> gang.getOfficerKills() >= 2,
            gang -> gang.getLevelSystem().getPaidForQuest() >= 8675000
    ), new ItemStack[]{new ItemStack(Material.SUGAR, 287)}, " - Dræb 300 fanger\n" +
            " - Aflever {value} sugar til Ali Mustafa\n" +
            " - Køb bandeskaden ned til minimum 0%\n" +
            " - Køb allianceskaden ned til minimum 0%\n" +
            " - Dræb 5 vagter\n" +
            " - Dræb 1 officer\n" +
            " - Aflever ${moneyVal} til Ali Mustafa\n", new Integer[]{8675000, 2500000});





    private List<Predicate<Gang>> requirements;
    private ItemStack[] acceptedItems;
    private String requirementDesc;
    private Integer[] amountToPay;

    public List<Predicate<Gang>> getRequirements(){return requirements;}
    public ItemStack[] getAcceptedItems(){return acceptedItems;}
    public String getRequirementDesc(){return requirementDesc;}
    public Integer[] getAmountToPay(){return amountToPay;}

    Level(List<java.util.function.Predicate<Gang>> requirements, ItemStack[] acceptedItems, String requirementDesc, Integer[] amountToPay)
    {
        this.requirements = requirements;
        this.acceptedItems = acceptedItems;
        this.requirementDesc = requirementDesc;
        this.amountToPay = amountToPay;
    }

}
