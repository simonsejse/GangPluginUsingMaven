package dk.simonwinther.settingsprovider;

public class CustomSettingsProvider
{

    /* Constructor */
    public CustomSettingsProvider(){
        this.npcSettingsProvider = new NPCSettingsProvider();
        this.mySQLProvider = new MySQLProvider();
    }

    /* Properties - cannot be final since we won't be able to change in config.json */
    private int maxNameLength = 12;
    private int minNameLength = 4;
    private NPCSettingsProvider npcSettingsProvider;
    private MySQLProvider mySQLProvider;

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

    public MySQLProvider getMySQLProvider()
    {
        return mySQLProvider;
    }
}
