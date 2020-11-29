package dk.simonwinther;

import dk.simonwinther.enums.Rank;

import java.io.Serializable;

public class GangPermissions implements Serializable
{
    private final long serialVersionUID = 98798793217321L;

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


    public boolean accessToToilets = false, accessToFarm = false, accessToLab = false;

    public void setAccessToGangShop(Rank accessToGangShop)
    {
        this.accessToGangShop = accessToGangShop;
    }

    public void setAccessToTransferMoney(Rank accessToTransferMoney)
    {
        this.accessToTransferMoney = accessToTransferMoney;
    }

    public void setAccessToTransferItems(Rank accessToTransferItems)
    {
        this.accessToTransferItems = accessToTransferItems;
    }

    public void setAccessToKick(Rank accessToKick)
    {
        this.accessToKick = accessToKick;
    }

    public void setAccessToDeposit(Rank accessToDeposit)
    {
        this.accessToDeposit = accessToDeposit;
    }

    public void setAccessToEnemy(Rank accessToEnemy)
    {
        this.accessToEnemy = accessToEnemy;
    }

    public void setAccessToAlly(Rank accessToAlly)
    {
        this.accessToAlly = accessToAlly;
    }

    public void setAccessToInvite(Rank accessToInvite)
    {
        this.accessToInvite = accessToInvite;
    }

    public void setAccessToLevelUp(Rank accessToLevelUp)
    {
        this.accessToLevelUp = accessToLevelUp;
    }

    public void setAccessToGangChat(Rank accessToGangChat)
    {
        this.accessToGangChat = accessToGangChat;
    }

    public void setAccessToAllyChat(Rank accessToAllyChat)
    {
        this.accessToAllyChat = accessToAllyChat;
    }

    public void setAccessToToilets(boolean accessToToilets)
    {
        this.accessToToilets = accessToToilets;
    }

    public void setAccessToFarm(boolean accessToFarm)
    {
        this.accessToFarm = accessToFarm;
    }

    public void setAccessToLab(boolean accessToLab)
    {
        this.accessToLab = accessToLab;
    }
}
