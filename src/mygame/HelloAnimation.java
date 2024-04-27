package mygame;

import com.jme3.anim.AnimComposer;
import static com.jme3.anim.AnimComposer.DEFAULT_LAYER;
import com.jme3.anim.tween.Tween;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.Action;
import com.jme3.anim.tween.action.BlendSpace;
import com.jme3.anim.tween.action.LinearBlendSpace;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * Sample 7 - Load an OgreXML model and play some of its animations.
 */
public class HelloAnimation extends SimpleApplication {

    private Action advance;
    private AnimComposer control;
    private Node player;
    private boolean isWalking = false;

    public static void main(String[] args) {
        HelloAnimation app = new HelloAnimation();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(ColorRGBA.LightGray);
        initKeys();

        /* Agregar una fuente de luz para poder ver el modelo */
            /** A white ambient light source. */ 
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient); 

        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.1f, -1f, -1).normalizeLocal());
        rootNode.addLight(dl);

        /* Cargar un modelo que contenga animaciones */
        player = (Node) assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        player.setLocalScale(0.5f);
        rootNode.attachChild(player);

        /* Utilizar el AnimComposer del modelo para reproducir su animación "stand" */
        control = player.getControl(AnimComposer.class);
        // Crear una máscara de animación vacía
        control.setCurrentAction("stand");

        /* Componer una acción de animación llamada "halt"
           que transicione de "Walk" a "stand" en medio segundo. */
        BlendSpace quickBlend = new LinearBlendSpace(0f, 0.5f);
        Action halt = control.actionBlended("halt", quickBlend, "stand", "Walk");
        halt.setLength(0.5);
    }
  
    /**
     * Actualizar la posición del jugador basándose en la entrada del usuario.
     */
    @Override
    public void simpleUpdate(float tpf) {
        // Actualizar la posición y rotación de la cámara para seguir al jugador
        Vector3f camDir = player.getLocalRotation().mult(Vector3f.UNIT_Z);
        Vector3f camLeft = player.getLocalRotation().mult(Vector3f.UNIT_X);
        camDir.y = 0; // ensure no vertical movement
        camLeft.y = 0; // ensure no vertical movement
        camDir.normalizeLocal();
        camLeft.normalizeLocal();
        Vector3f camOffset = camDir.mult(-5).add(new Vector3f(0, 7, -9)); // adjust offset
        cam.setLocation(player.getWorldTranslation().add(camOffset));
        cam.lookAt(player.getWorldTranslation(), Vector3f.UNIT_Y);
    }

  /**
   * Mapear las teclas a la acción de entrada "Walk",
   * y agregar un ActionListener para iniciar la acción de animación "advance"
   * cada vez que se presione.
   */
    private void initKeys() {
        inputManager.addMapping("Walk", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("pull", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));


        ActionListener handler = new ActionListener() {
          @Override
          public void onAction(String name, boolean keyPressed, float tpf) {
                if (name.equals("Walk")) {
                    if (keyPressed && !isWalking) {
                        // Comenzar la animación de caminar solo si no está actualmente caminando
                        control.setCurrentAction("Walk", DEFAULT_LAYER, true);
                        isWalking = true;
                    } else if (!keyPressed && isWalking) {
                        // Detener la animación de caminar cuando se suelta la tecla 'W'
                        control.setCurrentAction("stand");
                        isWalking = false;
                    }
                }else if (name.equals("pull")) {
                    if (keyPressed) {
                        // Comenzar la animación de caminar solo si no está actualmente caminando
                        control.setCurrentAction("pull", DEFAULT_LAYER, false);
                    }
                }
            }
        };
        inputManager.addListener(handler, "Walk");
        inputManager.addListener(handler, "pull");
    }
}