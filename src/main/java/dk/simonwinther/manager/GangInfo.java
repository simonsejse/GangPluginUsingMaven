package dk.simonwinther.manager;

public class GangInfo {

    private int gangID;
    private String gangName;

    public GangInfo(int gangID, String gangName){
        this.gangID = gangID;
        this.gangName = gangName;
    }

    public int getGangID() {
        return gangID;
    }

    public String getGangName() {
        return gangName;
    }

    public void setGangName(String gangName) {
        this.gangName = gangName;
    }
}
