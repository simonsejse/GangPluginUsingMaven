package dk.simonwinther;

import dk.simonwinther.enums.Rank;
import dk.simonwinther.levelsystem.LevelSystem;
import org.bukkit.Bukkit;

import java.io.Serializable;
import java.util.*;

public class Gang implements Serializable
{

    private final long serialVersionUID = 2109381209382109L;

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gang gang = (Gang) o;
        return gangId == gang.gangId &&
                gangBalance == gang.gangBalance &&
                gangLevel == gang.gangLevel &&
                maxMembers == gang.maxMembers &&
                maxAllies == gang.maxAllies &&
                maxEnemies == gang.maxEnemies &&
                gangDamage == gang.gangDamage &&
                allyDamage == gang.allyDamage &&
                guardKills == gang.guardKills &&
                prisonerKills == gang.prisonerKills &&
                officerKills == gang.officerKills &&
                deaths == gang.deaths &&
                nameBeenChanged == gang.nameBeenChanged &&
                Objects.equals(gangName, gang.gangName) &&
                Objects.equals(gangPermissions, gang.gangPermissions) &&
                Objects.equals(levelSystem, gang.levelSystem) &&
                Objects.equals(enemies, gang.enemies) &&
                Objects.equals(allies, gang.allies) &&
                Objects.equals(memberInvitations, gang.memberInvitations) &&
                Objects.equals(allyInvitation, gang.allyInvitation) &&
                Objects.equals(membersSorted, gang.membersSorted);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(gangName, gangId, gangBalance, gangLevel, maxMembers, maxAllies, maxEnemies, gangDamage, allyDamage, guardKills, prisonerKills, officerKills, deaths, gangPermissions, levelSystem, nameBeenChanged, enemies, allies, memberInvitations, allyInvitation, membersSorted);
    }

    public Gang(int gangId, String gangName, UUID ownerUuid)
    {
        this.gangId = gangId;
        this.gangName = gangName;
        membersSorted.put(ownerUuid, Rank.LEADER.getValue());
    }

    private String gangName = "";
    private int gangId, gangBalance = 100, gangLevel = 1, maxMembers = 3, maxAllies = 2, maxEnemies = 3, gangDamage = 100, allyDamage = 100;
    private int guardKills = 0, prisonerKills = 0, officerKills = 0, deaths = 0;

    private boolean nameBeenChanged = false;

    public GangPermissions gangPermissions = new GangPermissions();
    private LevelSystem levelSystem = new LevelSystem();

    public LevelSystem getLevelSystem(){
        return levelSystem;
    }

    private Map<Integer, String> enemies = new HashMap<>(), allies = new HashMap<>();
    private List<String> memberInvitations = new ArrayList<>(), allyInvitation = new ArrayList<>();

    public int getGangLevel(){
        return levelSystem.getGangLevel();
    }



    public boolean hasNameBeenChanged(){
        return nameBeenChanged;
    }

    public void setNameBeenChanged(boolean nameBeenChanged){
        this.nameBeenChanged = nameBeenChanged;
    }

    public void removeMoney(int money){
        this.gangBalance -= money;
    }

    public Gang getInstance(){
        return this;
    }

    public Map<Integer, String> getEnemies()
    {
        return enemies;
    }

    public Map<Integer, String> getAllies()
    {
        return allies;
    }

    public int getMaxEnemies()
    {
        return maxEnemies;
    }

    public void setMaxEnemies(int maxEnemies)
    {
        this.maxEnemies = maxEnemies;
    }

    public int getGangDamage(){
        return gangDamage;
    }

    public int getAllyDamage(){
        return allyDamage;
    }

    public void setGangDamage(int gangDamage){
        this.gangDamage = gangDamage;
    }

    public void setAllyDamage(int allyDamage){
        this.allyDamage = allyDamage;
    }

    public GangPermissions getGangPermissions(){
        return gangPermissions;
    }

    public void setGangPermissions(GangPermissions gangPermissions){
        this.gangPermissions = gangPermissions;
    }

    public int getDeaths(){
        return deaths;
    }

    public int getOfficerKills(){
        return officerKills;
    }

    public int getAmountOfMembers(){
        return membersSorted.size();
    }

    public int getAmountOfAllies(){
        return allies.size();
    }

    public int getAmountOfEnemys(){
        return enemies.size();
    }

    public void depositMoney(int amount)
    {
        gangBalance += amount;
    }

    public int getGuardKills()
    {
        return guardKills;
    }

    public void setGuardKills(int guardKills)
    {
        this.guardKills = guardKills;
    }

    public int getPrisonerKills()
    {
        return prisonerKills;
    }

    public void setPrisonerKills(int prisonerKills)
    {
        this.prisonerKills = prisonerKills;
    }

    public void inviteMember(String displayname)
    {
        memberInvitations.add(displayname.toLowerCase());
    }

    public boolean isPlayerInvited(String displayname)
    {
        return memberInvitations.contains(displayname.toLowerCase());
    }

    public void askAlly(String gangName)
    {
        allyInvitation.add(gangName);
    }

    public List<String> getMemberInvitations()
    {
        return memberInvitations;
    }

    public void setMemberInvitations(List<String> memberInvitations)
    {
        this.memberInvitations = memberInvitations;
    }

    public List<String> getAllyInvitation()
    {
        return allyInvitation;
    }

    public void setAllyInvitation(List<String> allyInvitation)
    {
        this.allyInvitation = allyInvitation;
    }

    private Map<UUID, Integer> membersSorted = new LinkedHashMap<>();

    //Sorting players by ID
    //------------
    //owner - 4
    //co-leader - 3
    //mod - 2
    //member - 1

    public Map<UUID, Integer> getMembersSorted()
    {
        return membersSorted;
    }

    public int getGangId()
    {
        return gangId;
    }

    public int getGangBalance()
    {
        return gangBalance;
    }

    public void setGangBalance(int gangBalance)
    {
        this.gangBalance = gangBalance;
    }

    public int getMaxMembers()
    {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers)
    {
        this.maxMembers = maxMembers;
    }

    public int getMaxAllies()
    {
        return maxAllies;
    }

    public void setMaxAllies(int maxAllies)
    {
        this.maxAllies = maxAllies;
    }

    public String getGangName()
    {
        return gangName;
    }

    public void setGangName(String gangName)
    {
        this.gangName = gangName;
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

    public void addMember(UUID playerUuid, String displayName, Rank rank)
    {
        memberInvitations.remove(displayName);
        membersSorted.put(playerUuid, rank.getValue());
    }



}
