package dk.simonwinther.utility;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.files.FileInterface;
import dk.simonwinther.files.MessageFile;
import org.bukkit.ChatColor;

public class ChatUtil
{
    //TODO: Check if I purposely left out "ONE"!
    public static String[] numbers = {"NULL", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN", "ELEVEN", "TWELVE", "THIRTEEN"};

    public ChatUtil(MainPlugin plugin)
    {
        FileInterface messageYml = new MessageFile(plugin, "messages.yml");
        plugin.setMessageConfig(messageYml);

        PREFIX = messageYml.get("PREFIX");
        ACCESS_TO_TOILETS = messageYml.get("adgang.toilets");
        ACCESS_TO_FARM = messageYml.get("adgang.farm");
        ACCESS_TO_LABORATORY = messageYml.get("adgang.laboratory");
        MISSING_ARGUMENTS = PREFIX + messageYml.get("missingArguments");
        ALREADY_IN_GANG = PREFIX + messageYml.get("alreadyInGang");
        GANG_EXISTS = PREFIX + messageYml.get("gangIsAlreadyCreated");
        GANG_DOES_NOT_EXISTS = PREFIX + messageYml.get("gangDoesNotExist");
        GANG_CREATED = PREFIX + messageYml.get("gangCreated");
        NOT_IN_GANG = PREFIX + messageYml.get("notInGang");
        KICK_PLAYERS_TO_LEAVE_GANG = PREFIX + messageYml.get("kickPlayersToLeaveGang");
        SUCCESSFULLY_LEFT_GANG = PREFIX + messageYml.get("leftGangPlayer");
        MEMBER_LEFT_GANG = PREFIX + messageYml.get("leftGangMembers");
        NOT_HIGH_RANK_ENOUGH = PREFIX + messageYml.get("notHighRankEnough");
        PLAYER_NOT_IN_GANG = PREFIX + messageYml.get("playerNotInAGang");
        NOT_IN_SAME_GANG = PREFIX + messageYml.get("notInSameGang");
        MEMBER_KICKED = PREFIX + messageYml.get("kickedFromGang");
        PLAYER_WAS_KICKED = PREFIX + messageYml.get("kickPlayer");
        PLAYER_KICK_HIMSELF = PREFIX + messageYml.get("cantKickYourself");
        SUCCESSFULLY_CREATED_GANG_GLOBAL = PREFIX + messageYml.get("gangCreatedGlobal");
        PLAYER_WAS_UNINVITED = PREFIX + messageYml.get("playerUninvited");
        PLAYER_WAS_INVITED = PREFIX + messageYml.get("playerInvited");
        HAS_NEVER_PLAYED = PREFIX + messageYml.get("neverPlayed");
        GANG_NOT_SPACE_ENOUGH = PREFIX + messageYml.get("noMoreMemberSpace");
        SAME_GANG = PREFIX + messageYml.get("sameGangInvite");
        CANT_INVITE_YOURSELF = PREFIX + messageYml.get("cantInviteYourself");
        NOT_INVITED_TO_GANG = PREFIX + messageYml.get("notInvited");
        SUCCESSFULLY_JOINED_GANG = PREFIX + messageYml.get("joinGang");
        SUCCESSFULLY_JOINED_GANG_GLOBAL = PREFIX + messageYml.get("joinGangGlobal");
        INVITED_TO_GANG = PREFIX + messageYml.get("invitedToGang");
        NEW_LEADER = PREFIX + messageYml.get("newLeader");
        CANT_RANK_YOURSELF = PREFIX + messageYml.get("noRankingYourself");
        ALLY_SUCCESSFUL = PREFIX + messageYml.get("allySuccess");
        ENEMY_SUCCESSFUL = PREFIX + messageYml.get("enemySuccess");
        CANT_ALLY_OWN_GANG = PREFIX + messageYml.get("noAllyOwnGang");
        CANT_ENEMY_OWN_GANG = PREFIX + messageYml.get("noEnemyOwnGang");
        PLAYER_GANG_MAX_ALLYS = PREFIX + messageYml.get("maxAllys");
        OTHER_GANG_MAX_ALLYS = PREFIX + messageYml.get("maxAllysOtherGang");
        PLAYER_GANG_MAX_ENEMIES = PREFIX + messageYml.get("maxEnemiesOwnGang");
        OTHER_GANG_MAX_ENEMIES = PREFIX + messageYml.get("maxEnemiesOtherGang");
        ALREADY_ALLYS = PREFIX + messageYml.get("alreadyAllies");
        WISHES_TO_BE_ALLY = PREFIX + messageYml.get("wishesAlly");
        REGRET_TO_BE_ALLY = PREFIX + messageYml.get("regretAlly");
        ALREADY_ENEMIES = PREFIX + messageYml.get("alreadyEnemies");
        PASS_OWNERSHIP = PREFIX + messageYml.get("passOwnership");
        CANT_AFFORD_GANG = PREFIX + messageYml.get("cantAffordGang");
        GANG_FULL = PREFIX + messageYml.get("gangFull");
        ASK_ALLY = PREFIX + messageYml.get("askAlly");
        UN_ALLY = PREFIX + messageYml.get("unAlly");
        UN_ENEMY = PREFIX + messageYml.get("unEnemy");
        CANT_UN_ENEMY = PREFIX + messageYml.get("cantUnEnemy");
        KICK_HIGHER_RANK = PREFIX + messageYml.get("kickHigherRank");
        NOT_ENOUGH_MONEY = PREFIX + messageYml.get("notEnoughMoney");
        WHO_TO_INVITE_CHAT = PREFIX + messageYml.get("writeInChatWhoToInvite");
        WHO_TO_ALLY_CHAT = PREFIX + messageYml.get("writeInChatWhoToAlly");
        WHO_TO_ENEMY_CHAT = PREFIX + messageYml.get("writeInChatWhoToEnemy");
        CREATE_GANG_CHAT = PREFIX + messageYml.get("writeInChatToCreateGang");
        DENY_ALLY = PREFIX + messageYml.get("denyAlly");
        ALLY_DENIED = PREFIX + messageYml.get("allyDenied");
        NO_LONGER_ALLYS = PREFIX + messageYml.get("noLongerAllys");
        STILL_MISSING_REQUIREMENTS = PREFIX + messageYml.get("stillMissingRequirements");
        LEVELUP_SUCCESS = PREFIX + messageYml.get("levelUp");
        INSERT_BANK = PREFIX + messageYml.get("insertBank");
        TOGGLE_DAMAGE = PREFIX + messageYml.get("toggleDamage");
        MAX_GANG_NAME_LENGTH = plugin.getCustomSettingsProvider().getMaxNameLength();
        MIN_GANG_NAME_LENGTH = plugin.getCustomSettingsProvider().getMinNameLength();
        MAX_MIN_GANG_NAME_LENGTH_REACHED = PREFIX + messageYml.get("maxMinNameLengthReached");
        CONTAINS_BAD_WORDS = PREFIX + messageYml.get("containsBadWords");
        NO_SPACE = PREFIX + messageYml.get("noSpace");
    }

    public final String PREFIX;

    public final String ACCESS_TO_TOILETS;

    public final String ACCESS_TO_FARM;

    public final String ACCESS_TO_LABORATORY;

    public final String MISSING_ARGUMENTS;

    public final String ALREADY_IN_GANG;

    public final String GANG_EXISTS;

    public final String GANG_DOES_NOT_EXISTS;

    public final String GANG_CREATED;

    public final String NOT_IN_GANG;

    public final String KICK_PLAYERS_TO_LEAVE_GANG;

    public final String SUCCESSFULLY_LEFT_GANG;

    public final String MEMBER_LEFT_GANG;

    public final String NOT_HIGH_RANK_ENOUGH;

    public final String PLAYER_NOT_IN_GANG;

    public final String NOT_IN_SAME_GANG;

    public final String MEMBER_KICKED;

    public final String PLAYER_WAS_KICKED;

    public final String PLAYER_KICK_HIMSELF;

    public final String SUCCESSFULLY_CREATED_GANG_GLOBAL;

    public final String PLAYER_WAS_UNINVITED;

    public final String PLAYER_WAS_INVITED;

    public final String HAS_NEVER_PLAYED;

    public final String GANG_NOT_SPACE_ENOUGH;

    public final String SAME_GANG;

    public final String CANT_INVITE_YOURSELF;

    public final String NOT_INVITED_TO_GANG;

    public final String SUCCESSFULLY_JOINED_GANG;

    public final String SUCCESSFULLY_JOINED_GANG_GLOBAL;

    public final String INVITED_TO_GANG;

    public final String NEW_LEADER;

    public final String CANT_RANK_YOURSELF;

    public final String ALLY_SUCCESSFUL;

    public final String ENEMY_SUCCESSFUL;

    public final String CANT_ALLY_OWN_GANG;

    public final String CANT_ENEMY_OWN_GANG;

    public final String PLAYER_GANG_MAX_ALLYS;

    public final String OTHER_GANG_MAX_ALLYS;

    public final String PLAYER_GANG_MAX_ENEMIES;

    public final String OTHER_GANG_MAX_ENEMIES;

    public final String ALREADY_ALLYS;

    public final String WISHES_TO_BE_ALLY;

    public final String REGRET_TO_BE_ALLY;

    public final String ALREADY_ENEMIES;

    public final String PASS_OWNERSHIP;

    public final String CANT_AFFORD_GANG;

    public final String GANG_FULL;

    public final String ASK_ALLY;

    public final String UN_ALLY;

    public final String UN_ENEMY;

    public final String CANT_UN_ENEMY;

    public final String KICK_HIGHER_RANK;

    public final String NOT_ENOUGH_MONEY;

    public final String WHO_TO_INVITE_CHAT;

    public final String WHO_TO_ALLY_CHAT;

    public final String WHO_TO_ENEMY_CHAT;

    public final String CREATE_GANG_CHAT;

    public final String DENY_ALLY;

    public final String ALLY_DENIED;

    public final String NO_LONGER_ALLYS;

    public final String STILL_MISSING_REQUIREMENTS;

    public final String LEVELUP_SUCCESS;

    public final String INSERT_BANK;

    public final String TOGGLE_DAMAGE;

    public int MAX_GANG_NAME_LENGTH, MIN_GANG_NAME_LENGTH;

    public final String MAX_MIN_GANG_NAME_LENGTH_REACHED;

    public final String CONTAINS_BAD_WORDS;

    public final String NO_SPACE;

    public static void setup(FileInterface messageYml)
    {
        messageYml.set("PREFIX", "&8&l| &cBande &8&l| ");
        messageYml.set("accessToToilets", "adgang.toilets");
        messageYml.set("accessToFarm", "adgang.farm");
        messageYml.set("accessToLaboratory", "adgang.laboratory");
        messageYml.set("missingArguments", "&cYou're missing arguments!");
        messageYml.set("alreadyInGang", "&bYou're already in a gang!");
        messageYml.set("gangIsAlreadyCreated", "&cThe gang &7{name}&c already exists.");
        messageYml.set("gangDoesNotExist", "&cThe gang &7{name}&c does not exist.");
        messageYml.set("gangCreated", "&aYou've created a gang with the name {name}");
        messageYml.set("gangCreatedGlobal", "&a{player}&7 has created a gang with the name &2{name}.");
        messageYml.set("notInGang", "&cYou're not in a gang.");
        messageYml.set("kickPlayersToLeaveGang", "&cYou're the owner of the gang and can't leave until everyone has left.");
        messageYml.set("leftGangPlayer", "&cYou've left the gang {name}");
        messageYml.set("leftGangMembers", "&9{oldmember}&b has left your gang.");
        messageYml.set("notHighRankEnough", "&cFollowing command is only for people with higher ranks.");
        messageYml.set("playerNotInAGang", "&cThe player is not in a gang.");
        messageYml.set("notInSameGang", "&cThe player is not in the same gang as you.");
        messageYml.set("kickedFromGang", "&cYou've been kicked from the gang &7{name}.");
        messageYml.set("kickPlayer", "&cYou've kicked the player &4{args}&c from the gang.");
        messageYml.set("cantKickYourself", "&cYou cannot kick yourself from a gang. Use /gang leave.");
        messageYml.set("playerUninvited", "&4{args}&c is no longer invited to your gang.");
        messageYml.set("playerInvited", "&2{args}&a has been invited to the gang.");
        messageYml.set("sameGangInvite", "&cYou cannot invite a player who's already in your gang.");
        messageYml.set("cantInviteYourself", "&cYou cannot invite yourself.");
        messageYml.set("notInvited", "&cYou're not invited to this gang.");
        messageYml.set("invitedToGang", "&aYou've been invited to join &2{name}&a by &2{player}");
        messageYml.set("neverPlayed", "&4{args}&c has never played on the server.");
        messageYml.set("noMoreMemberSpace", "&cThe gang has reached it's maximum of players, buy more in /gang shop!");
        messageYml.set("joinGang", "&aYou've joined the gang &a{name}");
        messageYml.set("joinGangGlobal", "&2{spiller}&a has now joined the gang &2{name}");
        messageYml.set("newLeader", "&2{player}&a has stepped off as the leader in the gang &2{name}&a your new leader is &2{newleader}");
        messageYml.set("noRankingYourself", "&cYou cannot change your own rank.");
        messageYml.set("allySuccess", "&2{name}&a is now your ally.");
        messageYml.set("enemySuccess", "&4{name}&c is now your rival.");
        messageYml.set("unEnemy", "&2{name}&7 er ikke længere din rival!");
        messageYml.set("cantUnEnemy", "&cYour gang is &4not&c rival with this gang.");
        messageYml.set("noAllyOwnGang", "&cYou cannot ally your own gang.");
        messageYml.set("noEnemyOwnGang", "&cYou cannot enemy your own gang.");
        messageYml.set("maxAllys", "&cThe gang has reached it's maximum amount of allies, upgrade using /gang shop.");
        messageYml.set("maxAllysOtherGang", "&cThe gang you're trying to ally has reached its maximum amount.");
        messageYml.set("maxEnemiesOwnGang", "&cYou're gang has reached the maximum amount of enemies, upgrade using /gang shop");
        messageYml.set("maxEnemiesOtherGang", "&cThe gang you're trying to enemy has reached it's maximum amount of enemies.");
        messageYml.set("alreadyAllies", "&cYou're already allied with this gang.");
        messageYml.set("alreadyEnemies", "&cYou're already enemies with this gang, use /gang neutral or /gang ally!");
        messageYml.set("wishesAlly", "&2{name}&a wants to be allies with your gang.");
        messageYml.set("regretAlly", "&4{name}&c no longer wishes to be allies.");
        messageYml.set("passOwnership", "&cYou have to give leadership of the gang before you can leave!");
        messageYml.set("cantAffordGang", "&cYou can't afford the creation of a gang! It costs: &f&l{0}$");
        messageYml.set("gangFull", "&cThe gang has to many members!");
        messageYml.set("askAlly", "&aYou've asked &2{name} &afor alliance!");
        messageYml.set("unAlly", "&cYou no longer wishes to be ally with &4{name}!");
        messageYml.set("kickHigherRank", "&cYou cannot kick someone who's in the same or higher rank than you.");
        messageYml.set("notEnoughMoney", "&cYou cannot afford this.");
        messageYml.set("writeInChatWhoToInvite", "&cWrite in chat who you want to invite to the gang.");
        messageYml.set("writeInChatWhoToAlly", "&cWrite in chat which gang you want to ally.");
        messageYml.set("writeInChatWhoToEnemy", "&cWrite in chat which gang you want to enemy.");
        messageYml.set("writeInChatToCreateGang", "&cWrite the name of the gang you wish to create!\nWrite !stop to cancel");
        messageYml.set("denyAlly", "&aYou've cancelled to be ally with &2{name}!");
        messageYml.set("allyDenied", "&cThe gang &4{name}&c has rejected to be your ally.");
        messageYml.set("noLongerAllys", "&7The gang &f{name}&7 is no longer your ally!");
        messageYml.set("stillMissingRequirements",  "&cYou gang still needs to deliver some items to Ali Mustafa!");
        messageYml.set("levelUp", "&a&lCONGRATULATIONS! &7Your gang is now level {level}");
        messageYml.set("insertBank", "&7You've inserted &2&l{0}$&7 in your gang bank");
        messageYml.set("toggleDamage", "&7You've turned gang damage: {value}");
        messageYml.set("maxMinNameLengthReached", "&7The gang name is missing requirements.");
        messageYml.set("containsBadWords", "&7You're gang name contains something illegal!");
        messageYml.set("noSpace", "No spaces in gang!");
    }

    public String color(String s)
    {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}