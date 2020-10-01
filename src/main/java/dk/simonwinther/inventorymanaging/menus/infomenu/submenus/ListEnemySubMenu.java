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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ListEnemySubMenu extends Menu
{
    private MainPlugin plugin;
    private InfoMenu infoMenu;
    private Gang gang;

    ItemBuilder playersSkull;

    public ListEnemySubMenu(MainPlugin plugin, InfoMenu infoMenu, Gang gang)
    {
        this.plugin = plugin;
        this.infoMenu = infoMenu;
        this.gang = gang;
        playersSkull = new ItemBuilder(Material.SKULL_ITEM, 1, SkullType.PLAYER).setPlayerSkull(gang.getOwnerName());
    }

    @Override
    protected String getName()
    {
        return "Liste over rivaler";
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
        if (slot == InventoryUtility.backSlot) whoClicked.openInventory(infoMenu.getInventory());
        else if (slot == 47)
        {
            plugin.getEventHandling().addEnemyChat.accept(uuid);
            whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().WHO_TO_ENEMY_CHAT));
            whoClicked.getOpenInventory().close();
        } else if (slot == 51) whoClicked.openInventory(new Menu()
        {
            private int slot;

            @Override
            public Inventory getInventory()
            {
                slot = 10;

                InventoryUtility.decorate(super.inventory, InventoryUtility.menuLookSevenPredicate, new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.LIME.value[ColorIndexEnum.STAINED_GLASS.index]), true);
                GangManaging.enemyGangListFunction.apply(gang.getGangName())
                        .stream()
                        .forEach(gang ->
                        {
                            super.setItem(slot++, playersSkull.setItemName("&c&l" + gang.getGangName()).setLore("&7Leder: &f" + gang.getOwner(), "&7Co-Leader: &f" + gang.getCoOwner(), "&7Level: &f" + gang.getGangLevel(), "", "&fDenne bande har dig som rival!").buildItem());
                        });

                return super.inventory;
            }

            @Override
            protected String getName()
            {
                return "§cRival list";
            }

            @Override
            protected int getSize()
            {
                return 9 * 6;
            }

            @Override
            public void onGuiClick(int slot, ItemStack item, Player whoClicked, ClickType clickType)
            {
                if (slot == InventoryUtility.backSlot) whoClicked.openInventory(ListEnemySubMenu.this.getInventory());
            }
        }.getInventory());
        else if (item.getType() == Material.SKULL_ITEM)
        {
            String gangName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            if (gang.getEnemies().contains(gangName))
            {
                gang.getEnemies().remove(gangName);
                whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().UN_ENEMY.replace("{name}", gangName)));
            } else
            {
                whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().CANT_UN_ENEMY));
            }

        }
    }

    private int slot;

    @Override
    public Inventory getInventory()
    {
        InventoryUtility.decorate(super.inventory, InventoryUtility.menuLookSixPredicate, new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.LIGHT_BLUE.value[ColorIndexEnum.STAINED_GLASS.index]), true);

        super.setItem(47, playersSkull.setItemName("&a&lTilføj rival").setLore("&7Klik her for at tilføje", "&7en bande som rival").buildItem());
        super.setItem(51, playersSkull.setItemName("&a&lRival list").setLore("&7Klik her for at se en liste", "&7over rivaler imod din bande").buildItem());
        slot = 10;

        gang.getEnemies()
                .stream()
                .map(GangManaging.getGangByNameFunction::apply)
                .forEach(gang ->
                {
                    if (slot > 43) return;
                    if (slot == 17 | slot == 26 | slot == 35) slot += 2;
                    super.setItem(slot++, playersSkull.setItemName("&c&l" + gang.getGangName()).setLore("&7Leder: &f" + gang.getOwner(), "&7Co-Leader: &f" + gang.getCoOwner(), "&7Level: &f" + gang.getGangLevel(), "", "&fKlik her for at fjerne denne", "&fbande som rival!").buildItem());
                });
        return super.inventory;
    }
}