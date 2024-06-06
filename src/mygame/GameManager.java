/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;

/**
 *
 * @author kirih
 */

public class GameManager extends BaseAppState {
    private SimpleApplication app;
    private BitmapText gameOverText;

    @Override
    protected void initialize(Application app) {
        this.app = (SimpleApplication) app;

        BitmapFont font = this.app.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
        gameOverText = new BitmapText(font, false);
        gameOverText.setSize(font.getCharSet().getRenderedSize());
        gameOverText.setText("Perdiste");
        gameOverText.setLocalTranslation(
            (this.app.getCamera().getWidth() - gameOverText.getLineWidth()) / 2,
            this.app.getCamera().getHeight() / 2,
            0
        );
        gameOverText.setColor(com.jme3.math.ColorRGBA.Red);
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

    public void showGameOver() {
    if (app != null) {
        app.getGuiNode().attachChild(gameOverText);
    } else {
        // Manejar el caso donde app es null
        System.err.println("Error: app is null, cannot attach gameOverText to GuiNode.");
    }
}
}