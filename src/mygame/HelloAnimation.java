package mygame;

import com.jme3.anim.AnimComposer;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;


public class HelloAnimation extends SimpleApplication {

    private PlayerLogic player;
    final private Vector3f walkDirection = new Vector3f();
    private boolean rotate = false;


    public static void main(String[] args) {
        HelloAnimation app = new HelloAnimation();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(ColorRGBA.LightGray);
        
        // Configurar la escena con colisiones
        Spatial primaryScene = assetManager.loadModel("Scenes/terreno.j3o");
        rootNode.attachChild(primaryScene);

        // Cargar el modelo
        Node playerNode = (Node) assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        ((Node) rootNode.getChild("PrimaryScene")).attachChild(playerNode);
        playerNode.setLocalTranslation(0, 4, 0);

        /* Utilizar el AnimComposer del modelo para reproducir su animación "stand" */
        AnimComposer control = playerNode.getControl(AnimComposer.class);
        // Crear una máscara de animación vacía
        control.setCurrentAction("stand");
        inputManager.setCursorVisible(true);
        
        // Inicializar la lógica del jugador
        player = new PlayerLogic(playerNode, control);
        
        // Disable the default flyby cam
        flyCam.setEnabled(false);
        //create the camera Node
        CameraNode camNode = new CameraNode("Camera Node", cam);
        //This mode means that camera copies the movements of the target:
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        //Attach the camNode to the target:
        player.getPlayerNode().attachChild(camNode);
        //Move camNode, e.g. behind and above the target:
        camNode.setLocalTranslation(new Vector3f(0, 6, -20));
        //Rotate the camNode to look at the target:
        camNode.lookAt(player.getPlayerNode().getLocalTranslation(), Vector3f.UNIT_Y);
        initKeys();
    }
  
    /**
     * Actualizar la posición del jugador basándose en la entrada del usuario.
     */
    @Override
    public void simpleUpdate(float tpf) {
        
    }

  /**
   * Mapear las teclas a la acción de entrada "Walk",
   * y agregar un ActionListener para iniciar la acción de animación "advance"
   * cada vez que se presione.
   */
    private void initKeys() {
        inputManager.addMapping("Walk", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("WalkLeft", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("WalkRight", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("WalkBackward", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("pull", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        
        AnalogListener handlerAnalog = new AnalogListener(){
            @Override
            public void onAnalog(String name, float value, float tpf) {
                Vector3f camDirection = cam.getDirection().multLocal(1, 0, 1).normalizeLocal();
                Vector3f camLeft = cam.getLeft().multLocal(1, 0, 1).normalizeLocal();
                if (name.equals("Walk")) {
                    player.getPlayerNode().move(camDirection.mult(5 * tpf));
                }
                if (name.equals("WalkBackward")) {
                    //Quaternion rotation = new Quaternion().fromAngleAxis(FastMath.PI, Vector3f.UNIT_Y);
                    //playerLogic.getPlayerNode().rotate(rotation);
                    player.getPlayerNode().move(camDirection.negate().mult(5 * tpf));
                }
                if (name.equals("WalkRight")) {
                    player.getPlayerNode().move(camLeft.mult(-5 * tpf));
                }
                if (name.equals("WalkLeft")) {
                    player.getPlayerNode().move(camLeft.mult(5 * tpf));
                }
            }
        };

        ActionListener handler = new ActionListener() {
            @Override
            public void onAction(String name, boolean keyPressed, float tpf) {
                if(name.equals("Walk")){
                    player.handleWalkAction(keyPressed);
                }
                if(name.equals("WalkLeft")){
                    player.handleWalkAction(keyPressed);
                }
                if(name.equals("WalkRight")){
                    player.handleWalkAction(keyPressed);
                }
                if(name.equals("WalkBackward")){
                    player.handleWalkAction(keyPressed);
                }
                if(name.equals("pull")){
                    player.handlePullAction(keyPressed);
                }
            }
        };
        
        inputManager.addListener(handlerAnalog, "Walk", "WalkLeft", "WalkRight", "WalkBackward", "pull");
        inputManager.addListener(handler, "Walk", "WalkLeft", "WalkRight", "WalkBackward", "pull");
    }
}