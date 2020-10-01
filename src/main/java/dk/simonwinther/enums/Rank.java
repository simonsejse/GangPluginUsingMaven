package dk.simonwinther.enums;

public enum Rank
{
    LEADER(4, "Leder","&4"),
    CO_LEADER(3, "Co-Leder","&c"),
    OFFICER(2, "Officer", "&e"),
    MEMBER(1, "Medlem", "&2");

    int value;
    String rankName;
    String color;

    Rank(int value, String rankName, String color)
    {
        this.value = value;
        this.rankName = rankName;
        this.color = color;

    }

    public String getColor(){
        return color;
    }

    public String getRankName(){
        return rankName;
    }

    public int getValue()
    {
        return value;
    }
}
