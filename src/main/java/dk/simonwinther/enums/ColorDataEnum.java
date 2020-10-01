package dk.simonwinther.enums;

public enum ColorDataEnum
{
    RED((short)14, (short)14, (short)1),
    GREEN((short)13, (short)13, (short)2),
    LIGHT_BLUE((short)3, (short)3, (short)12),
    LIME((short)5, (short)5, (short)10),
    YELLOW((short)4, (short)4, (short)11),
    MAGENTA((short)2, (short)2, (short)13),
    GRAY((short)7, (short)7, (short)8);


    public final short[] value;


    ColorDataEnum(final short... value){
        this.value = value;
    }
}
