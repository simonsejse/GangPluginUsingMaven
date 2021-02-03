package dk.simonwinther.events;

import dk.simonwinther.Gang;
import dk.simonwinther.MainPlugin;
import dk.simonwinther.enums.Level;
import dk.simonwinther.enums.Rank;
import dk.simonwinther.inventorymanaging.Menu;
import dk.simonwinther.utility.MessageProvider;
import dk.simonwinther.utility.GangManaging;
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
    private MessageProvider mp;

    private final String npcName;
    private List<String> goAwayMessages;
    private List<String> deliveredMessages;

    private final GangManaging gangManaging;

    public EventHandling(GangManaging gangManaging, MainPlugin plugin)
    {
        this.plugin = plugin;
        this.mp = this.plugin.getMessageProvider();
        this.npcName = this.plugin.getCustomSettingsProvider().getNpcProvider().getNpcName();
        this.goAwayMessages = this.plugin.getCustomSettingsProvider().getNpcProvider().getGoAwayMessages();
        this.deliveredMessages = this.plugin.getCustomSettingsProvider().getNpcProvider().getDeliveredMessages();
        this.gangManaging = gangManaging;
    }

    public Map<UUID, String> lastOnline = new HashMap<>();
    //TODO: Maybe create PlayerData object

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event)
    {
        UUID playerUuid = event.getPlayer().getUniqueId();
        refreshDate(playerUuid);

        if (!gangManaging.damageMap.containsKey(playerUuid)) gangManaging.damageMap.put(playerUuid, false);

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

    private final Set<UUID> createGang = new HashSet<>();
    public Consumer<UUID> addCreateGangConsumer = createGang::add;
    public Consumer<UUID> removeCreateGangConsumer = createGang::remove;


    private final Set<UUID> inviteMemberChat = new HashSet<>();
    public Consumer<UUID> addInviteMemberChatConsumer = inviteMemberChat::add;
    private final Consumer<UUID> removeInviteMemberChatConsumer = inviteMemberChat::remove;

    private final Set<UUID> allyChat = new HashSet<>();
    public Consumer<UUID> addAllyConsumer = allyChat::add;
    private final Consumer<UUID> removeAllyConsumer = allyChat::remove;

    private final Set<UUID> enemyChat = new HashSet<>();
    public Consumer<UUID> addEnemyChat = enemyChat::add;
    private final Consumer<UUID> removeEnemyChat = enemyChat::remove;

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
                if (gangManaging.getGangByUuidFunction.apply(uuid).isPlayerInvited(message)){
                    gangManaging.removeInvitationConsumer.accept(uuid, message);
                    player.sendMessage(this.mp.playerWasUninvited.replace("{args}", message));
                    return;
                }
                gangManaging.addInvitationConsumer.accept(uuid, message);
                player.sendMessage(this.mp.playerWasInvited.replace("{args}", message));
                if (Bukkit.getPlayer(message) != null) {
                    Bukkit.getPlayer(message).sendMessage(this.mp.invitedToGang.replace("{name}", gangManaging.getGangByUuidFunction.apply(uuid).getGangName()).replace("{player}", player.getName()));
                }
            } else
                player.sendMessage(this.mp.hasNeverPlayed.replace("{args}", message));
        } else if (allyChat.contains(uuid))
        {
            event.setCancelled(true);
            removeAllyConsumer.accept(uuid);
            if (gangManaging.gangExistsPredicate.test(message))
            {
                Gang argGang = gangManaging.getGangByNameFunction.apply(message);
                Gang playerGang = gangManaging.getGangByUuidFunction.apply(uuid);
                if (playerGang.getAllies().size() < playerGang.getMaxAllies())
                {
                    if (!(playerGang.getGangName().equalsIgnoreCase(message)))
                    {
                        if (!playerGang.getAllyInvitation().contains(message))
                        {
                            if (gangManaging.gangContainsAllyInvitationPredicate.test(argGang, playerGang.getGangName()))
                            {
                                playerGang.getAllyInvitation().remove(message);
                                argGang.getAllyInvitation().remove(playerGang.getGangName());

                                playerGang.getAllies().put(argGang.getGangId(), message.toLowerCase());
                                argGang.getAllies().put(playerGang.getGangId(), playerGang.getGangName().toLowerCase());

                                this.gangManaging.sendTeamMessage.accept(argGang, this.mp.allySuccessful.replace("{name}", playerGang.getGangName()));
                                this.gangManaging.sendTeamMessage.accept(playerGang, this.mp.allySuccessful.replace("{name}", message));
                            } else
                            {

                                playerGang.askAlly(message);

                                player.sendMessage(this.mp.askAlly.replace("{name}", message));

                                this.gangManaging.sendTeamMessage.accept(argGang, this.mp.wishesToBeAlly.replace("{name}", playerGang.getGangName()));

                                //TODO: Check if I've written more of these duplicate codes in other classes, there's a big probability that I have.
                                /*
                                argGang.getMembersSorted().keySet()
                                        .stream()
                                        .filter(_uuid -> Bukkit.getPlayer(_uuid) != null)
                                        .map(Bukkit::getPlayer)
                                        .forEach(gangMembers -> gangMembers.sendMessage(this.mp.wishesToBeAlly.replace("{name}", playerGang.getGangName()))));
                                 */
                            }
                        } else
                        {
                            playerGang.getAllyInvitation().remove(message);
                            player.sendMessage(this.mp.unAlly.replace("{name}", message));
                            this.gangManaging.sendTeamMessage.accept(argGang, this.mp.askAlly.replace("{name}", message));
                        }
                    } else player.sendMessage(this.mp.cantAllyOwnGang);
                } else player.sendMessage(this.mp.playerGangMaxAllies);
            } else
                player.sendMessage(this.mp.gangDoesNotExists.replace("{name}", message));
        } else if (enemyChat.contains(uuid))
        {
            event.setCancelled(true);
            removeEnemyChat.accept(uuid);
            UUID playerUuid = player.getUniqueId();
            message = message.toLowerCase();
            if (gangManaging.playerInGangPredicate.test(playerUuid))
            {
                Gang playerGang = gangManaging.getGangByUuidFunction.apply(playerUuid);
                if (gangManaging.isRankMinimumPredicate.test(playerUuid, playerGang.gangPermissions.accessToEnemy))
                {
                    if (gangManaging.gangExistsPredicate.test(message))
                    {
                        Gang argsGang = gangManaging.getGangByNameFunction.apply(message);
                        //TODO: Make it a function instead
                        if (playerGang.getEnemies().size() < playerGang.getMaxEnemies())
                        {

                            if (!playerGang.equals(argsGang))
                            {
                                if (!playerGang.getEnemies().values().contains(message))
                                {
                                    this.gangManaging.addEnemyGang.accept(playerGang, argsGang);
                                } else player.sendMessage(this.mp.alreadyEnemies);
                            } else player.sendMessage(this.mp.cantEnemyOwnGang);
                        } else player.sendMessage(this.mp.playerGangMaxEnemies);
                    } else player.sendMessage(this.mp.gangDoesNotExists.replace("{name}", String.valueOf(message.charAt(0)).toUpperCase() + message.substring(1)));
                } else player.sendMessage(this.mp.notHighRankEnough);
            } else player.sendMessage(this.mp.notInGang);
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

    @EventHandler
    public void onAliMustafaClick(NPCRightClickEvent event){
        NPC npc = event.getNPC();

        if (npc.getName().contains(npcName)){
            Player player = event.getClicker();
            playerCheck(player);
        }
    }

    @EventHandler
    public void onAliMustafaClick(NPCLeftClickEvent event){
        NPC npc = event.getNPC();
        if (npc.getName().contains(npcName)){
            Player player = event.getClicker();
            playerCheck(player);
        }
    }


    private final Set<UUID> activeMoneyPlayers = new HashSet<>();
    public Consumer<UUID> removeActiveMoneyPlayers = activeMoneyPlayers::remove;
    public Consumer<UUID> addActiveMoneyPlayers = activeMoneyPlayers::add;
    public Function<UUID, Boolean> containsActiveMoneyPlayer = activeMoneyPlayers::contains;

    private void playerCheck(Player player){

        UUID uuid = player.getUniqueId();
        ItemStack item = player.getInventory().getItemInHand();
        //Ternary operator to determine whether it should check for transfer items or money access.

        if (this.gangManaging.playerInGangPredicate.test(player.getUniqueId()))
        {
            Gang gang = gangManaging.getGangByUuidFunction.apply(player.getUniqueId());
            Rank rank = item.getType() != Material.AIR ? gang.getGangPermissions().accessToTransferItems : gang.getGangPermissions().accessToTransferMoney;
            if(this.gangManaging.isRankMinimumPredicate.test(uuid, rank))
            {
                String gangLevelString = MessageProvider.numbers[gang.getGangLevel()];
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
                        player.sendMessage(goAwayMessages.get(new Random().nextInt(goAwayMessages.size())));
                    }
                    return;
                }
                int amount = playerItemInHand.getAmount();

                if (level.getAcceptedItems() == null)
                {
                    player.sendMessage(goAwayMessages.get(new Random().nextInt(goAwayMessages.size())));
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

                            String randomMessage = this.deliveredMessages.get(new Random().nextInt(this.deliveredMessages.size()));
                            player.sendMessage(this.mp.prefix + "&7"+randomMessage);
                        }
                        return;
                    }
                }
            } else player.sendMessage(this.mp.notHighRankEnough);
        } else player.sendMessage(this.mp.notInGang);
    }



}
