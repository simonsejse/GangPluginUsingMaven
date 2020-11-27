package dk.simonwinther.files;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.utility.ChatUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MessageFile implements FileInterface
{

    private File f;
    private YamlConfiguration yamlConfiguration;

    public MessageFile(MainPlugin plugin, String PATH){
        f = new File(plugin.getDataFolder(), PATH);
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
            ChatUtil.setup(this);
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
