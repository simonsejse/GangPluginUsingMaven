package dk.simonwinther;

import dk.simonwinther.settingsprovider.CustomSettingsProvider;
import dk.simonwinther.settingsprovider.MySQLProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionProvider
{

    /* Properties */
    private MySQLProfile mySQLProfile;
    private Connection connection;

    public ConnectionProvider(MySQLProfile mySQLProfile){
        this.mySQLProfile = mySQLProfile;
    }



    public void openConnection(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://"+this.mySQLProfile.host+":"+this.mySQLProfile.port+"/"+this.mySQLProfile.database, this.mySQLProfile.username, this.mySQLProfile.password);
            Bukkit.getLogger().log(Level.SEVERE, "Connection was established!");
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
            Bukkit.getLogger().log(Level.SEVERE, "Connection was not established!");
        }finally{
            try{
                if (connection != null){
                    connection.close();
                }
            }catch(SQLException e){
                Bukkit.getLogger().log(Level.SEVERE, "Connection was not closed!");
            }
        }

    }

    public Connection getConnection(){
        return connection;
    }

}
