package dk.simonwinther.inventorymanaging.menus.infomenu;

import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.MainPlugin;
import dk.simonwinther.constants.*;
import dk.simonwinther.inventorymanaging.AbstractMenu;
import dk.simonwinther.inventorymanaging.menus.infomenu.submenus.*;
import dk.simonwinther.inventorymanaging.menus.mainmenu.MainMenu;
import dk.simonwinther.manager.Gang;
import dk.simonwinther.manager.GangManaging;
import dk.simonwinther.utility.InventoryUtility;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
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

public class InfoMenu extends AbstractMenu
{

    private Gang gang;
    private MainPlugin plugin;
    private boolean isOwnGang;
    private MessageProvider mp;
    private final GangManaging gangManaging;

    private static final int MAX_LORE_ITEMS_SHOWN = 4;

    //When /bande info is used this will be null
    private MainMenu mainMenu;

    public InfoMenu(MainPlugin plugin, Gang gang, boolean isOwnGang, MainMenu mainMenu)
    {
        super();
        this.plugin = plugin;
        this.gangManaging = this.plugin.getGangManaging();
        this.gang = gang;
        this.mp = this.plugin.getMessageProvider();
        this.isOwnGang = isOwnGang;
        this.mainMenu = mainMenu;
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
        this.gang = gangManaging.getGangByNameFunction.apply(gangName); //Refreshes gang instance to pass through new inventorys
        UUID uuid = whoClicked.getUniqueId();

        if (slot == InventoryUtility.BACK_SLOT){
            if (mainMenu == null){
                whoClicked.closeInventory();
            }else whoClicked.openInventory(mainMenu.getInventory());
        } else if (slot == InventoryUtility.PERMISSION_SLOT) whoClicked.openInventory(new PermissionSubMenu(plugin, this).getInventory());
        else if (slot == InventoryUtility.GANG_SHOP_SLOT) whoClicked.openInventory(new ShopSubMenu(plugin, gang, this).getInventory());
        else if (slot == InventoryUtility.MEMBERS_SLOT) whoClicked.openInventory(new PaginatedMemberSubMenu(plugin, gang, this).getInventory());
        else if (slot == InventoryUtility.ECONOMY_SLOT) whoClicked.openInventory(new BankSubMenu(plugin, this, gang, uuid).getInventory());
        else if (slot == InventoryUtility.LEVEL_SLOT){
            if(gangManaging.isRankMinimumPredicate.test(uuid, gang.gangPermissions.accessToLevelUp)){
                Level level = Level.valueOf(MessageProvider.numbers[gang.getGangLevel()]);
                boolean allMatch = Stream.of(level.getRequirements())
                        .flatMap(Collection::stream)
                        .allMatch(gangPredicate -> gangPredicate.test(gang));
                if (allMatch){
                    //Levelup
                    gang.getLevelSystem().setGangLevel(gang.getGangLevel() + 1);
                    whoClicked.sendMessage(this.mp.levelUpSuccess.replace("{level}", String.valueOf(gang.getGangLevel())));
                    super.inventory.clear();
                    whoClicked.openInventory(this.getInventory());
                }else{
                    //Not everything has been made!
                    whoClicked.sendMessage(this.mp.stillMissingRequirements);
                }
            }else whoClicked.sendMessage(this.mp.notHighRankEnough);
        }else if (slot == InventoryUtility.ALLY_SLOT) whoClicked.openInventory(new PaginatedListAllySubMenu(plugin, gang, this).getInventory());
        else if (slot == InventoryUtility.ENEMY_SLOT) whoClicked.openInventory(new ListEnemySubMenu(this.gangManaging, plugin, this, gang).getInventory());
        else if (slot == InventoryUtility.EMBLEM_SLOT){
            whoClicked.sendMessage("Nyt emblem!");
        } else if (slot == InventoryUtility.DELETE_SLOT) whoClicked.openInventory(new DeleteGangMenu(this.gang, this, uuid).getInventory());

    }

    private final ItemBuilder economyItem = new ItemBuilder(Material.GOLD_INGOT).setItemName("&6&lØkonomi");
    private final ItemBuilder membersItem = new ItemBuilder(Material.GOLD_SWORD).setItemName("&a&lMedlemmer");
    private final ItemBuilder gangInformationItem = new ItemBuilder(Material.NETHER_STAR).setItemName("&d&lMin bande");
    private final ItemBuilder limitsItem = new ItemBuilder(Material.ENDER_CHEST).setItemName("&e&lBegræsninger");
    private final ItemBuilder levelItem = new ItemBuilder(Material.OBSIDIAN);
    private final ItemBuilder allyItem = new ItemBuilder(new ItemStack(Material.STAINED_CLAY, 1, ColorDataEnum.GREEN.value[ColorIndexEnum.STAINED_CLAY.index]));
    private final ItemBuilder enemyItem = new ItemBuilder(new ItemStack(Material.STAINED_CLAY, 1, ColorDataEnum.RED.value[ColorIndexEnum.STAINED_CLAY.index]));
    private final static ItemStack changelogItem =new ItemBuilder(Material.EMERALD).setItemName("&9✯&b&l Changelogs &9✯").setLore("&7Release version: 1.0", "&7Nuværende version: 1.0", "", "&8[&91:&b&l ♛&8] &7➤ &9Ingen nye ændringer...", "&8&m&l——————————————", "&7Kommende ændringer:", "&6&l - &eBande skade (allierede/bandemedlemmer)").buildItem();
    private final ItemBuilder emblemSlot = new ItemBuilder(Material.SKULL_ITEM, 1, SkullType.CREEPER).setItemName("&6Dit bande logo");
    private final ItemStack gangShopItem = new ItemBuilder(Material.GOLD_HELMET).setItemName("&d&lBandeshop").setLore("&fKøb opgraderinger til din bande!").addFlags(ItemFlag.HIDE_ATTRIBUTES).buildItem();
    private final ItemStack permissionItem = new ItemBuilder(Material.BOOK).setItemName("&a&lTilladelser").buildItem();
    private final ItemStack deleteItem = new ItemBuilder(Material.BARRIER).setItemName("&4&lSlet bande").setLore("&fKlik her for at", "&fslette din bande").buildItem();


    @Override
    public Inventory getInventory()
    {
        InventoryUtility.decorate(super.inventory, InventoryUtility.MENU_PREDICATE_THREE, new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.LIME.value[ColorIndexEnum.STAINED_GLASS.index]), true);

        super.setItem(InventoryUtility.ECONOMY_SLOT, economyItem.setLore("&8&m----------------------", "&7Bandens saldo: &f$" + MessageFormat.format("{0}", gang.getGangBalance()), "", "&7Klik for at indsætte penge", "&7bandens konto").buildItem());
        super.setItem(InventoryUtility.MEMBERS_SLOT, membersItem.setLore(getMembersList()).addFlags(ItemFlag.HIDE_ATTRIBUTES).buildItem());
        try
        {
            super.setItem(InventoryUtility.GANG_INFORMATION_SLOT, gangInformationItem.setLore("&8&m----------------------", "&7Bande ID: &f" + gang.getGangId(), "&7Navn: &f" + gang.getGangName(), "&7Level: &f" + gang.getGangLevel(), "&7Leder: &f" + gang.getOwner(), "&7Coleder: &f" + gang.getCoOwner(), "&7Fangedrab: &f" + gang.getPrisonerKills(), "&7Vagtdrab: &f" + gang.getGuardKills(), "&7Officerdrab: &f" + gang.getOfficerKills()).buildItem());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        super.setItem(InventoryUtility.LIMITS_SLOT, limitsItem.setLore(MessageFormat.format("&6Medlemmer&f: &2&l{0}&f ud af &c&l{1}", gang.getMembersSorted().size(), gang.getMaxMembers()), MessageFormat.format("&6Allierede&f: &2&l{0}&f ud af &c&l{1}", gang.getAllies().size(), gang.getMaxAllies()), MessageFormat.format("&6Rivaler&f: &2&l{0}&f ud af &c&l{1}", gang.getEnemies().size(), gang.getMaxEnemies()), MessageFormat.format("&6Bande skade&f: &c&l{0}%", gang.getGangDamage()), MessageFormat.format("&6Alliance skade&f: &c&l{0}%", gang.getAllyDamage()), "&6Adgang til:", "&8 »&e Toiletterne: " + (gang.gangPermissions.accessToToilets ? "&aJa" : "&cNej"), "&8 »&e Gården: " + (gang.gangPermissions.accessToToilets ? "&aJa" : "&cNej"), "&8 »&e Laboratoriet: " + (gang.gangPermissions.accessToLab ? "&aJa" : "&cNej")).buildItem());
        super.setItem(InventoryUtility.LEVEL_SLOT, levelItem.setItemName("&5&lKrav til level "+(gang.getGangLevel() + 1)).setLore("&8&m----------------------", levelDescFunc.apply(gang)).buildItem());
        super.setItem(InventoryUtility.ALLY_SLOT, allyItem.setItemName("&a&lAllierede &e" + gang.getAllies().size() + "&7/&6" + gang.getMaxAllies()).setLore(getAllyList()).buildItem());
        super.setItem(InventoryUtility.ENEMY_SLOT, enemyItem.setItemName("&4&lRivaler &c" + gang.getEnemies().size() + "&7/&4" + gang.getMaxEnemies()).setLore(getEnemiesList()).buildItem());
        super.setItem(InventoryUtility.CHANGELOG_SLOT, changelogItem);
        super.setItem(InventoryUtility.EMBLEM_SLOT, emblemSlot.setLore(getGangEmblem()).buildItem());
        super.setItem(InventoryUtility.GANG_SHOP_SLOT, gangShopItem);
        super.setItem(InventoryUtility.PERMISSION_SLOT, permissionItem);
        super.setItem(InventoryUtility.DELETE_SLOT, deleteItem);
        return super.inventory;
    }
    private Function<Gang, String> levelDescFunc = (gang) ->
    {
        StringBuilder stringBuilder = new StringBuilder();
        Level level = Level.valueOf(MessageProvider.numbers[gang.getGangLevel()]);
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

    public String getGangEmblem(){
        final StringBuilder stringBuilder = new StringBuilder();
        for(String[] eachColumn : this.gang.getEmblemColors()){
            stringBuilder.append("     ");
            for(int i = 0; i < eachColumn.length;i++){
                stringBuilder.append(eachColumn[i]).append("▊");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    private String getAllyList()
    {
        if (gang.getAllies().size() < 1) return "&cIngen..";
        int size = gang.getAllies().size();
        final StringBuilder stringBuilder = new StringBuilder();
        final String[] possibleAllies = gang.getEnemies().values().toArray(new String[]{});

        for (int i = 0; i < MAX_LORE_ITEMS_SHOWN; i++){
            stringBuilder.append("&c").append(possibleAllies[i]).append("\n&e");
        }
        if (size - MAX_LORE_ITEMS_SHOWN > 0) stringBuilder.append("&2&l+").append((size - MAX_LORE_ITEMS_SHOWN)).append("&aallierede mere!");
        stringBuilder.append("\n&cKlik for at fjerne eller se dine allierede.");
        return stringBuilder.toString();
    }
    private String getEnemiesList()
    {
        if (gang.getEnemies().size() < 1) return "&cIngen..";
        int size = gang.getEnemies().size();
        final StringBuilder stringBuilder = new StringBuilder();
        final String[] possibleEnemies = gang.getEnemies().values().toArray(new String[]{});

        for (int i = 0; i < MAX_LORE_ITEMS_SHOWN; i++){
            stringBuilder.append("&c").append(possibleEnemies[i]).append("\n&e");
        }
        if (size - MAX_LORE_ITEMS_SHOWN > 0) stringBuilder.append("&4&l+").append((size - MAX_LORE_ITEMS_SHOWN)).append("&crivaler mere!");
        stringBuilder.append("\n&cKlik for at fjerne eller se dine rivaler.");
        return stringBuilder.toString();
    }

    private String getMembersList()
    {
        final StringBuilder lore = new StringBuilder();
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
