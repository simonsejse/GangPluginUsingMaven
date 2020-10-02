package dk.simonwinther;


import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import dk.simonwinther.commandmanaging.ConfirmTransferCmd;
import dk.simonwinther.commandmanaging.GangCommand;
import dk.simonwinther.events.EventHandling;
import dk.simonwinther.files.ConfigFile;
import dk.simonwinther.files.FileInterface;
import dk.simonwinther.files.MessageFile;
import dk.simonwinther.inventorymanaging.Menu;
import dk.simonwinther.utility.ChatUtil;
import dk.simonwinther.utility.GangManaging;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

public final class MainPlugin extends JavaPlugin
{

    /* Properties */
    private FileInterface messageConfig;
    private ConfigFile configFile;
    private ChatUtil chatUtil;
    private EventHandling eventHandling;
    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;


    @Override
    public void onEnable()
    {
        createFiles();
        System.out.println(getCommand("bande"));

        getCommand("bande").setExecutor(new GangCommand(this));
        getCommand("confirmtransfercmd").setExecutor(new ConfirmTransferCmd(this));


        eventHandling = new EventHandling(this);

        if (!setupEconomy())
        {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        startScheduler();
        registerEvents(eventHandling);
        setupPermissions();
        setupChat();
        loadData();
    }

    @Override
    public void onDisable()
    {
        Bukkit.getOnlinePlayers()
                .stream()
                .filter(p -> p.getOpenInventory().getTopInventory().getHolder() instanceof Menu)
                .forEach(Player::closeInventory);
        saveData();
    }

    private void registerEvents(Listener... listeners)
    {
        for(Listener listener : listeners){
            getServer().getPluginManager().registerEvents(listener, this);
        }

    }


    private void startScheduler()
    {
        Bukkit.getScheduler().runTaskTimer(this, () ->
        {
            saveData();
            Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(Player::isOp)
                    .forEach(p -> p.sendMessage(ChatColor.translateAlternateColorCodes('&', schedulerUpdated.get())));

        }, 6000, 6000);
    }


    private final Supplier<String> schedulerUpdated = () -> "&8&l| &b&lBande &8&l| &2&l" + GangManaging.gangMap.keySet().size() + "&a bander er blevet automatisk gemt...!";


    private void loadData()
    {
        /*
        File f = new File(getDataFolder() + File.separator + "Gangs");
        if (f.listFiles() != null) {
            File[] files = f.listFiles();
            for(File file : files)
            {
                try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file)))
                {
                    Gang gang = (Gang) objectInputStream.readObject();
                    String gangName = gang.getGangName();
                    GangManaging.gangMap.put(gangName, gang);
                } catch (IOException | ClassNotFoundException e)
                {
                    logMessage.accept("ObjectInputStream couldn't be established, or Gang object wasn't written right.");
                }
            }
        }

        File f2 = new File(getDataFolder() + File.separator + "Users");
        if (f2.listFiles() != null){
            File[] files = f2.listFiles();
            for(File file2 : files){
                try(DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file2))){
                    String uuidString = file2.getName().substring(0, file2.getName().length()-4); //Remove .bin
                    UUID uuid = UUID.fromString(uuidString);
                    final String utf = dataInputStream.readUTF();
                    GangManaging.namesOfGang.put(uuid, utf);
                    final boolean value = dataInputStream.readBoolean();
                    GangManaging.damageMap.put(uuid, value);
                }catch(IOException e){
                    logMessage.accept("ObjectInputStream couldn't be established, or Gang object wasn't written right.");
                }
            }
        }
         */
        //TODO: Load from MySQL
    }

    private void saveData()
    {
        /*
        GangManaging.getGangMap().values().forEach(gang ->
        {
            File file = new File(getDataFolder() + File.separator + "Gangs", gang.getGangName() + ".bin");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                try
                {
                    file.createNewFile();
                } catch (IOException e)
                {
                    file.delete();
                    logMessage.accept("File couldn't be created! Trying again!");
                    saveData(); //Recursion
                }
            }
            try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file))){
                objectOutputStream.writeObject(gang);
                objectOutputStream.flush();
            } catch (IOException e)
            {
                file.delete();
                logMessage.accept("Gang data couldn't be saved! Trying again!");
                saveData(); //Recursion
            }

            GangManaging.namesOfGang.forEach((key, value) ->
            {
                File file2 = new File(getDataFolder() + File.separator + "Users", key+".bin");
                if (!file2.getParentFile().exists()){
                    file2.getParentFile().mkdirs();
                    try
                    {
                        file2.getParentFile().createNewFile();
                    } catch (IOException e)
                    {
                        logMessage.accept("File couldn't be created!");
                    }
                }
                if (!GangManaging.damageMap.containsKey(key)) GangManaging.damageMap.put(key, false);

                try(DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file2))){
                    dataOutputStream.writeUTF(value);
                    dataOutputStream.writeBoolean(GangManaging.damageMap.get(key));
                    dataOutputStream.flush();
                }catch(IOException e){
                   logMessage.accept("Couldn't write Object penis!");
                }
            });

        });
         */
        //TODO: Save into MySQL
        // [ ]
    }

    private void createFiles()
    {
        messageConfig = new MessageFile(this, "messages.yml");
        messageConfig.create();
        configFile = new ConfigFile(this, "config.json");
        configFile.create();
        chatUtil = new ChatUtil(this);
    }

    public WorldGuardPlugin getWorldGuard()
    {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");

        if (plugin == null)
        {
            logMessage.accept("Du skal have WorldGuard dependency");
            getServer().getPluginManager().disablePlugin(this);
            return null;
        }
        return (WorldGuardPlugin) plugin;
    }

    public String getBlockAtPlayerLoc(UUID playerUuid)
    {
        if (Bukkit.getPlayer(playerUuid) == null) return "&cIkke online...";
        try
        {
            StringBuilder stringBuilder = new StringBuilder();
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            Player player = Bukkit.getPlayer(playerUuid);
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

            com.sk89q.worldedit.util.Location loc = localPlayer.getLocation();

            RegionQuery query = container.createQuery();
            ApplicableRegionSet applicableRegionSet = query.getApplicableRegions(loc);

            final Optional<String> stringOptional = applicableRegionSet.getRegions()
                    .stream()
                    .map(ProtectedRegion::getId)
                    .map(String::toUpperCase)
                    .findFirst();

            if (stringOptional.isPresent())
            {
                stringBuilder.append(stringOptional.get());
            } else
            {
                stringBuilder.append("Ingen blok..");
            }
            return stringBuilder.toString();
        } catch (NullPointerException e)
        {
            return "Ingen blok..";
        }
    }

    private boolean setupChat()
    {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp == null) return false;
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    private boolean setupEconomy()
    {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
        {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
        {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    /* Getters And Setters */
    public Economy getEconomy()
    {
        return econ;
    }

    public static Permission getPermissions()
    {
        return perms;
    }

    public static Chat getChat()
    {
        return chat;
    }

    private Consumer<String> logMessage = (string) -> Bukkit.getConsoleSender().sendMessage(ChatColor.RED + string);

    public ConfigFile getConfigFile()
    {
        return configFile;
    }

    public FileInterface getMessageConfig()
    {
        return messageConfig;
    }

    public ChatUtil getChatUtil()
    {
        return chatUtil;
    }

    public EventHandling getEventHandling()
    {
        return eventHandling;
    }

    public static Logger getLog()
    {
        return log;
    }

    public static Economy getEcon()
    {
        return econ;
    }

    public static void setEcon(Economy econ)
    {
        MainPlugin.econ = econ;
    }

    public static Permission getPerms()
    {
        return perms;
    }

    public static void setPerms(Permission perms)
    {
        MainPlugin.perms = perms;
    }

    public static void setChat(Chat chat)
    {
        MainPlugin.chat = chat;
    }
}
