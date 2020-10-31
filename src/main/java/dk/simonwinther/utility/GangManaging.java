package dk.simonwinther.utility;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Gang;
import dk.simonwinther.enums.Rank;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.function.*;

public class GangManaging
{
    //Normally use dependency injection instead of singleton instance, but since it's a Utility class I never create an instance!
    private MainPlugin plugin = MainPlugin.getPlugin(MainPlugin.class);

    private final int gangCost = 10000;

    public int getGangCost()
    {
        return gangCost;
    }

    public int gangTotal = 0; //Set MySQL value.

    //UUID, Gangname
    public Map<UUID, String> namesOfGang = new HashMap<>();
    //Gangname, Gang instance
    public Map<String, Gang> gangMap = new HashMap<>();

    public Map<UUID, Boolean> damageMap = new HashMap<>();

    public Map<String, Gang> getGangMap()
    {
        return gangMap;
    }

    public int getGangTotal()
    {
        return gangTotal;
    }

    public BiConsumer<? super UUID, String> createNewGangBiConsumer = (ownerUuid, gangName) -> {
        namesOfGang.put(ownerUuid, gangName);
        gangMap.put(gangName, new Gang(++gangTotal, gangName, ownerUuid));
    };

    private Function<String, String> lowerCaseFunc = String::toLowerCase;
    public Function<? super UUID, Gang> getGangByUuidFunction = uuid -> gangMap.get(namesOfGang.get(uuid));
    public Predicate<String> gangExistsPredicate = gangMap::containsKey;
    public BiPredicate<Gang, String> gangContainsAllyInvitationPredicate = (gang, string) -> gang.getAllyInvitation().contains(string);
    public Function<? super UUID, Integer> rankFunction = uuid -> getGangByUuidFunction.apply(uuid).getMembersSorted().get(uuid);

    public BiConsumer<? super Gang, UUID> kick = (gang, uuid) -> {
        this.namesOfGang.remove(uuid);
        gang.getMembersSorted().remove(uuid);
    };

    public BiPredicate<? super UUID, Rank> isRankMinimumPredicate = (uuid, rank) -> getGangByUuidFunction.apply(uuid).getMembersSorted().get(uuid) >= rank.getValue();
    public BiConsumer<? super Gang, ? super Gang> addEnemyGang = (playerGang, enemyGang) -> {
        String enemyGangName = lowerCaseFunc.apply(enemyGang.getGangName());
        String playerGangName = lowerCaseFunc.apply(playerGang.getGangName());
        playerGang.getEnemies().add(lowerCaseFunc.apply(enemyGangName));
        playerGang.getMembersSorted().keySet().stream().filter(uuid -> Bukkit.getPlayer(uuid) != null).map(Bukkit::getPlayer).forEach(teamMember -> teamMember.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().ENEMY_SUCCESSFUL.replace("{name}", enemyGang.getGangName()))));
        if (playerGang.getAllies().contains(enemyGangName) || enemyGang.getAllies().contains(playerGangName))
        {
            playerGang.getAllies().remove(enemyGangName);
            enemyGang.getAllies().remove(playerGangName);

            enemyGang.getMembersSorted().keySet()
                    .stream()
                    .filter(uuid -> Bukkit.getPlayer(uuid) != null)
                    .map(Bukkit::getPlayer)
                    .forEach(member -> member.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NO_LONGER_ALLYS.replace("{name}", playerGang.getGangName()))));
        }
    };
    public Function<String, Gang> getGangByNameFunction = gangMap::get;
    public BiPredicate<? super UUID, ? super UUID> playersInSameGangPredicate = (uuid, uuid2) -> getGangByUuidFunction.apply(uuid).equals(getGangByUuidFunction.apply(uuid2));
    public Consumer<? super UUID> deleteGangConsumer = uuid -> {
        gangMap.remove(namesOfGang.get(uuid));
        namesOfGang.remove(uuid);
    };
    public Function<String, List<Gang>> allyInvitationGangListFunction = gangName -> { //Enter gangname to check if other gangs contains that Gangname
        List<Gang> listOfGangs = new ArrayList<>();
        final Consumer<Gang> gangConsumer = listOfGangs::add;
        final Predicate<Gang> gangPredicate = gang -> gang.getAllyInvitation().contains(gangName);
        gangMap.values().stream().filter(gangPredicate).forEach(gangConsumer);
        return listOfGangs;
    };
    public Function<String, List<Gang>> enemyGangListFunction = gangName -> { //Enter gangname to check if other gangs contains that Gangname
        List<Gang> listOfGangs = new ArrayList<>();
        final Consumer<Gang> gangConsumer = listOfGangs::add;
        final Predicate<Gang> gangPredicate = gang -> gang.getEnemies().contains(gangName);
        gangMap.values().stream().filter(gangPredicate).forEach(gangConsumer);
        return listOfGangs;
    };

    public Predicate<? super UUID> ownerOfGangPredicate = uuid -> getGangByUuidFunction.apply(uuid).getMembersSorted().get(uuid) == Rank.LEADER.getValue();
    public Predicate<? super UUID> playerInGangPredicate = namesOfGang::containsKey;
    public Function<? super UUID, String> gangNameFunction = namesOfGang::get;
    public Consumer<? super UUID> kickConsumer = uuid -> {
        getGangByUuidFunction.apply(uuid).getMembersSorted().remove(uuid);
        namesOfGang.remove(uuid);
    };
    public void joinGang(UUID uuid, String displayName, String gangName){
        getGangByNameFunction.apply(gangName).addMember(uuid, lowerCaseFunc.apply(displayName), Rank.MEMBER);
        namesOfGang.put(uuid, gangName);
    }
    public Predicate<? super UUID> hasMemberSpacePredicate = uuid -> {
        Gang gang = getGangByUuidFunction.apply(uuid);
        return gang.getMembersSorted().size() < gang.getMaxMembers();
    };
    public BiConsumer<? super UUID, String> addInvitationConsumer = (uuid, s) -> getGangByUuidFunction.apply(uuid).inviteMember(s);
    public BiPredicate<? super String, String> alreadyInvitedByGangNamePredicate = (gangName, nameOfInvitedPlayer) -> getGangByNameFunction.apply(gangName).isPlayerInvited(lowerCaseFunc.apply(nameOfInvitedPlayer));
    public BiPredicate<? super UUID, String> alreadyInvitedByUuidPredicate = (uuid, nameOfInvitedPlayer) -> getGangByUuidFunction.apply(uuid).isPlayerInvited(lowerCaseFunc.apply(nameOfInvitedPlayer));
    public BiConsumer<? super UUID, String> removeInvitationConsumer = (uuid, nameOfInvitedPlayer) -> getGangByUuidFunction.apply(uuid).getMemberInvitations().remove(lowerCaseFunc.apply(nameOfInvitedPlayer));

}