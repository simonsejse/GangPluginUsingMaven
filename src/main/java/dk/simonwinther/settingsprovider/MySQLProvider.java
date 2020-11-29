package dk.simonwinther.settingsprovider;

public class MySQLProvider
{
    /* Constructor */
    public MySQLProvider(){
        this.sqlQueries = new SQLQueriesProvider();
    }

    /* Properties */
    private int port = 3306;
    private String host = "mysql58.unoeuro.com", database="simon_winther_dk_db", username="simon_winther_dk", password="ay6bzcxk";
    private final SQLQueriesProvider sqlQueries; //Final so it won't be shown in JSON file
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
