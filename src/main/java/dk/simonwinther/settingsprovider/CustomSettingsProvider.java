package dk.simonwinther.settingsprovider;

public class CustomSettingsProvider
{

    /**
     * All data is read from JSON Object Mapper in MainPlugin.java
     */
    public String accessToToiletsPermission, accessToFarmPermission, accessToLaboratoryPermission;
    public int maxNameLength, minNameLength;
    public NPCSettingsProvider npcSettingsProvider;

}
