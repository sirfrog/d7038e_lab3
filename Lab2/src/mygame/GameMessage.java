/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import com.jme3.network.serializing.Serializer;

/**
 *
 * @author sirfrog
 */
public class GameMessage {
    
    /*
     * The constants below work for me at home but must be changed in order
     * to work elsewhere.
     */
    public static final int portNumber = 6003;
    public static final String host2 = "10.0.1.11";
    public static final String host1 = "10.0.1.3"; 
    public static final String hostName = host2;

    /*
     * This method must be called from all classes that send and/or receives 
     * messages. Both the server and the client classes do this. 
     */
    public static void initialiseSerializables() {
        /*
         * Supply all (yes all) message classes as argument in the method 
         * call below. For every message class M, you should add M.class. 
         * (Put a comma between the arguments.)
         */
        Serializer.registerClass(GameMessage.class);
    }

    // =======================================================================
    // CLIENT -> SERVER
    // =======================================================================
    // -------
    // Infrastructure
    // -------
    @Serializable
    public static class ConnectMessage extends AbstractMessage {

        /*
         * A NetworkMessage contains just a string. 
         */
        private String nickname = "";

        /*
         * Every message class must have a parameterless constructor.
         */
        public ConnectMessage() {
        }

        /*
         * In addition, the class can have any number of other constructors.
         */
        public ConnectMessage(String n) {
            this.nickname = n;
        }
    }
    
    @Serializable
    public static class DisconnectMessage extends AbstractMessage {

        /*
         * A NetworkMessage contains just a string. 
         */

        /*
         * Every message class must have a parameterless constructor.
         */
        public DisconnectMessage() {
        }
    }
    
    @Serializable
    public static class AliveMessage extends AbstractMessage {


        /*
         * Every message class must have a parameterless constructor.
         */
        public AliveMessage() {
        }
    }
    
    // --------------
    // Game messages
    // --------------
    @Serializable
    public static class LaserInputMessage extends AbstractMessage {

        
        public LaserInputMessage() {
        }

    }
    @Serializable
    public static class RotateInputMessage extends AbstractMessage {

        /*
         * A NetworkMessage contains just a string. 
         */
        private boolean r = true;
        private float a = -1f;

        /*
         * Every message class must have a parameterless constructor.
         */
        public RotateInputMessage() {
        }

        /*
         * In addition, the class can have any number of other constructors.
         */
        public RotateInputMessage(boolean r, float a) {
            this.r=r;
            this.a=a;
        }

    }
    
    @Serializable
    public static class FireInputMessage extends AbstractMessage {
        public FireInputMessage() {
        }
    }
    
    // =======================================================================
    // SERVER -> CLIENT
    // =======================================================================
    // ----------
    // Infrastructure
    // ----------
    
    @Serializable
    public static class NewClientAcceptedMessage extends AbstractMessage {   
        
        private String nickname = "";
        private int id = -1;
        
        public NewClientAcceptedMessage() {
        }
        
        public NewClientAcceptedMessage(String n, int id) {
            this.nickname=n;
            this.id=id;
        }
    }
    
    @Serializable
    public static class RejectMessage extends AbstractMessage {
        private int e = -1;
        public RejectMessage() {
        }
        public RejectMessage(int e) {
            this.e = e;
        }
    }
    
    @Serializable
    public static class DisconnectedMessage extends AbstractMessage {
        private int c = -1;
        public DisconnectedMessage() {
        }
        public DisconnectedMessage(int c) {
            this.c=c;
        }
    }
    
    @Serializable
    public static class PrepareMessage extends AbstractMessage {
        
        private Vector3f[] pa;
        private String[] na;
        private float [] ra;
        private boolean[] la;
        
        public PrepareMessage() {
        }
    }
    
    @Serializable
    public static class StartMessage extends AbstractMessage {
        public StartMessage() {
        }
    }
    
    // -----------
    // Concerning game
    // -----------
    @Serializable
    public static class ActivateMessage extends AbstractMessage {
        
        private int id = -1;
        private int index = -1;
        private Vector3f pos;
        private Quaternion rot;
        
        public ActivateMessage() {
        }
        public ActivateMessage(int id, int index, Vector3f pos, Quaternion rot) {
            this.id = id;
            this.index = index;
            this.pos = pos;
            this.rot = rot;
        }
    }
    
    public static class InactiveMessage extends AbstractMessage {
        private int id = -1;
        private int index = -1;
        public InactiveMessage() {
        }
        public InactiveMessage(int id, int index) {
            this.id = id;
            this.index=index;
        }
    }
    
    public static class MoveMessage extends AbstractMessage {
        private int index = -1;
        private Vector3f pos;
        public MoveMessage() {
        }
        public MoveMessage(int index, Vector3f p) {
            this.index=index;
            this.pos = p;
        }
    }
    
    public static class ChangeMessage extends AbstractMessage {
        private int id = -1;
        private int index = -1;
        private Vector3f pos;
        private Quaternion rot;
        public ChangeMessage() {
        }
        public ChangeMessage(int id, int index, Vector3f pos, Quaternion rot) {
            this.id = id;
            this.index = index;
            this.pos = pos;
            this.rot = rot;
        }
    }
    
    public static class AwardMessage extends AbstractMessage {
        private int id = -1;
        private int points = 0;
        public AwardMessage() {
        }
        public AwardMessage(int id, int points) {
            this.id=id;
            this.points=points;
        }
    }
    
    public static class RotateMessage extends AbstractMessage {
        private int id = -1;
        private Quaternion rot;
        public RotateMessage() {
        }
        public RotateMessage(int id, Quaternion rot) {
            this.id = id;
            this.rot = rot;
        }
    }
    
    public static class LaserToggledMessage extends AbstractMessage {
        private int id = -1;
        private boolean b = false;
        public LaserToggledMessage() {
        }
        public LaserToggledMessage(int id, boolean b) {
            this.id=id;
            this.b=b;
        }
    }
    
    public static class CongratulateMessage extends AbstractMessage {
        private int[] winners;
        public CongratulateMessage() {
        }
        public CongratulateMessage(int[] winners) {
            this.winners=winners;
        }
    }
}
