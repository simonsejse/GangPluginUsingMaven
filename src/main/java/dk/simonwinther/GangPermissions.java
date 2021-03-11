package dk.simonwinther;

import dk.simonwinther.constants.Rank;

import javax.persistence.*;
import java.io.Serializable;


public class GangPermissions implements Serializable
{

    private final long serialVersionUID = 98798793217321L;

    public GangPermissions(int gangID){
        this.gangID = gangID;
    }

    public GangPermissions(){

    }

    private int gangID;

    public Rank accessToGangShop = Rank.CO_LEADER
            , accessToTransferMoney = Rank.OFFICER
            , accessToTransferItems = Rank.OFFICER
            , accessToKick = Rank.LEADER
            , accessToDeposit = Rank.MEMBER
            , accessToEnemy = Rank.OFFICER
            , accessToAlly = Rank.OFFICER
            , accessToInvite = Rank.CO_LEADER
            , accessToLevelUp = Rank.CO_LEADER
            , accessToGangChat = Rank.MEMBER
            , accessToAllyChat = Rank.OFFICER;


    public boolean accessToToilets = false;
    public boolean accessToFarm = false;
    public boolean accessToLab = false;


    public int getGangID() {
        return gangID;
    }

    public void setGangID(int gangID) {
        this.gangID = gangID;
    }

    public Rank getAccessToGangShop() {
        return accessToGangShop;
    }

    public void setAccessToGangShop(Rank accessToGangShop) {
        this.accessToGangShop = accessToGangShop;
    }

    public Rank getAccessToTransferMoney() {
        return accessToTransferMoney;
    }

    public void setAccessToTransferMoney(Rank accessToTransferMoney) {
        this.accessToTransferMoney = accessToTransferMoney;
    }

    public Rank getAccessToTransferItems() {
        return accessToTransferItems;
    }

    public void setAccessToTransferItems(Rank accessToTransferItems) {
        this.accessToTransferItems = accessToTransferItems;
    }

    public Rank getAccessToKick() {
        return accessToKick;
    }

    public void setAccessToKick(Rank accessToKick) {
        this.accessToKick = accessToKick;
    }

    public Rank getAccessToDeposit() {
        return accessToDeposit;
    }

    public void setAccessToDeposit(Rank accessToDeposit) {
        this.accessToDeposit = accessToDeposit;
    }

    public Rank getAccessToEnemy() {
        return accessToEnemy;
    }

    public void setAccessToEnemy(Rank accessToEnemy) {
        this.accessToEnemy = accessToEnemy;
    }

    public Rank getAccessToAlly() {
        return accessToAlly;
    }

    public void setAccessToAlly(Rank accessToAlly) {
        this.accessToAlly = accessToAlly;
    }

    public Rank getAccessToInvite() {
        return accessToInvite;
    }

    public void setAccessToInvite(Rank accessToInvite) {
        this.accessToInvite = accessToInvite;
    }

    public Rank getAccessToLevelUp() {
        return accessToLevelUp;
    }

    public void setAccessToLevelUp(Rank accessToLevelUp) {
        this.accessToLevelUp = accessToLevelUp;
    }

    public Rank getAccessToGangChat() {
        return accessToGangChat;
    }

    public void setAccessToGangChat(Rank accessToGangChat) {
        this.accessToGangChat = accessToGangChat;
    }

    public Rank getAccessToAllyChat() {
        return accessToAllyChat;
    }

    public void setAccessToAllyChat(Rank accessToAllyChat) {
        this.accessToAllyChat = accessToAllyChat;
    }

    public boolean isAccessToToilets() {
        return accessToToilets;
    }

    public void setAccessToToilets(boolean accessToToilets) {
        this.accessToToilets = accessToToilets;
    }

    public boolean isAccessToFarm() {
        return accessToFarm;
    }

    public void setAccessToFarm(boolean accessToFarm) {
        this.accessToFarm = accessToFarm;
    }

    public boolean isAccessToLab() {
        return accessToLab;
    }

    public void setAccessToLab(boolean accessToLab) {
        this.accessToLab = accessToLab;
    }
}
