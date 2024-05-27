/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.anim.AnimComposer;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author jesus
 */
public class EnemyLogic {
    private Node enemyNode;
    private AnimComposer animComposer;
    private Vector3f target;
    private int health;

    public EnemyLogic(Node enemyNode, AnimComposer animComposer, Vector3f target, int health) {
        this.enemyNode = enemyNode;
        this.animComposer = animComposer;
        this.target = target;
        this.health = health;
    }

    public Node getEnemyNode() {
        return enemyNode;
    }

    public void update(float tpf, Vector3f playerPosition) {
        // Calcular la rotación necesaria para que el enemigo mire hacia el jugador
        enemyNode.lookAt(playerPosition, Vector3f.UNIT_Y);
        Vector3f direction = target.subtract(enemyNode.getLocalTranslation()).normalize();
        enemyNode.move(direction.mult(tpf)); // Ajusta la velocidad según sea necesario
    }
    
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            // Lógica cuando el enemigo muere
            animComposer.setCurrentAction("StandUpBack");
            enemyNode.removeFromParent();
        }
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
