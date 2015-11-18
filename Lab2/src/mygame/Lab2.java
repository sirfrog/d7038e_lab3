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
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.TangentBinormalGenerator;
import java.util.List;




/** Sample 2 - How to use nodes as handles to manipulate objects in the scene.
 * You can rotate, translate, and scale objects by manipulating their parent nodes.
 * The Root Node is special: Only what is attached to the Root Node appears in the scene. */
public class Lab2 extends SimpleApplication {
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
    
    public static final float PLAYINGFIELD_HEIGHT = 2.3f * CANNON_BARREL_RADIUS;
    public static final float LASER_SIDE = 0.3f;
    public static final float LASER_LENGTH = PLAYINGFIELD_RADIUS *2f;

    public static void main(String[] args){
        Lab2 app = new Lab2();
        app.start();
    }

    //Variables that are useful to reach from anywhere.
    protected Node playingFieldNode;
    protected Node baseNode;
    protected Node supportPlateNode;
    protected Node cannonNode;
    protected Node laserNode;
    protected Node allProjectiles;
    protected Node allCans;
    protected AudioNode cannonAudioNode;
    protected boolean laser_on = false;
    protected int active_cannonballs = 0;

    private void initNodeTree(){
        
        playingFieldNode = new Node("playingFieldNode");
        rootNode.attachChild(playingFieldNode);
        
        baseNode = new Node("basePlateNode");
        playingFieldNode.attachChild(baseNode);
        
        supportPlateNode = new Node("supportNode");
        baseNode.attachChild(supportPlateNode);
        
        cannonNode = new Node("cannonNode");
        supportPlateNode.attachChild(cannonNode);
        
        laserNode = new Node("laserNode");
        cannonNode.attachChild(laserNode);
        
        allProjectiles = new Node("allProjectiles");
        playingFieldNode.attachChild(allProjectiles);
        
        allCans = new Node("allCans");
        playingFieldNode.attachChild(allCans);
        
        playingFieldNode.setLocalTranslation(0f, -150f, -350f);
        baseNode.setLocalTranslation(0f, 0, PLAYINGFIELD_RADIUS);
        supportPlateNode.setLocalTranslation(0,CANNON_BASE_HEIGHT*0.5f,0);
        cannonNode.setLocalTranslation(0,CANNON_BARREL_RADIUS,
                  CANNON_BARREL_LENGTH*-0.5f);
        laserNode.setLocalTranslation(0,-CANNON_BARREL_RADIUS-LASER_SIDE,
                  -LASER_LENGTH);
        
        allProjectiles.setLocalTranslation(0,
                CANNON_BASE_HEIGHT*0.5f+CANNON_BARREL_RADIUS,
                PLAYINGFIELD_RADIUS);//Cannonballs originate within the cannon.

    }

    private void initShapes() {
        
        //PLaying field. Flat green.
        Cylinder playingFieldCyl = new Cylinder(PLAYINGFIELD_RESOLUTION,
                PLAYINGFIELD_RESOLUTION,PLAYINGFIELD_RADIUS,PLAYINGFIELD_HEIGHT,
                true);
        Geometry playingField = new Geometry("Playing Field", playingFieldCyl);
        Material grass = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        grass.setBoolean("UseMaterialColors",true);
        grass.setColor("Ambient", new ColorRGBA(0,0.5f,0,0)); 
        grass.setColor("Diffuse", new ColorRGBA(0,0.5f,0,0));
        //Somewhat less eye-searing green.
        playingField.setMaterial(grass);
        playingField.rotate(90*FastMath.DEG_TO_RAD,0,0);
        playingFieldNode.attachChild(playingField);
        
        //The base.
        Cylinder baseCyl = new Cylinder(CANNONBALL_RESOLUTION,
                CANNONBALL_RESOLUTION,CANNON_BASE_RADIUS,CANNON_BASE_HEIGHT, 
                true);
        Geometry base = new Geometry("Base", baseCyl);
        Material black_metal = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        black_metal.setBoolean("UseMaterialColors",true);
        black_metal.setColor("Ambient", ColorRGBA.Black); 
        black_metal.setColor("Diffuse", ColorRGBA.Black); 
        black_metal.setColor("Specular", ColorRGBA.White);
        black_metal.setFloat("Shininess", 128f);
        base.setMaterial(black_metal);
        baseNode.attachChild(base);
        base.rotate(90*FastMath.DEG_TO_RAD,0,0);
        
        //The Support
        Cylinder supportPlateCyl = new Cylinder(CANNONBALL_RESOLUTION,
                CANNONBALL_RESOLUTION,CANNON_SUPPORT_RADIUS,
                CANNON_SUPPORT_HEIGHT, true);
        Geometry supportPlate = new Geometry("Support Plate", supportPlateCyl);
        Material wood = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        wood.setBoolean("UseMaterialColors",true);
        wood.setColor("Ambient", ColorRGBA.Brown); 
        wood.setColor("Diffuse", ColorRGBA.Brown); 
        supportPlate.setMaterial(wood);
        supportPlateNode.attachChild(supportPlate);
        supportPlate.rotate(90*FastMath.DEG_TO_RAD,0,90*FastMath.DEG_TO_RAD);
        
        //Cannon barrel
        Cylinder cannonCyl = new Cylinder(CANNONBALL_RESOLUTION,
                CANNONBALL_RESOLUTION,CANNON_BARREL_RADIUS,
                CANNON_BARREL_LENGTH, true);
        Geometry cannon = new Geometry("Base", cannonCyl);
        Material gunmetal = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        gunmetal.setBoolean("UseMaterialColors",true);
        gunmetal.setColor("Ambient", ColorRGBA.Gray); 
        gunmetal.setColor("Diffuse", ColorRGBA.Gray); 
        gunmetal.setColor("Specular", ColorRGBA.White);
        gunmetal.setFloat("Shininess", 64f);
        cannon.setMaterial(gunmetal);
        cannonNode.attachChild(cannon);
        
        //laser geometry. Now with glow!
        Box laserBox = new Box(LASER_SIDE,LASER_SIDE,LASER_LENGTH);
        Geometry laser = new Geometry("Laser", laserBox);
        Material laser_red = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        laser_red.setColor("Color", new ColorRGBA(1f,0f,0f,0f));
        laser_red.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        laser.setMaterial(laser_red);
        laser.setQueueBucket(Bucket.Transparent); 
        
        laserNode.attachChild(laser);
        

        Sphere sphere = new Sphere(32,32,3f);
        Geometry yellow = new Geometry("Sphere", sphere);
        sphere.setTextureMode(Sphere.TextureMode.Projected);
        Material mat3 = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat3.setColor("Color", ColorRGBA.Yellow);
        yellow.setMaterial(mat3);
        
    }

    private void initKeys() {
        
        //Immediate actions:
        inputManager.addMapping("Toggle laser",  new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping("Shoot", new KeyTrigger(KeyInput.KEY_SPACE));
        
        inputManager.addMapping("Turn left",  new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("Turn right",  new KeyTrigger(KeyInput.KEY_K));
       
        //inputManager.addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

        // Add the names to the action listener.
        inputManager.addListener(actionListener, "Toggle laser", "Shoot");
        inputManager.addListener(analogListener,"Turn left", "Turn right");
  };

    private void initSound() {
        
        cannonAudioNode = new AudioNode(assetManager, "Sound/Effects/Gun.wav", 
                          false);
        cannonAudioNode.setPositional(true);
        cannonAudioNode.setLooping(false);
        cannonAudioNode.setVolume(1);
        supportPlateNode.attachChild(cannonAudioNode);
        
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
    
    protected void initLight() {
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(0f,-100f, -1f));
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
        
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.2f));
        rootNode.addLight(al);
        
        //This is for shininess of laser.
        FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);
        viewPort.addProcessor(fpp);
    };
    
    private Node makeCannonball(){
        Sphere cannonballSphere = new Sphere(CANNONBALL_RESOLUTION, 
                CANNONBALL_RESOLUTION, CANNONBALL_RADIUS);
        Geometry cannonball = new Geometry("Cannonball", cannonballSphere);
        Material dark_metal = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        dark_metal.setBoolean("UseMaterialColors",true);
        dark_metal.setColor("Ambient", ColorRGBA.DarkGray); 
        dark_metal.setColor("Diffuse", ColorRGBA.DarkGray); 
        dark_metal.setColor("Specular", ColorRGBA.White);
        dark_metal.setFloat("Shininess", 64f);
        cannonball.setMaterial(dark_metal);
        
        Node cannonballNode = new Node("Cannonball Node");
        cannonballNode.attachChild(cannonball);
        return cannonballNode;
    }
    
    private Node makeSmallCan(){
        Cylinder smallCanCyl = new Cylinder(CAN_RESOLUTION,
                CAN_RESOLUTION,SMALLCAN_RADIUS,SMALLCAN_HEIGHT,
                true);
        Geometry smallCan = new Geometry("Can", smallCanCyl);
        Material blue_metal = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        blue_metal.setBoolean("UseMaterialColors",true);
        blue_metal.setColor("Ambient", ColorRGBA.Blue); 
        blue_metal.setColor("Diffuse", ColorRGBA.Blue);
        blue_metal.setColor("Specular", ColorRGBA.White);
        blue_metal.setFloat("Shininess", 64f);
        smallCan.setMaterial(blue_metal);
        smallCan.rotate(90*FastMath.DEG_TO_RAD,0,0);
        
        Node smallCanNode = new Node("Cannonball Node");
        smallCanNode.attachChild(smallCan);
        allCans.attachChild(smallCanNode);
        return smallCanNode;
    }
    
    private Node makeMediumCan(){
        Cylinder mediumCanCyl = new Cylinder(CAN_RESOLUTION,
                CAN_RESOLUTION,MEDIUMCAN_RADIUS,MEDIUMCAN_HEIGHT,
                true);
        Geometry mediumCan = new Geometry("Can", mediumCanCyl);
        Material purple_metal = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        purple_metal.setBoolean("UseMaterialColors",true);
        purple_metal.setColor("Ambient", new ColorRGBA(0.5f,0,0.5f,1f)); 
        purple_metal.setColor("Diffuse", new ColorRGBA(0.5f,0,0.5f,1f));
        purple_metal.setColor("Specular", ColorRGBA.White);
        purple_metal.setFloat("Shininess", 64f);
        mediumCan.setMaterial(purple_metal);
        mediumCan.rotate(90*FastMath.DEG_TO_RAD,0,0);
        
        Node mediumCanNode = new Node("Cannonball Node");
        mediumCanNode.attachChild(mediumCan);
        allCans.attachChild(mediumCanNode);
        return mediumCanNode;
    }
    
    private Node makeLargeCan(){
        Cylinder largeCanCyl = new Cylinder(CAN_RESOLUTION,
                CAN_RESOLUTION,LARGECAN_RADIUS,LARGECAN_HEIGHT,
                true);
        Geometry largeCan = new Geometry("Can", largeCanCyl);
        Material pink_metal = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        pink_metal.setBoolean("UseMaterialColors",true);
        pink_metal.setColor("Ambient", ColorRGBA.Magenta); 
        pink_metal.setColor("Diffuse", ColorRGBA.Magenta);
        pink_metal.setColor("Specular", ColorRGBA.White);
        pink_metal.setFloat("Shininess", 64f);
        largeCan.setMaterial(pink_metal);
        largeCan.rotate(90*FastMath.DEG_TO_RAD,0,0);
        
        Node largeCanNode = new Node("Cannonball Node");
        largeCanNode.attachChild(largeCan);
        allCans.attachChild(largeCanNode);
        return largeCanNode;
    }
    
    @Override
    public void simpleInitApp() {
        
        initNodeTree();
        initLight();
        initShapes();
        
        initKeys();
        initSound();
        
        flyCam.setMoveSpeed(60);
    };

private ActionListener actionListener = new ActionListener() {
    public void onAction(String name, boolean keyPressed, float tpf) {
        if (name.equals("Toggle laser") && keyPressed) {
            laser_on = !laser_on; //This will flip between off and on.
            
            //typecasting is not good, but Laser should only be
            //a geometry. Spatials don't have materials, normally.
            Geometry laser =(Geometry)laserNode.getChild("Laser"); 
            Material laser_red = laser.getMaterial();
            if (laser_on) {
                laser_red.setColor("Color", new ColorRGBA(1f,0f,0f,0.5f));
                laser_red.setColor("GlowColor", ColorRGBA.Red);
            } else {
                laser_red.setColor("Color", new ColorRGBA(0f,0f,0f,0f));
                laser_red.setColor("GlowColor", ColorRGBA.Black);
            }
            laser.setMaterial(laser_red);
        }
        if (name.equals("Shoot") && keyPressed) {
            if (active_cannonballs < CANNONBALL_NUM) {
                active_cannonballs++;
                Node cannonball = makeCannonball();
                allProjectiles.attachChild(cannonball);
                Quaternion launchrotation = baseNode.getLocalRotation();
                cannonball.rotate(launchrotation);
                Vector3f forward = baseNode.getLocalRotation().mult(Vector3f.UNIT_Z);
                cannonball.setLocalTranslation(forward.mult(-CANNON_BARREL_LENGTH+CANNONBALL_RADIUS));
                cannonAudioNode.playInstance();
            }
        }
    }
};


private AnalogListener analogListener = new AnalogListener() {
    
    //inputManager.addListener(analogListener,"Backward","Forward","Shrink","Grow");
    public void onAnalog(String name, float value, float tpf) {
        if (name.equals("Turn left")) {
            baseNode.rotate(0,tpf*CANNON_ROTATION_SPEED*0.1f,0);
        }
        if (name.equals("Turn right")) {
            baseNode.rotate(0,tpf*-CANNON_ROTATION_SPEED*0.1f,0);
        }
    }
};

    @Override
    public void simpleUpdate(float tpf) {
        listener.setLocation(cam.getLocation());
        listener.setRotation(cam.getRotation());
        
        List<Spatial> cannonballs = allProjectiles.getChildren();
        for (int i = 0; i < cannonballs.size(); i++) {
            Spatial element = cannonballs.get(i);
            //System.out.println(element.getName());
            if (element.getName().equals("Cannonball Node")) {
                //This piece of code wizardry moves it in the direction it's pointing
                //Turns out it's a lot harder than you'd think.
                Vector3f forward = element.getLocalRotation().mult(Vector3f.UNIT_Z);
                element.move(forward.mult(-CANNONBALL_SPEED).mult(tpf));
            }
            if (element.getWorldTranslation().distance(
                    playingFieldNode.getWorldTranslation())
                    >= PLAYINGFIELD_RADIUS+DEAD_MARGIN ) {
                element.removeFromParent();
                element = null; //THis should remove it permanently.
                active_cannonballs--;
                
            }
             //Remove it if distance is radius + safety distance.
        }
    }
}