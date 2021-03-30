package dk.simonwinther.inventorymanaging.menus.rankmenu;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.inventorymanaging.menus.infomenu.submenus.EditMemberMenu;
import dk.simonwinther.manager.Gang;
import dk.simonwinther.manager.GangManaging;
import dk.simonwinther.constants.Rank;
import dk.simonwinther.inventorymanaging.AbstractMenu;
import dk.simonwinther.utility.MessageProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class RankMenu extends AbstractMenu
{
    private MainPlugin plugin;

    private Gang gang;

    private final UUID otherUUID;
    private final String othersName;
    private final UUID playerUUID;
    private final GangManaging gangManaging;
    private final MessageProvider mp;


    public RankMenu(GangManaging gangManaging, MainPlugin plugin, Gang gang, UUID playerUUID, UUID otherUUID, String othersName)
    {
        super();
        this.plugin = plugin;
        this.mp = plugin.getMessageProvider();
        this.gang = gang;
        this.playerUUID = playerUUID;
        this.otherUUID = otherUUID;
        this.othersName = othersName;
        this.gangManaging = gangManaging;
    }

    @Override
    protected String getName()
    {
        return "Rank player";
    }

    @Override
    protected int getSize()
    {
        return 9 * 3;
    }

    @Override
    public void onGuiClick(int slot, ItemStack item, Player whoClicked, ClickType clickType)
    {
        if (playerUUID.equals(otherUUID)){
            whoClicked.getOpenInventory().close();
            whoClicked.sendMessage("Du kan ikke ændre din egen rang.");
            return;
        }
        if (!this.gangManaging.playerInGangPredicate.test(this.playerUUID)) {
            whoClicked.getOpenInventory().close();
            return;
        }
        switch (item.getType())
        {
            case IRON_INGOT:
                if (gang.getMembersSorted().get(this.playerUUID) > gang.getMembersSorted().get(otherUUID) && gang.getMembersSorted().get(this.playerUUID) >= Rank.MEMBER.getValue())
                    gang.getMembersSorted().compute(otherUUID, (uuid, key) -> Rank.MEMBER.getValue());
                else whoClicked.sendMessage(this.mp.notHighRankEnough);
                break;
            case GOLD_INGOT:
                if (gang.getMembersSorted().get(this.playerUUID) > gang.getMembersSorted().get(otherUUID) && gang.getMembersSorted().get(this.playerUUID) > Rank.OFFICER.getValue())
                    gang.getMembersSorted().compute(otherUUID, (key, value) -> Rank.OFFICER.getValue());
                else whoClicked.sendMessage(this.mp.notHighRankEnough);
                break;
            case DIAMOND:
                if (gang.getMembersSorted().get(this.playerUUID) > gang.getMembersSorted().get(otherUUID) && gang.getMembersSorted().get(this.playerUUID) >= Rank.CO_LEADER.getValue())
                    gang.getMembersSorted().compute(otherUUID, (key, value) -> Rank.CO_LEADER.getValue());
                else whoClicked.sendMessage(this.mp.notHighRankEnough);
                break;
            case EMERALD:
                if (gang.getMembersSorted().get(this.playerUUID) == Rank.LEADER.getValue())
                {
                    gang.getMembersSorted().compute(otherUUID, (key, value) -> Rank.LEADER.getValue());
                    gang.getMembersSorted().compute(this.playerUUID, (key, value) -> Rank.CO_LEADER.getValue());
                    gang.getMembersSorted().keySet()
                            .stream()
                            .filter(p -> Bukkit.getPlayer(p) != null)
                            .map(Bukkit::getPlayer)
                            .forEach(p ->
                            {
                                if (p != null)
                                    p.sendMessage(this.mp.newLeader.replace("{player}", whoClicked.getName()).replace("{name}", gang.getGangName()).replace("{newleader}", othersName));
                            });

                } else whoClicked.sendMessage(this.mp.notHighRankEnough);

                break;
        }
        whoClicked.openInventory(this.getInventory());
    }

    @Override
    public Inventory getInventory()
    {
        String rankOfPlayer = getRankOfPlayer();
        if (rankOfPlayer == null) Bukkit.getPlayer(playerUUID).closeInventory();
        setItem(10, memberItem.isItemChosen(rankOfPlayer.equalsIgnoreCase("member")).buildItem());
        setItem(12, officerItem.isItemChosen(rankOfPlayer.equalsIgnoreCase("moderator")).buildItem());
        setItem(14, coLeaderItem.isItemChosen(rankOfPlayer.equalsIgnoreCase("co-leder")).buildItem());
        setItem(16, leaderItem.isItemChosen(rankOfPlayer.equalsIgnoreCase("leder")).buildItem());
        return super.inventory;
    }

    public String getRankOfPlayer()
    {
        for (Rank rankEnum : Rank.values())
        {
            if (gang.getMembersSorted().get(otherUUID) == rankEnum.getValue())
            {
                return rankEnum.getRankName();
            }
        }
        return null;
    }

    private final ItemBuilder memberItem = new ItemBuilder(Material.IRON_INGOT).setItemName("&a&lRANK &eMember");
    private final ItemBuilder officerItem = new ItemBuilder(Material.GOLD_INGOT).setItemName("&a&lRANK &dOfficer");
    private final ItemBuilder coLeaderItem = new ItemBuilder(Material.DIAMOND).setItemName("&a&lRANK &bCo-Leder");
    private final ItemBuilder leaderItem = new ItemBuilder(Material.EMERALD).setItemName("&a&lRANK &6Leder");
}
