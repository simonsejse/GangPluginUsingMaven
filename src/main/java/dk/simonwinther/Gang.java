package dk.simonwinther;

import dk.simonwinther.constants.Rank;
import dk.simonwinther.levelsystem.LevelSystem;
import org.bukkit.Bukkit;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "gangs")
public class Gang implements Serializable
{

    public Gang(int gangId, String gangName, UUID ownerUuid)
    {
        this.gangId = gangId;
        this.gangName = gangName;
        membersSorted.put(ownerUuid, Rank.LEADER.getValue());
    }

    public Gang() {

    }

    private String gangName = "";
    private int gangId;
    private int gangBalance = 100;
    private int gangLevel = 1;
    private int maxMembers = 3;
    private int maxAllies = 2;
    private int maxEnemies = 3;
    private int gangDamage = 100;
    private int allyDamage = 100;
    private int guardKills = 0;
    private int prisonerKills = 0;
    private int officerKills = 0;
    private int deaths = 0;
    private boolean nameBeenChanged = false;

    private LevelSystem levelSystem = new LevelSystem(this.gangId);
    public GangPermissions gangPermissions = new GangPermissions(this.gangId);
    private Map<UUID, Integer> membersSorted = new LinkedHashMap<>();
    private Map<Integer, String> enemies = new HashMap<>();
    private Map<Integer, String> allies = new HashMap<>();
    private List<String> memberInvitations = new ArrayList<>();
    private List<String> allyInvitation = new ArrayList<>();

    public void removeMoney(int money){
        this.gangBalance -= money;
    }

    public void depositMoney(int amount)
    {
        gangBalance += amount;
    }

    public boolean isPlayerInvited(String displayname)
    {
        return memberInvitations.contains(displayname.toLowerCase());
    }

    public void addEnemyGang(int gangID, String otherGangName){
        this.enemies.put(gangID, otherGangName.toLowerCase());
    }

    public boolean hasReachedMaxMembers(){
        return this.membersSorted.size() < this.maxMembers;

    }
    public boolean isGangEnemy(String otherGangName){
        return this.enemies.values().contains(otherGangName.toLowerCase());
    }
    public boolean hasReachedMaxAllies(){
        return this.allies.size() < this.maxAllies;
    }

    public void addGangAlly(Gang gangAlly){
        this.allies.put(gangAlly.getGangId(), gangAlly.getGangName().toLowerCase());
    }

    public void addAllyRequest(Gang otherGang){
        this.allyInvitation.add(otherGang.getGangName().toLowerCase());
    }

    public void removeAllyRequest(String otherGangName){
        this.allyInvitation.remove(otherGangName.toLowerCase());
    }

    public boolean hasRequestedAlly(String otherGangName)
    {
        return allyInvitation.contains(otherGangName.toLowerCase());
    }

    public boolean isGangAlly(Gang gang){
        return this.allies.values().contains(gang.getGangName().toLowerCase());
    }

    public void removeMemberInvitation(String nameOfInvitedPlayer){
        this.memberInvitations.remove(nameOfInvitedPlayer.toLowerCase());
    }

    public void addMemberInvitation(String nameOfInvitedPlayer){
        this.memberInvitations.add(nameOfInvitedPlayer.toLowerCase());
    }


    public UUID getOwnerUuid(){
        Optional<UUID> first = membersSorted.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(Rank.LEADER.getValue()))
                .map(Map.Entry::getKey)
                .findFirst();

        return first.get();
    }

    public String getOwnerName()
    {
        Optional<UUID> first = membersSorted.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(Rank.LEADER.getValue()))
                .map(Map.Entry::getKey)
                .findFirst();

        return first.isPresent() ? Bukkit.getOfflinePlayer(first.get()).getName() : "Player not found!";
    }

    public String getOwner()
    {
        for (Map.Entry<UUID, Integer> map : membersSorted.entrySet())
        {
            if (map.getValue().equals(Rank.LEADER.getValue()))
            {
                return Bukkit.getOfflinePlayer(map.getKey()).getName();
            }
        }
        return "&cIngen ejer..";
    }

    public String getCoOwner()
    {
        for (Map.Entry<UUID, Integer> map : membersSorted.entrySet())
        {
            if (map.getValue().equals(Rank.CO_LEADER.getValue()))
            {
                return Bukkit.getOfflinePlayer(map.getKey()).getName();
            }
        }
        return "&cIngen..";
    }

    public void addMember(UUID playerUUID, String displayName, Rank rank)
    {
        memberInvitations.remove(displayName.toLowerCase());
        membersSorted.put(playerUUID, rank.getValue());
    }


    public void removeEnemyGang(int gangID) {
        this.enemies.remove(gangID);
    }


    public String getGangName() {
        return gangName;
    }

    public void setGangName(String gangName) {
        this.gangName = gangName;
    }

    public int getGangId() {
        return gangId;
    }

    public void setGangId(int gangId) {
        this.gangId = gangId;
    }

    public int getGangBalance() {
        return gangBalance;
    }

    public void setGangBalance(int gangBalance) {
        this.gangBalance = gangBalance;
    }

    public int getGangLevel() {
        return gangLevel;
    }

    public void setGangLevel(int gangLevel) {
        this.gangLevel = gangLevel;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public int getMaxAllies() {
        return maxAllies;
    }

    public void setMaxAllies(int maxAllies) {
        this.maxAllies = maxAllies;
    }

    public int getMaxEnemies() {
        return maxEnemies;
    }

    public void setMaxEnemies(int maxEnemies) {
        this.maxEnemies = maxEnemies;
    }

    public int getGangDamage() {
        return gangDamage;
    }

    public void setGangDamage(int gangDamage) {
        this.gangDamage = gangDamage;
    }

    public int getAllyDamage() {
        return allyDamage;
    }

    public void setAllyDamage(int allyDamage) {
        this.allyDamage = allyDamage;
    }

    public int getGuardKills() {
        return guardKills;
    }

    public void setGuardKills(int guardKills) {
        this.guardKills = guardKills;
    }

    public int getPrisonerKills() {
        return prisonerKills;
    }

    public void setPrisonerKills(int prisonerKills) {
        this.prisonerKills = prisonerKills;
    }

    public int getOfficerKills() {
        return officerKills;
    }

    public void setOfficerKills(int officerKills) {
        this.officerKills = officerKills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public boolean isNameBeenChanged() {
        return nameBeenChanged;
    }

    public void setNameBeenChanged(boolean nameBeenChanged) {
        this.nameBeenChanged = nameBeenChanged;
    }

    public GangPermissions getGangPermissions() {
        return gangPermissions;
    }

    public void setGangPermissions(GangPermissions gangPermissions) {
        this.gangPermissions = gangPermissions;
    }

    public LevelSystem getLevelSystem() {
        return levelSystem;
    }

    public void setLevelSystem(LevelSystem levelSystem) {
        this.levelSystem = levelSystem;
    }

    public Map<UUID, Integer> getMembersSorted() {
        return membersSorted;
    }

    public void setMembersSorted(Map<UUID, Integer> membersSorted) {
        this.membersSorted = membersSorted;
    }

    public Map<Integer, String> getEnemies() {
        return enemies;
    }

    public void setEnemies(Map<Integer, String> enemies) {
        this.enemies = enemies;
    }

    public Map<Integer, String> getAllies() {
        return allies;
    }

    public void setAllies(Map<Integer, String> allies) {
        this.allies = allies;
    }

    public List<String> getMemberInvitations() {
        return memberInvitations;
    }

    public void setMemberInvitations(List<String> memberInvitations) {
        this.memberInvitations = memberInvitations;
    }

    public List<String> getAllyInvitation() {
        return allyInvitation;
    }

    public void setAllyInvitation(List<String> allyInvitation) {
        this.allyInvitation = allyInvitation;
    }
}
