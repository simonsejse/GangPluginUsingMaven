package dk.simonwinther.utility;

import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;

public class UrlUtil {


    /**
     *
     * @param url The url for the wesite we want to read value from
     * @return returns the HTML code on the site
     * @throws IOException in case it couldn't read from the website it will return null,
     * *> therefore very important to check if String returned is null.
     * *> if (newValueString == null) throw someException() and then have inside a catch statement
     */
    public final static String readValueFromUrl(String url) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
            for(int i = bufferedReader.read(); i != -1; i = bufferedReader.read()){
                stringBuilder.append((char) i);
            }
        }catch(IOException e){
            Bukkit.getLogger().log(Level.SEVERE, "§cKunne ikke hente data fra URL!");
            return null;
        }
        return stringBuilder.toString().isEmpty() ? null : stringBuilder.toString();
    }

}
