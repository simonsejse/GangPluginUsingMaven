package dk.simonwinther.utility;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Gang;
import dk.simonwinther.enums.Rank;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class GangManaging
{
    //Normally use dependency injection instead of singleton instance, but since it's a Utility class I never create an instance!
    private MessageProvider mp;

    public GangManaging(MainPlugin plugin){
        this.mp = plugin.getMessageProvider();

    }
    public final int GANG_COST = 10000;

    //UUID, Gang Name
    public Map<UUID, String> userGangMap = new HashMap<>();
    //Gang Name, Gang Instance
    public Map<String, Gang> gangMap = new HashMap<>();

    public Map<UUID, Boolean> damageMap = new HashMap<>();

    public Map<String, Gang> getGangMap()
    {
        return gangMap;
    }

    public BiConsumer<? super UUID, String> createNewGangBiConsumer = (ownerUuid, gangName) -> {
        userGangMap.put(ownerUuid, gangName);
        gangMap.put(gangName, new Gang((this.gangMap.size() + 1), gangName, ownerUuid));
    };

    private Function<String, String> lowerCaseFunc = String::toLowerCase;
    public Function<? super UUID, Gang> getGangByUuidFunction = uuid -> gangMap.get(userGangMap.get(uuid));
    public Predicate<String> gangExistsPredicate = gangMap::containsKey;
    public BiPredicate<Gang, String> gangContainsAllyInvitationPredicate = (gang, string) -> gang.getAllyInvitation().contains(string);
    public Function<? super UUID, Integer> rankFunction = uuid -> getGangByUuidFunction.apply(uuid).getMembersSorted().get(uuid);

    public BiConsumer<? super Gang, UUID> kick = (gang, uuid) -> {
        this.userGangMap.remove(uuid);
        gang.getMembersSorted().remove(uuid);
    };

    public BiPredicate<? super UUID, Rank> isRankMinimumPredicate = (uuid, rank) -> getGangByUuidFunction.apply(uuid).getMembersSorted().get(uuid) >= rank.getValue();
    public BiConsumer<? super Gang, ? super Gang> addEnemyGang = (playerGang, enemyGang) -> {
        int playerGangID = playerGang.getGangId();
        int enemyGangID = enemyGang.getGangId();

        playerGang.getEnemies().put(enemyGangID, enemyGang.getGangName());

        playerGang.getMembersSorted().keySet().stream().filter(uuid -> Bukkit.getPlayer(uuid) != null).map(Bukkit::getPlayer).forEach(teamMember -> teamMember.sendMessage(this.mp.enemySuccessful.replace("{name}", enemyGang.getGangName())));
        if (playerGang.getEnemies().keySet().contains(enemyGangID)){
            playerGang.getAllies().remove(enemyGangID);
            sendNoLongerEnemyMessage(enemyGang.getGangName(), playerGang.getMembersSorted());
        }
        if (enemyGang.getEnemies().keySet().contains(playerGangID)){
            enemyGang.getEnemies().remove(playerGangID);
            sendNoLongerEnemyMessage(playerGang.getGangName(), enemyGang.getMembersSorted());
        }
    };
    public Function<String, Gang> getGangByNameFunction = gangMap::get;
    public BiPredicate<? super UUID, ? super UUID> playersInSameGangPredicate = (uuid, uuid2) -> getGangByUuidFunction.apply(uuid).equals(getGangByUuidFunction.apply(uuid2));
    public Consumer<? super UUID> deleteGangConsumer = uuid -> {
        gangMap.remove(userGangMap.get(uuid));
        userGangMap.remove(uuid);
    };
    public Function<String, List<Gang>> allyInvitationGangListFunction = gangName -> { //Enter gangname to check if other gangs contains that Gangname
        List<Gang> listOfGangs = new ArrayList<>();
        final Consumer<Gang> gangConsumer = listOfGangs::add;
        final Predicate<Gang> gangPredicate = gang -> gang.getAllyInvitation().contains(gangName);
        gangMap.values().stream().filter(gangPredicate).forEach(gangConsumer);
        return listOfGangs;
    };
    public Function<String, List<Gang>> enemyGangListFunction = gangName -> { //Enter gangname to check if other gangs contains that Gangname
        return gangMap.values().stream().filter(gang -> gang.getEnemies().values().contains(gangName)).collect(Collectors.toList());
    };
    public Predicate<? super UUID> ownerOfGangPredicate = uuid -> getGangByUuidFunction.apply(uuid).getMembersSorted().get(uuid) == Rank.LEADER.getValue();

    public Predicate<? super UUID> playerInGangPredicate = userGangMap::containsKey;
    public Function<? super UUID, String> gangNameFunction = userGangMap::get;
    public Consumer<? super UUID> kickConsumer = uuid -> {
        getGangByUuidFunction.apply(uuid).getMembersSorted().remove(uuid);
        userGangMap.remove(uuid);
    };
    public void joinGang(UUID uuid, String displayName, String gangName){
        getGangByNameFunction.apply(gangName).addMember(uuid, lowerCaseFunc.apply(displayName), Rank.MEMBER);
        userGangMap.put(uuid, gangName);
    }
    public BiConsumer<? super Gang, ? super String> sendTeamMessage = (gang, message) -> {
        gang.getMembersSorted()
                .keySet()
                .stream()
                .filter(uuid -> Bukkit.getPlayer(uuid) != null)
                .map(Bukkit::getPlayer)
                .forEach(player -> player.sendMessage(message));
    };
    public Predicate<? super UUID> hasMemberSpacePredicate = uuid -> {
        Gang gang = getGangByUuidFunction.apply(uuid);
        return gang.getMembersSorted().size() < gang.getMaxMembers();
    };
    public BiConsumer<? super UUID, String> addInvitationConsumer = (uuid, s) -> getGangByUuidFunction.apply(uuid).inviteMember(s);
    public BiPredicate<? super String, String> alreadyInvitedByGangNamePredicate = (gangName, nameOfInvitedPlayer) -> getGangByNameFunction.apply(gangName).isPlayerInvited(lowerCaseFunc.apply(nameOfInvitedPlayer));
    public BiPredicate<? super UUID, String> alreadyInvitedByUuidPredicate = (uuid, nameOfInvitedPlayer) -> getGangByUuidFunction.apply(uuid).isPlayerInvited(lowerCaseFunc.apply(nameOfInvitedPlayer));
    public BiConsumer<? super UUID, String> removeInvitationConsumer = (uuid, nameOfInvitedPlayer) -> getGangByUuidFunction.apply(uuid).getMemberInvitations().remove(lowerCaseFunc.apply(nameOfInvitedPlayer));

    //
    public void sendNoLongerEnemyMessage(String enemyGangName, Map<UUID, Integer> members){
        members.keySet()
                .stream()
                .filter(uuid -> Bukkit.getPlayer(uuid) != null)
                .map(Bukkit::getPlayer)
                .forEach(member -> member.sendMessage(this.mp.noLongerAllies.replace("{name}", enemyGangName)));
    }
}