package dk.simonwinther.exceptions;

public class PlayerNotFoundException extends Exception
{

    public PlayerNotFoundException(String exceptionCause){
        super(exceptionCause);
    }

}
