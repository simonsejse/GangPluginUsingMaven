package dk.simonwinther.events;

import dk.simonwinther.MainPlugin;
import dk.simonwinther.inventorymanaging.AbstractMenu;
import dk.simonwinther.inventorymanaging.AbstractPaginatedMenu;
import dk.simonwinther.manager.Gang;
import dk.simonwinther.manager.GangManaging;
import dk.simonwinther.utility.MessageProvider;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
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

public class EventHandling implements Listener {
    private final MainPlugin plugin;
    private final MessageProvider mp;

    private final GangManaging gangManaging;

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

    public EventHandling(GangManaging gangManaging, MainPlugin plugin) {
        this.plugin = plugin;
        this.mp = this.plugin.getMessageProvider();
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
    public void interactWithNPCLeft(NPCLeftClickEvent event) {
        interactWithNPC(event.getClicker(), event.getNPC().getName());
    }

    @EventHandler
    public void interactWithNPCRight(NPCLeftClickEvent event) {
        interactWithNPC(event.getClicker(), event.getNPC().getName());
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
        }else if (holder instanceof AbstractPaginatedMenu){
            event.setCancelled(true);
            AbstractPaginatedMenu abstractPaginatedMenu = (AbstractPaginatedMenu) holder;
            if (event.getSlot() == abstractPaginatedMenu.getBackPageSlot()) {
                if (abstractPaginatedMenu.getCurrentPageIndex() == 0) return;
                abstractPaginatedMenu.setCurrentPageIndex(abstractPaginatedMenu.getCurrentPageIndex() - 1);
                event.getWhoClicked().openInventory(abstractPaginatedMenu.getInventories().get(abstractPaginatedMenu.getCurrentPageIndex()));
            }else if (event.getSlot() == abstractPaginatedMenu.getNextPageSlot()) {
                if (abstractPaginatedMenu.getCurrentPageIndex() >= abstractPaginatedMenu.getInventories().size() - 1) return;
                abstractPaginatedMenu.setCurrentPageIndex(abstractPaginatedMenu.getCurrentPageIndex() + 1);
                event.getWhoClicked().openInventory(abstractPaginatedMenu.getInventories().get(abstractPaginatedMenu.getCurrentPageIndex()));
            }else abstractPaginatedMenu.onGuiClick(event.getSlot(), event.getCurrentItem(), (Player) event.getWhoClicked(), event.getClick());
        }
    }

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
        this.removePlayerFromAwaitEnemyRequest.accept(playerUUID);
        if(this.gangManaging.playerInGangPredicate.test(playerUUID)){
            if (this.gangManaging.gangExistsPredicate.test(otherGangName)){
                Gang playerGang = this.gangManaging.getGangByUuidFunction.apply(playerUUID);
                this.gangManaging.requestEnemy(playerGang, otherGangName, player);

            } else player.sendMessage(this.mp.gangDoesNotExists.replace("{name}", otherGangName));
        }else player.sendMessage(this.mp.notInGang);

    }

    private void performChatInputAllyRequest(Player player, UUID playerUUID, String otherGangName) {
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

    public void refreshDate(UUID playerUUID) {
        DateFormat dateFormat = new SimpleDateFormat("EEEEEEE, d MMMMMMMMM yyyy, HH:mm:s");
        Date currentDate = new Date();
        String currentDateString = dateFormat.format(currentDate);
        lastOnline.compute(playerUUID, (k, v) -> currentDateString);
    }

    public String getDate(UUID playerUUID) {
        StringBuilder stringBuilder = new StringBuilder();
        lastOnline.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(playerUUID))
                .findAny()
                .map(Map.Entry::getValue)
                .ifPresent(stringBuilder::append);
        return stringBuilder.toString().length() != 0 ? stringBuilder.toString() : "&cReload..";
    }


    private void interactWithNPC(Player whoInteract, String npcName) {
        if(npcName.equals(this.plugin.getConfiguration().npcSettingsProvider.npcName)){
            whoInteract.sendMessage("youre having sexual intercourse with npc");
        }
    }

    /*
    private final Set<UUID> activeMoneyPlayers = new HashSet<>();
    public Consumer<UUID> removeActiveMoneyPlayers = activeMoneyPlayers::remove;
    public Consumer<UUID> addActiveMoneyPlayers = activeMoneyPlayers::add;
    public Function<UUID, Boolean> containsActiveMoneyPlayer = activeMoneyPlayers::contains;
     */

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
