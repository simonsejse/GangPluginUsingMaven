package dk.simonwinther.inventorymanaging.menus.mainmenu.submenus;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.inventorymanaging.AbstractPaginatedMenu;
import dk.simonwinther.manager.Gang;
import dk.simonwinther.manager.GangManaging;
import dk.simonwinther.constants.ColorDataEnum;
import dk.simonwinther.constants.ColorIndexEnum;
import dk.simonwinther.inventorymanaging.menus.mainmenu.MainMenu;
import dk.simonwinther.utility.InventoryUtility;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.IntPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InviteSubMenu extends AbstractPaginatedMenu
{
    private MainPlugin plugin;
    private MessageProvider mp;
    private UUID playerUUID;
    private MainMenu mainMenu;
    private final GangManaging gangManaging;


    public InviteSubMenu(GangManaging gangManaging, MainPlugin plugin, MainMenu mainMenu, UUID playerUUID)
    {
        super();
        this.plugin = plugin;
        this.mp = this.plugin.getMessageProvider();
        this.mainMenu = mainMenu;
        this.playerUUID = playerUUID;
        this.gangManaging = gangManaging;
        setItemStacksForInventory(this.gangManaging.gangMap
                .values()
                .stream()
                .filter(gang -> gang.getMemberInvitations().contains(Bukkit.getPlayer(playerUUID).getName().toLowerCase()))
                .map(gang -> new ItemBuilder(Material.SKULL_ITEM, gang.getGangLevel(), SkullType.PLAYER).setPlayerSkull(gang.getOwnerName()).setItemName("&a&lInvitation").setLore("&aNavn:&f " + gang.getGangName(), "&aLevel: &f" + gang.getGangLevel(), "", "&fKlik her for at", "&facceptere invitationen").buildItem())
                .toArray(ItemStack[]::new)
        );
        this.addSeparateItems();
    }

    @Override
    protected String getName()
    {
        return "Bande";
    }

    @Override
    protected int backPageSlot() {
        return 42;
    }

    @Override
    protected int frontPageSlot() {
        return 43;
    }

    @Override
    protected IntPredicate menuDecoration() {
        return InventoryUtility.MENU_PREDICATE_SIX;
    }

    @Override
    protected ItemStack itemDecoration() {
        return new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.LIME.value[ColorIndexEnum.STAINED_GLASS.index]);
    }

    @Override
    protected int offsetStart() {
        return 10;
    }

    @Override
    protected int availableSlots() {
        return 26;
    }

    @Override
    protected int getSizeOfInventory() {
        return 9*6;
    }

    @Override
    public void onGuiClick(int slot, ItemStack item, Player whoClicked, ClickType clickType) {
        switch (item.getType())
        {
            case BED:
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
                                whoClicked.sendMessage(this.mp.successfullyJoinedGang.replace("{name}", gangName));
                                Bukkit.getOnlinePlayers()
                                        .stream()
                                        .forEach(globalPlayer -> globalPlayer.sendMessage(mp.successfullyJoinedGangGlobal.replace("{spiller}", globalPlayer.getName()).replace("{name}", gangName)));

                            } else
                                whoClicked.sendMessage(this.mp.notInvitedToGang);
                        } else
                            whoClicked.sendMessage(this.mp.gangDoesNotExists.replace("{name}", gangName));
                    } else
                        whoClicked.sendMessage(this.mp.alreadyInGang);
                }
                break;
        }
    }

    @Override
    protected void addSeparateItems() {

    }

    @Override
    public Inventory getInventory() {
        return inventories.get(0);
    }

}
