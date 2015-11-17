package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import java.util.List;




/** Sample 2 - How to use nodes as handles to manipulate objects in the scene.
 * You can rotate, translate, and scale objects by manipulating their parent nodes.
 * The Root Node is special: Only what is attached to the Root Node appears in the scene. */
public class Lab2 extends SimpleApplication {
    //Integer constants
    public static final int PLAYINGFIELD_RESOLUTION = 100;
    public static final int CAN_RESOLUTION = 100;
    public static final int CANNONBAL_NUM = 5;
    public static final int CANNONBALL_RESOLUTION = 100;
    public static final int LARGECAN_NUM = 10;
    public static final int LARGECAN_VALUE = 10;
    public static final int MEDIUMCAN_NUM = 6;
    public static final int MEDIUMCAN_VALUE = 20;
    public static final int SMALLCAN_NUM = 3;
    public static final int SMALLCAN_VALUE = 40;
    public static final int CANS_NUM = LARGECAN_NUM + MEDIUMCAN_NUM + 
                                       SMALLCAN_NUM;
    
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
    
    public static final float PLAYINGFIELD_HEIGHT = 0.1f;

    public static void main(String[] args){
        Lab2 app = new Lab2();
        app.start();
    }

    //Variables that are useful to reach from anywhere.

    protected Node playingFieldNode;
    protected Node basePlateNode;
    protected Node supportNode;
    protected Node cannonNode;
    protected Node laserNode;
    protected AudioNode cannonAudioNode;
    
    //protected int reverse = 1;
    //protected int opposite = 1;
    //protected boolean toggle = false; //If whole system spins
    //protected boolean cylinderVisible = true;
    //protected float localscale = 1; //Used for keeping scale

    private void initNodeTree(){
        
        playingFieldNode = new Node("playingFieldNode");
        rootNode.attachChild(playingFieldNode);
        
        basePlateNode = new Node("basePlateNode");
        playingFieldNode.attachChild(basePlateNode);
        
        supportNode = new Node("supportNode");
        basePlateNode.attachChild(supportNode);
        
        cannonNode = new Node("cannonNode");
        supportNode.attachChild(cannonNode);
        
        laserNode = new Node("laserNode");
        cannonNode.attachChild(laserNode);
        
        playingFieldNode.setLocalTranslation(0f, -50f, -150f);
        playingFieldNode.rotate(90*FastMath.DEG_TO_RAD,0,0);
    }

    private void initShapes() {
        
        Cylinder playingFieldCyl = new Cylinder(PLAYINGFIELD_RESOLUTION,
                PLAYINGFIELD_RESOLUTION,PLAYINGFIELD_RADIUS,PLAYINGFIELD_HEIGHT,
                true);
        Geometry playingField = new Geometry("Playing Field", playingFieldCyl);
        Material grass = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        grass.setTexture("ColorMap",
                assetManager.loadTexture("Textures/Terrain/splat/grass.jpg"));
        playingFieldCyl.scaleTextureCoordinates(new Vector2f(60f,60f));
        playingField.setMaterial(grass);
        playingFieldNode.attachChild(playingField);
        
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1,20, -50));
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
        
        // create blue box
        Box box = new Box(1f,9f,4f);
        Geometry blue = new Geometry("Box", box);
        Material mat1 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Blue);
        blue.setMaterial(mat1);
        
        Cylinder cyl = new Cylinder(32,32,2f,3f, true);
        Geometry red = new Geometry("Cylinder", cyl);
        Material mat2 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Red);
        red.setMaterial(mat2);

        Sphere sphere = new Sphere(32,32,3f);
        Geometry yellow = new Geometry("Sphere", sphere);
        sphere.setTextureMode(Sphere.TextureMode.Projected);
        Material mat3 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat3.setColor("Color", ColorRGBA.Yellow);
        yellow.setMaterial(mat3);
        
    }

    private void initKeys() {
        /*
        //Immediate actions:
        inputManager.addMapping("Reverse",  new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("Toggle", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addMapping("Opposite", new KeyTrigger(KeyInput.KEY_O));

        //while pressed
        inputManager.addMapping("Backward",   new KeyTrigger(KeyInput.KEY_B));
        inputManager.addMapping("Forward",  new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping("Shrink", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Grow", new KeyTrigger(KeyInput.KEY_G));

        //When released
        inputManager.addMapping("Disappear", new KeyTrigger(KeyInput.KEY_D));

        inputManager.addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

        // Add the names to the action listener.
        inputManager.addListener(actionListener, "Reverse","Toggle","Opposite", "Disappear","Shoot");
        inputManager.addListener(analogListener,"Backward","Forward","Shrink","Grow");
        */
  };

    private void initSound() {
        /*
        audio_gun = new AudioNode(assetManager, "Sound/Effects/Gun.wav", false);
        audio_gun.setPositional(true);
        audio_gun.setLooping(false);
        audio_gun.setVolume(1);
        soundNode.attachChild(audio_gun);
        */
    }
    
    protected void initCrossHairs() {
        /*
        setDisplayStatView(false);
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
            settings.getWidth() / 2 - ch.getLineWidth()/2, settings.getHeight() / 2 + ch.getLineHeight()/2, 0);
        guiNode.attachChild(ch);
        */
    }
    
    private Geometry makeSnowball(){
        /*
        Sphere ball = new Sphere(30, 30, 0.2f);
        Geometry snowball = new Geometry("Snowball", ball);
        Material snow = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        snow.setColor("Color", ColorRGBA.White);
        snowball.setMaterial(snow);
        return snowball;
        */
        return null;
    }
    
    @Override
    public void simpleInitApp() {
        
        initNodeTree();
        initShapes();
        /*
        initKeys();
        initSound();
        initCrossHairs();
        */
    };

private ActionListener actionListener = new ActionListener() {
    public void onAction(String name, boolean keyPressed, float tpf) {
        /*
        if (name.equals("Reverse") && keyPressed) {
            reverse = reverse *-1; //This will flip between 1 and -1 always.
        }
        if (name.equals("Opposite") && keyPressed) {
            opposite = opposite *-1; //This will flip between 1 and -1 always.
        }
        if (name.equals("Toggle") && keyPressed) {
            toggle = !toggle; //Toggles between on and off.
        }
        if (name.equals("Disappear") && !keyPressed) {
            if (cylinderVisible){
                cylinderVisible = false;
                cylinderNode.removeFromParent();
                audio_gun.playInstance();
            }
            else {
                cylinderVisible = true;
                pivot.attachChild(cylinderNode);
            }
        }
        if (name.equals("Shoot") && !keyPressed) {
            // 1. Reset results list.
            CollisionResults results = new CollisionResults();
            // 2. Aim the ray from cam loc to cam direction.
            Ray ray = new Ray(cam.getLocation(), cam.getDirection());
            // 3. Collect intersections between Ray and Shootables in results list.
            rootNode.collideWith(ray, results);
            // 5. Use the results (we mark the hit object)
            if (results.size() > 0) {
                // The closest collision point is what was truly hit:
                CollisionResult closest = results.getClosestCollision();
                Node hitNode = closest.getGeometry().getParent();
                Geometry newSnowball = makeSnowball();
                Vector3f hitPoint = new Vector3f(0,0,0);
                hitNode.worldToLocal(closest.getContactPoint(), hitPoint);
                newSnowball.setLocalTranslation(hitPoint);
                hitNode.attachChild(newSnowball);
            }
        }
        */
    }
};


/*
private AnalogListener analogListener = new AnalogListener() {
    
    //inputManager.addListener(analogListener,"Backward","Forward","Shrink","Grow");
    public void onAnalog(String name, float value, float tpf) {
        if (name.equals("Backward")) {
            if (toggle) {
                center.rotate(0, 0, 1f*tpf);
            } else {
                boxNode.rotate(0, 0, 1f*tpf);
            }
        }
        if (name.equals("Forward")) {
            if (toggle) {
                center.rotate(0, 0, -1f*tpf);
            } else {
                boxNode.rotate(0, 0, -1f*tpf);
            }
        }
        //Consider only scaling the sphere and then
        //scale position of snowballs.
        if (name.equals("Grow")) {
            float scalefactor = (1f+tpf);
            localscale += tpf;
            //sphereNode.getChild("Sphere").setLocalScale(cpt);
            sphereNode.getChild("Sphere").scale(scalefactor);
            
            List<Spatial> allChildren = sphereNode.getChildren();
            
            for(int i = 0; i < allChildren.size(); i++){    
                Spatial child = allChildren.get(i);
                
                if (child.getName().equals("Snowball")){
                    
                    Vector3f distance = child.getLocalTranslation();
                    Vector3f newDistance = new Vector3f(0,0,0);
                    distance.mult(scalefactor, newDistance);
                    child.setLocalTranslation(newDistance);
                } 
            }
        }
        if (name.equals("Shrink")) {
            if (!(localscale <= 0.01f)){ //So that node does not invert
                float scalefactor = (1f-tpf);
                localscale -= tpf;
                sphereNode.getChild("Sphere").scale(scalefactor);
                //sphereNode.getChild("Sphere").setLocalScale(cpt);
                List<Spatial> allChildren = sphereNode.getChildren();
                
                for(int i = 0; i < allChildren.size(); i++){
                    Spatial child = allChildren.get(i);
                    if (child.getName().equals("Snowball")){
                    
                        Vector3f distance = child.getLocalTranslation();
                        Vector3f newDistance = new Vector3f(0,0,0);
                        distance.mult(scalefactor, newDistance);
                        child.setLocalTranslation(newDistance);
                    } 
                }
            }
        }
    }
    
};
*/

    @Override
    public void simpleUpdate(float tpf) {
        /*
        // make the player rotate:
        center.rotate(0, 0.5f*tpf*reverse, 0);
        pivot.rotate(0,0,2.5f*tpf*opposite);
        listener.setLocation(cam.getLocation());
        listener.setRotation(cam.getRotation());
        */
    }
}