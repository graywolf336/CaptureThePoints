 package me.dalton.capturethepoints.beans;

import java.util.ArrayList;
import java.util.List;


 /** A Capture Point in a CaptureThePoints arena */
 public class Points {
   private double x;
   private double y;
   private double z;
   private double dir = 0.0D;
   private String name;
   private String pointDirection = null;
   private String controlledByTeam = null;
   private List<String> notAllowedToCaptureTeams = new ArrayList<String>();
   
   /** Sets the X coordinate of this Point */
   public void setX(double x) {
	   this.x = x;
   }
   
   /** Gets the X coordinate of this Point */
   public double getX() {
	   return this.x;
   }
   
   /** Sets the Y coordinate of this Point */
   public void setY(double y) {
	   this.y = y;
   }
   
   /** Gets the Y coordinate of this Point */
   public double getY() {
	   return this.y;
   }
   
   /** Sets the Z coordinate of this Point */
   public void setZ(double z) {
	   this.z = z;
   }
   
   /** Gets the Z coordinate of this Point */
   public double getZ() {
	   return this.z;
   }
   
   /** Sets the direction the players spawn in this Point */
   public void setDir(double dir) {
	   this.dir = dir;
   }
   
   /** Gets the direction the players spawn in this Point */
   public double getDir() {
	   return this.dir;
   }
   
   /** Sets the name of this Point */
   public void setName(String name) {
	   this.name = name;
   }
   
   /** Gets the name of this Point */
   public String getName() {
	   return this.name;
   }
   
   /** Sets the direction the Block in a Capture Point this is [NORTH, EAST, SOUTH, WEST] */
   public void setPointDirection(String direction) {
	   this.pointDirection = direction;
   }
   
   /** Gets the direction the Block in a Capture Point this is [NORTH, EAST, SOUTH, WEST] */
   public String getPointDirection() {
	   return this.pointDirection;
   }
   
   /** Gets which Team controls this Point 
    * @see Team */
   public void setControlledByTeam(String team) {
	   this.controlledByTeam = team;
   }
   
   /** Gets which Team controls this Point 
    * @see Team */
   public String getControlledByTeam() {
	   return this.controlledByTeam;
   }
   
   /** Sets the teams who are not allowed to capture this point **/
   public void setNotAllowedToCaptureTeams(List<String> teams) {
	   this.notAllowedToCaptureTeams = teams;
   }
   
   /** Gets the teams who are not allowed to capture this point**/
   public List<String> getNotAllowedToCaptureTeams() {
	   return this.notAllowedToCaptureTeams;
   }
 }