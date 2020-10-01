package dk.simonwinther.files;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public interface FileInterface //interfaces are completely abstract
{


    File getFile();
    YamlConfiguration getYamlConfiguration();

    void save();
    void create();
    void load();
    void set(String path, Object object);
    String get(String path);
}
