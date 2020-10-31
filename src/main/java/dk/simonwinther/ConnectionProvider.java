package dk.simonwinther;

import dk.simonwinther.settingsprovider.CustomSettingsProvider;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionProvider
{

    /* Properties */
    private CustomSettingsProvider customSettingsProvider;
    private Connection connection;

    public ConnectionProvider(CustomSettingsProvider customSettingsProvider){
        this.customSettingsProvider = customSettingsProvider;
    }


    public void openConnection(){
        synchronized(this){
            try
            {
                Class.forName("com.mysql.jdbc.Driver");
                this.connection = DriverManager.getConnection("jdbc:mysql://" + customSettingsProvider.getMySQLProvider().getHost()+ ":" + customSettingsProvider.getMySQLProvider().getPort() + "/" + customSettingsProvider.getMySQLProvider().getDatabase(), customSettingsProvider.getMySQLProvider().getUsername(), customSettingsProvider.getMySQLProvider().getPassword());
            } catch (ClassNotFoundException | SQLException e)
            {
                Logger.getLogger(ConnectionProvider.class.getName()).log(Level.SEVERE, "ConnectionProvider couldn't establish connection check your config.json");
            }
        }
    }

    public Connection getConnection(){
        return connection;
    }

}
