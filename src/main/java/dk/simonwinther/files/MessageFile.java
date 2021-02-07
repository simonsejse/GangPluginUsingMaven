package dk.simonwinther.files;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.exceptions.ReadValueException;
import dk.simonwinther.utility.UrlUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.logging.Level;

public class MessageFile implements FileInterface
{

    private static final String MESSAGES_URL = "https://raw.githubusercontent.com/simonsejse/gang-messages.json/main/README.md";

    private File f;
    private MainPlugin plugin;

    public MessageFile(MainPlugin plugin, String PATH){
        f = new File(plugin.getDataFolder(), PATH);
        this.plugin = plugin;
    }

    @Override
    public File getFile()
    {
        return f;
    }

    @Override
    public void initFile()
    {
        if (!f.getParentFile().exists()) f.getParentFile().mkdirs();
        if (!f.exists()){
            FileOutputStream fileOutputStream = null;
            try {
                f.createNewFile();
                fileOutputStream = new FileOutputStream(this.f);
                String jsonData = UrlUtil.readValueFromUrl(MESSAGES_URL);
                if (jsonData == null) throw new ReadValueException("Kunne ikke hente data from URL\n§cJsonData er null, og vil kaste en NullPointerException!");

                fileOutputStream.write(jsonData.getBytes());

            } catch (IOException exception) {
                Bukkit.getLogger().log(Level.SEVERE, "§cKunne ikke oprette/finde message.json filen");
                this.plugin.getServer().getPluginManager().disablePlugin(plugin);
            }catch(ReadValueException readValueException){
                Bukkit.getLogger().log(Level.SEVERE, readValueException.getMessage());
                this.plugin.getServer().getPluginManager().disablePlugin(plugin);
            }finally{
                try {
                    fileOutputStream.close();
                } catch (IOException exception) {
                    Bukkit.getLogger().log(Level.SEVERE, "§cOutputStreamen blev ikke lukket ordentligt!");
                    this.plugin.getServer().getPluginManager().disablePlugin(plugin);
                }
            }
        }
    }

}
