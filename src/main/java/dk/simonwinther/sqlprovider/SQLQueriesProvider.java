package dk.simonwinther.sqlprovider;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SQLQueriesProvider
{

    private final static String CREATE_USERS_TABLE = "CREATE TABLE `users` (\n" +
            "\t`Uuid` VARCHAR(255) NOT NULL,\n" +
            "\t`GangId` INT(11) NOT NULL,\n" +
            "\tPRIMARY KEY (`Uuid`)\n" +
            ");";


    private final static String CREATE_MEMBER_INVITATION_TABLE = "CREATE TABLE `memberInvitations` (\n" +
            "\t`GangId` INT(11) DEFAULT NULL,\n" +
            "\t`InvitedGangName` VARCHAR(255) DEFAULT NULL\n" +
            ");";

    private final static String CREATE_MEMBERS_TABLE = "CREATE TABLE `gangMembers` (\n" +
            "\t`GangId` INT(11) DEFAULT NULL,\n" +
            "\t`Uuid` VARCHAR(255) DEFAULT NULL,\n" +
            "\t`Rank` INT(11) DEFAULT NULL,\n" +
            "\tPRIMARY KEY (`Uuid`)\n" +
            ");";

    private final static String CREATE_GANG_ALLIES_TABLE = "CREATE TABLE `gangAllies` (\n" +
            "\t`GangId` INT(11) DEFAULT NULL,\n" +
            "\t`AllyGangId` INT(11) DEFAULT NULL,\n" +
            "\t`AllyGangName` INT(255) DEFAULT NULL\n" +
            ");";

    private final static String CREATE_GANG_PERMISSIONS_TABLE = "CREATE TABLE `gangPermissions` (\n" +
            "\t`GangId` INT(11),\n" +
            "\t`AccessToGangShop` VARCHAR(100) DEFAULT NULL,\n" +
            "\t`AccessToTransferMoney` VARCHAR(100) DEFAULT NULL,\n" +
            "\t`AccessToTransferItems` VARCHAR(100) DEFAULT NULL,\n" +
            "\t`AccessToKick` VARCHAR(100) DEFAULT NULL,\n" +
            "\t`AccessToDeposit` VARCHAR(100) DEFAULT NULL,\n" +
            "\t`AccessToEnemy` VARCHAR(100) DEFAULT NULL,\n" +
            "\t`AccessToAlly` VARCHAR(100) DEFAULT NULL,\n" +
            "\t`AccessToInvite` VARCHAR(100) DEFAULT NULL,\n" +
            "\t`AccessToLevelUp` VARCHAR(100) DEFAULT NULL,\n" +
            "\t`AccessToGangChat` VARCHAR(100) DEFAULT NULL,\n" +
            "\t`AccessToAllyChat` VARCHAR(100) DEFAULT NULL\n" +
            ");";

    private final static String CREATE_GANG_TABLE = "CREATE TABLE `gangs` (\n" +
            "\t`GangId` INT(11) DEFAULT NULL,\n" +
            "\t`GangName` VARCHAR(255) DEFAULT NULL,\n" +
            "\t`GangLevel` INT(11) DEFAULT NULL,\n" +
            "\t`MaxMembers` INT(11) DEFAULT NULL,\n" +
            "\t`MaxAllies` INT(11) DEFAULT NULL,\n" +
            "\t`MaxEnemies` INT(11) DEFAULT NULL,\n" +
            "\t`GangDamage` INT(11) DEFAULT NULL,\n" +
            "\t`AllyDamage` INT(11) DEFAULT NULL,\n" +
            "\t`GuardKills` INT(11) DEFAULT NULL,\n" +
            "\t`PrisonerKills` INT(11) DEFAULT NULL,\n" +
            "\t`OfficerKills` INT(11) DEFAULT NULL,\n" +
            "\t`Deaths` INT(11) DEFAULT NULL,\n" +
            "\t`NameChangedBefore` BOOLEAN(11) DEFAULT NULL,\n" +
            "\tPRIMARY KEY (`GangId`)\n" +
            ");";

    private final static String CREATE_ALLY_INVITATION_TABLE = "CREATE TABLE `allyInvitations` (\n" +
            "\t`GangId` INT(11),\n" +
            "\t`InvitedGangName` VARCHAR(100)\n" +
            ");";

    private final static String CREATE_ENEMIES_TABLE = "CREATE TABLE `enemies` (\n" +
            "\t`GangId` INT(11),\n" +
            "\t`EnemyGangId` INT(11),\n" +
            "\t`EnemyGangName` VARCHAR(255)\n" +
            ");";

    private final static String CREATE_LEVEL_SYSTEM_TABLE = "CREATE TABLE `enemies` (\n" +
            "\t`GangId` INT(11),\n" +
            "\t`Bread` INT(11),\n" +
            "\t`Sugar` INT(11),\n" +
            "\t`IronSword` INT(11),\n" +
            "\t`IronHelmet` INT(11),\n" +
            "\t`IronChestplate` INT(11),\n" +
            "\t`IronLeggings` INT(11),\n" +
            "\t`IronBoots` INT(11),\n" +
            "\t`DiamondSword` INT(11),\n" +
            "\t`DiamondHelmet` INT(11),\n" +
            "\t`DiamondChestplate` INT(11),\n" +
            "\t`DiamondLeggings` INT(11),\n" +
            "\t`DiamondBoots` INT(11)\n" +
            ");";

    private final List<String> SQL_TABLES = Arrays.asList(
            CREATE_USERS_TABLE,
            CREATE_MEMBER_INVITATION_TABLE,
            CREATE_MEMBERS_TABLE,
            CREATE_GANG_ALLIES_TABLE,
            CREATE_GANG_PERMISSIONS_TABLE,
            CREATE_GANG_TABLE,
            CREATE_ALLY_INVITATION_TABLE,
            CREATE_ENEMIES_TABLE,
            CREATE_LEVEL_SYSTEM_TABLE
    );

    public List<String> getTables(){
        return this.SQL_TABLES;
    }

}
