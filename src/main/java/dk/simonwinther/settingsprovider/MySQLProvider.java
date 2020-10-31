package dk.simonwinther.settingsprovider;

public class MySQLProvider
{
    /* Constructor */
    public MySQLProvider(){

    }

    /* Properties */
    private String host = "localhost", database="bande", username="root", password="admin";
    private int port;

    /* Getters and Setters */

    public String getHost()
    {
        return host;
    }

    public String getDatabase()
    {
        return database;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public int getPort()
    {
        return port;
    }
}
