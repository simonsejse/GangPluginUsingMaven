package dk.simonwinther.events;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.Gang;
import dk.simonwinther.files.ConfigFile;
import dk.simonwinther.utility.GangManaging;
import dk.simonwinther.enums.Level;
import dk.simonwinther.enums.Rank;
import dk.simonwinther.inventorymanaging.Menu;
import dk.simonwinther.utility.ChatUtil;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class EventHandling implements Listener
{
    private MainPlugin plugin;
    private final String npcName;
    private List<String> npcMessages;
    private List<String> goAwayMessages;
    public EventHandling(MainPlugin plugin)
    {
        this.plugin = plugin;
        Map map = null;
        ConfigFile cf = plugin.getConfigFile();
        if (plugin.getConfigFile().retrieve("npc") instanceof Map){
            map = (Map) plugin.getConfigFile().retrieve("npc");
        }
        npcName = map.get("npcName") != null ? map.get("npcName").toString() : "ali mustafa";
        npcMessages = map.get("messages") != null ? (ArrayList<String>) map.get("messages") : Arrays.asList("Tak for stoffer!");
        goAwayMessages = map.get("goAwayMessages") != null ? (ArrayList<String>) map.get("goAwayMessages") : Arrays.asList("Gå væk!!", "Føj!");
    }

    public static Map<UUID, String> lastOnline = new HashMap<>();

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event)
    {
        UUID playerUuid = event.getPlayer().getUniqueId();
        refreshDate(playerUuid);

        if (!GangManaging.damageMap.containsKey(playerUuid)) GangManaging.damageMap.put(playerUuid, false);

    }

    @EventHandler
    public void onGuiClick(InventoryClickEvent event)
    {
        if (event.getInventory() == null || event.getClickedInventory() == null || event.getCurrentItem() == null) return;

        if (event.getClickedInventory().getType() == InventoryType.PLAYER) return;
        if (event.getView().getTopInventory().getType() == InventoryType.PLAYER) return;


        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Menu)
        {
            event.setCancelled(true);
            Menu menu = (Menu) holder;
            menu.onGuiClick(event.getSlot(), event.getCurrentItem(), (Player) event.getWhoClicked(), event.getClick());
        }
    }

    private Set<UUID> createGang = new HashSet<>();
    public Consumer<UUID> addCreateGangConsumer = createGang::add;
    public Consumer<UUID> removeCreateGangConsumer = createGang::remove;


    private Set<UUID> inviteMemberChat = new HashSet<>();
    public Consumer<UUID> addInviteMemberChatConsumer = inviteMemberChat::add;
    private Consumer<UUID> removeInviteMemberChatConsumer = inviteMemberChat::remove;

    private Set<UUID> allyChat = new HashSet<>();
    public Consumer<UUID> addAllyConsumer = allyChat::add;
    private Consumer<UUID> removeAllyConsumer = allyChat::remove;

    private static Set<UUID> enemyChat = new HashSet<>();
    public Consumer<UUID> addEnemyChat = enemyChat::add;
    private Consumer<UUID> removeEnemyChat = enemyChat::remove;

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event)
    {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String message = event.getMessage();
        if (inviteMemberChat.contains(uuid))
        {
            event.setCancelled(true);
            removeInviteMemberChatConsumer.accept(uuid);
            if (Bukkit.getOfflinePlayer(message) != null)
            {
                if (GangManaging.getGangByUuidFunction.apply(uuid).isPlayerInvited(message)){
                    GangManaging.removeInvitationConsumer.accept(uuid, message);
                    player.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().PLAYER_WAS_UNINVITED.replace("{args}", message)));
                    return;
                }
                GangManaging.addInvitationConsumer.accept(uuid, message);
                player.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().PLAYER_WAS_INVITED.replace("{args}", message)));
                if (Bukkit.getPlayer(message) != null) Bukkit.getPlayer(message).sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().INVITED_TO_GANG.replace("{name}", GangManaging.getGangByUuidFunction.apply(uuid).getGangName()).replace("{player}", player.getName())));
            } else
                player.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().HAS_NEVER_PLAYED.replace("{args}", message)));
        } else if (allyChat.contains(uuid))
        {
            event.setCancelled(true);
            removeAllyConsumer.accept(uuid);
            if (GangManaging.gangExistsPredicate.test(message))
            {
                Gang argGang = GangManaging.getGangByNameFunction.apply(message);
                Gang playerGang = GangManaging.getGangByUuidFunction.apply(uuid);
                if (playerGang.getAllies().size() < playerGang.getMaxAllies())
                {
                    if (!(playerGang.getGangName().equalsIgnoreCase(message)))
                    {
                        if (!playerGang.getAllyInvitation().contains(message))
                        {
                            if (GangManaging.gangContainsAllyInvitationPredicate.test(argGang, playerGang.getGangName()))
                            {
                                playerGang.getAllyInvitation().remove(message);
                                argGang.getAllyInvitation().remove(playerGang.getGangName());

                                playerGang.getAllies().add(message.toLowerCase());
                                argGang.getAllies().add(playerGang.getGangName().toLowerCase());

                                sendMessageToTeamMembers(argGang, plugin.getChatUtil().color(plugin.getChatUtil().ALLY_SUCCESSFUL.replace("{name}", playerGang.getGangName())));
                                sendMessageToTeamMembers(playerGang, plugin.getChatUtil().color(plugin.getChatUtil().ALLY_SUCCESSFUL.replace("{name}", message)));
                            } else
                            {

                                playerGang.askAlly(message);

                                player.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().ASK_ALLY.replace("{name}", message)));
                                argGang.getMembersSorted().keySet()
                                        .stream()
                                        .filter(_uuid -> Bukkit.getPlayer(_uuid) != null)
                                        .map(Bukkit::getPlayer)
                                        .forEach(gangMembers -> gangMembers.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().WISHES_TO_BE_ALLY.replace("{name}", playerGang.getGangName()))));

                            }
                        } else
                        {
                            playerGang.getAllyInvitation().remove(message);
                            player.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().UN_ALLY.replace("{name}", message)));
                            sendMessageToTeamMembers(argGang, plugin.getChatUtil().color(plugin.getChatUtil().REGRET_TO_BE_ALLY.replace("{name}", playerGang.getGangName())));
                        }
                    } else player.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().CANT_ALLY_OWN_GANG));
                } else player.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().PLAYER_GANG_MAX_ALLYS));
            } else
                player.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().GANG_DOES_NOT_EXISTS.replace("{name}", message)));
        } else if (enemyChat.contains(uuid))
        {
            event.setCancelled(true);
            removeEnemyChat.accept(uuid);
            UUID playerUuid = player.getUniqueId();
            message = message.toLowerCase();
            if (GangManaging.playerInGangPredicate.test(playerUuid))
            {
                Gang playerGang = GangManaging.getGangByUuidFunction.apply(playerUuid);
                if (GangManaging.isRankMinimumPredicate.test(playerUuid, playerGang.gangPermissions.accessToEnemy))
                {
                    if (GangManaging.gangExistsPredicate.test(message))
                    {
                        Gang argsGang = GangManaging.getGangByNameFunction.apply(message);
                        if (argsGang.getEnemies().size() < playerGang.getMaxEnemies())
                        {
                            if (playerGang.getEnemies().size() < playerGang.getMaxEnemies())
                            {

                                if (!playerGang.equals(argsGang))
                                {
                                    if (!playerGang.getEnemies().contains(message))
                                    {
                                        GangManaging.addEnemyGang.accept(playerGang, argsGang);
                                    } else player.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().ALREADY_ENEMIES));
                                } else player.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().CANT_ENEMY_OWN_GANG));
                            } else player.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().PLAYER_GANG_MAX_ENEMIES));
                        }else player.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().OTHER_GANG_MAX_ENEMIES));
                    } else player.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().GANG_DOES_NOT_EXISTS.replace("{name}", String.valueOf(message.charAt(0)).toUpperCase() + message.substring(1))));
                } else player.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_HIGH_RANK_ENOUGH));
            } else player.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_IN_GANG));
        } else if (createGang.contains(uuid))
        {
            event.setCancelled(true);
            if (message.equalsIgnoreCase("!stop")) {
                removeCreateGangConsumer.accept(uuid);
                player.sendMessage("Du har afbrudt at lave bande!");
                return;
            }
            removeCreateGangConsumer.accept(uuid);
            player.performCommand("bande create "+message);

        }
    }


    public void refreshDate(UUID playerUuid)
    {
        //K key, BiFunction<? super K, ? super V, ? extends V>
        //#compute -> first a key, then functional programming with an object of some type that extends Key and an object of some type that extends Value called remappingFunction
        DateFormat dateFormat = new SimpleDateFormat("EEEEEEE, d MMMMMMMMM yyyy, HH:mm:s");
        Date currentDate = new Date();
        String currentDateString = dateFormat.format(currentDate);
        lastOnline.compute(playerUuid, (k, v) -> currentDateString);
    }

    public String getDate(UUID playerUuid)
    {
        StringBuilder stringBuilder = new StringBuilder();
        lastOnline
                .entrySet() //Map.Entry<UUID, Integer>
                .stream() //Stream<Map.Entry<UUID, Integer>>
                .filter(entry -> entry.getKey().equals(playerUuid))
                .findAny()
                .map(Map.Entry::getValue)
                .ifPresent(stringBuilder::append);

        return stringBuilder.toString().length() != 0 ? stringBuilder.toString() : "&cReload..";
    }

    public void sendMessageToTeamMembers(Gang gang, String msg)
    {
        gang.getMembersSorted()
                .keySet()
                .stream()
                .filter(_uuid -> Bukkit.getPlayer(_uuid) != null)
                .map(Bukkit::getPlayer)
                .forEach(p -> p.sendMessage(msg));
    }



    @EventHandler
    public void onAliMustafaClick(NPCRightClickEvent event){
        NPC npc = event.getNPC();

        if (npc.getName().contains(npcName)){
            Player player = event.getClicker();
            playerConsumer.accept(player);
        }
    }

    @EventHandler
    public void onAliMustafaClick(NPCLeftClickEvent event){
        NPC npc = event.getNPC();
        if (npc.getName().contains(npcName)){
            Player player = event.getClicker();
            playerConsumer.accept(player);
        }
    }


    private Set<UUID> activeMoneyPlayers = new HashSet<>();
    public Consumer<UUID> removeActiveMoneyPlayers = activeMoneyPlayers::remove;
    public Consumer<UUID> addActiveMoneyPlayers = activeMoneyPlayers::add;
    public Function<UUID, Boolean> containsActiveMoneyPlayer = activeMoneyPlayers::contains;

    private Consumer<Player> playerConsumer = (player) -> {
        UUID uuid = player.getUniqueId();
        ItemStack item = player.getInventory().getItemInHand();
        //Ternary operator to determine whether it should check for transfer items or money access.

        if (GangManaging.playerInGangPredicate.test(player.getUniqueId()))
        {
            Gang gang = GangManaging.getGangByUuidFunction.apply(player.getUniqueId());
            Rank rank = item.getType() != Material.AIR ? gang.getGangPermissions().accessToTransferItems : gang.getGangPermissions().accessToTransferMoney;
            if(GangManaging.isRankMinimumPredicate.test(uuid, rank))
            {
                String gangLevelString = ChatUtil.numbers[gang.getGangLevel()];
                Level level = Level.valueOf(gangLevelString);
                ItemStack playerItemInHand = player.getInventory().getItemInHand();
                Material playerItemInHandType = playerItemInHand.getType();
                if (playerItemInHandType == Material.AIR){
                    if (level.getAmountToPay() != null) {
                        int money = gang.getGangBalance();
                        gang.getLevelSystem().setPaidForQuest(gang.getLevelSystem().getPaidForQuest()+money);
                        gang.setGangBalance(Math.max((gang.getGangBalance() - money), 0));
                        String commandLine = "[\"\",{\"text\":\"----------------------\",\"bold\":true,\"italic\":true,\"strikethrough\":true,\"color\":\"dark_aqua\"},{\"text\":\"\\n\\n\\n\"},{\"text\":\"Accepter\",\"bold\":true,\"italic\":true,\"underlined\":true,\"color\":\"dark_green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/confirmtransfercmd yes\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"§2Klik her§a, hvis du vil overføre penge til Ali!\"}},{\"text\":\" eller \"},{\"text\":\"Fortryd\",\"bold\":true,\"italic\":true,\"underlined\":true,\"color\":\"dark_red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/confirmtransfercmd no\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"§4Klik her§c, hvis du ikke vil overføre penge til Ali!\"}},{\"text\":\" \\nFor at annullere overførelsen til\\n\\u25ba Ali Mustafa\\n\\n\"},{\"text\":\"----------------------\",\"bold\":true,\"italic\":true,\"strikethrough\":true,\"color\":\"dark_aqua\"}]";

                        IChatBaseComponent iChatBaseComponent = IChatBaseComponent.ChatSerializer.a(commandLine);
                        PacketPlayOutChat playOutChat = new PacketPlayOutChat(iChatBaseComponent);
                        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(playOutChat);

                        addActiveMoneyPlayers.accept(player.getUniqueId());
                    }else{
                        player.sendMessage(plugin.getChatUtil().color(goAwayMessages.get(new Random().nextInt(goAwayMessages.size()))));
                    }
                    return;
                }
                int amount = playerItemInHand.getAmount();

                if (level.getAcceptedItems() == null)
                {
                    player.sendMessage(plugin.getChatUtil().color(goAwayMessages.get(new Random().nextInt(goAwayMessages.size()))));
                    return;
                }
                for (ItemStack acceptedItemStack : level.getAcceptedItems())
                {
                    if (acceptedItemStack.getType() == playerItemInHandType)
                    {
                        int amountDelivered = gang.getLevelSystem().getValueOfMaterial(playerItemInHandType);

                        if (amountDelivered >= acceptedItemStack.getAmount())
                        {
                            player.sendMessage("Du kan ikke aflevere mere af dette item!");
                        } else
                        {
                            int restAmount = acceptedItemStack.getAmount() - amountDelivered;
                            if(amount >= restAmount) amount = restAmount;

                            if (player.getItemInHand().getAmount() == amount){
                                player.setItemInHand(new ItemStack(Material.AIR));
                            }
                            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - amount);

                            gang.getLevelSystem().addValue(playerItemInHandType, amount);

                            String randomMessage = npcMessages.get(new Random().nextInt(npcMessages.size()));
                            player.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().PREFIX + "&7"+randomMessage));
                        }
                        return;
                    }
                }
            } else player.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_HIGH_RANK_ENOUGH));
        } else player.sendMessage(plugin.getChatUtil().color(plugin.getChatUtil().NOT_IN_GANG));

    };


}