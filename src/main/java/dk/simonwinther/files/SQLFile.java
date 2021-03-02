package dk.simonwinther.files;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.exceptions.ReadValueException;
import dk.simonwinther.utility.UrlUtil;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;

public class SQLFile implements FileInterface {

    private static final String SQL_QUERY_URL = "https://github.com/simonsejse/gang-files/blob/main/gang-tables-sql.json";
    private final File file;
    private final MainPlugin plugin;

    public SQLFile(MainPlugin plugin){
        this.file = new File(plugin.getDataFolder(), "SQLFile.sql");
        this.plugin = plugin;
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public void initFile() {
        if (!this.file.exists() || this.file.length() <= 1){
            if (!this.file.getParentFile().exists()) this.file.getParentFile().mkdirs();

            try(FileOutputStream fileIO = new FileOutputStream(this.file)){
                if (!this.file.exists()) this.file.createNewFile();
                String jsonData = UrlUtil.readValueFromUrl(SQL_QUERY_URL);
                if (jsonData == null) throw new ReadValueException("Kunne ikke hente data from URL\n§cJsonData er null, og vil kaste en NullPointerException!");

                fileIO.write(jsonData.getBytes());
            } catch (IOException io) {
                Bukkit.getLogger().log(Level.SEVERE, "Kunne ikke oprette/skrive SQLFile.sql filen!");
                Bukkit.getLogger().log(Level.INFO, io.getMessage());
            }catch(ReadValueException readValueException){
                Bukkit.getLogger().log(Level.SEVERE, readValueException.getMessage());

            }
        }

    }
}
