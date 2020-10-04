package dk.simonwinther.exceptions;

public class ConfigFileNotFoundException extends Exception
{
    public ConfigFileNotFoundException(String message){
        super(message);
    }
}
