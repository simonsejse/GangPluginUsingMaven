package dk.simonwinther.inventorymanaging.menus.infomenu.submenus;

import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.MainPlugin;
import dk.simonwinther.constants.ColorDataEnum;
import dk.simonwinther.constants.ColorIndexEnum;
import dk.simonwinther.inventorymanaging.AbstractMenu;
import dk.simonwinther.inventorymanaging.menus.rankmenu.RankMenu;
import dk.simonwinther.manager.Gang;
import dk.simonwinther.manager.GangManaging;
import dk.simonwinther.utility.InventoryUtility;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class EditMemberMenu extends AbstractMenu {

    private final String chosenPlayer;
    private final Gang gang;
    private final MainPlugin plugin;
    private final MessageProvider mp;
    private final GangManaging gangManaging;
    private final PaginatedMemberSubMenu paginatedMemberSubMenu;

    public EditMemberMenu(String chosenPlayer, GangManaging gangManaging, MainPlugin plugin, Gang gang, PaginatedMemberSubMenu paginatedMemberSubMenu){
        this.chosenPlayer = chosenPlayer;
        this.gangManaging = gangManaging;
        this.plugin = plugin;
        this.mp = this.plugin.getMessageProvider();
        this.gang = gang;
        this.paginatedMemberSubMenu = paginatedMemberSubMenu;
    }

    @Override
    public Inventory getInventory()
    {
        InventoryUtility.decorate(super.inventory, InventoryUtility.MENU_PREDICATE_TWO, decorateItem, true);

        super.setItem(20, changeRankItem.setItemName("&a&lÆndre &2" + chosenPlayer + "&a&l rang").setLore("&fKlik her for at", "&fændre spillerens rang").buildItem());
        super.setItem(24, kickPlayerItem.setItemName("&c&lKick &4&l" + chosenPlayer).setLore("&fKlik her for at", "&fsmide &7" + chosenPlayer + "&f ud af banden").buildItem());
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
                whoClicked.openInventory(this.paginatedMemberSubMenu.getInventory());
                break;
            case NETHER_STAR:
                if (whoClicked.getName().equalsIgnoreCase(chosenPlayer))
                {
                    whoClicked.sendMessage(mp.cantRankYourself);
                    return;
                }
                whoClicked.openInventory(new RankMenu(this.gangManaging, this.plugin, this.gang, whoClickedUuid, getChosenPlayerUUID(), chosenPlayer).getInventory());
                break;
            case BARRIER:
                if (gangManaging.isRankMinimumPredicate.test(whoClickedUuid, gang.gangPermissions.accessToKick))
                {
                    if (gangManaging.rankFunction.apply(whoClickedUuid) > gangManaging.rankFunction.apply(getChosenPlayerUUID()))
                    {
                        gangManaging.kickConsumer.accept(getChosenPlayerUUID());
                        whoClicked.getOpenInventory().close();
                        whoClicked.sendMessage(mp.playerWasKicked.replace("{args}", chosenPlayer));
                        if(Bukkit.getPlayer(getChosenPlayerUUID()) != null) Bukkit.getPlayer(getChosenPlayerUUID()).sendMessage(mp.memberKicked.replace("{name}", gang.getGangName()));
                    } else whoClicked.sendMessage(mp.playerWasKicked);
                } else whoClicked.sendMessage(mp.notHighRankEnough);
                break;
        }
    }

    private final ItemStack decorateItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, ColorDataEnum.LIME.value[ColorIndexEnum.STAINED_GLASS.index]);
    private final ItemBuilder changeRankItem = new ItemBuilder(Material.NETHER_STAR);
    private final ItemBuilder kickPlayerItem = new ItemBuilder(Material.BARRIER);

    public UUID getChosenPlayerUUID()
    {
        return Bukkit.getOfflinePlayer(chosenPlayer).getUniqueId();
    }

}
