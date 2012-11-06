package me.dalton.capturethepoints;

import java.sql.*;

/**
  * @author Humsas
*/

public class MysqlConnector {
    private final CaptureThePoints ctp;
    private Connection con;

    public MysqlConnector(CaptureThePoints instance){
        ctp = instance;
    }

    public boolean checkMysqlData() {
        if(ctp.globalConfigOptions.mysqlAddress == null
        		|| ctp.globalConfigOptions.mysqlDatabase == null
        		|| ctp.globalConfigOptions.mysqlPass == null
                || ctp.globalConfigOptions.mysqlUser == null) {
        	return false;
        }

        Statement stmt = null;
		@SuppressWarnings("unused")
		ResultSet rs = null;
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
            ctp.logSevere("Couldn't find the mySQL Driver! Please check your CraftBukkit version to see if it isn't corrupt.");
        }
        
        try {
            con = DriverManager.getConnection("jdbc:mysql://"
            		+ ctp.globalConfigOptions.mysqlAddress
            		+ ":" + ctp.globalConfigOptions.mysqlPort
            		+ "/"
                    + ctp.globalConfigOptions.mysqlDatabase
                    + "?user="
                    + ctp.globalConfigOptions.mysqlUser
                    + "&password=" + ctp.globalConfigOptions.mysqlPass);

            stmt = con.createStatement();
            
            try {
                rs = stmt.executeQuery("SELECT * FROM Arena");
                rs = stmt.executeQuery("SELECT * FROM Simple_block");
                rs = stmt.executeQuery("SELECT * FROM Note_block");
                rs = stmt.executeQuery("SELECT * FROM Spawner_block");
                rs = stmt.executeQuery("SELECT * FROM Item");
                rs = stmt.executeQuery("SELECT * FROM Sign");
            }
            catch (Exception e) {
                stmt.executeUpdate("CREATE TABLE  IF NOT EXISTS `" + ctp.globalConfigOptions.mysqlDatabase + "`.`Arena` ( `name` TEXT NOT NULL , `world` TEXT NOT NULL) ENGINE=MyISAM DEFAULT CHARSET=latin1");
                stmt.executeUpdate("CREATE TABLE  IF NOT EXISTS `" + ctp.globalConfigOptions.mysqlDatabase + "`.`Simple_block` ( `data` INT NOT NULL , `x` INT NOT NULL , `y` INT NOT NULL , `z` INT NOT NULL, `z2` INT NOT NULL, `arena_name` TEXT NOT NULL , `block_type` INT NOT NULL , `id` INT NOT NULL AUTO_INCREMENT, `direction` TEXT NOT NULL , PRIMARY KEY ( `id` )) ENGINE=MyISAM DEFAULT CHARSET=latin1");
                stmt.executeUpdate("CREATE TABLE  IF NOT EXISTS `" + ctp.globalConfigOptions.mysqlDatabase + "`.`Note_block` ( `block_ID` INT NOT NULL , `note_type` INT NOT NULL ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
                stmt.executeUpdate("CREATE TABLE  IF NOT EXISTS `" + ctp.globalConfigOptions.mysqlDatabase + "`.`Spawner_block` (  `block_ID` INT NOT NULL , `creature_type` TEXT NOT NULL , `delay` INT NOT NULL ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
                stmt.executeUpdate("CREATE TABLE  IF NOT EXISTS `" + ctp.globalConfigOptions.mysqlDatabase + "`.`Item` (  `type` INT NOT NULL , `block_ID` INT NOT NULL , `durability` INT NOT NULL, `amount` INT NOT NULL, `place_in_inv` INT NOT NULL, `data` INT NOT NULL ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
                stmt.executeUpdate("CREATE TABLE  IF NOT EXISTS `" + ctp.globalConfigOptions.mysqlDatabase + "`.`Sign` (  `block_ID` INT NOT NULL , `first_line` TEXT NOT NULL , `second_line` TEXT NOT NULL, `third_line` TEXT NOT NULL, `fourth_line` TEXT NOT NULL ) ENGINE=MyISAM DEFAULT CHARSET=latin1");
            }
        }
        catch (Exception e) { e.printStackTrace(); }

        return true;
    }

    public void connectToMySql() {
        if(ctp.globalConfigOptions.enableHardArenaRestore) {
            try {
                Class.forName("com.mysql.jdbc.Driver");  //try to register the mysql jdbc driver
            } catch (Exception e) {
               e.printStackTrace();
               ctp.logSevere("Couldn't find the mySQL Driver! Please check your CraftBukkit version to see if it isn't corrupt.");
            }
            
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + ctp.globalConfigOptions.mysqlAddress + ":" + ctp.globalConfigOptions.mysqlPort + "/"
                        + ctp.globalConfigOptions.mysqlDatabase + "?user=" + ctp.globalConfigOptions.mysqlUser + "&password=" + ctp.globalConfigOptions.mysqlPass);
            } catch (Exception e) {
            	e.printStackTrace();
            	ctp.logSevere("Error connecting to the database, pleaes check your creditals.");
            }
        }
    }

    public void modifyData(String statement) {
        Statement stmt = null;
        try {
            stmt = con.createStatement();

            stmt.executeUpdate(statement);
        } catch (SQLException ex) {
            ex.printStackTrace();
            ctp.logSevere("There was an error somewhere modifing the data in the database, please see the above StackTrace.");
        }
    }


    public ResultSet getData(String statement) {
        Statement stmt = null;
        try {
            stmt = con.createStatement();

            return stmt.executeQuery(statement);
        } catch (SQLException ex) {
            ex.printStackTrace();
            ctp.logSevere("There was an error getting the data from the database, please see the above StackTrace.");
        }
        return null;
    }


    public int getLastInsertedId() {
        try {
            ResultSet rs = getData("SELECT LAST_INSERT_ID();");
            while(rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            ctp.logSevere("There was an error while we tried to get the last inserted id, please see the above StackTrace.");
        }
        return -1;
    }
}