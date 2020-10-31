package dk.simonwinther.inventorymanaging.menus.infomenu.submenus;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.enums.ColorDataEnum;
import dk.simonwinther.enums.ColorIndexEnum;
import dk.simonwinther.inventorymanaging.Menu;
import dk.simonwinther.inventorymanaging.menus.infomenu.InfoMenu;
import dk.simonwinther.utility.InventoryUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ListAllySubMenu extends Menu
{
    private final MainPlugin plugin;
    private InfoMenu infoMenu;
    private Gang gang;
    private GangManaging gangManaging;
    private ItemBuilder skullBuilder;

    public ListAllySubMenu(GangManaging gangManaging, MainPlugin plugin, InfoMenu infoMenu, Gang gang)
    {
        this.plugin = plugin;
        this.infoMenu = infoMenu;
        this.gang = gang;
        this.gangManaging = gangManaging;
        skullBuilder = new ItemBuilder(Material.SKULL_ITEM, 1, SkullType.PLAYER).setPlayerSkull(gang.getOwnerName());
    }

    @Override
    protected String getName()
    {
        return "Liste over allierede";
    }

    @Override
    protected int getSize()
    {
        return 9 * 6;
    }

    @Override
    public void onGuiClick(int slot, ItemStack item, Player whoClicked, ClickType clickType)
    {
        UUID uuid = whoClicked.getUniqueId();
        if (slot == InventoryUtility.BACK_SLOT) whoClicked.openInventory(infoMenu.getInventory());
        else if (slot == 4)
        {
            plugin.getEventHandling().addAllyConsumer.accept(uuid);
            whoClicked.getOpenInventory().close();
            whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().WHO_TO_ALLY_CHAT));
        } else if (slot == 47)
        {
            whoClicked.openInventory(new Menu(uuid)
            {
                private int slot;

                @Override
                public Inventory getInventory()
                {
                    slot = 10;
                    InventoryUtility.decorate(super.inventory, InventoryUtility.MENU_PREDICATE_SIX, new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.LIME.value[ColorIndexEnum.STAINED_GLASS.index]), true);
                    List<Gang> _listOfGangs = gangManaging.allyInvitationGangListFunction.apply(gang.getGangName());
                    _listOfGangs.forEach(gang ->
                    {
                        if (slot == 43) return;
                        if (slot == 17 | slot == 26 | slot == 35) slot += 2;
                        super.setItem(slot++, skullBuilder.setItemName("&a&l" + gang.getGangName()).setLore("&7Bandens leder: &f" + gang.getOwner(), "&7Bandens co-leader: &f" + gang.getCoOwner(), "", "&7Venstreklik for at acceptere alliancen", "&7Højreklik for at afvise alliancen").buildItem());
                    });
                    return super.inventory;
                }

                @Override
                protected String getName()
                {
                    return "Invitationer";
                }

                @Override
                protected int getSize()
                {
                    return 9 * 6;
                }

                @Override
                public void onGuiClick(int slot, ItemStack item, Player whoClicked, ClickType clickType)
                {
                    if (slot == InventoryUtility.BACK_SLOT) whoClicked.openInventory(ListAllySubMenu.this.getInventory());

                    if (item.getType() == Material.SKULL_ITEM)
                    {
                        String gangName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                        Gang localGang = gangManaging.getGangByNameFunction.apply(gangName);
                        Gang playerGang = gangManaging.getGangByUuidFunction.apply(whoClicked.getUniqueId());
                        if (!localGang.getAllyInvitation().contains(playerGang.getGangName())) return;

                        if (clickType == ClickType.RIGHT)
                        {
                            localGang.getAllyInvitation().remove(playerGang.getGangName());

                            localGang.getMembersSorted()
                                    .keySet()
                                    .stream()
                                    .filter(uuid -> Bukkit.getPlayer(uuid) != null)
                                    .map(Bukkit::getPlayer)
                                    .forEach(p -> p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().ALLY_DENIED.replace("{name}", playerGang.getGangName()))));

                            playerGang.getMembersSorted()
                                    .keySet()
                                    .stream()
                                    .filter(uuid -> Bukkit.getPlayer(uuid) != null)
                                    .map(Bukkit::getPlayer)
                                    .forEach(p -> p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().DENY_ALLY.replace("{name}", gangName))));
                            whoClicked.openInventory(ListAllySubMenu.this.getInventory());

                        } else if (clickType == ClickType.LEFT)
                        {
                            localGang.getAllyInvitation().remove(playerGang.getGangName());
                            if (playerGang.getAllyInvitation().contains(gangName))
                                playerGang.getAllyInvitation().remove(gangName);

                            localGang.getAllies().add(playerGang.getGangName().toLowerCase());
                            playerGang.getAllies().add(gangName.toLowerCase());

                            playerGang.getMembersSorted()
                                    .keySet()
                                    .stream()
                                    .filter(uuid -> Bukkit.getPlayer(uuid) != null)
                                    .map(Bukkit::getPlayer)
                                    .forEach(p -> p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().ALLY_SUCCESSFUL.replace("{name}", gangName))));

                            localGang.getMembersSorted()
                                    .keySet()
                                    .stream()
                                    .filter(uuid -> Bukkit.getPlayer(uuid) != null)
                                    .map(Bukkit::getPlayer)
                                    .forEach(p -> p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().ALLY_SUCCESSFUL.replace("{name}", playerGang.getGangName()))));

                            whoClicked.openInventory(ListAllySubMenu.this.getInventory());
                        }
                    }
                }
            }.getInventory());
        } else if (slot == 51)
        {
            whoClicked.openInventory(new Menu(uuid)
            {
                private int slot;

                @Override
                public Inventory getInventory()
                {
                    slot = 10;

                    InventoryUtility.decorate(super.inventory, InventoryUtility.MENU_PREDICATE_SIX, new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.GREEN.value[ColorIndexEnum.STAINED_GLASS.index]), true);

                    gang.getAllyInvitation()
                            .stream()
                            .map(gangManaging.getGangByNameFunction::apply)
                            .forEach(gang ->
                            {
                                if (slot == 43) return;
                                if (slot == 17 | slot == 26 | slot == 35) slot += 2;
                                super.setItem(slot++, skullBuilder.setItemName("&a&l" + gang.getGangName()).setLore("&7Bandens leder: &f" + gang.getOwner(), "&7Bandens co-leader: &f" + gang.getCoOwner(), "", "&7Klik for at fjerne", "&7alliance invitationen").buildItem());
                            });
                    return super.inventory;
                }

                @Override
                protected String getName()
                {
                    return "Anmodninger";
                }

                @Override
                protected int getSize()
                {
                    return 9 * 6;
                }

                @Override
                public void onGuiClick(int slot, ItemStack item, Player whoClicked, ClickType clickType)
                {
                    if (item.getType() == Material.STAINED_GLASS_PANE) return;
                    if (slot == InventoryUtility.BACK_SLOT) whoClicked.openInventory(ListAllySubMenu.this.getInventory());
                    else
                    {
                        String gangName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                        gang.getAllyInvitation().remove(gangName);
                        whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().UN_ALLY.replace("{name}", gangName)));
                        whoClicked.getOpenInventory().close();
                    }
                }
            }.getInventory());
        } else if (item.getType() == Material.SKULL_ITEM)
        {
            //remove gang as allies
            String gangName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            Gang localGang = this.gangManaging.getGangByNameFunction.apply(gangName);
            Gang playerGang = this.gangManaging.getGangByUuidFunction.apply(uuid);

            if (!localGang.getAllies().contains(playerGang.getGangName()) || !playerGang.getAllies().contains(localGang.getGangName())) {
                whoClicked.sendMessage(ChatColor.translateAlternateColorCodes('&',"&8&l| &e&lVENT &8&l| &eVent venligst, vær tålmodig.."));
                return;
            }

            localGang.getAllies().remove(playerGang.getGangName().toLowerCase());
            playerGang.getAllies().remove(gangName.toLowerCase());

            localGang.getMembersSorted()
                    .keySet()
                    .stream()
                    .filter(_uuid -> Bukkit.getPlayer(_uuid) != null)
                    .map(Bukkit::getPlayer)
                    .filter(Objects::nonNull)
                    .forEach(p -> p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NO_LONGER_ALLYS.replace("{name}", playerGang.getGangName()))));

            playerGang.getMembersSorted()
                    .keySet()
                    .stream()
                    .filter(_uuid -> Bukkit.getPlayer(_uuid) != null)
                    .map(Bukkit::getPlayer)
                    .filter(Objects::nonNull)
                    .forEach(p -> p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NO_LONGER_ALLYS.replace("{name}", gangName))));

        }
    }

    private int slot;

    @Override
    public Inventory getInventory()
    {
        new BukkitRunnable() {
            @Override
            public void run(){
                ListAllySubMenu.this.inventory.clear();
                slot = 10;
                InventoryUtility.decorate(inventory, InventoryUtility.MENU_PREDICATE_ONE, new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.LIME.value[ColorIndexEnum.STAINED_GLASS.index]), true);

                gang.getAllies()
                        .forEach(gangName ->
                        {
                            if (slot > 43) return;
                            if (slot == 17 | slot == 26 | slot == 35) slot += 2;
                            Gang gang = gangManaging.getGangByNameFunction.apply(gangName);
                            setItem(slot++, skullBuilder.setItemName("&a&l" + gangName).setLore("&7Leder: &f" + gang.getOwner(), "&7Co-Leader: &f" + gang.getCoOwner(), "&7Level: &f" + gang.getGangLevel(), "", "&7Klik her for at fjerne", "&7denne alliance").buildItem());
                        });
                setItem(4, skullBuilder.setItemName("&a&lAlliance").setLore("&7Klik her for at spørge", "&7en bande om alliance").buildItem());
                setItem(47, skullBuilder.setItemName("&a&lInvitationer").setLore("&7Klik her for at se hvilken bander der", "&7har spurgt din bande om alliance").buildItem());
                setItem(51, skullBuilder.setItemName("&a&lAlliance anmodninger").setLore("&7Klik her for at se en liste over", "&7bander som er blevet spurgt om alliance").buildItem());

            }
        }.runTaskTimer(plugin, 0L, 40L);
        return super.inventory;
    }

}
