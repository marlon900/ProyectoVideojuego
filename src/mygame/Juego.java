package mygame;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.Joint;
import com.jme3.anim.SkinningControl;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.List;


public class Juego extends SimpleApplication {

    private PlayerLogic player;
    private List<EnemyLogic> enemies = new ArrayList<>();
    private float gameTime;
    private BitmapText timeText;
    private float enemySpawnTime = 0;
    private float enemySpawnInterval = 30;
    private int difficulty;
    private static final Box mesh = new Box(0.8f, 0.8f, 0.8f);

    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true); //Creamos el objeto para controlar las especificaciones
        settings.setTitle("El Despertar del Héroe"); //Cambiamos el nombre de la ventana 
        //Integramos una imagen personal a la pantalla de inicio
        settings.setSettingsDialogImage("Logos/Portada.png");
        //modificar la resolucion 
        settings.setFullscreen(true);
        settings.setResolution(1024, 720);
        Juego app = new Juego();
        
        app.setSettings(settings);//Aplicamos las especificaciones a la app
        
        app.start();// Iniciamos el juego
    }

    @Override
    public void simpleInitApp() {
        setDisplayStatView(false);//Dejamos de mostrar información
        setDisplayFps(false);//Quitamos el numero de fps
        
        // Cargar el terreno
        Spatial primaryScene = assetManager.loadModel("Scenes/terreno.j3o");
        primaryScene.setLocalTranslation(0, -5, 0);
        rootNode.attachChild(primaryScene);

        // Cargar el modelo para crear el jugador
        Node playerNode = (Node) assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        
        // Crear y configurar las luces para iluminar el modelo
        PointLight frontLight = new PointLight();
        frontLight.setColor(ColorRGBA.White);
        PointLight backLight = new PointLight();
        backLight.setColor(ColorRGBA.White);
        PointLight leftLight = new PointLight();
        leftLight.setColor(ColorRGBA.White);
        PointLight rightLight = new PointLight();
        rightLight.setColor(ColorRGBA.White);
        // Agregar las luces al nodo del jugador
        playerNode.addLight(frontLight);
        playerNode.addLight(backLight);
        playerNode.addLight(frontLight);
        playerNode.addLight(backLight);
        // Obtener el SkinningControl
        SkinningControl skinningControl = playerNode.getControl(SkinningControl.class);
        
        // Cargar el modelo del arma
        Node weaponNode = (Node) assetManager.loadModel("Models/shotgun/shotgun.j3o");
        // Encontrar el hueso de la mano derecha
        Joint rightHand = skinningControl.getArmature().getJoint("hand.right");
        // Adjuntar el arma al AttachmentNode del hueso de la mano derecha
        skinningControl.getAttachmentsNode(rightHand.getName()).attachChild(weaponNode);
        weaponNode.setLocalTranslation(0, 0.5f, 0);
        
        rootNode.attachChild(playerNode);
        /* Utilizar el AnimComposer del modelo para reproducir su animación "stand" */
        AnimComposer control = playerNode.getControl(AnimComposer.class);
        // Crear una máscara de animación vacía
        control.setCurrentAction("stand");
        // Inicializar la lógica del jugador
        player = new PlayerLogic(playerNode, control, 100);
        
        // Desactivamos la camara voladora
        flyCam.setEnabled(false);
        inputManager.setCursorVisible(false);
        //inputManager.setCursorVisible(false);
        
        //create the camera Node
        CameraNode camNode = new CameraNode("Camera Node", cam);
        //This mode means that camera copies the movements of the target:
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        //Attach the camNode to the target:
        player.getPlayerNode().attachChild(camNode);
        //Move camNode, e.g. behind and above the target:
        camNode.setLocalTranslation(new Vector3f(0, 20, -50));
        //Rotate the camNode to look at the target:
        camNode.lookAt(player.getPlayerNode().getLocalTranslation(), Vector3f.UNIT_Y);
        
        gameTime = 0;//Inciamos el tiempo de juego
        // Configurar el texto para mostrar el tiempo
        initTimeDisplay();
        initKeys();
        spawnEnemies();
        difficulty = 1;//Establecemos la dificultad incial en 1
    }
  
    /**
     * Actualizar la posición del jugador basándose en la entrada del usuario.
     */
    @Override
    public void simpleUpdate(float tpf) {
        // Actualizar el tiempo de la partida
        gameTime += tpf;
        // Actualizar el texto en pantalla
        timeText.setText(String.format("Time: %.1f", gameTime));
        
        enemySpawnTime += tpf;
        if (enemySpawnTime >= enemySpawnInterval) {
            difficulty += 1;
            spawnEnemies();
            enemySpawnTime = 0; // Reiniciar el contador
        }
        
        for (EnemyLogic enemy : enemies) {
            // Cálculo de la dirección hacia el jugador
            Vector3f playerPosition = player.getPlayerNode().getLocalTranslation();
            enemy.update(4 * tpf, playerPosition);
        }
        
        attachLight();
        
        inputManager.setCursorVisible(false);
    }
    
    private void attachLight(){
        Vector3f playerWorldPosition = player.getPlayerNode().getWorldTranslation();

        // Actualizar la posición relativa de las luces
        PointLight frontLight = (PointLight) player.getPlayerNode().getLocalLightList().get(0);
        PointLight backLight = (PointLight) player.getPlayerNode().getLocalLightList().get(1);
        PointLight leftLight = (PointLight) player.getPlayerNode().getLocalLightList().get(2);
        PointLight rightLight = (PointLight) player.getPlayerNode().getLocalLightList().get(3);

        frontLight.setPosition(playerWorldPosition.add(0, 3, 6)); // Ajusta la posición frente al nodo
        backLight.setPosition(playerWorldPosition.add(0, 4, -6));
        leftLight.setPosition(playerWorldPosition.add(-6, 0, 0)); // Ajusta la posición frente al nodo
        rightLight.setPosition(playerWorldPosition.add(6, 0, 0));
    }
    
    private void initTimeDisplay() {
        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        timeText = new BitmapText(guiFont, false);
        timeText.setSize(guiFont.getCharSet().getRenderedSize());
        timeText.setColor(ColorRGBA.White);
        timeText.setText("Tiempo: 0.0");
        timeText.setLocalTranslation(10, settings.getHeight() - timeText.getLineHeight(), 0);
        guiNode.attachChild(timeText);
    }
    
    private void spawnEnemies() {
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
                        player.getPlayerNode().getLocalTranslation(), (10*difficulty));
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
        inputManager.addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        
        inputManager.addMapping("RotateLeft", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("RotateRight", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        
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
                if (name.equals("RotateLeft")) {
                    player.getPlayerNode().rotate(0, value, 0); // Rotar alrededor del eje Y
                }
                if (name.equals("RotateRight")) {
                    player.getPlayerNode().rotate(0, -value, 0); // Rotar alrededor del eje Y
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
                if (name.equals("shoot")) {
                    if(keyPressed){
                        shoot();
                    }
                }
            }
        };
        
        inputManager.addListener(handlerAnalog, "Walk", "WalkLeft", "WalkRight", "WalkBackward", "pull", 
                "shoot", "RotateLeft", "RotateRight");
        inputManager.addListener(handler, "Walk", "WalkLeft", "WalkRight", "WalkBackward", "pull", "shoot");
    }
    
    public void shoot() {
        // Obtener la posición y dirección del arma
        Spatial weapon = player.getPlayerNode().getChild("shotgun");
        Vector3f weaponPosition = weapon.getWorldTranslation();
        // Obtener la dirección de la cámara y ajustar la componente Y a 0
        Vector3f shootDirection = cam.getDirection().clone();
        shootDirection.y = 0;
        shootDirection.normalizeLocal();

        // Crear el disparo
        Geometry bullet = createBullet();
        bullet.setLocalTranslation(weaponPosition);
        rootNode.attachChild(bullet);

        // Mover el disparo
        bullet.addControl(new BulletControl(shootDirection, enemies));
    }

    private Geometry createBullet() {
        Sphere bulletMesh = new Sphere(10, 10, 0.1f);
        Geometry bullet = new Geometry("Bullet", bulletMesh);
        Material bulletMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bulletMat.setColor("Color", ColorRGBA.LightGray);
        bullet.setMaterial(bulletMat);
        return bullet;
    }
}