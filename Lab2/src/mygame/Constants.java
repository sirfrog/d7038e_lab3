/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

/**
 *
 * @author sirfrog
 */
public class Constants {
    //Integer constants
    public static final int PLAYINGFIELD_RESOLUTION = 100;
    public static final int CAN_RESOLUTION = 100;
    public static final int CANNONBALL_NUM = 5;
    public static final int CANNONBALL_RESOLUTION = 100;
    public static final int LARGECAN_NUM = 10;
    public static final int LARGECAN_VALUE = 10;
    public static final int MEDIUMCAN_NUM = 6;
    public static final int MEDIUMCAN_VALUE = 20;
    public static final int SMALLCAN_NUM = 3;
    public static final int SMALLCAN_VALUE = 40;
    public static final int CANS_NUM = LARGECAN_NUM + MEDIUMCAN_NUM + 
                                       SMALLCAN_NUM;
    public static final int MAX_CHARS = 8;
    public static final int MAX_PLAYERS = 20;
    
    //Float constants
    public static final float DEAD_MARGIN = 1f;
    public static final float START_TIME = 30f;
    public static final float PLAYINGFIELD_RADIUS = 200f;
    public static final float SMALLCAN_RADIUS = 3f;
    public static final float SMALLCAN_HEIGHT = 10f;
    public static final float MEDIUMCAN_RADIUS = 4f;
    public static final float MEDIUMCAN_HEIGHT = 15f;
    public static final float LARGECAN_RADIUS = 5f;
    public static final float LARGECAN_HEIGHT = 20f;
    public static final float MAXIMAL_CAN_RADIUS = LARGECAN_RADIUS;
    public static final float CANNON_SAFETYDISTANCE = 20f;
    public static final float SAFETY_MARGIN = 2f * MAXIMAL_CAN_RADIUS + 
                                              CANNON_SAFETYDISTANCE;
    public static final float CANNONBALL_RADIUS = 1.1f * MAXIMAL_CAN_RADIUS;
    public static final float CANNONBALL_SPEED = 80f;
    public static final float CANNON_BARREL_RADIUS = CANNONBALL_RADIUS;
    public static final float CANNON_BARREL_LENGTH = MAXIMAL_CAN_RADIUS +
                                                     CANNON_SAFETYDISTANCE;
    public static final float CANNON_SUPPORT_RADIUS = 2.1f * CANNON_BARREL_RADIUS;
    public static final float CANNON_SUPPORT_HEIGHT = 2.4f * CANNON_BARREL_RADIUS;
    public static final float CANNON_BASE_RADIUS = 3f * CANNON_BARREL_RADIUS;
    public static final float CANNON_BASE_HEIGHT = 3f * CANNON_BARREL_RADIUS;
    public static final float CANNON_ROTATION_SPEED = 20f;
    public static final float TIMEOUT = 5.0f;
    
    
    //My own constants.
    public static final float PLAYINGFIELD_HEIGHT = 2.3f * CANNON_BARREL_RADIUS;
    public static final float LASER_SIDE = 0.3f;
    public static final float LASER_LENGTH = PLAYINGFIELD_RADIUS *2f;
    //A small fudge factor to ensure that cannons are not placed too closely.
    public static final float STACKING_MARGIN = CANNON_BARREL_LENGTH +0.5f;
}
