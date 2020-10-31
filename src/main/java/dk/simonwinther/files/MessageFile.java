package dk.simonwinther.files;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.utility.ChatUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MessageFile implements FileInterface
{

    private MainPlugin plugin;
    private File f;
    private YamlConfiguration yamlConfiguration;
    private ChatUtil chatUtil;

    public MessageFile(ChatUtil chatUtil, MainPlugin plugin, String PATH){
        this.plugin = plugin;
        f = new File(plugin.getDataFolder(), PATH);
        this.chatUtil = chatUtil;
        load();
    }

    @Override
    public File getFile()
    {
        return f;
    }

    @Override
    public YamlConfiguration getYamlConfiguration()
    {
        return YamlConfiguration.loadConfiguration(getFile());
    }

    @Override
    public void save()
    {
        try{
            yamlConfiguration.save(getFile());
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void create()
    {
        if(!f.exists()){
            f.getParentFile().mkdirs();
            try{
                f.createNewFile();
            }catch(IOException e){e.printStackTrace();}
            this.chatUtil.setup(this);
            save();
        }
    }

    @Override
    public void load()
    {
        yamlConfiguration = YamlConfiguration.loadConfiguration(getFile());
    }

    @Override
    public void set(String path, Object object)
    {
        yamlConfiguration.set(path, object);
        save();
    }

    @Override
    public String get(String path)
    {
        return String.valueOf(getYamlConfiguration().get(path));
    }
}
