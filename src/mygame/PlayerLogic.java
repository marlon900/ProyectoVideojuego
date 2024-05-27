/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.anim.AnimComposer;
import static com.jme3.anim.AnimComposer.DEFAULT_LAYER;
import com.jme3.scene.Node;

/**
 *
 * @author jesus
 */
public class PlayerLogic {
    private Node playerNode;
    private AnimComposer animComposer;
    private boolean isWalking = false;
    private int health;

    public PlayerLogic(Node playerNode, AnimComposer animComposer, int health) {
        this.playerNode = playerNode;
        this.animComposer = animComposer;
        this.health = health;
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

    public Node getPlayerNode() {
        return playerNode;
    }

    public boolean isWalking() {
        return isWalking;
    }

    public void setIsWalking(boolean isWalking) {
        this.isWalking = isWalking;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
