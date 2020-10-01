package dk.simonwinther.inventorymanaging.menus.infomenu.submenus;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.enums.ColorDataEnum;
import dk.simonwinther.enums.ColorIndexEnum;
import dk.simonwinther.enums.Rank;
import dk.simonwinther.inventorymanaging.Menu;
import dk.simonwinther.inventorymanaging.menus.infomenu.InfoMenu;
import dk.simonwinther.inventorymanaging.menus.rankmenu.RankMenu;
import dk.simonwinther.utility.InventoryUtility;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;

public class MemberSubMenu extends Menu
{
    private InfoMenu infoMenu;
    private Gang gang;
    private MainPlugin plugin;

    public MemberSubMenu(MainPlugin plugin, InfoMenu infoMenu, Gang gang)
    {
        this.plugin = plugin;
        this.infoMenu = infoMenu;
        this.gang = gang;
    }

    @Override
    protected String getName()
    {
        return "Medlemmer";
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
                whoClicked.openInventory(infoMenu.getInventory());
                break;

            case 51:
                whoClicked.openInventory(new ListInvitationsSubMenu(plugin, GangManaging.getGangByUuidFunction.apply(whoClicked.getUniqueId()), this).getInventory());
                break;
            case 47:
                whoClicked.getOpenInventory().close();
                plugin.getEventHandling().addInviteMemberChatConsumer.accept(whoClicked.getUniqueId());
                whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().WHO_TO_INVITE_CHAT));
                break;
            default:
                if (item.getType() != Material.SKULL_ITEM) return;

                String chosenPlayer = ChatColor.stripColor(item.getItemMeta().getDisplayName());

                whoClicked.openInventory(new Menu()
                {


                    @Override
                    public Inventory getInventory()
                    {
                        InventoryUtility.decorate(super.inventory, InventoryUtility.menuLookTwoPredicate, new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.LIME.value[ColorIndexEnum.STAINED_GLASS.index]), true);

                        super.setItem(20, new ItemBuilder(Material.NETHER_STAR).setItemName("&a&lÆndre &2" + chosenPlayer + "&a&l rang").setLore("&fKlik her for at", "&fændre spillerens rang").buildItem());
                        super.setItem(24, new ItemBuilder(Material.BARRIER).setItemName("&c&lKick &4&l" + chosenPlayer).setLore("&fKlik her for at", "&fsmide &7" + chosenPlayer + "&f ud af banden").buildItem());
                        return super.inventory;
                    }

                    @Override
                    protected String getName()
                    {
                        return "Administrer " + chosenPlayer;
                    }

                    @Override
                    protected int getSize()
                    {
                        return 9 * 6;
                    }

                    @Override
                    public void onGuiClick(int slot, ItemStack item, Player whoClicked, ClickType clickType)
                    {
                        UUID whoClickedUuid = whoClicked.getUniqueId();
                        switch (item.getType())
                        {
                            case BED:
                                whoClicked.openInventory(MemberSubMenu.this.getInventory());
                                break;
                            case NETHER_STAR:
                                if (whoClicked.getName().equalsIgnoreCase(chosenPlayer))
                                {
                                    whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().CANT_RANK_YOURSELF));
                                    return;
                                }
                                whoClicked.openInventory(new RankMenu(plugin, gang, whoClickedUuid, whoClicked.getName(), getChosenPlayerUuid(), chosenPlayer).getInventory());
                                break;
                            case BARRIER:
                                if (GangManaging.isRankMinimumPredicate.test(whoClickedUuid, gang.gangPermissions.accessToKick))
                                {
                                    if (GangManaging.rankFunction.apply(whoClickedUuid) > GangManaging.rankFunction.apply(getChosenPlayerUuid()))
                                    {
                                        GangManaging.kickConsumer.accept(getChosenPlayerUuid());
                                        whoClicked.getOpenInventory().close();
                                        whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().PLAYER_WAS_KICKED.replace("{args}", chosenPlayer)));
                                        if(Bukkit.getPlayer(getChosenPlayerUuid()) != null) Bukkit.getPlayer(getChosenPlayerUuid()).sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().MEMBER_KICKED.replace("{name}", gang.getGangName())));
                                    } else
                                        whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().KICK_HIGHER_RANK));
                                } else
                                    whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_HIGH_RANK_ENOUGH));
                                break;
                        }
                    }

                    public UUID getChosenPlayerUuid()
                    {
                        return Bukkit.getOfflinePlayer(chosenPlayer).getUniqueId();
                    }
                }.getInventory());
                break;
        }
    }

    private int slot;

    @Override
    public Inventory getInventory()
    {
        slot = 10;
        InventoryUtility.decorate(super.inventory, InventoryUtility.menuLookSixPredicate, new ItemStack(Material.STAINED_GLASS_PANE,1, ColorDataEnum.LIME.value[ColorIndexEnum.STAINED_GLASS.index]), true);
        gang.getMembersSorted()
                .entrySet()
                .stream()
                .sorted(new Comparator<Map.Entry<UUID, Integer>>()
                {
                    @Override
                    public int compare(Map.Entry<UUID, Integer> a, Map.Entry<UUID, Integer> b)
                    {
                        return a.getValue() - b.getValue();
                    }
                }.reversed())
                .map(Map.Entry::getKey)
                .forEach(uuid ->
                {
                    if (slot > 43) return;
                    if (slot == 17 | slot == 26 | slot == 35) slot += 2;
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                    String rank = getRank(uuid);
                    String block = plugin.getBlockAtPlayerLoc(uuid);
                    String lastOnline = getLastOnline(uuid);
                    super.setItem(slot++, new ItemBuilder(Material.SKULL_ITEM, 1, SkullType.PLAYER).setPlayerSkull(offlinePlayer.getName()).setItemName("&a" + offlinePlayer.getName()).setLore("&7Bande rank: " + rank, "&7Blok: &f" + block, "&7Sidst online:&f " + lastOnline, "", "&7Klik her for at administrere medlem").buildItem());
                });

        super.setItem(47, new ItemBuilder(Material.SKULL_ITEM, 1, SkullType.PLAYER).setItemName("&a&lInviter").setLore("&fKlik her for at invitere", "&fet medlem til banden").buildItem());
        super.setItem(51, new ItemBuilder(Material.SKULL_ITEM,1, SkullType.PLAYER).setItemName("&a&lInvitationer").setLore("&fKlik for at se en liste over", "&fhvem der er inviteret til banden").buildItem());
        return super.inventory;
    }



    private String getLastOnline(UUID uuid)
    {
        return plugin.getEventHandling().getDate(uuid);
    }

    private String getRank(UUID uuid)
    {
        for (Rank rank : Rank.values())
        {
            if (rank.getValue() == gang.getMembersSorted().get(uuid)) return rank.getColor() + rank.getRankName();
        }
        return "&aError | &cfejl";
    }
}