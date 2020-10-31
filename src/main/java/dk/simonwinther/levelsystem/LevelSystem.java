package dk.simonwinther.levelsystem;

import org.bukkit.Material;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LevelSystem implements Serializable
{
    private final long serialVersionUID = 12387213612321L;

    //bread = 0, sugar = 0,ironSword = 0, ironHelmet = 0, ironChestPlate = 0, ironLegs = 0, ironBoots = 0, diamondSword = 0, diamondHelmet = 0, diamondChest = 0, diamondLegs = 0, diamondBoots = 0,
    private int gangLevel = 1, paidForQuest = 0;

    private Map<Material, Integer> itemsMap = new HashMap() {{
        put(Material.BREAD, 0);
        put(Material.SUGAR, 0);
        put(Material.IRON_SWORD, 0);
        put(Material.IRON_HELMET, 0);
        put(Material.IRON_CHESTPLATE, 0);
        put(Material.IRON_LEGGINGS, 0);
        put(Material.IRON_BOOTS, 0);
        put(Material.DIAMOND_SWORD, 0);
        put(Material.DIAMOND_HELMET, 0);
        put(Material.DIAMOND_CHESTPLATE, 0);
        put(Material.DIAMOND_LEGGINGS, 0);
        put(Material.DIAMOND_BOOTS, 0);
    }};

    public Integer getValueOfMaterial(Material material) {
        return itemsMap.get(material);
    }

    public void addValue(Material material, Integer integer){
        itemsMap.compute(material, (oldMat, oldNum) -> oldNum + integer);
    }

    public Map<Material, Integer> getItemsMap(){
        return itemsMap;
    }

    public void setItemsMap(Map<Material, Integer> itemsMap){
        this.itemsMap = itemsMap;
    }

    public int getPaidForQuest(){
        return this.paidForQuest;
    }

    public void setPaidForQuest(int paidForQuest){
        this.paidForQuest = paidForQuest;
    }

    public LevelSystem(){

    }

    public int getGangLevel()
    {
        return gangLevel;
    }

    public void setGangLevel(int gangLevel)
    {
        this.gangLevel = gangLevel;
    }


}
