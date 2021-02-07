package dk.simonwinther.utility;

public class MessageProvider {
    //TODO: Check if I purposely left out "ONE"!
    public static String[] numbers = {"NULL", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN", "ELEVEN", "TWELVE", "THIRTEEN"};

    /**
     *
     * All messages in here are READ using Jackson Object Mapper
     * URL for json data:
     * https://github.com/simonsejse/gang-messages.json/blob/main/README.md
     *
     */
    public String prefix;
    public String missingArguments;
    public String alreadyInGang;
    public String gangExists;
    public String gangDoesNotExists;
    public String gangCreated;
    public String successfullyCreatedGangGlobal;
    public String notInGang;
    public String successfullyLeftGang;
    public String memberLeftGang;
    public String notHighRankEnough;
    public String playerNotInGang;
    public String notInSameGang;
    public String memberKicked;
    public String playerWasKicked;
    public String playerKickHimself;
    public String playerWasUninvited;
    public String playerWasInvited;
    public String sameGang;
    public String cantInviteYourself;
    public String notInvitedToGang;
    public String invitedToGang;
    public String hasNeverPlayed;
    public String gangNotSpaceEnough;
    public String successfullyJoinedGang;
    public String successfullyJoinedGangGlobal;
    public String newLeader;
    public String cantRankYourself;
    public String allySuccessful;
    public String enemySuccessful;
    public String unEnemy;
    public String cantUnEnemy;
    public String cantAllyOwnGang;
    public String cantEnemyOwnGang;
    public String playerGangMaxAllies;
    public String otherGangMaxAllys;
    public String playerGangMaxEnemies;
    public String alreadyAllies;
    public String alreadyEnemies;
    public String wishesToBeAlly;
    public String regretToBeAlly; //TODO: wtf ?? check where this is supposted to be at
    public String passOwnership;
    public String cantAffordGang;
    public String askAlly;
    public String unAlly;
    public String kickHigherRank;
    public String notEnoughMoney;
    public String whoToInviteChat;
    public String whoToAllyChat;
    public String whoToEnemyChat;
    public String createGangChat;
    public String denyAlly;
    public String allyDenied;
    public String noLongerAllies;
    public String stillMissingRequirements;
    public String levelUpSuccess;
    public String insertBank;
    public String toggleDamage;
    public String gangNameDoesNotMeetRequirements;
    public String noSpace;

}