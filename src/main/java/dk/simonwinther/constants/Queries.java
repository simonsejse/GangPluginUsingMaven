package dk.simonwinther.constants;

public enum Queries {

    UPDATE_GANGS("create table gangs " +
            "(Id integer not null, AllyDamage integer not null, Deaths integer not null, Balance integer not null, " +
            "GangDamage integer not null, Level integer not null, Name varchar(255) not null, " +
            "GKills integer not null, MaxAllies integer not null, MaxEnemies integer not null, " +
            "MaxMembers integer not null, NameChanged boolean not null, OKills integer not null, " +
            "PKills integer not null, primary key (Id))");




    public String sql;

    Queries(String sql){
        this.sql = sql;
    }

}
