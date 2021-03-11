package dk.simonwinther;

import dk.simonwinther.settingsprovider.MySQLProfile;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public class ConnectionProvider
{

    /* Properties */
    private MySQLProfile mySQLProfile;
    public Connection connection;

    public ConnectionProvider(MySQLProfile mySQLProfile){
        this.mySQLProfile = mySQLProfile;
    }



    public void openConnection(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://"+this.mySQLProfile.host+":"+this.mySQLProfile.port+"/"+this.mySQLProfile.database, this.mySQLProfile.username, this.mySQLProfile.password);
            Bukkit.getLogger().log(Level.SEVERE, "Connection established!");
        } catch (SQLException | ClassNotFoundException t) {
            Bukkit.getLogger().log(Level.SEVERE, "Connection was not established!");
            t.printStackTrace();
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
