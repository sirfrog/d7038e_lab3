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

import mygame.Constants;
import mygame.Lab3;
import mygame.Lab3_server;

/**
 *
 * @author sirfrog
 */
public class GameMessage {
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
    
    private abstract class HandleableMessage extends AbstractMessage {
        abstract public void handle();
    }
    // =======================================================================
    // CLIENT -> SERVER
    // =======================================================================
    // -------
    // Infrastructure
    // -------
    @Serializable
    public static class ConnectMessage extends HandleableMessage {

        /*
         * A NetworkMessage contains just a string. 
         */
        private String nickname = "";
        private Lab3 client;

        /*
         * Every message class must have a parameterless constructor.
         */
        public ConnectMessage() {
        }

        /*
         * In addition, the class can have any number of other constructors.
         */
        public ConnectMessage(String n, Lab3 client) {
            this.nickname = n;
            this.client = client;
        }
        
        @Override
        public void handle(){
            
        }
    }
    
    @Serializable
    public static class DisconnectMessage extends HandleableMessage {

        /*
         * A NetworkMessage contains just a string. 
         */

        /*
         * Every message class must have a parameterless constructor.
         */
        public DisconnectMessage() {
        }

        @Override
        public void handle() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    @Serializable
    public static class AliveMessage extends HandleableMessage {


        /*
         * Every message class must have a parameterless constructor.
         */
        public AliveMessage() {
        }

        @Override
        public void handle() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    // --------------
    // Game messages
    // --------------
    @Serializable
    public static class LaserInputMessage extends HandleableMessage {

        
        public LaserInputMessage() {
        }

        @Override
        public void handle() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }
    @Serializable
    public static class RotateInputMessage extends HandleableMessage {

        /*
         * A NetworkMessage contains just a string. 
         */
        private boolean r = true;
        private float a = -1f;
        private Lab3 client;

        /*
         * Every message class must have a parameterless constructor.
         */
        public RotateInputMessage() {
        }

        /*
         * In addition, the class can have any number of other constructors.
         */
        public RotateInputMessage(boolean r, float a, Lab3 client) {
            this.r=r;
            this.a=a;
            this.client=client;
        }

        @Override
        public void handle() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }
    
    @Serializable
    public static class FireInputMessage extends HandleableMessage {
        public FireInputMessage() {
        }

        @Override
        public void handle() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    // =======================================================================
    // SERVER -> CLIENT
    // =======================================================================
    // ----------
    // Infrastructure
    // ----------
    
    @Serializable
    public static class NewClientAcceptedMessage extends HandleableMessage {   
        
        private String nickname = "";
        private int id = -1;
        private Lab3_server server;
        
        public NewClientAcceptedMessage() {
        }
        
        public NewClientAcceptedMessage(String n, int id, Lab3_server server) {
            this.nickname=n;
            this.id=id;
            this.server = server;
        }

        @Override
        public void handle() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    @Serializable
    public static class RejectMessage extends HandleableMessage {
        private int e = -1;
        private Lab3_server server;
        public RejectMessage() {
        }
        public RejectMessage(int e, Lab3_server server) {
            this.e = e;
            this.server=server;
        }

        @Override
        public void handle() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    @Serializable
    public static class DisconnectedMessage extends HandleableMessage {
        private int c = -1;
        private Lab3_server server;
        public DisconnectedMessage() {
        }
        public DisconnectedMessage(int c, Lab3_server server) {
            this.c=c;
            this.server = server;
        }

        @Override
        public void handle() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    
    @Serializable
    public static class PrepareMessage extends HandleableMessage {
        
        private Vector3f[] pa;
        private String[] na;
        private float [] ra;
        private boolean[] la;
        private Lab3_server server;
        
        public PrepareMessage() {
        }
        public PrepareMessage(Vector3f[] pa, String[] na, float[] ra, 
                boolean[] la, Lab3_server server){
            this.pa = pa;
            this.na=na;
            this.ra=ra;
            this.la=la;
            this.server=server;
        }
        
        @Override
        public void handle() {
            System.out.println("BOO");
        }
    }
    
    @Serializable
    public static class StartMessage extends HandleableMessage {
        public StartMessage() {
        }

        @Override
        public void handle() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    // -----------
    // Concerning game
    // -----------
    @Serializable
    public static class ActivateMessage extends HandleableMessage {
        
        private int id = -1;
        private int index = -1;
        private Vector3f pos;
        private Quaternion rot;
        private Lab3_server server;
        
        public ActivateMessage() {
        }
        public ActivateMessage(int id, int index, Vector3f pos, Quaternion rot,
                Lab3_server server) {
            this.id = id;
            this.index = index;
            this.pos = pos;
            this.rot = rot;
            this.server = server;
        }

        @Override
        public void handle() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    public static class InactiveMessage extends HandleableMessage {
        private int id = -1;
        private int index = -1;
        private Lab3_server server;
        public InactiveMessage() {
        }
        public InactiveMessage(int id, int index, Lab3_server server) {
            this.id = id;
            this.index=index;
            this.server=server;
        }

        @Override
        public void handle() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    public static class MoveMessage extends HandleableMessage {
        private int index = -1;
        private Vector3f pos;
        private Lab3_server server;
        public MoveMessage() {
        }
        public MoveMessage(int index, Vector3f p, Lab3_server server) {
            this.index=index;
            this.pos = p;
            this.server=server;
        }

        @Override
        public void handle() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    public static class ChangeMessage extends HandleableMessage {
        private int id = -1;
        private int index = -1;
        private Vector3f pos;
        private Quaternion rot;
        private Lab3_server server;
        public ChangeMessage() {
        }
        public ChangeMessage(int id, int index, Vector3f pos, Quaternion rot,
                Lab3_server server) {
            this.id = id;
            this.index = index;
            this.pos = pos;
            this.rot = rot;
            this.server = server;
     
        @Override
        public void handle() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
   }
    }
    
    public static class AwardMessage extends HandleableMessage {
        private int id = -1;
        private int points = 0;
        private Lab3_server server;
        public AwardMessage() {
        }
        public AwardMessage(int id, int points, Lab3_server server) {
            this.id=id;
            this.points=points;
            this.server=server;
        }
    }
    
    public static class RotateMessage extends HandleableMessage {
        private int id = -1;
        private Quaternion rot;
        private Lab3_server server;
        public RotateMessage() {
        }
        public RotateMessage(int id, Quaternion rot, Lab3_server server) {
            this.id = id;
            this.rot = rot;
            this.server = server;
        }
    }
    
    public static class LaserToggledMessage extends HandleableMessage {
        private int id = -1;
        private boolean b = false;
        private Lab3_server server;
        public LaserToggledMessage() {
        }
        public LaserToggledMessage(int id, boolean b, Lab3_server server) {
            this.id=id;
            this.b=b;
            this.server = server;
        }
    }
    
    public static class CongratulateMessage extends HandleableMessage {
        private int[] winners;
        private Lab3_server server;
        public CongratulateMessage() {
        }
        public CongratulateMessage(int[] winners, Lab3_server server) {
            this.winners=winners;
            this.server = server;
        }
    }
}
