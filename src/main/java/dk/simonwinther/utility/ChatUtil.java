package dk.simonwinther.utility;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.files.FileInterface;
import dk.simonwinther.files.MessageFile;
import dk.simonwinther.settingsprovider.CustomSettingsProvider;
import org.bukkit.ChatColor;

public class ChatUtil
{
    //TODO: Check if I purposely left out "ONE"!
    public static String[] numbers = {"NULL", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN", "ELEVEN", "TWELVE", "THIRTEEN"};

    private  String PREFIX;

    private String MISSING_ARGUMENTS;

    private String ALREADY_IN_GANG;

    private String GANG_EXISTS;

    public final String GANG_DOES_NOT_EXISTS;

    public final String GANG_CREATED;

    public final String SUCCESSFULLY_CREATED_GANG_GLOBAL;

    public final String NOT_IN_GANG;

    public final String SUCCESSFULLY_LEFT_GANG;

    public final String MEMBER_LEFT_GANG;

    public final String NOT_HIGH_RANK_ENOUGH;

    public final String PLAYER_NOT_IN_GANG;

    public final String NOT_IN_SAME_GANG;

    public final String MEMBER_KICKED;

    public final String PLAYER_WAS_KICKED;

    public final String PLAYER_KICK_HIMSELF;

    public final String PLAYER_WAS_UNINVITED;

    public final String PLAYER_WAS_INVITED;

    public final String SAME_GANG;

    public final String CANT_INVITE_YOURSELF;

    public final String NOT_INVITED_TO_GANG;

    public final String INVITED_TO_GANG;

    public final String HAS_NEVER_PLAYED;

    public final String GANG_NOT_SPACE_ENOUGH;

    public final String SUCCESSFULLY_JOINED_GANG;

    public final String SUCCESSFULLY_JOINED_GANG_GLOBAL;

    public final String NEW_LEADER;

    public final String CANT_RANK_YOURSELF;

    public final String ALLY_SUCCESSFUL;

    public final String ENEMY_SUCCESSFUL;

    public final String UN_ENEMY;

    public final String CANT_UN_ENEMY;

    public final String CANT_ALLY_OWN_GANG="&cDu kan ikke alliere din egen bande.";

    public final String CANT_ENEMY_OWN_GANG = "&cDu kan ikke have din egen bande som rival.";

    public final String PLAYER_GANG_MAX_ALLYS = "&cBanden har nået sit maksimale antal af allierede. Opgrader vha. /bande menuen eller /bande shop.";

    public final String OTHER_GANG_MAX_ALLYS = "&cBanden du forsøger at alliere har nået sit maksimale antal af allierede.";

    public final String PLAYER_GANG_MAX_ENEMIES = "&cDin bande har nået sit maksimale antal af rivaler, opgrader vha. /bande menuen eller /bande shop.";

    public final String ALREADY_ALLYS = "&cDu er allerede allieret med denne bande.";

    public final String ALREADY_ENEMIES = "&cDu er allerede rival med denne bande, brug bande menuen til at ophæve dette, ellers brug /gang neutral - /gang ally!";

    public final String WISHES_TO_BE_ALLY = "&2{name}&a vil gerne være jeres allierede.";

    public final String REGRET_TO_BE_ALLY = "&4{name}&c ønsker ikke længere at være allierede med jer.";

    public final String PASS_OWNERSHIP = "&cDu kan ikke forlade banden før alle er ude eller du har givet en anden ejerskab af banden.";

    public final String CANT_AFFORD_GANG = "&cDu har ikke råd til at oprette en bande! Det koster: &f&l{0}$";

    public final String ASK_ALLY = "&aDu har spurgt &2{name} &aom alliance!";

    public final String UN_ALLY = "&cDu ønsker ikke længere alliance med &4{name}!";

    public final String KICK_HIGHER_RANK = "&cDu kan ikke smide nogen ud som er i højere rang end dig.";

    public final String NOT_ENOUGH_MONEY = "&cDu har ikke råd til dette.";

    public final String WHO_TO_INVITE_CHAT = "&cSkriv i chatten hvem du vil invitere til banden.";

    public final String WHO_TO_ALLY_CHAT = "&cSkriv i chatten hvilken bande du vil alliere.";

    public final String WHO_TO_ENEMY_CHAT = "&cSkriv i chatten hvem du ønsker at blive rival med.";

    public final String CREATE_GANG_CHAT = "&cSkriv navnet på banden du ønsker at oprette.\nSkriv !stop for at annullere.";

    public final String DENY_ALLY = "&aDu ønsker ikke længere at være allierede med &2{name}!";

    public final String ALLY_DENIED = "&cBanden &4{name}&c afslog jeres invitation til at være allierede.";

    public final String NO_LONGER_ALLYS = "&7Din bande &f{name}&7 er ikke længere jeres allierede!";

    public final String STILL_MISSING_REQUIREMENTS = "&cDin bande mangler stadig nogle krav til Ali Mustafa!";

    public final String LEVELUP_SUCCESS = "&a&lTILLYKKE! &7Din bande er nu i level: {level}";

    public final String INSERT_BANK = "&7Du har indsat &2&l{0}$&7 i din bande bank";

    public final String TOGGLE_DAMAGE = "&7Du har skiftet bande skade: {value}";

    public int MAX_GANG_NAME_LENGTH = CustomSettingsProvider.MaxNameLength, MIN_GANG_NAME_LENGTH=CustomSettingsProvider.MinNameLength;

    public final String MAX_MIN_GANG_NAME_LENGTH_REACHED = "&7Bandenavnet mangler nogle krav.";

    public final String CONTAINS_BAD_WORDS = "&7Dit bandenavn indeholder noget ulovligt!";

    public final String NO_SPACE = "Intet mellemrum i bandenavnet!";


    public String color(String s)
    {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}