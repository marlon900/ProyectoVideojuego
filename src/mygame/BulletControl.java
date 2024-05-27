/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jesus
 */
public class BulletControl extends AbstractControl {

    private final Vector3f direction;
    private final float speed = 50f;
    private List<EnemyLogic> enemies = new ArrayList<>();

    /**
     *
     * @param direction
     * @param enemies
     */
    public BulletControl(Vector3f direction, List<EnemyLogic> enemies) {
        this.direction = direction;
        this.enemies = enemies;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (spatial != null) {
            spatial.move(direction.mult(speed * tpf));
            
            for (EnemyLogic enemy : enemies) {
                if (spatial.getWorldBound().intersects(enemy.getEnemyNode().getWorldBound())) {
                    // Lógica cuando el disparo colisiona con un enemigo
                    System.out.println("Hit!");
                    spatial.removeFromParent();
                    enemy.takeDamage(5);
                    break;
                }
            }
            
            if (spatial.getWorldTranslation().length() > 100) {
                spatial.removeFromParent();
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // No es necesario implementar esto para nuestro control
    }
}
