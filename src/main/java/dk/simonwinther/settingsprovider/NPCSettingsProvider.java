package dk.simonwinther.settingsprovider;

import java.util.Arrays;
import java.util.List;

public class NPCSettingsProvider
{
    /* Constructor */
    public NPCSettingsProvider(){

    }

    /* Properties */
    private final List<String> bannedWords = Arrays.asList("tissemand", "tissemænd", "fisse", "pik", "patter", "luder", "fede", "homo", "bøsse");
    private final List<String> goAwayMessages = Arrays.asList("Gå væk!!!", "Jeg vil ikke have noget!!!", "Føj!!");
    private final List<String> deliveredMessages = Arrays.asList("Tak du gamle!", "Uha, stoffer!!", "Jeg håber for dig det er god kvalitet!", "MERE!! Jeg vil have mere!");
    private final String npcName = "Ali Mustafa";

    /* Getters */
    public List<String> getBannedWords()
    {
        return bannedWords;
    }

    public List<String> getGoAwayMessages()
    {
        return goAwayMessages;
    }

    public List<String> getDeliveredMessages()
    {
        return deliveredMessages;
    }

    public String getNpcName()
    {
        return npcName;
    }
}
