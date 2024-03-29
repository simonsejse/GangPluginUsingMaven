package dk.simonwinther.manager;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.constants.Rank;
import dk.simonwinther.utility.MessageProvider;
import dk.simonwinther.utility.ProgessBar;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;

public class GangManaging {
    //Normally use dependency injection instead of singleton instance, but since it's a Utility class I never create an instance!
    private final MainPlugin plugin;
    private final MessageProvider mp;
    private final List<String> bannedWords;

    public GangManaging(MainPlugin plugin) {
        this.plugin = plugin;
        this.mp = this.plugin.getMessageProvider();
        this.bannedWords = this.plugin.getConfiguration().bannedWords;
    }

    public final int GANG_COST = 10000;

    /**
     * UUID, GangInfo -> {name, id
     */
    public Map<UUID, GangInfo> userGangMap = new HashMap<>();
    /**
     * Gang Name, Gang Instance
     */
    public Map<String, Gang> gangMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    /**
     * @Key: Players UUID
     * @Value boolean whether player has damage turned on or off
     */
    public Map<UUID, Boolean> damageMap = new HashMap<>();

    public BiConsumer<? super UUID, String> addNewGangToMapsBiConsumer = (ownerUuid, gangName) -> {
        int gangID = (this.gangMap.size() + 1);
        userGangMap.put(ownerUuid, new GangInfo(gangID, gangName));
        gangMap.put(gangName, new Gang(gangID, gangName, ownerUuid));
    };

    private Function<String, String> lowerCaseFunc = String::toLowerCase;
    public Function<? super UUID, Gang> getGangByUuidFunction = uuid -> gangMap.get(userGangMap.get(uuid).getGangName());
    public Predicate<String> gangExistsPredicate = gangMap::containsKey;
    public Function<? super UUID, Integer> rankFunction = uuid -> getGangByUuidFunction.apply(uuid).getMembersSorted().get(uuid);

    public BiConsumer<? super Gang, ? super String> sendTeamMessage = (gang, message) -> gang.getMembersSorted().keySet().stream().filter(uuid -> Bukkit.getPlayer(uuid) != null).map(Bukkit::getPlayer).forEach(player -> player.sendMessage(message));
    public BiConsumer<? super Gang, UUID> kick = (gang, uuid) -> {
        this.userGangMap.remove(uuid);
        gang.getMembersSorted().remove(uuid);
    };
    public BiPredicate<? super UUID, Rank> isRankMinimumPredicate = (uuid, rank) -> getGangByUuidFunction.apply(uuid).getMembersSorted().get(uuid) >= rank.getValue();
    public Function<String, Gang> getGangByNameFunction = gangMap::get;
    public Consumer<? super UUID> deleteGangConsumer = uuid -> {
        gangMap.remove(userGangMap.get(uuid));
        userGangMap.remove(uuid);
    };
    public Function<String, List<Gang>> allyInvitationGangListFunction = gangName -> { //Enter gangname to check if other gangs contains that Gangname
        List<Gang> listOfGangs = new ArrayList<>();
        final Consumer<Gang> gangConsumer = listOfGangs::add;
        final Predicate<Gang> gangPredicate = gang -> gang.hasRequestedAlly(gangName.toLowerCase());
        gangMap.values().stream().filter(gangPredicate).forEach(gangConsumer);
        return listOfGangs;
    };

    public Function<String, List<Gang>> enemyGangListFunction = gangName -> { //Enter gangname to check if other gangs contains that Gangname
        return gangMap.values().stream().filter(gang -> gang.getEnemies().values().contains(gangName)).collect(Collectors.toList());
    };
    public Predicate<? super UUID> ownerOfGangPredicate = uuid -> getGangByUuidFunction.apply(uuid).getMembersSorted().get(uuid) == Rank.LEADER.getValue();

    public Predicate<? super UUID> playerInGangPredicate = userGangMap::containsKey;

    /**
     * Cannot be a method inside Gang Object since you access userGangMap and is not smart to use DI for this class
     */
    public Consumer<? super UUID> kickConsumer = uuid -> {
        getGangByUuidFunction.apply(uuid).getMembersSorted().remove(uuid);
        userGangMap.remove(uuid);
    };

    public BiPredicate<? super String, String> alreadyInvitedByGangNamePredicate = (gangName, nameOfInvitedPlayer) -> getGangByNameFunction.apply(gangName).isPlayerInvited(lowerCaseFunc.apply(nameOfInvitedPlayer));


    /**
     *
     * @param playerGang the gang that adds new enemy
     * @param otherGangName the name of the enemy gang
     * @param player the player who executed the command
     */
    public void requestEnemy(Gang playerGang, String otherGangName, Player player) {
        {

            UUID playerUUID = player.getUniqueId();
            if (playerInGangPredicate.test(playerUUID))
            {
                if (isRankMinimumPredicate.test(playerUUID, playerGang.gangPermissions.accessToEnemy))
                {
                    if (gangExistsPredicate.test(otherGangName))
                    {
                        if (playerGang.getEnemies().size() < playerGang.getMaxEnemies())
                        {
                            Gang otherGang = getGangByNameFunction.apply(otherGangName);
                            if (!playerGang.equals(otherGang))
                            {
                                int enemyGangID = otherGang.getGangId();
                                if (playerGang.isGangEnemy(otherGangName))
                                {
                                    playerGang.removeEnemyGang(enemyGangID);
                                    sendTeamMessage.accept(playerGang, this.mp.noLongerAllies.replace("{name}", otherGangName));
                                    return;
                                }
                                playerGang.addEnemyGang(enemyGangID, otherGangName);
                                sendTeamMessage.accept(playerGang, this.mp.enemySuccessful.replace("{name}", otherGangName));
                            } else player.sendMessage(this.mp.cantEnemyOwnGang);
                        } else player.sendMessage(this.mp.playerGangMaxEnemies);
                    } else player.sendMessage(this.mp.gangDoesNotExists.replace("{name}", String.valueOf(otherGangName.charAt(0)).toUpperCase() + otherGangName.substring(1)));
                } else player.sendMessage(this.mp.notHighRankEnough);
            } else player.sendMessage(this.mp.notInGang);
        }
    }

    /**
     * Make sure to check if player is in gang before invoking method
     * @param gang gang instance of player can be optained from map
     * @param p player object
     * @param playerInvited the name of the invited player gets added to List<String> in the gang object
     */
    public void requestPlayerToJoinGang(Gang gang, Player p, String playerInvited) {
        if (!p.getDisplayName().equalsIgnoreCase(playerInvited)) {
            //TODO: check if getofflineplayer() == null might not work and has to use this instead
            if (Bukkit.getOfflinePlayer(playerInvited).hasPlayedBefore()) {
                if (isRankMinimumPredicate.test(p.getUniqueId(), gang.gangPermissions.accessToInvite)) {
                    UUID playerInvitedUUID = Bukkit.getOfflinePlayer(playerInvited).getUniqueId();
                    if (getGangByUuidFunction.apply(playerInvitedUUID) == null || !getGangByUuidFunction.apply(playerInvitedUUID).equals(gang)) {
                        if (!gang.hasReachedMaxMembers()) {
                            if (gang.isPlayerInvited(playerInvited)) {
                                //Remove invitation
                                gang.removeMemberInvitation(playerInvited);
                                p.sendMessage(this.mp.playerWasUninvited.replace("{args}", playerInvited));
                            } else {
                                //Invite player to gang
                                if (Bukkit.getPlayer(playerInvitedUUID) != null)
                                    Bukkit.getPlayer(playerInvitedUUID).sendMessage(this.mp.invitedToGang.replace("{name}", gang.getGangName()).replace("{player}", p.getName()));
                                gang.addMemberInvitation(playerInvited);
                                p.sendMessage(this.mp.playerWasInvited.replace("{args}", playerInvited));
                            }
                        } else p.sendMessage(this.mp.gangNotSpaceEnough);
                    } else p.sendMessage(this.mp.sameGang);
                } else p.sendMessage(this.mp.notHighRankEnough);
            } else p.sendMessage(this.mp.hasNeverPlayed);
        } else p.sendMessage(this.mp.cantInviteYourself);
    }

    /**
     * Make sure to check if player is in gang before invoking method
     * @param gang gang instance
     * @param otherGang name of other gang
     * @param requester instance of the player who request for ally
     */
    public void requestAlly(Gang gang, Gang otherGang, Player requester) {
        String gangName = gang.getGangName().toLowerCase();
        String otherGangName = otherGang.getGangName().toLowerCase();
        if (isRankMinimumPredicate.test(requester.getUniqueId(), gang.gangPermissions.accessToAlly)) {
            if (!gang.isGangAlly(otherGang)) {
                if (gang.hasReachedMaxAllies()) {
                    if (!gang.equals(otherGang)) {
                        if (gang.hasRequestedAlly(otherGangName)) {
                            gang.removeAllyRequest(otherGangName);
                            sendTeamMessage.accept(gang, this.mp.regretToBeAlly.replace("{name}", otherGang.getGangName()));
                        } else {
                            /**
                             * Our gang has not requested for ally before,
                             * therefore we need to check if other gang is requesting ally too,
                             * if they are, we need to ally them.
                             */
                            if (otherGang.hasRequestedAlly(gang.getGangName())) {
                                gang.removeAllyRequest(otherGangName);
                                otherGang.removeAllyRequest(gangName);

                                gang.addGangAlly(otherGang);
                                otherGang.addGangAlly(gang);

                                sendTeamMessage.accept(gang, this.mp.allySuccessful.replace("{name}", otherGang.getGangName()));
                                sendTeamMessage.accept(otherGang, this.mp.allySuccessful.replace("{name}", gang.getGangName()));
                            } else {
                                gang.addAllyRequest(otherGang);
                                requester.sendMessage(this.mp.askAlly.replace("{name}", otherGang.getGangName()));
                                sendTeamMessage.accept(otherGang, this.mp.wishesToBeAlly.replace("{name}", gang.getGangName()));
                            }
                        }
                    } else requester.sendMessage(this.mp.cantAllyOwnGang);
                } else requester.sendMessage(this.mp.playerGangMaxAllies);
            } else requester.sendMessage(this.mp.alreadyAllies);
        } else requester.sendMessage(this.mp.notHighRankEnough);
    }

    public void joinGang(UUID uuid, String displayName, String gangName) {
        Gang gang = getGangByNameFunction.apply(gangName);
        gang.addMember(uuid, lowerCaseFunc.apply(displayName), Rank.MEMBER);
        userGangMap.put(uuid, new GangInfo(gang.getGangId(), gangName));
    }

    /**
     *
     * @param player the player who's going to be the leader of the gang
     * @param gangName the gang name which would be added to the TreeMap above
     */
    public void createNewGang(Player player, String gangName) {
        UUID playerUUID = player.getUniqueId();
        if (!(playerInGangPredicate.test(playerUUID))) {
            if (!(gangExistsPredicate.test(gangName))) {
                double playerBalance = this.plugin.getEconomy().getBalance(Bukkit.getOfflinePlayer(playerUUID));
                if (playerBalance >= GANG_COST) {
                    if (doesGangNameFollowRequirements(gangName)) {
                        gangName = gangName.replace(" ", "");
                        addNewGangToMapsBiConsumer.accept(playerUUID, gangName);

                        player.sendMessage(this.mp.gangCreated.replace("{name}", gangName));
                        sendGlobalGangCreatedMessage(player, gangName);
                        plugin.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(playerUUID), GANG_COST);

                        if (this.plugin.getConfiguration().useDiscord && this.plugin.getJDA() != null){
                            this.plugin.getJDA()
                                    .getTextChannelById(this.plugin.getConfiguration().gangAnnouncementsChannelID)
                                    .sendMessage(new EmbedBuilder()
                                            .setTitle("NY BANDE OPRETTET!")
                                            .setColor(Color.GREEN)
                                            .setDescription(player.getName()+" har oprettet banden '"+gangName+"'")
                                            .addField("Bande navn", gangName, true)
                                            .addField("Oprettet af", player.getName(), true)
                                            .addField("Datoen", String.valueOf(LocalDateTime.now()), false)
                                            .setImage("https://www.icegif.com/wp-content/uploads/congratulations-icegif.gif")
                                            .build())
                                    .queue();
                        }
                    } else player.sendMessage(this.mp.gangNameDoesNotMeetRequirements);
                } else {
                    player.sendMessage(this.mp.cantAffordGang.replace("{0}", String.valueOf(GANG_COST)));
                    player.sendMessage(this.mp.progessBar.replace("{bar}", ProgessBar.buildProgressBar((int) playerBalance, GANG_COST)+""));
                }
            } else player.sendMessage(this.mp.gangExists.replace("{name}", gangName));
        } else player.sendMessage(this.mp.alreadyInGang);
    }

    public boolean doesGangNameFollowRequirements(String gangName) {
        return gangName.length() <= this.plugin.getConfiguration().maxNameLength
                && gangName.length() >= this.plugin.getConfiguration().minNameLength
                && !bannedWords.stream().anyMatch(gangName::contains);
    }

    public void sendGlobalGangCreatedMessage(Player p, String gangName) {
        Bukkit.getOnlinePlayers()
                .forEach(_localPlayer ->
                {
                    //Check if player name is same as the guy who created, dont wanna send 2 messages.
                    if (!(_localPlayer.getName().equalsIgnoreCase(p.getName())))
                        _localPlayer.sendMessage(this.mp.successfullyCreatedGangGlobal.replace("{player}", p.getName()).replace("{name}", gangName));
                });

        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l-" + GANG_COST + "&f$"));

    }
}
