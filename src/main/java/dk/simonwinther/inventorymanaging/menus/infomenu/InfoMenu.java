package dk.simonwinther.inventorymanaging.menus.infomenu;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import com.simonsejse.bande.enums.*;
import dk.simonwinther.inventorymanaging.Menu;
import com.simonsejse.bande.inventorymanaging.menus.infomenu.submenus.*;
import dk.simonwinther.inventorymanaging.menus.mainmenu.MainMenu;
import dk.simonwinther.utility.ChatUtil;
import dk.simonwinther.utility.InventoryUtility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InfoMenu extends Menu
{

    private Gang gang;
    private MainPlugin plugin;
    private boolean isOwnGang;


    public InfoMenu(MainPlugin plugin, Gang gang, boolean isOwnGang)
    {
        super();
        this.plugin = plugin;
        this.gang = gang;
        this.isOwnGang = isOwnGang;
    }

    @Override
    protected String getName()
    {
        return "Info";
    }

    @Override
    protected int getSize()
    {
        return 9 * 6;
    }

    @Override
    public void onGuiClick(int slot, ItemStack item, Player whoClicked, ClickType clickType)
    {
        if (!isOwnGang) return;
        String gangName = this.gang.getGangName();
        this.gang = GangManaging.getGangByNameFunction.apply(gangName); //Refreshes gang instance to pass through new inventorys
        UUID uuid = whoClicked.getUniqueId();

        if (slot == InventoryUtility.backSlot) whoClicked.openInventory(new MainMenu(plugin, uuid, GangManaging.playerInGangPredicate.test(uuid)).getInventory());
        else if (slot == InventoryUtility.permissionSlot) whoClicked.openInventory(new PermissionSubMenu(plugin, this).getInventory());
        else if (slot == InventoryUtility.gangShopSlot) whoClicked.openInventory(new ShopSubMenu(plugin, this, gang).getInventory());
        else if (slot == InventoryUtility.membersSlot) whoClicked.openInventory(new MemberSubMenu(plugin, this, gang).getInventory());
        else if (slot == InventoryUtility.economySlot) whoClicked.openInventory(new BankSubMenu(plugin, this, gang, uuid).getInventory());
        else if (slot == InventoryUtility.levelSlot){
            if(GangManaging.isRankMinimumPredicate.test(uuid, gang.getGangPermissions().accessToLevelUp)){
                Level level = Level.valueOf(ChatUtil.numbers[gang.getGangLevel()]);
                boolean allMatch = Stream.of(level.getRequirements())  //Stream Stream<List<Predicate>>
                        .flatMap(Collection::stream) //  Stream<Predicate>
                        .allMatch(gangPredicate -> gangPredicate.test(gang));
                if (allMatch){
                    //Levelup
                    gang.getLevelSystem().setGangLevel(gang.getGangLevel() + 1);
                    whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().LEVELUP_SUCCESS.replace("{level}", String.valueOf(gang.getGangLevel()))));
                    super.inventory.clear();
                    whoClicked.openInventory(this.getInventory());
                }else{
                    //Not everything has been made!
                    whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().STILL_MISSING_REQUIREMENTS));
                }
            }else whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_HIGH_RANK_ENOUGH));
        }else if (slot == InventoryUtility.allySlot) whoClicked.openInventory(new ListAllySubMenu(plugin, this, gang).getInventory());
        else if (slot == InventoryUtility.enemySlot) whoClicked.openInventory(new ListEnemySubMenu(plugin, this, gang).getInventory());
        else if (slot == InventoryUtility.deleteSlot)
        {
            /*
            @Anonymous class: Using anonymous class of the menu class to open a confirm or cancel menu
            @param: the player's UUID
             */
            whoClicked.openInventory(new Menu(uuid)
            {
                @Override
                public Inventory getInventory()
                {
                    InventoryUtility.decorate(super.inventory, InventoryUtility.menuLookTwoPredicate, new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.LIME.value[ColorIndexEnum.STAINED_GLASS.index]), true);
                    super.setItem(20, gang.getMembersSorted().size() > 1 ? new ItemBuilder(Material.BARRIER).setItemName("&c&lDer er flere i bande").setLore("&fDu skal være den sidste", "&fi banden før du kan", "&fforlade den").buildItem() : (gang.getMembersSorted().get(uuid) == Rank.LEADER.getValue()) ? new ItemBuilder(new ItemStack(Material.STAINED_CLAY, 1, ColorDataEnum.MAGENTA.value[ColorIndexEnum.STAINED_CLAY.index])).setItemName("&a&lSlet bande").setLore("&fKlik her for at", "&fslette din bande").buildItem() : new ItemBuilder(Material.BARRIER).setItemName("&c&lDu ikke leder").setLore("&fKun lederen af banden", "&fkan slette den").buildItem());
                    super.setItem(24, new ItemBuilder(new ItemStack(Material.STAINED_CLAY, 1, ColorDataEnum.RED.value[ColorIndexEnum.STAINED_CLAY.index])).setItemName("&c&lAnnullere").setLore("&fKlik her for at", "&fannullere").buildItem());
                    return super.inventory;
                }

                @Override
                protected String getName()
                {
                    return "Er du sikker?";
                }

                @Override
                protected int getSize()
                {
                    return 9 * 6;
                }

                @Override
                public void onGuiClick(int slot, ItemStack item, Player whoClicked, ClickType clickType)
                {
                    switch (slot)
                    {
                        case 49:
                        case 24:
                            whoClicked.openInventory(InfoMenu.this.getInventory());
                            break;
                        case 20:
                            whoClicked.performCommand("bande delete");
                            whoClicked.getOpenInventory().close();
                            break;
                    }
                }
            }.getInventory());
        }
    }


    @Override
    public Inventory getInventory()
    {
        InventoryUtility.decorate(super.inventory, InventoryUtility.menuLookThreePredicate, new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.LIME.value[ColorIndexEnum.STAINED_GLASS.index]), true);

        super.setItem(InventoryUtility.economySlot, new ItemBuilder(Material.GOLD_INGOT).setItemName("&6&lØkonomi").setLore("&8&m----------------------", "&7Bandens saldo: &f$" + MessageFormat.format("{0}", gang.getGangBalance()), "", "&7Klik for at indsætte penge", "&7bandens konto").buildItem());
        super.setItem(InventoryUtility.membersSlot, new ItemBuilder(Material.GOLD_SWORD).setItemName("&a&lMedlemmer").setLore(getMembersList()).addFlags(ItemFlag.HIDE_ATTRIBUTES).buildItem());
        try
        {
            super.setItem(InventoryUtility.gangInformationSlot, new ItemBuilder(Material.NETHER_STAR).setItemName("&d&lMin bande").setLore("&8&m----------------------", "&7Bande ID: &f" + gang.getGangId(), "&7Navn: &f" + gang.getGangName(), "&7Level: &f" + gang.getGangLevel(), "&7Leder: &f" + gang.getOwner(), "&7Coleder: &f" + gang.getCoOwner(), "&7Fangedrab: &f" + gang.getPrisonerKills(), "&7Vagtdrab: &f" + gang.getGuardKills(), "&7Officerdrab: &f" + gang.getOfficerKills()).buildItem());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        super.setItem(InventoryUtility.limitsSlot, new ItemBuilder(Material.ENDER_CHEST).setItemName("&e&lBegræsninger").setLore(MessageFormat.format("&6Medlemmer&f: &2&l{0}&f ud af &c&l{1}", gang.getAmountOfMembers(), gang.getMaxMembers()), MessageFormat.format("&6Allierede&f: &2&l{0}&f ud af &c&l{1}", gang.getAmountOfAllies(), gang.getMaxAllies()), MessageFormat.format("&6Rivaler&f: &2&l{0}&f ud af &c&l{1}", gang.getAmountOfEnemys(), gang.getMaxEnemies()), MessageFormat.format("&6Bande skade&f: &c&l{0}%", gang.getGangDamage()), MessageFormat.format("&6Alliance skade&f: &c&l{0}%", gang.getAllyDamage()), "&6Adgang til:", "&8 »&e Toiletterne: " + (gang.gangPermissions.accessToToilets ? "&aJa" : "&cNej"), "&8 »&e Gården: " + (gang.gangPermissions.accessToToilets ? "&aJa" : "&cNej"), "&8 »&e Laboratoriet: " + (gang.gangPermissions.accessToLab ? "&aJa" : "&cNej")).buildItem());
        super.setItem(InventoryUtility.levelSlot, new ItemBuilder(Material.OBSIDIAN).setItemName("&5&lKrav til level "+(gang.getGangLevel() + 1)).setLore("&8&m----------------------", levelDescFunc.apply(gang)).buildItem());
        super.setItem(InventoryUtility.allySlot, new ItemBuilder(new ItemStack(Material.STAINED_CLAY, 1, ColorDataEnum.GREEN.value[ColorIndexEnum.STAINED_CLAY.index])).setItemName("&a&lAllierede &e" + gang.getAllies().size() + "&7/&6" + gang.getMaxAllies()).setLore(getAllyList()).buildItem());
        super.setItem(InventoryUtility.enemySlot, new ItemBuilder(new ItemStack(Material.STAINED_CLAY, 1, ColorDataEnum.RED.value[ColorIndexEnum.STAINED_CLAY.index])).setItemName("&4&lRivaler &c" + gang.getEnemies().size() + "&7/&4" + gang.getMaxEnemies()).setLore(getEnemiesList()).buildItem());
//        super.setItem(28, new ItemBuilder(Material.NETHER_STAR).setItemName("&9✯&b&l Changelogs &9✯").setLore("&7Release version: 1.0", "&7Nuværende version: 1.0", "", "&8[&91:&b&l ♛&8] &7➤ &9Ingen nye ændringer...", "&8&m&l——————————————", "&7Kommende ændringer:", "&6&l - &eBegrænsninger",  "&6&l - &eBande skade (allierede/bandemedlemmer)").buildItem());
        super.setItem(InventoryUtility.gangShopSlot, new ItemBuilder(Material.GOLD_HELMET).setItemName("&d&lBandeshop").setLore("&fKøb opgraderinger til din bande!").addFlags(ItemFlag.HIDE_ATTRIBUTES).buildItem());
        super.setItem(InventoryUtility.permissionSlot, new ItemBuilder(Material.BOOK).setItemName("&a&lTilladelser").buildItem());
        super.setItem(InventoryUtility.deleteSlot, new ItemBuilder(Material.BARRIER).setItemName("&4&lSlet bande").setLore("&fKlik her for at", "&fslette din bande").buildItem());
        return super.inventory;
    }

    private Function<Gang, String> levelDescFunc = (gang) ->
    {
        StringBuilder stringBuilder = new StringBuilder();
        Level level = Level.valueOf(ChatUtil.numbers[gang.getGangLevel()]);
        if (level.getRequirements().stream().allMatch(gangPredicate -> gangPredicate.test(gang))){
            return stringBuilder.append("&b").append("Klik for at gå i level ").append((gang.getGangLevel() + 1)).toString();
        }
        String[] args = level.getRequirementDesc().split("\n");
        for(int i = 0, itemIndex = 0; i < level.getRequirements().size(); i++){
            String requirement = args[i];
            if (level.getRequirements().get(i).test(gang)){
                if (requirement.contains("{value}")){
                    requirement = requirement.replace("{value}", String.valueOf(level.getAcceptedItems()[itemIndex++].getAmount()));
                }else if (requirement.contains("{moneyVal}")){
                    requirement = requirement.replace("{moneyVal}", String.valueOf(level.getAmountToPay()[QuestPayEnum.AMOUNT_PAY_DESC_INDEX.value]));
                }
                stringBuilder.append("&a&l✔").append("&a").append(requirement).append("\n");
            }else{
                if (requirement.contains("{value}")){
                    final int value = (level.getAcceptedItems()[itemIndex].getAmount() - gang.getLevelSystem().getValueOfMaterial(level.getAcceptedItems()[itemIndex++].getType()));
                    requirement = requirement.replace("{value}", String.valueOf(value));
                }else if (requirement.contains("{moneyVal}")){
                    requirement = requirement.replace("{moneyVal}", String.valueOf(level.getAmountToPay()[QuestPayEnum.AMOUNT_PAY_INDEX.value] - gang.getLevelSystem().getPaidForQuest()));
                }
                stringBuilder.append("&C&l✘").append("&7").append(requirement).append("\n");
            }
        }


        stringBuilder.append("&bDit level: &f&l").append(gang.getGangLevel());
        return stringBuilder.toString();
    };



    private String getAllyList()
    {
        if (gang.getAllies().size() < 1) return "&cIngen..";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("&2" + gang.getAllies().stream().collect(Collectors.joining("\n&e")));
        stringBuilder.append("\n&cKlik for at fjerne eller se dine allierede.");
        return stringBuilder.toString();
    }

    private String getEnemiesList()
    {
        if (gang.getEnemies().size() < 1) return "&cIngen..";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("&c" + gang.getEnemies().stream().collect(Collectors.joining("\n&e")));
        stringBuilder.append("\n&cKlik for at fjerne eller se dine allierede.");
        return stringBuilder.toString();
    }

    private String getMembersList()
    {
        StringBuilder lore = new StringBuilder();
        gang.getMembersSorted().entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Comparator.comparing(Map.Entry::getValue)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::compareTo, LinkedHashMap::new))
                .forEach((uuid, rankInt) ->
                {
                    String rank = "";
                    for (Rank rankEnum : Rank.values())
                    {
                        if (rankEnum.getValue() == rankInt) rank = rankEnum.getRankName();
                    }
                    lore.append((Bukkit.getOfflinePlayer(uuid).isOnline() ? "&a" + Bukkit.getPlayer(uuid).getName() : "&c" + Bukkit.getOfflinePlayer(uuid).getName()) + ": &f" + rank + "\n"); //Bukkit.getOfflinePlayer(uuid).getName()
                });

        return lore.toString();
    }
}
