package dk.simonwinther.settingsprovider;

public class CustomSettingsProvider
{

    /* Constructor */
    public CustomSettingsProvider(){
        this.NpcSettingsProvider = new NPCSettingsProvider();
    }

    /* Properties - cannot be final since we won't be able to change in config.json */
    private final String AccessToToiletsPermission = "adgang.toilets";
    private final String AccessToFarmPermission = "adgang.farm";
    private final String AccessToLaboratoryPermission = "adgang.laboratory";

    public static final int MaxNameLength = 12;
    public static final int MinNameLength = 4;
    private final NPCSettingsProvider NpcSettingsProvider;


    /* Getters */
    public int getMaxNameLength()
    {
        return this.MaxNameLength;
    }

    public int getMinNameLength()
    {
        return this.MinNameLength;
    }

    public NPCSettingsProvider getNpcProvider()
    {
        return this.NpcSettingsProvider;
    }

}
