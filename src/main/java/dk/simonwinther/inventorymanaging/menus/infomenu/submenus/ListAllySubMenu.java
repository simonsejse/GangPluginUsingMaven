package dk.simonwinther.inventorymanaging.menus.infomenu.submenus;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.Gang;
import dk.simonwinther.manager.GangManaging;
import dk.simonwinther.constants.ColorDataEnum;
import dk.simonwinther.constants.ColorIndexEnum;
import dk.simonwinther.inventorymanaging.AbstractMenu;
import dk.simonwinther.inventorymanaging.menus.infomenu.InfoMenu;
import dk.simonwinther.utility.InventoryUtility;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class ListAllySubMenu extends AbstractMenu
{
    private final MainPlugin plugin;
    private InfoMenu infoMenu;
    private final MessageProvider mp;
    private Gang gang;
    private final GangManaging gangManaging;
    private ItemBuilder skullBuilder;

    public ListAllySubMenu(GangManaging gangManaging, MainPlugin plugin, InfoMenu infoMenu, Gang gang)
    {
        this.plugin = plugin;
        this.mp = this.plugin.getMessageProvider();
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
            plugin.getEventHandling().addPlayerToAwaitAllyRequest.accept(uuid);
            whoClicked.getOpenInventory().close();
            whoClicked.sendMessage(this.mp.whoToAllyChat);
        } else if (slot == 47)
        {
            whoClicked.openInventory(new AbstractMenu(uuid)
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
                        Gang argGang = gangManaging.getGangByNameFunction.apply(gangName);
                        Gang playerGang = gangManaging.getGangByUuidFunction.apply(whoClicked.getUniqueId());
                        if (!argGang.hasRequestedAlly(playerGang.getGangName())) return;

                        if (clickType == ClickType.RIGHT)
                        {
                            argGang.removeAllyRequest(playerGang.getGangName());

                            gangManaging.sendTeamMessage.accept(argGang, mp.allyDenied.replace("{name}", playerGang.getGangName()));
                            gangManaging.sendTeamMessage.accept(playerGang, mp.denyAlly.replace("{name}", gangName));
                            whoClicked.openInventory(ListAllySubMenu.this.getInventory());

                        } else if (clickType == ClickType.LEFT)
                        {
                            argGang.removeAllyRequest(playerGang.getGangName());
                            if (playerGang.hasRequestedAlly(gangName))
                                playerGang.removeAllyRequest(gangName);

                            argGang.getAllies().put(playerGang.getGangId(), playerGang.getGangName().toLowerCase());
                            playerGang.getAllies().put(argGang.getGangId(), gangName.toLowerCase());

                            gangManaging.sendTeamMessage.accept(playerGang, mp.allySuccessful.replace("{name}", gangName));
                            gangManaging.sendTeamMessage.accept(argGang, mp.allySuccessful.replace("{name}", playerGang.getGangName()));

                            whoClicked.openInventory(ListAllySubMenu.this.getInventory());
                        }
                    }
                }
            }.getInventory());
        } else if (slot == 51)
        {
            whoClicked.openInventory(new AbstractMenu(uuid)
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
                        whoClicked.sendMessage(mp.unAlly.replace("{name}", gangName));
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

            if (!localGang.getAllies().values().contains(playerGang.getGangName()) || !playerGang.getAllies().values().contains(localGang.getGangName())) {
                whoClicked.sendMessage(ChatColor.translateAlternateColorCodes('&',"&8&l| &e&lVENT &8&l| &eVent venligst, vær tålmodig.."));
                return;
            }

            localGang.getAllies().remove(playerGang.getGangName().toLowerCase());
            playerGang.getAllies().remove(gangName.toLowerCase());

            this.gangManaging.sendTeamMessage.accept(localGang, this.mp.noLongerAllies.replace("{name}", playerGang.getGangName()));
            this.gangManaging.sendTeamMessage.accept(playerGang, this.mp.noLongerAllies.replace("{name}", gangName));



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
                        .values()
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
