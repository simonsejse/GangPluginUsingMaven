package dk.simonwinther.settingsprovider;

public class MySQLProvider
{
    /* Constructor */
    public MySQLProvider(){ }

    /* Properties */
    private int port = 3306;
    private String host = "localhost", database="gangdata", username="root", password="";
    //private final SQLQueriesProvider sqlQueries; //Final so it won't be shown in JSON file
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
