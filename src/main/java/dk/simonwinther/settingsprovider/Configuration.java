package dk.simonwinther.settingsprovider;

import dk.simonwinther.utility.MessageProvider;

import java.util.List;

public class Configuration {

    //TODO: check on region enter 'toilet' if they have this permissions
    public String accessToToiletsPermission, accessToFarmPermission, accessToLaboratoryPermission;

    public int maxNameLength, minNameLength;
    public List<String> bannedWords;

    public boolean useDiscord;
    public String discordToken;

    public int maxMembers, maxAllies, maxEnemies;

    public long gangAnnouncementsChannelID;

    public NPCSettingsProvider npcSettingsProvider;
    public MySQLProfile mySQLProfile;
    public MessageProvider messageProvider;

}
