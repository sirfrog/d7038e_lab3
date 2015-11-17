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
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
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

    public static void main(String[] args){
        Lab2 app = new Lab2();
        app.start();
    }

    protected Node center;
    protected Node boxNode;
    protected Node pivot;
    protected Node sphereNode;
    protected Node cylinderNode;
    protected Node soundNode;
    protected AudioNode audio_gun;
    //protected Geometry snowball;
    
    protected int reverse = 1;
    protected int opposite = 1;
    protected boolean toggle = false; //If whole system spins
    protected boolean cylinderVisible = true;
    protected float localscale = 1; //Used for keeping scale

    private void initNodeTree(){

        //Create node. This should be at (0,0,0).
        center = new Node("center");
        rootNode.attachChild(center);


        pivot = new Node("pivot");
        center.attachChild(pivot);
        
        boxNode = new Node("boxNode");
        center.attachChild(boxNode);

        sphereNode = new Node("sphereNode");
        pivot.attachChild(sphereNode);


        cylinderNode = new Node("cylinderNode");
        pivot.attachChild(cylinderNode);
        
        soundNode = new Node("audioNode");
        center.attachChild(soundNode);
        
    }

    private void initShapes() {
        // create blue box
        Box box = new Box(1f,9f,4f);
        Geometry blue = new Geometry("Box", box);
        Material mat1 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Blue);
        blue.setMaterial(mat1);
        /*
        Box box2 = new Box(0.1f,0.1f,0.1f);
        Geometry green = new Geometry("Box2", box2);
        Material matg = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        matg.setColor("Color", ColorRGBA.Green);
        green.setMaterial(matg);
        */
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
        
        /** Attach the two boxes to the *pivot* node. (And transitively to the root node.) */
        boxNode.attachChild(blue);
        cylinderNode.attachChild(red);
        sphereNode.attachChild(yellow);
        //soundNode.attachChild(green);
        center.move(0, 0, -20f);
        pivot.move(8f, 0, 0);
        sphereNode.move(0, 3f, 0);
        cylinderNode.move(0, -2f, 0);
        soundNode.move(20f, 0, 0);
    }

    private void initKeys() {
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

  };

    private void initSound() {
        audio_gun = new AudioNode(assetManager, "Sound/Effects/Gun.wav", false);
        audio_gun.setPositional(true);
        audio_gun.setLooping(false);
        audio_gun.setVolume(1);
        soundNode.attachChild(audio_gun);
    }
    
    protected void initCrossHairs() {
    setDisplayStatView(false);
    guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
    BitmapText ch = new BitmapText(guiFont, false);
    ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
    ch.setText("+"); // crosshairs
    ch.setLocalTranslation( // center
      settings.getWidth() / 2 - ch.getLineWidth()/2, settings.getHeight() / 2 + ch.getLineHeight()/2, 0);
    guiNode.attachChild(ch);
  }
    
    private Geometry makeSnowball(){
        Sphere ball = new Sphere(30, 30, 0.2f);
        Geometry snowball = new Geometry("Snowball", ball);
        Material snow = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        snow.setColor("Color", ColorRGBA.White);
        snowball.setMaterial(snow);
        return snowball;
    }
    
    @Override
    public void simpleInitApp() {

        initNodeTree();
        initShapes();
        initKeys();
        initSound();
        initCrossHairs();
        /** Rotate the pivot node: Note that both boxes have rotated! */
        //pivot.rotate(.4f,.4f,0f);
    };

private ActionListener actionListener = new ActionListener() {
    public void onAction(String name, boolean keyPressed, float tpf) {
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
    }
};


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

    @Override
    public void simpleUpdate(float tpf) {
        // make the player rotate:
        center.rotate(0, 0.5f*tpf*reverse, 0);
        pivot.rotate(0,0,2.5f*tpf*opposite);
        listener.setLocation(cam.getLocation());
        listener.setRotation(cam.getRotation());
    }
}