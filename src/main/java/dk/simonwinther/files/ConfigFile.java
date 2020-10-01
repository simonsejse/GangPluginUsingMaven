package dk.simonwinther.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.simonwinther.MainPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConfigFile
{
    private File f;

    public ConfigFile(MainPlugin plugin, String PATH){
        f = new File(plugin.getDataFolder(), PATH);
    }


    public void create()
    {
        if(!f.exists()){
            f.getParentFile().mkdirs();
            try{
                f.createNewFile();
            }catch(IOException e){e.printStackTrace();}
            initDefaultOptions();
        }
    }

    private void initDefaultOptions(){
        Map<String, Object> obj = new HashMap<>();

        obj.put("maxNameLength", 12);
        obj.put("minNameLength", 4);
        obj.put("bannedWords", Arrays.asList("tissemand", "tissemænd", "fisse", "pik", "patter", "luder", "fede", "homo", "bøsse"));

        Map<String, Object> structure = new HashMap<>();
        structure.put("npcName", "ali mustafa");
        structure.put("messages", Arrays.asList("Tak du gamle!", "Uha, stoffer!!", "Jeg vil have mere!!!", "Jeg håber for dig det er god kvalitet!", "Tak for handlen, jeg skylder!"));
        structure.put("goAwayMessages", Arrays.asList("Gå væk!!!", "Jeg vil ikke have noget!!!", "Føj!!"));

        obj.put("npc", structure);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(obj);

        FileOutputStream io = null;
        try
        {
            io = new FileOutputStream(f);
            io.write(json.getBytes());
        } catch (IOException e)
        {
            e.printStackTrace();
        }finally{
            try{
                if (io != null){
                    io.close();
                }
            }catch(IOException e){
                System.err.println(e);
            }
        }
    }




    public String get(String path){
        return retrieve(path).toString();
    }

    public Object retrieve(String path)
    {

        FileInputStream fileInputStream = null;
        try
        {
            fileInputStream = new FileInputStream(f);
            byte[] bytes = new byte[fileInputStream.available()];
            fileInputStream.read(bytes);

            Gson gson = new Gson();

            Map<String, Object> json = gson.fromJson(new String(bytes), HashMap.class);

            return json.get(path);
        } catch (IOException e)
        {
            System.err.println(e);
        } finally
        {
            try
            {
                if (fileInputStream != null)
                {
                    fileInputStream.close();
                }
            } catch (IOException e)
            {
                System.err.println(e);
            }
        }
        return "";
    }
}
