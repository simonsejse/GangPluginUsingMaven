package dk.simonwinther.inventorymanaging.menus.rankmenu;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Builders.ItemBuilder;
import dk.simonwinther.Gang;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.enums.Rank;
import dk.simonwinther.inventorymanaging.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class RankMenu extends Menu
{
    private MainPlugin plugin;

    private Gang gang;

    private UUID argsUuid;

    private String nameOfArgs;
    private UUID playerUuid;
    private String nameOfPlayer;
    private final GangManaging gangManaging;





    public UUID getArgsUuid()
    {
        return argsUuid;
    }



    public String getNameOfArgs()
    {
        return nameOfArgs;
    }

    public void setNameOfArgs(String nameOfArgs)
    {
        this.nameOfArgs = nameOfArgs;
    }

    public UUID getPlayerUuid()
    {
        return playerUuid;
    }

    public void setPlayerUuid(UUID playerUuid)
    {
        this.playerUuid = playerUuid;
    }

    public RankMenu(GangManaging gangManaging, MainPlugin plugin, Gang gang, UUID playerUuid, String nameOfPlayer, UUID argsUuid, String nameOfArgs)
    {
        super();
        this.plugin = plugin;
        this.gang = gang;
        this.playerUuid = playerUuid;
        this.nameOfPlayer = nameOfPlayer;
        this.argsUuid = argsUuid;
        this.nameOfArgs = nameOfArgs;
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
        if (playerUuid.equals(argsUuid)){
            whoClicked.getOpenInventory().close();
            whoClicked.sendMessage("Du kan ikke ændre din egen rang.");
            return;
        }
        if (!this.gangManaging.playerInGangPredicate.test(whoClicked.getUniqueId())) {
            whoClicked.getOpenInventory().close();
            return;
        }
        switch (item.getType())
        {
            case IRON_INGOT:
                if (gang.getMembersSorted().get(getPlayerUuid()) > gang.getMembersSorted().get(getArgsUuid()) && gang.getMembersSorted().get(getPlayerUuid()) >= Rank.MEMBER.getValue())
                    gang.getMembersSorted().compute(getArgsUuid(), (uuid, key) -> Rank.MEMBER.getValue());
                else whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_HIGH_RANK_ENOUGH));
                break;
            case GOLD_INGOT:
                if (gang.getMembersSorted().get(getPlayerUuid()) > gang.getMembersSorted().get(getArgsUuid()) && gang.getMembersSorted().get(getPlayerUuid()) > Rank.OFFICER.getValue())
                    gang.getMembersSorted().compute(getArgsUuid(), (key, value) -> Rank.OFFICER.getValue());
                else whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_HIGH_RANK_ENOUGH));
                break;
            case DIAMOND:
                if (gang.getMembersSorted().get(getPlayerUuid()) > gang.getMembersSorted().get(getArgsUuid()) && gang.getMembersSorted().get(getPlayerUuid()) >= Rank.CO_LEADER.getValue())
                    gang.getMembersSorted().compute(getArgsUuid(), (key, value) -> Rank.CO_LEADER.getValue());
                else whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_HIGH_RANK_ENOUGH));
                break;
            case EMERALD:
                if (gang.getMembersSorted().get(getPlayerUuid()) == Rank.LEADER.getValue())
                {
                    gang.getMembersSorted().compute(getArgsUuid(), (key, value) -> Rank.LEADER.getValue());
                    gang.getMembersSorted().compute(getPlayerUuid(), (key, value) -> Rank.CO_LEADER.getValue());
                    gang.getMembersSorted().keySet()
                            .stream()
                            .filter(p -> Bukkit.getPlayer(p) != null)
                            .map(Bukkit::getPlayer)
                            .forEach(p ->
                            {
                                if (p != null)
                                    p.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NEW_LEADER.replace("{player}", whoClicked.getName()).replace("{name}", gang.getGangName()).replace("{newleader}", getNameOfArgs())));
                            });

                } else whoClicked.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_HIGH_RANK_ENOUGH));

                break;
        }
        whoClicked.openInventory(this.getInventory());
    }

    @Override
    public Inventory getInventory()
    {

        String rankOfPlayer = null;
        try
        {
            rankOfPlayer = getRankOfPlayer();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        setItem(10, new ItemBuilder(Material.IRON_INGOT).setItemName("&a&lRANK &eMember").isItemChosen(rankOfPlayer.equalsIgnoreCase("member")).buildItem());
        setItem(12, new ItemBuilder(Material.GOLD_INGOT).setItemName("&a&lRANK &dOfficer").isItemChosen(rankOfPlayer.equalsIgnoreCase("moderator")).buildItem());
        setItem(14, new ItemBuilder(Material.DIAMOND).setItemName("&a&lRANK &bCo-Leder").isItemChosen(rankOfPlayer.equalsIgnoreCase("co-leder")).buildItem());
        setItem(16, new ItemBuilder(Material.EMERALD).setItemName("&a&lRANK &6Leder").isItemChosen(rankOfPlayer.equalsIgnoreCase("leder")).buildItem());
        return super.inventory;
    }

    public String getRankOfPlayer() throws Exception
    {
        for (Rank rankEnum : Rank.values())
        {
            if (gang.getMembersSorted().get(argsUuid) == rankEnum.getValue())
            {
                return rankEnum.getRankName();
            }
        }
        throw new Exception("MenuRank spilleren har ingen rank?");
    }


}
