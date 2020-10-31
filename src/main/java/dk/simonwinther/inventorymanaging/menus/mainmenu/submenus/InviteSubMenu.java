package dk.simonwinther.inventorymanaging.menus.mainmenu.submenus;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.enums.ColorDataEnum;
import dk.simonwinther.enums.ColorIndexEnum;
import dk.simonwinther.inventorymanaging.Menu;
import dk.simonwinther.inventorymanaging.menus.mainmenu.MainMenu;
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

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InviteSubMenu extends Menu
{
    private MainPlugin plugin;
    private UUID playerUuid;
    private MainMenu mainMenu;
    private int slot;
    private GangManaging gangManaging;

    public InviteSubMenu(GangManaging gangManaging, MainPlugin plugin, MainMenu mainMenu, UUID playerUuid)
    {
        this.plugin = plugin;
        this.mainMenu = mainMenu;
        this.playerUuid = playerUuid;
        this.gangManaging = gangManaging;
    }

    @Override
    protected String getName()
    {
        return "Bande";
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
            default:

                String line = ChatColor.stripColor(item.getItemMeta().getLore().toString().split(",")[0].trim());

                Pattern pattern = Pattern.compile("Navn: (?<gangName>.+)");

                Matcher m = pattern.matcher(line);

                if (m.find())
                {
                    if (!this.gangManaging.playerInGangPredicate.test(whoClicked.getUniqueId()))
                    {
                        String gangName = m.group("gangName").replace(" ", "");
                        if (this.gangManaging.gangExistsPredicate.test(gangName))
                        {
                            Gang gang = this.gangManaging.getGangByNameFunction.apply(gangName);
                            if (gang.getMemberInvitations().contains(whoClicked.getName().toLowerCase()))
                            {
                                this.gangManaging.joinGang(whoClicked.getUniqueId(), whoClicked.getName(), gangName);
                                whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().SUCCESSFULLY_JOINED_GANG.replace("{name}", gangName)));
                                Bukkit.getOnlinePlayers()
                                        .stream()
                                        .forEach(globalPlayer -> globalPlayer.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().SUCCESSFULLY_JOINED_GANG_GLOBAL.replace("{spiller}", globalPlayer.getName()).replace("{name}", gangName))));

                            } else
                                whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_INVITED_TO_GANG));
                        } else
                            whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().GANG_DOES_NOT_EXISTS.replace("{name}", gangName)));
                    } else
                        whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().ALREADY_IN_GANG));
                }


                break;

        }
    }

    @Override
    public Inventory getInventory()
    {

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (inventory.getViewers().size() < 1)
                {
                    this.cancel();
                    return;
                }
                inventory.clear();
                InventoryUtility.decorate(inventory, InventoryUtility.MENU_PREDICATE_SIX, new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.LIME.value[ColorIndexEnum.STAINED_GLASS.index]), true);
                addInviteItemsToInventory();
            }
        }.runTaskTimer(plugin, 0l, 10l);
        return super.inventory;
    }

    private void addInviteItemsToInventory()
    {
        slot = 10;
        this.gangManaging.getGangMap()
                .values()
                .stream()
                .filter(gang -> gang.getMemberInvitations().contains(Bukkit.getPlayer(playerUuid).getName().toLowerCase()))
                .forEach(gang ->
                {
                    if (slot > 43) return;
                    if (slot == 17 | slot == 26 | slot == 35) slot += 2;
                    super.inventory.setItem(slot++, new ItemBuilder(Material.SKULL_ITEM, gang.getGangLevel(), SkullType.PLAYER).setPlayerSkull(gang.getOwnerName()).setItemName("&a&lInvitation").setLore("&aNavn:&f " + gang.getGangName(), "&aLevel: &f" + gang.getGangLevel(), "", "&fKlik her for at", "&facceptere invitationen").buildItem());
                });
    }


}
