package dk.simonwinther.files;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.exceptions.ReadValueException;
import dk.simonwinther.utility.UrlUtil;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;

public class SettingsConfig implements FileInterface{

    private final File f;
    private final MainPlugin plugin;
    private static final String configJsonURL = "https://raw.githubusercontent.com/simonsejse/gang-files/main/gang-config.json";

    public SettingsConfig(MainPlugin plugin){
        this.f = new File(plugin.getDataFolder(), "config.yml");
        this.plugin = plugin;
    }

    @Override
    public File getFile() {
        return this.f;
    }

    @Override
    public void initFile() {
        if(!f.exists()){
            if (!f.getParentFile().exists()) f.getParentFile().mkdirs();
            try{
                f.createNewFile();
                try(FileOutputStream fileOutputStream = new FileOutputStream(this.f)){
                    String jsonData = UrlUtil.readValueFromUrl(SettingsConfig.configJsonURL);
                    if (jsonData == null) throw new ReadValueException("Kunne ikke hente data from URL\n§cJsonData er null, og vil kaste en NullPointerException!");

                    fileOutputStream.write(jsonData.getBytes());
                }catch(IOException e){
                    Bukkit.getLogger().log(Level.SEVERE, "Kunne ikke skrive JSON data til config.json fil! ");
                    this.plugin.getPluginLoader().disablePlugin(this.plugin);
                }catch(ReadValueException readValueException){
                    Bukkit.getLogger().log(Level.SEVERE, readValueException.getMessage());
                    this.plugin.getPluginLoader().disablePlugin(this.plugin);
                }
            }catch(IOException e){
                Bukkit.getLogger().log(Level.SEVERE, "Kunne ikke oprette config.json fil!");
                this.plugin.getPluginLoader().disablePlugin(this.plugin);
            }
        }
    }
}
