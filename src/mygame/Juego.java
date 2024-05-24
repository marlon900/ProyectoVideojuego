package mygame;

import com.jme3.anim.AnimComposer;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.List;


public class Juego extends SimpleApplication {

    private PlayerLogic player;
    private List<EnemyLogic> enemies = new ArrayList<>();

    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true); //Creamos el objeto para controlar las especificaciones
        settings.setTitle("El Despertar del Héroe"); //Cambiamos el nombre de la ventana 
        //Integramos una imagen personal a la pantalla de inicio
        settings.setSettingsDialogImage("Logos/Portada.png");
        //modificar la resolucion 
        settings.setResolution(1280, 960);
        Juego app = new Juego();
        
        app.setSettings(settings);//Aplicamos las especificaciones a la app
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        // Configurar la escena con colisiones
        Spatial primaryScene = assetManager.loadModel("Scenes/terreno.j3o");
        primaryScene.setLocalTranslation(0, -5, 0);
        rootNode.attachChild(primaryScene);

        // Cargar el modelo
        Node playerNode = (Node) assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        playerNode.addLight(ambient); 
        rootNode.attachChild(playerNode);

        /* Utilizar el AnimComposer del modelo para reproducir su animación "stand" */
        AnimComposer control = playerNode.getControl(AnimComposer.class);
        // Crear una máscara de animación vacía
        control.setCurrentAction("stand");
        inputManager.setCursorVisible(true);
        
        // Inicializar la lógica del jugador
        player = new PlayerLogic(playerNode, control, 100);
        
        // Disable the default flyby cam
        flyCam.setEnabled(false);
        //create the camera Node
        CameraNode camNode = new CameraNode("Camera Node", cam);
        //This mode means that camera copies the movements of the target:
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        //Attach the camNode to the target:
        player.getPlayerNode().attachChild(camNode);
        //Move camNode, e.g. behind and above the target:
        camNode.setLocalTranslation(new Vector3f(0, 20, -50));
        //Rotate the camNode to look at the target:
        camNode.lookAt(player.getPlayerNode().getLocalTranslation(), Vector3f.UNIT_Y);
        
        initKeys();
        initEnemies();
    }
  
    /**
     * Actualizar la posición del jugador basándose en la entrada del usuario.
     */
    @Override
    public void simpleUpdate(float tpf) {
        for (EnemyLogic enemy : enemies) {
            // Cálculo de la dirección hacia el jugador
            Vector3f playerPosition = player.getPlayerNode().getLocalTranslation();
            enemy.update(4 * tpf, playerPosition);
        }
    }
    
    private void initEnemies() {
        // Encuentra el nodo "Cuevas" dentro del nodo "terreno"
        Node terrenoNode = (Node) rootNode.getChild("terreno");
        for (Spatial spatial : terrenoNode.getChildren()) {
            if (spatial.getName() != null && spatial.getName().startsWith("cueva")) {
                // Carga el modelo del enemigo
                Node enemyNode = (Node) assetManager.loadModel("Models/Sinbad/Sinbad.mesh.xml");

                // Establece la posición del enemigo
                Vector3f enemyPosition = spatial.getLocalTranslation();
                enemyNode.setLocalTranslation(enemyPosition);

                // Añadir una luz ambiental al enemigo
                AmbientLight ambient = new AmbientLight();
                ambient.setColor(ColorRGBA.White);
                enemyNode.addLight(ambient);

                // Adjuntar el enemigo al rootNode
                rootNode.attachChild(enemyNode);

                // Configurar la animación del enemigo
                AnimComposer animComposer = enemyNode.getControl(AnimComposer.class);
                animComposer.setCurrentAction("RunBase");

                // Inicializar la lógica del enemigo
                EnemyLogic enemy = new EnemyLogic(enemyNode, animComposer, 
                        player.getPlayerNode().getLocalTranslation(), 10);
                enemies.add(enemy);
            }
        }
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
        inputManager.addMapping("pull", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        
        AnalogListener handlerAnalog = new AnalogListener(){
            @Override
            public void onAnalog(String name, float value, float tpf) {
                Vector3f camDirection = cam.getDirection().multLocal(1, 0, 1).normalizeLocal();
                Vector3f camLeft = cam.getLeft().multLocal(1, 0, 1).normalizeLocal();
                if (name.equals("Walk")) {
                    player.getPlayerNode().move(camDirection.mult(5 * tpf));
                }
                if (name.equals("WalkBackward")) {
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
                if (name.equals("Walk") || name.equals("WalkLeft") || name.equals("WalkRight") 
                        || name.equals("WalkBackward")) {
                    // Manejar la animación de caminar
                    player.handleWalkAction(keyPressed);
                } 
                if (name.equals("pull")) {
                    // Manejar la animación de empujar
                    player.handlePullAction(keyPressed);
                }
            }
        };
        
        inputManager.addListener(handlerAnalog, "Walk", "WalkLeft", "WalkRight", "WalkBackward", "pull");
        inputManager.addListener(handler, "Walk", "WalkLeft", "WalkRight", "WalkBackward", "pull");
    }
}