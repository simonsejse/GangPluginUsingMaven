package dk.simonwinther.levelsystem;

import org.bukkit.Material;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Entity
public class LevelSystem implements Serializable
{


    public LevelSystem(int gangID){
        this.gangID = gangID;
    }

    public LevelSystem(){

    }

    private final long serialVersionUID = 12387213612321L;


    private int gangID;
    private int gangLevel = 1;
    private int paidForQuest = 0;

    private Map<Material, Integer> itemsMap = new HashMap<>();


    {
        itemsMap.put(Material.BREAD, 0);
        itemsMap.put(Material.SUGAR, 0);
        itemsMap.put(Material.IRON_SWORD, 0);
        itemsMap.put(Material.IRON_HELMET, 0);
        itemsMap.put(Material.IRON_CHESTPLATE, 0);
        itemsMap.put(Material.IRON_LEGGINGS, 0);
        itemsMap.put(Material.IRON_BOOTS, 0);
        itemsMap.put(Material.DIAMOND_SWORD, 0);
        itemsMap.put(Material.DIAMOND_HELMET, 0);
        itemsMap.put(Material.DIAMOND_CHESTPLATE, 0);
        itemsMap.put(Material.DIAMOND_LEGGINGS, 0);
        itemsMap.put(Material.DIAMOND_BOOTS, 0);
    }

    public Integer getValueOfMaterial(Material material) {
        return itemsMap.get(material);
    }

    public void addValue(Material material, Integer integer){
        itemsMap.compute(material, (oldMat, oldNum) -> oldNum + integer);
    }


    public long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getGangID() {
        return gangID;
    }

    public void setGangID(int gangID) {
        this.gangID = gangID;
    }

    public int getGangLevel() {
        return gangLevel;
    }

    public void setGangLevel(int gangLevel) {
        this.gangLevel = gangLevel;
    }

    public int getPaidForQuest() {
        return paidForQuest;
    }

    public void setPaidForQuest(int paidForQuest) {
        this.paidForQuest = paidForQuest;
    }

    public Map<Material, Integer> getItemsMap() {
        return itemsMap;
    }

    public void setItemsMap(Map<Material, Integer> itemsMap) {
        this.itemsMap = itemsMap;
    }
}
