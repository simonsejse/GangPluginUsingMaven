package dk.simonwinther.events;

import dk.simonwinther.Gang;
import dk.simonwinther.MainPlugin;
import dk.simonwinther.inventorymanaging.AbstractMenu;
import dk.simonwinther.manager.GangManaging;
import dk.simonwinther.utility.MessageProvider;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.InventoryHolder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class EventHandling implements Listener {
    private final MainPlugin plugin;
    private final MessageProvider mp;

    private final String npcName;
    private final List<String> goAwayMessages;
    private final List<String> deliveredMessages;

    private final GangManaging gangManaging;

    public EventHandling(GangManaging gangManaging, MainPlugin plugin) {
        this.plugin = plugin;
        this.mp = this.plugin.getMessageProvider();
        this.npcName = this.plugin.getConfiguration().npcSettingsProvider.npcName;
        this.goAwayMessages = this.plugin.getConfiguration().npcSettingsProvider.goAwayMessages;
        this.deliveredMessages = this.plugin.getConfiguration().npcSettingsProvider.deliveredMessages;
        this.gangManaging = gangManaging;
    }

    public Map<UUID, String> lastOnline = new HashMap<>();
    //TODO: Maybe create PlayerData object

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        refreshDate(playerUUID);

        if (!gangManaging.damageMap.containsKey(playerUUID)) gangManaging.damageMap.put(playerUUID, false);

    }

    @EventHandler
    public void onGuiClick(InventoryClickEvent event) {
        if (event.getInventory() == null || event.getClickedInventory() == null || event.getCurrentItem() == null)
            return;

        if (event.getClickedInventory().getType() == InventoryType.PLAYER) return;
        if (event.getView().getTopInventory().getType() == InventoryType.PLAYER) return;


        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof AbstractMenu) {
            event.setCancelled(true);
            AbstractMenu abstractMenu = (AbstractMenu) holder;
            abstractMenu.onGuiClick(event.getSlot(), event.getCurrentItem(), (Player) event.getWhoClicked(), event.getClick());
        }
    }

    private final Set<UUID> awaitChatInputForGangCreation = new HashSet<>();
    public final Consumer<UUID> addPlayerToAwaitGangCreation = awaitChatInputForGangCreation::add;
    public final Consumer<UUID> removePlayerToAwaitGangCreation = awaitChatInputForGangCreation::remove;

    private final Set<UUID> awaitChatInputForInvitation = new HashSet<>();
    public final Consumer<UUID> addPlayerToAwaitInvitation = awaitChatInputForInvitation::add;
    private final Consumer<UUID> removePlayerToAwaitInvitation = awaitChatInputForInvitation::remove;

    private final Set<UUID> awaitChatInputForAlly = new HashSet<>();
    public final Consumer<UUID> addPlayerToAwaitAllyRequest = awaitChatInputForAlly::add;
    public final Consumer<UUID> removePlayerFromAwaitAllyRequest = awaitChatInputForAlly::remove;

    private final Set<UUID> awaitChatInputForEnemy = new HashSet<>();
    public Consumer<UUID> addPlayerToAwaitEnemyRequest = awaitChatInputForEnemy::add;
    private final Consumer<UUID> removePlayerFromAwaitEnemyRequest = awaitChatInputForEnemy::remove;

    @EventHandler
    public void awaitPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (awaitChatInputForGangCreation.contains(playerUUID)) {
            event.setCancelled(true);
            performChatInputGangCreation(player, player.getUniqueId(), event.getMessage());
        }else if (awaitChatInputForInvitation.contains(playerUUID)){
            event.setCancelled(true);
            performChatInputInvitation(player, player.getUniqueId(), event.getMessage());
        }else if (awaitChatInputForAlly.contains(playerUUID)){
            event.setCancelled(true);
            performChatInputAllyRequest(player, player.getUniqueId(), event.getMessage());
        }else if (awaitChatInputForEnemy.contains(playerUUID)){
            event.setCancelled(true);
            performChatInputEnemyRequest(player, player.getUniqueId(),  event.getMessage());
        }
    }


    private void performChatInputEnemyRequest(Player player, UUID playerUUID, String otherGangName) {
        //TODO: check if gang exists
        this.removePlayerFromAwaitEnemyRequest.accept(playerUUID);
        if(this.gangManaging.playerInGangPredicate.test(playerUUID)){
            if (this.gangManaging.gangExistsPredicate.test(otherGangName)){
                Gang playerGang = this.gangManaging.getGangByUuidFunction.apply(playerUUID);
                this.gangManaging.requestEnemy(playerGang, otherGangName, player);

            } else player.sendMessage(this.mp.gangDoesNotExists.replace("{name}", otherGangName));
        }else player.sendMessage(this.mp.notInGang);

    }

    private void performChatInputAllyRequest(Player player, UUID playerUUID, String otherGangName) {
        //TODO: check if gang exists
        removePlayerFromAwaitAllyRequest.accept(playerUUID);
        if(this.gangManaging.playerInGangPredicate.test(playerUUID)){
            if (this.gangManaging.gangExistsPredicate.test(otherGangName)){
                Gang playerGang = this.gangManaging.getGangByUuidFunction.apply(playerUUID);
                Gang otherGang = this.gangManaging.getGangByNameFunction.apply(otherGangName);
                this.gangManaging.requestAlly(playerGang, otherGang, player);
            } else player.sendMessage(this.mp.gangDoesNotExists.replace("{name}", otherGangName));
        }else player.sendMessage(this.mp.notInGang);

    }

    private void performChatInputGangCreation(Player player, UUID playerUUID, String message) {
        removePlayerToAwaitGangCreation.accept(playerUUID);
        if (message.equals("!stop")) {
            player.sendMessage("§cDu ønsker ikke længere at lave en bande!");
            return;
        }
        this.gangManaging.createNewGang(player, message);
    }


    private void performChatInputInvitation(Player player, UUID playerUUID, String invitedPlayerName) {
        removePlayerToAwaitInvitation.accept(playerUUID);
        if (this.gangManaging.playerInGangPredicate.test(playerUUID)) {
            this.gangManaging.requestPlayerToJoinGang(
                    gangManaging.getGangByUuidFunction.apply(playerUUID),
                    player,
                    invitedPlayerName
            );
        }
    }

    /*
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
                        if (!playerGang.getAllyInvitation().contains(message)) //TODO: dette skal tjekke først om man skal fjerne sin ally invitation eller tilføje
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

                                argGang.getMembersSorted().keySet()
                                        .stream()
                                        .filter(_uuid -> Bukkit.getPlayer(_uuid) != null)
                                        .map(Bukkit::getPlayer)
                                        .forEach(gangMembers -> gangMembers.sendMessage(this.mp.wishesToBeAlly.replace("{name}", playerGang.getGangName()))));

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
            UUID playerUUID = player.getUniqueId();
            message = message.toLowerCase();
            if (gangManaging.playerInGangPredicate.test(playerUUID))
            {
                Gang playerGang = gangManaging.getGangByUuidFunction.apply(playerUUID);
                if (gangManaging.isRankMinimumPredicate.test(playerUUID, playerGang.gangPermissions.accessToEnemy))
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
     */


    public void refreshDate(UUID playerUUID) {
        //K key, BiFunction<? super K, ? super V, ? extends V>
        //#compute -> first a key, then functional programming with an object of some type that extends Key and an object of some type that extends Value called remappingFunction
        DateFormat dateFormat = new SimpleDateFormat("EEEEEEE, d MMMMMMMMM yyyy, HH:mm:s");
        Date currentDate = new Date();
        String currentDateString = dateFormat.format(currentDate);
        lastOnline.compute(playerUUID, (k, v) -> currentDateString);
    }

    public String getDate(UUID playerUUID) {
        StringBuilder stringBuilder = new StringBuilder();
        lastOnline.entrySet() //Map.Entry<UUID, Integer>
                .stream() //Stream<Map.Entry<UUID, Integer>>
                .filter(entry -> entry.getKey().equals(playerUUID))
                .findAny()
                .map(Map.Entry::getValue)
                .ifPresent(stringBuilder::append);
        return stringBuilder.toString().length() != 0 ? stringBuilder.toString() : "&cReload..";
    }

    @EventHandler
    public void interactWithNPCLeft(NPCLeftClickEvent event) {
        interactWithNPC(event.getClicker(), event.getNPC());
    }

    @EventHandler
    public void interactWithNPCRight(NPCLeftClickEvent event) {
        interactWithNPC(event.getClicker(), event.getNPC());
    }

    private void interactWithNPC(Player whoInteract, NPC npc) {
        //if(npc.getName().equals(this.plugin.getCustomSettingsProvider().getNpcProvider().getNpcName())){
        //    whoInteract.sendMessage("youre having sexual intercourse with npc");
        //}
    }


    private final Set<UUID> activeMoneyPlayers = new HashSet<>();
    public Consumer<UUID> removeActiveMoneyPlayers = activeMoneyPlayers::remove;
    public Consumer<UUID> addActiveMoneyPlayers = activeMoneyPlayers::add;
    public Function<UUID, Boolean> containsActiveMoneyPlayer = activeMoneyPlayers::contains;

    /*private void playerCheck(Player player){

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

     */


}
