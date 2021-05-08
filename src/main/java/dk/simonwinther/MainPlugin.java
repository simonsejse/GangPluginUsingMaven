package dk.simonwinther;


import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dk.simonwinther.commandmanaging.GangCommand;
import dk.simonwinther.events.EventHandling;
import dk.simonwinther.inventorymanaging.AbstractMenu;
import dk.simonwinther.manager.GangManaging;
import dk.simonwinther.settingsprovider.Configuration;
import dk.simonwinther.utility.MessageProvider;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class MainPlugin extends JavaPlugin
{
    private ConnectionProvider connectionProvider;

    private Configuration configuration;

    private EventHandling eventHandling;
    private GangManaging gangManaging;

    private static Permission perms = null;
    private Chat chat = null;
    private Economy econ = null;

    /**
     * @Throws NullPointerException
     * Reason: if useDiscord is false in config.yml
     */
    private JDA jda;

    private final Consumer<String> log = (string) -> Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + string);

    @Override
    public void onEnable()
    {
        this.saveDefaultConfig();
        initializeConfiguration();
        connectToDiscord();
        establishDatabaseConnection();

        this.gangManaging = new GangManaging(this);

        getCommand("bande").setExecutor(new GangCommand(this.gangManaging, this));

        this.eventHandling = new EventHandling(gangManaging, this);

        if (!setupEconomy())
        {
            log.accept(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }


        startSchedulers();
        registerEvents(this.eventHandling);
        setupPermissions();
        setupChat();

        //loadData();

    }


    private void saveData(){

    }

    private void connectToDiscord(){
        if (!this.configuration.useDiscord) return;
        try {
            this.jda = JDABuilder.createDefault(this.configuration.discordToken).build();
        } catch (LoginException e) {
            log.accept("Din token i din config.yml er forkert!");
        }
    }


    private void initializeConfiguration() {
        Yaml yaml = new Yaml(new CustomClassLoaderConstructor(Configuration.class.getClassLoader()));
        try(InputStream in = Files.newInputStream(Paths.get(getDataFolder().getPath()+"/config.yml"))){
            this.configuration = yaml.loadAs(in, Configuration.class);
        }catch(IOException e){
            log.accept("Kunne ikke læse config.yml data!");
            this.getPluginLoader().disablePlugin(this);
        }
    }

    private void establishDatabaseConnection(){
        log.accept("Trying to establish connection to database.");
        this.connectionProvider = new ConnectionProvider(this.configuration.mySQLProfile);
    }


    private void registerEvents(Listener... listeners)
    {
        Arrays.stream(listeners).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
    }


    private void startSchedulers()
    {
        Bukkit.getScheduler().runTaskTimer(this, () ->
        {
            Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(Player::isOp)
                    .forEach(p -> p.sendMessage(ChatColor.translateAlternateColorCodes('&', gangsUpdated.get())));

        }, 6000, 6000);
    }
    private final Supplier<String> gangsUpdated = () -> "&8&l| &b&lBande &8&l| &2&l" + gangManaging.gangMap.keySet().size() + "&a bander er blevet automatisk gemt...!";


    public String getBlockAtPlayerLoc(UUID playerUUID)
    {
        if (Bukkit.getPlayer(playerUUID) == null) return "&cIkke online...";

        StringBuilder stringBuilder = new StringBuilder();
        RegionContainer container = WGBukkit.getPlugin().getRegionContainer();
        Player player = Bukkit.getPlayer(playerUUID);

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

    @Override
    public void onDisable()
    {

        saveData();
        if (this.gangManaging.gangMap.values().size() < 1) return;
        Bukkit.getOnlinePlayers()
                .stream()
                .filter(p -> p.getOpenInventory().getTopInventory().getHolder() instanceof AbstractMenu)
                .forEach(Player::closeInventory);
    }

    /**
     * Getters And Setters
     *
     */
    public Economy getEconomy()
    {
        return econ;
    }

    public GangManaging getGangManaging() {
        return gangManaging;
    }

    public static Permission getPermissions()
    {
        return MainPlugin.perms;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public MessageProvider getMessageProvider()
    {
        return this.configuration.messageProvider;
    }

    public EventHandling getEventHandling()
    {
        return eventHandling;
    }

    public JDA getJDA() {
        return jda;
    }
}
