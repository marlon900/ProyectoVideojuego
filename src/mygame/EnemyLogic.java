/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.anim.AnimClip;
import com.jme3.anim.AnimComposer;
import com.jme3.audio.AudioNode;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
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
    private PlayerLogic player;
    private boolean isAttacking;
    private Geometry healthBar;
    private float attackCooldown = 2f;
    private float attackTimer = 2f;
    private boolean die = false;
    // Declarar el nodo de audio para el sonido de daño
    private AudioNode damageSound;

    public EnemyLogic(Node enemyNode, AnimComposer animComposer, Vector3f target, int health, PlayerLogic player, AudioNode damageSound) {
        this.enemyNode = enemyNode;
        this.animComposer = animComposer;
        this.target = target;
        this.health = health;
        this.player = player;
        this.isAttacking = false;
        this.damageSound = damageSound;
    }

    public Node getEnemyNode() {
        return enemyNode;
    }

    public void update(float tpf, Vector3f playerPosition) {
        // Calcular la rotación necesaria para que el enemigo mire hacia el jugador
        enemyNode.lookAt(playerPosition, Vector3f.UNIT_Y);
        Vector3f direction = target.subtract(enemyNode.getLocalTranslation()).normalize();
        enemyNode.move(direction.mult(tpf)); // Ajusta la velocidad según sea necesario

        // Verificar la distancia al jugador para activar la animación de ataque
        float distanceToPlayer = enemyNode.getLocalTranslation().distance(playerPosition);
        
        // Reducir el temporizador de enfriamiento
        if (attackTimer > 0) {
            attackTimer -= tpf;
        }

        // Activar la animación de ataque si el enemigo está lo suficientemente cerca y el temporizador de enfriamiento ha terminado
        if (distanceToPlayer < 5 && !isAttacking && attackTimer <= 0) {
            animComposer.setCurrentAction("SliceVertical");
            isAttacking = true;
        }

        if (isAttacking && distanceToPlayer < 2) {
            player.takeDamage(10);
            attackTimer = attackCooldown;
            isAttacking = false;
        }
    }

    public void takeDamage(int damage) {
        // Reducir la salud y actualizar la barra de salud
        if (health <= 0) {
            // Lógica cuando el enemigo muere
            enemyNode.removeFromParent();
            die = true;
        } else {
            updateHealthBar();
            // Reproducir el sonido de daño
            if (damageSound != null) {
                damageSound.playInstance();
            }
        }
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
    
    public void setHealthBar(Geometry healthBar) {
        this.healthBar = healthBar;
    }
    
    private void updateHealthBar() {
        if (healthBar != null) {
            float healthPercentage = (float) health / 100f;
            healthBar.setLocalScale(healthPercentage, 1, 1);
        }
    }

    public boolean isDie() {
        return die;
    }
}
