package dk.simonwinther.settingsprovider;

public class CustomSettingsProvider
{

    /* Constructor */
    public CustomSettingsProvider(){
        npcSettingsProvider = new NPCSettingsProvider();
    }

    /* Properties */
    private int maxNameLength = 12;
    private int minNameLength = 4;
    private NPCSettingsProvider npcSettingsProvider;

    /* Getters */
    public int getMaxNameLength()
    {
        return this.maxNameLength;
    }

    public int getMinNameLength()
    {
        return this.minNameLength;
    }

    public NPCSettingsProvider getNpcProvider()
    {
        return this.npcSettingsProvider;
    }

}
