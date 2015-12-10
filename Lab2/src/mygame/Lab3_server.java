package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Random;

//Contains the game constants.
import static mygame.Constants.*;


/** Sample 2 - How to use nodes as handles to manipulate objects in the scene.
 * You can rotate, translate, and scale objects by manipulating their parent nodes.
 * The Root Node is special: Only what is attached to the Root Node appears in the scene. */
public class Lab3_server extends SimpleApplication{
    
    private boolean verbose = true;
    private boolean run = true;
    private String nick;
    private String address;
    
    public Lab3_server(String nick, String address){
        this.nick = nick;
        this.address = address;
    }

    //Variables that are useful to reach from anywhere.
    protected Node playingFieldNode;
    protected Node baseNode;
    protected Node supportPlateNode;
    protected Node cannonNode;
    protected Node laserNode;
    protected Node allProjectiles;
    protected Node allCans;
    protected BitmapText hudText;
    protected AudioNode cannonAudioNode;
    
    //Solve this by putting a the basenode into a second node.
    //Distance needed turns out to be ~25.5 if maximally packed.
    protected Quaternion resetBase;
    
    protected boolean laser_on = false;
    protected int active_cannonballs = 0;
    protected int[] active_cans = {0,0,0}; //Large, medium, small.
    protected int userScore;
    protected float time;
    protected boolean run_program;

    @Override
    public void simpleInitApp() {
        
        initNodeTree();
        initLight();
        initShapes();
        initHUD();
        
        initKeys();
        initSound();
        
        populatePlayingField();
        
        cam.lookAt(playingFieldNode.getWorldTranslation(),Vector3f.UNIT_Y);
        flyCam.setEnabled(false);
        //flyCam.setMoveSpeed(0);
        
        run=true;
        time=30f;
        userScore=0;
        
        //flyCam.setMoveSpeed(60);
    };
    
    private void initNodeTree(){
        //Node setup
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
        
        //Node positioning.
        playingFieldNode.setLocalTranslation(0f, -150f, -350f);
        baseNode.setLocalTranslation(0f, 0, PLAYINGFIELD_RADIUS);
        baseNode.lookAt(playingFieldNode.getLocalTranslation(), Vector3f.UNIT_Y);
        
        supportPlateNode.setLocalTranslation(0,CANNON_BASE_HEIGHT*0.5f,0);
        cannonNode.setLocalTranslation(0,CANNON_BARREL_RADIUS,
                  CANNON_BARREL_LENGTH*0.5f);
        laserNode.setLocalTranslation(0,-CANNON_BARREL_RADIUS-LASER_SIDE,
                  LASER_LENGTH);
        //Cannonballs originate within the cannon.
        allProjectiles.setLocalTranslation(0,
                CANNON_BASE_HEIGHT*0.5f+CANNON_BARREL_RADIUS,
                PLAYINGFIELD_RADIUS);
        
        resetBase = baseNode.getLocalRotation();

    }

    private void initShapes() {
        
        //Playing field. Flat green.
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
    }

    private void initKeys() {
        
        //toggle actions:
        inputManager.addMapping("Toggle laser",  new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping("Shoot", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Restart", new KeyTrigger(KeyInput.KEY_R));
        
        //Analog actions
        inputManager.addMapping("Turn left",  new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Turn right",  new KeyTrigger(KeyInput.KEY_D));

        // Add the names to the action listener.
        inputManager.addListener(actionListener, "Toggle laser", "Shoot","Restart");
        inputManager.addListener(analogListener,"Turn left", "Turn right");
  };

    private void initSound() {
        //Make the cannon do noise
        cannonAudioNode = new AudioNode(assetManager, "Sound/Effects/Gun.wav", 
                          false);
        cannonAudioNode.setPositional(true);
        cannonAudioNode.setLooping(false);
        cannonAudioNode.setVolume(1);
        supportPlateNode.attachChild(cannonAudioNode);
        
    }
    
    //Reuse this for HUD
    protected void initHUD() {
        setDisplayStatView(false); 
        setDisplayFps(false);
        hudText = new BitmapText(guiFont, false);          
        hudText.setSize(guiFont.getCharSet().getRenderedSize());      // font size
        hudText.setColor(ColorRGBA.White);                             // font color
        hudText.setText(formatTime(time)+"\n"+userScore);
        hudText.setLocalTranslation(0, settings.getHeight(), 0); // position
        hudText.setQueueBucket(Bucket.Gui);
        guiNode.attachChild(hudText);
        
    }
    
    protected void initLight() {
        //Create the directional light.
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(0f,-100f, -10f));
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
        
        //Lighten up the rest of the scene a bit.
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.3f));
        rootNode.addLight(al);
        
        //Make sure the GUI text is visible
        AmbientLight al2 = new AmbientLight();
        al2.setColor(ColorRGBA.White.mult(1f));
        guiNode.addLight(al2);
        
        //This is for shininess of laser.
        FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);
        viewPort.addProcessor(fpp);
    };
    
    //Makes a cannonball geometry attached to a node.
    private Node makeCannonball(){
        Sphere cannonballSphere = new Sphere(CANNONBALL_RESOLUTION, 
                CANNONBALL_RESOLUTION, CANNONBALL_RADIUS);
        Geometry cannonball = new Geometry("Cannonball", cannonballSphere);

        Material stone = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        stone.setTexture("DiffuseMap",assetManager.loadTexture(
                "Textures/Terrain/Rock/Rock.PNG"));
        stone.setTexture("NormalMap",assetManager.loadTexture(
                "Textures/Terrain/Rock/Rock_normal.png"));
        stone.setBoolean("UseMaterialColors",true);
        stone.setColor("Ambient", ColorRGBA.DarkGray); 
        stone.setColor("Diffuse", ColorRGBA.White); 
        stone.setColor("Specular", ColorRGBA.White);
        cannonball.setMaterial(stone);
        
        Node cannonballNode = new Node("Cannonball Node");
        cannonballNode.attachChild(cannonball);
        cannonballNode.setUserData("Radius", CANNONBALL_RADIUS);
        return cannonballNode;
    }
    
    //Makes a small can geometry attached to a node
    private Node makeSmallCan(){

        Geometry smallCan = makeCanGeometry(SMALLCAN_RADIUS,SMALLCAN_HEIGHT, 
                ColorRGBA.Red);
        
        Node smallCanNode = new Node("Can");
        smallCanNode.attachChild(smallCan);
        smallCanNode.setUserData("Radius", SMALLCAN_RADIUS);
        smallCanNode.setUserData("Value", SMALLCAN_VALUE);
        smallCanNode.setUserData("Index", 2);
        
        return smallCanNode;
    }
    
    //Makes a medium can geometry attached to a node
    private Node makeMediumCan(){
        
        Geometry mediumCan = makeCanGeometry(MEDIUMCAN_RADIUS,MEDIUMCAN_HEIGHT, 
                ColorRGBA.Orange);
        
        Node mediumCanNode = new Node("Can");
        mediumCanNode.attachChild(mediumCan);
        mediumCanNode.setUserData("Radius", MEDIUMCAN_RADIUS);
        mediumCanNode.setUserData("Value", MEDIUMCAN_VALUE);
        mediumCanNode.setUserData("Index", 1);
        
        return mediumCanNode;
    }
    
    //Makes a large can geometry attached to a node
    private Node makeLargeCan(){
        Geometry largeCan = makeCanGeometry(LARGECAN_RADIUS,LARGECAN_HEIGHT, 
                ColorRGBA.Yellow);
        Node largeCanNode = new Node("Can");
        largeCanNode.attachChild(largeCan);
        largeCanNode.setUserData("Radius", LARGECAN_RADIUS);
        largeCanNode.setUserData("Value", LARGECAN_VALUE);
        largeCanNode.setUserData("Index", 0);
                
        return largeCanNode;
    }
    
    private Geometry makeCanGeometry(Float radius, Float height, ColorRGBA color){
        Cylinder canCyl = new Cylinder(CAN_RESOLUTION,
                CAN_RESOLUTION,radius,height,
                true);
        Geometry can = new Geometry("Can", canCyl);
        Material stone = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        stone.setTexture("DiffuseMap",assetManager.loadTexture(
                "Textures/Terrain/Rock/Rock.PNG"));
        stone.setTexture("NormalMap",assetManager.loadTexture(
                "Textures/Terrain/Rock/Rock_normal.png"));
        stone.setBoolean("UseMaterialColors",true);
        stone.setColor("Ambient", color); 
        stone.setColor("Diffuse", color); 
        stone.setColor("Specular", ColorRGBA.White);
        can.setMaterial(stone);
        
        can.rotate(90*FastMath.DEG_TO_RAD,0,0);
        can.setLocalTranslation(0, PLAYINGFIELD_HEIGHT/2+height/2, 0);
        return can;
    }
    
    private void populatePlayingField(){
        Random r = new Random();
        tracePrint("Began populating.","POPULATE");
        while (active_cans[0]+active_cans[1]+active_cans[2]< CANS_NUM){
            tracePrint("Current cans on the board: "+active_cans[0]+"+"+active_cans[1]+"+"+active_cans[2],"POPULATE");
            //First, rotate in random direction.
            float randomdegrees = r.nextFloat()*2*(float)Math.PI; //Rotation in radians.
            float[] rotationalArray = {0f, randomdegrees, 0f};
            tracePrint("randomdegrees array: [0, "+rotationalArray[1]+", 0]","POPULATE");
            Quaternion random_rotation = new Quaternion(rotationalArray);
            //Next, move random distance.
            float distance = r.nextFloat()*(PLAYINGFIELD_RADIUS-SAFETY_MARGIN);
            //Make node to move
            Node can;
            int index;
            if (active_cans[0] < LARGECAN_NUM) {
                tracePrint("Placing large can...","POPULATE");
                can = makeLargeCan();
                index = 0;
            } else if (active_cans[1] < MEDIUMCAN_NUM) {
                tracePrint("Placing medium can...","POPULATE");
                can = makeMediumCan();
                index = 1;
            } else if (active_cans[2] < SMALLCAN_NUM) {
                tracePrint("Placing small can...","POPULATE");
                can = makeSmallCan();
                index = 2;
            } else {
                tracePrint("Whoops! Can quota filled.","POPULATE");
                break; //Should all else fail, break if we have all the cans we need.
            }
            
            playingFieldNode.attachChild(can);
            can.rotate(random_rotation);
            moveForwardZ(can,distance);
            tracePrint("Rotated and moved the can.","POPULATE");
            List<Spatial> cans = allCans.getChildren();
            boolean canPlace = true;
            for (int j=0; j < cans.size(); j++) {
                Spatial temp = cans.get(j);
                tracePrint("Testing collision for can index "+j,"POPULATE");
                if (checkForXZCollision(can,temp)) {
                    tracePrint("Collision! Breaking away.","POPULATE");
                    canPlace=false;
                    break;
                }
            }
            can.removeFromParent();
            //If we can't place the can, undo our action and start the loop again.
            if (!canPlace) {
                tracePrint("Couldn't place. Removing can from daddy.","POPULATE");
                can = null;
            } else {
                tracePrint("Adding another can to active_cans["+index+"]","POPULATE");
                allCans.attachChild(can);
                active_cans[index]++;
            }   
        }
    }
    
    private void checkForOutOfBounds(Spatial cannonball) {
        float distance = checkXZDistance(cannonball,playingFieldNode);
        if (distance >= PLAYINGFIELD_RADIUS+DEAD_MARGIN ) {
            cannonball.removeFromParent();
        }
    }
    
    //In this function, the node a is considered the collider and the node b
    // collided.
    private void doCollisionCannonballs(Node a, Node b) {
        tracePrint("Collided with something! Let's bounce!","BOUNCE");
        Node temp = new Node();
        a.attachChild(temp);
        temp.lookAt(b.getWorldTranslation(), Vector3f.UNIT_Y);
        float[] spin = new float[3];
        temp.getLocalRotation().toAngles(spin);
        temp.removeFromParent();
        tracePrint("We've spun "+Float.toString(spin[1])+" degrees", "BOUNCE");
        a.rotate(0, (FastMath.PI/2-spin[1])*-2, 0);
        Geometry geom = (Geometry)a.getChild("Cannonball");
        //geom.rotate(0, (FastMath.PI/2-spin[1])*2, 0);
    }
    
    private float checkXZDistance(Spatial A, Spatial B) {
        Vector3f a_vector = A.getWorldTranslation();
        Vector3f b_vector = B.getWorldTranslation();
        float a_x = a_vector.getX();
        float a_z = a_vector.getZ();
        float b_x = b_vector.getX();
        float b_z = b_vector.getZ();
        return (float) Math.sqrt((b_x-a_x)*(b_x-a_x) + (b_z-a_z)*(b_z-a_z));
    }
    
    private boolean checkForXZCollision(Spatial A, Spatial B) {
        tracePrint("Checkin collission.","COLLISION");
        tracePrint("subjects are "+A.getName()+", "+B.getName(),"COLLISION");
        float a_radius = A.getUserData("Radius");
        float b_radius = B.getUserData("Radius");
        float distance = checkXZDistance(A, B);
        float toHit = b_radius+a_radius;
        if ( distance <= toHit ){
            tracePrint("Did collide.","COLLISION");
            return true;
        }
        tracePrint("Did NOT collide..","COLLISION");
        return false;
    }
    
    private void moveForwardZ(Spatial element, float speed, float tpf ) {
        //This piece of code wizardry moves it in the direction it's pointing
        //Turns out it's a lot harder than you'd think.
        Vector3f forward = element.getLocalRotation().mult(Vector3f.UNIT_Z);
        element.move(forward.mult(speed).mult(tpf));
    }
    
    private void moveForwardZ(Spatial element, float distance ) {
        //This piece of code wizardry moves it in the direction it's pointing
        //Turns out it's a lot harder than you'd think.
        Vector3f forward = element.getLocalRotation().mult(Vector3f.UNIT_Z);
        element.move(forward.mult(distance));
    }
    
    private void alterLaserMode(boolean isActive) {
        Geometry laser =(Geometry)laserNode.getChild("Laser"); 
        Material laser_red = laser.getMaterial();
        if (isActive) {
            laser_red.setColor("Color", new ColorRGBA(1f,0f,0f,0.5f));
            laser_red.setColor("GlowColor", ColorRGBA.Red);
        } else {
            laser_red.setColor("Color", new ColorRGBA(0f,0f,0f,0f));
            laser_red.setColor("GlowColor", ColorRGBA.Black);
        }
        laser.setMaterial(laser_red);
    }
    
    private String formatTime(float time) {
        NumberFormat formatter = new DecimalFormat("00.00");
        return formatter.format(time);
    }
    
    //Create a Toggle Laser function
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Toggle laser") && keyPressed && run) {
                laser_on = !laser_on; //This will flip between off and on.
                alterLaserMode(laser_on);
                //typecasting is not good, but Laser should only be
                //a geometry. Spatials don't have materials, normally.
                
            }
            if (name.equals("Shoot") && keyPressed && run) {
                if (allProjectiles.getChildren().size() < CANNONBALL_NUM) {
                    Node cannonball = makeCannonball();
                    allProjectiles.attachChild(cannonball);
                    
                    Quaternion launchrotation = baseNode.getLocalRotation();
                    cannonball.rotate(launchrotation);
                    Vector3f forward = baseNode.getLocalRotation().mult(Vector3f.UNIT_Z);
                    cannonball.setLocalTranslation(forward.mult(CANNON_BARREL_LENGTH-CANNONBALL_RADIUS));
                    cannonAudioNode.playInstance();
                }
            }
            if (name.equals("Restart") && !keyPressed) {
                allCans.detachAllChildren();
                allProjectiles.detachAllChildren();
                //Need to spin basenode to center.
                //An alternative could be to remove the baseNode and reconstruct it.
                active_cans[0] = 0;
                active_cans[1] = 0;
                active_cans[2] = 0;
                populatePlayingField();
                baseNode.lookAt(playingFieldNode.getLocalTranslation(), Vector3f.UNIT_Y);
                run=true;
                laser_on=false;
                alterLaserMode(laser_on);
                time=30f;
                userScore=0;
                //restart();
                //Consider some soft restart function.
            }
        }
    };

    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            if (name.equals("Turn left") && run) {
                baseNode.rotate(0,tpf*CANNON_ROTATION_SPEED*FastMath.DEG_TO_RAD,0);
            }
            if (name.equals("Turn right") && run) {
                baseNode.rotate(0,tpf*-CANNON_ROTATION_SPEED*FastMath.DEG_TO_RAD,0);
            }
        }
    };
    
    @Override
    public void simpleUpdate(float tpf) {
        listener.setLocation(cam.getLocation());
        listener.setRotation(cam.getRotation());

        //Format this properly.
        //hudText.setText((float)Math.round(time * 100d) / 100d+"\n"+userScore);
        hudText.setText(formatTime(time)+"\n"+userScore);
        
        List<Spatial> cannonballs = allProjectiles.getChildren();
        
        List<Spatial> cans = allCans.getChildren();

        
        for (int i = 0; i < cannonballs.size(); i++) {
            Spatial cannonball = cannonballs.get(i);
            
            //This condition should be entered for all nodes in AllProjectiles,
            //but we're checking anyway.
            if (cannonball.getName().equals("Cannonball Node")) {    
                moveForwardZ(cannonball, CANNONBALL_SPEED, tpf);
                Node cNode = (Node)cannonball;
                cNode.getChild("Cannonball").rotate(
                        CANNONBALL_SPEED*tpf/(CANNONBALL_RADIUS),
                        0, 0);
                for (int j = 0; j < cans.size(); j++) {
                    //Check for collisions.
                    Spatial can = cans.get(j);
                    
                    if (can.getName().equals("Can")){
                        if (checkForXZCollision(cannonball, can)) {
                            //cannonball.removeFromParent();
                            doCollisionCannonballs((Node)cannonball, (Node)can);
                            int points = can.getUserData("Value");
                            if (run) {
                                userScore += points;
                            }
                            can.removeFromParent();
                            active_cans[can.getUserData("Index")]--;
                            populatePlayingField();
                        } else {
                            checkForOutOfBounds(cannonball);
                        }
                    }
                }
                checkForOutOfBounds(cannonball);
                //Check if we're out of bounds.
                
            }
        }
        
        if (time <= 0f) {
            time = 0f;
            run = false;
        } else {
            time -= tpf;
        }
    }
    
    public void tracePrint(String message) {
        if (verbose) { System.out.println(message); }
    }
    
    public void tracePrint(String message, String area ){
        if (verbose) { System.out.println("["+area+"] "+message); }
    }
}