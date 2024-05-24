/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.anim.AnimComposer;
import static com.jme3.anim.AnimComposer.DEFAULT_LAYER;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author jesus
 */
public class PlayerLogic {
    private Node playerNode;
    private AnimComposer animComposer;
    private boolean isWalking = false;
    private float moveSpeed = 0.2f;

    public PlayerLogic(Node playerNode, AnimComposer animComposer) {
        this.playerNode = playerNode;
        this.animComposer = animComposer;
    }

    public void handleWalkAction(boolean keyPressed) {
        if (keyPressed) {
            animComposer.setCurrentAction("Walk", AnimComposer.DEFAULT_LAYER, true);
        } else {
            animComposer.setCurrentAction("stand");
        }
    }
    
    public void handlePullAction(boolean keyPressed) {
        if(keyPressed){
            animComposer.setCurrentAction("pull", DEFAULT_LAYER, false);
        }
    }
    
    void handleWalkAndPullAction(boolean keyPressed) {
        if (keyPressed) {
            animComposer.setCurrentAction("Walk", AnimComposer.DEFAULT_LAYER, true);
            animComposer.setCurrentAction("pull", DEFAULT_LAYER, false);
        }else {
            // Si ninguna tecla está presionada, dejar que las animaciones continúen
            // reproduciéndose si ya estaban activadas
            if (animComposer.getCurrentAction("Walk").getLength() != 0) {
                animComposer.setCurrentAction("Walk", AnimComposer.DEFAULT_LAYER, true);
            }
            if (animComposer.getCurrentAction("pull").getLength() != 0) {
                animComposer.setCurrentAction("pull", DEFAULT_LAYER, true);
            }
        }
    }

    public Node getPlayerNode() {
        return playerNode;
    }

    public boolean isWalking() {
        return isWalking;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setIsWalking(boolean isWalking) {
        this.isWalking = isWalking;
    }
    
    
}
