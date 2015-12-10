/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

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

    /*
     * Infrastructure messages
     */
    @Serializable
    public static class ConnectMessage extends AbstractMessage {

        /*
         * A NetworkMessage contains just a string. 
         */
        private String message = "";

        /*
         * Every message class must have a parameterless constructor.
         */
        public ConnectMessage() {
        }

        /*
         * In addition, the class can have any number of other constructors.
         */
        public ConnectMessage(String message) {
            this.message = message;
        }

        /*
         * For each piece of data stored in the message, we add a method 
         * that returns the data.
         */
        public String getMessage() {
            return message;
        }
    }
    
    @Serializable
    public static class DisconnectMessage extends AbstractMessage {

        /*
         * A NetworkMessage contains just a string. 
         */
        private String message = "";

        /*
         * Every message class must have a parameterless constructor.
         */
        public DisconnectMessage() {
        }

        /*
         * In addition, the class can have any number of other constructors.
         */
        public DisconnectMessage(String message) {
            this.message = message;
        }

        /*
         * For each piece of data stored in the message, we add a method 
         * that returns the data.
         */
        public String getMessage() {
            return message;
        }
    }
    
    @Serializable
    public static class AliveMessage extends AbstractMessage {

        /*
         * A NetworkMessage contains just a string. 
         */
        private String message = "";

        /*
         * Every message class must have a parameterless constructor.
         */
        public AliveMessage() {
        }

        /*
         * In addition, the class can have any number of other constructors.
         */
        public AliveMessage(String message) {
            this.message = message;
        }

        /*
         * For each piece of data stored in the message, we add a method 
         * that returns the data.
         */
        public String getMessage() {
            return message;
        }
    }
    
    /*
     * Game message
     */
    @Serializable
    public static class LaserInputMessage extends AbstractMessage {

        /*
         * A NetworkMessage contains just a string. 
         */
        private String message = "";

        /*
         * Every message class must have a parameterless constructor.
         */
        public LaserInputMessage() {
        }

        /*
         * In addition, the class can have any number of other constructors.
         */
        public LaserInputMessage(String message) {
            this.message = message;
        }

        /*
         * For each piece of data stored in the message, we add a method 
         * that returns the data.
         */
        public String getMessage() {
            return message;
        }
    }
    @Serializable
    public static class RotateInputMessage extends AbstractMessage {

        /*
         * A NetworkMessage contains just a string. 
         */
        private String message = "";

        /*
         * Every message class must have a parameterless constructor.
         */
        public RotateInputMessage() {
        }

        /*
         * In addition, the class can have any number of other constructors.
         */
        public RotateInputMessage(String message) {
            this.message = message;
        }

        /*
         * For each piece of data stored in the message, we add a method 
         * that returns the data.
         */
        public String getMessage() {
            return message;
        }
    }
    @Serializable
    public static class FireInputMessage extends AbstractMessage {

        /*
         * A NetworkMessage contains just a string. 
         */
        private String message = "";

        /*
         * Every message class must have a parameterless constructor.
         */
        public FireInputMessage() {
        }

        /*
         * In addition, the class can have any number of other constructors.
         */
        public FireInputMessage(String message) {
            this.message = message;
        }

        /*
         * For each piece of data stored in the message, we add a method 
         * that returns the data.
         */
        public String getMessage() {
            return message;
        }
    }
}
