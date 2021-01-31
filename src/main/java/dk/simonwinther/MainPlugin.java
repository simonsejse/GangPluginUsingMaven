package dk.simonwinther;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dk.simonwinther.commandmanaging.ConfirmTransferCmd;
import dk.simonwinther.commandmanaging.GangCommand;
import dk.simonwinther.events.EventHandling;
import dk.simonwinther.files.FileInterface;
import dk.simonwinther.files.MessageFile;
import dk.simonwinther.inventorymanaging.Menu;
import dk.simonwinther.settingsprovider.CustomSettingsProvider;
import dk.simonwinther.utility.ChatUtil;
import dk.simonwinther.utility.GangManaging;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MainPlugin extends JavaPlugin
{

    /* Properties */
    private FileInterface messageConfig;
    private CustomSettingsProvider customSettingsProvider;
    private ChatUtil chatUtil;
    private EventHandling eventHandling;
    private static Permission perms = null;
    private Chat chat = null;
    private Economy econ = null;
    private GangManaging gangManaging = null;

    private final static Logger LOGGER = Logger.getLogger("Minecraft");
    public final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    //private ConnectionProvider connectionProvider;
    //private final SQLQueriesProvider sqlQueriesProvider = new SQLQueriesProvider();
    //private final String[] tables = new String[]{"users", "memberInvitations", "gangMembers", "gangAllies", "gangPermissions", "gangs"};


    @Override
    public void onEnable()
    {

        //Setup message.json
        this.messageConfig = new MessageFile(this, "message.json");
        this.messageConfig.initFile();

        try{
            TypeReference<ChatUtil> chatUtilTypeReference = new TypeReference<ChatUtil>(){};
            this.chatUtil = OBJECT_MAPPER.readValue(this.messageConfig.getFile(), chatUtilTypeReference);
        }catch(IOException e){
            Bukkit.getLogger().log(Level.SEVERE, "Du HAR fucket din message.json fil op!\nDisabler pluginnet.");
            this.getServer().getPluginManager().disablePlugin(this);
        }

        this.gangManaging = new GangManaging(this);

        //Setup config.json
        this.customSettingsProvider = new CustomSettingsProvider();

        //After initialising customSettingsProvider
        //this.connectionProvider = new ConnectionProvider(customSettingsProvider);
        //this.connectionProvider.openConnection();


        getCommand("bande").setExecutor(new GangCommand(gangManaging, this));
        getCommand("confirmtransfercmd").setExecutor(new ConfirmTransferCmd(this.gangManaging, this));

        this.eventHandling = new EventHandling(gangManaging, this);

        if (!setupEconomy())
        {
            LOGGER.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }


        startScheduler();
        registerEvents(this.eventHandling);
        setupPermissions();
        setupChat();

        //loadData();

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

    /*
    private void loadData(){
        //Check all tables exists

        try(Statement statement = connectionProvider.getConnection().createStatement()){
            for(int i = 0;i<tables.length;i++){
                try(ResultSet rs = connectionProvider.getConnection().getMetaData().getTables(null, null, tables[i], null)){
                    if (!rs.next()){
                        createTable(tables[i]);
                    }
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
        }catch(SQLException e ){
            e.printStackTrace();
        }
    }
     */

    void createTable(String table){

    }

    private void saveData(){

    }

    private void registerEvents(Listener... listeners)
    {
        Arrays.stream(listeners).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
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


    private final Supplier<String> schedulerUpdated = () -> "&8&l| &b&lBande &8&l| &2&l" + gangManaging.gangMap.keySet().size() + "&a bander er blevet automatisk gemt...!";

    private void createFiles() throws IOException, JsonSyntaxException
    {
        File configFile = new File(getDataFolder(), "config.json");
        if (!configFile.exists()){
            if (!configFile.getParentFile().exists()) configFile.getParentFile().mkdirs();
            configFile.createNewFile();
        }

        StringBuilder json = new StringBuilder();
        //Throws IOException means JSON wasn't read right, therefore in catch create new instance of CustomSettingsProvider using default settings.
        FileReader fileReader = new FileReader(configFile);
        for(int i = fileReader.read(); i != -1; i = fileReader.read()){
            json.append((char) i);
        }
        //Throws JsonSyntaxException means JSON wasn't WRITTEN right, therefore in catch create new instance of CustomSettingsProvider using default settings.
        this.customSettingsProvider = new Gson().fromJson(json.toString(), CustomSettingsProvider.class);
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

        StringBuilder stringBuilder = new StringBuilder();
        RegionContainer container = WGBukkit.getPlugin().getRegionContainer();
        Player player = Bukkit.getPlayer(playerUuid);

        RegionQuery query = container.createQuery();
        ApplicableRegionSet applicableRegionSet = query.getApplicableRegions(player.getLocation());

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
        return MainPlugin.perms;
    }

    private Consumer<String> logMessage = (string) -> Bukkit.getConsoleSender().sendMessage(ChatColor.RED + string);

    public CustomSettingsProvider getCustomSettingsProvider()
    {
        return customSettingsProvider;
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

    public void setMessageConfig(FileInterface messageConfig)
    {
        this.messageConfig = messageConfig;
    }
}
