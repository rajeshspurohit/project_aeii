package com.toyknight.aeii.renderer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.toyknight.aeii.ResourceManager;
import com.toyknight.aeii.entity.Status;
import com.toyknight.aeii.entity.Unit;
import com.toyknight.aeii.screen.GameScreen;

/**
 * Created by toyknight on 4/19/2015.
 */
public class UnitRenderer {

    private final int ts;
    private final GameScreen screen;

    private float state_time = 0f;

    public UnitRenderer(GameScreen screen, int ts) {
        this.ts = ts;
        this.screen = screen;
    }

    public void drawUnit(SpriteBatch batch, Unit unit, int map_x, int map_y) {
        drawUnit(batch, unit, map_x, map_y, 0, 0);
    }

    public void drawUnit(SpriteBatch batch, Unit unit, int map_x, int map_y, float offset_x, float offset_y) {
        TextureRegion unit_texture;
        if (unit.isStandby()) {
            batch.setShader(ResourceManager.getGrayscaleShader());
            unit_texture = ResourceManager.getUnitTexture(unit.getPackage(), unit.getTeam(), unit.getIndex(), unit.getLevel(), 0);
        } else {
            unit_texture = ResourceManager.getUnitTexture(unit.getPackage(), unit.getTeam(), unit.getIndex(), unit.getLevel(), getCurrentFrame());
        }
        int screen_x = screen.getXOnScreen(map_x);
        int screen_y = screen.getYOnScreen(map_y);
        batch.draw(unit_texture, screen_x + offset_x, screen_y + offset_y, ts, ts);
        batch.flush();
        batch.setShader(null);
    }

    public void drawUnitWithInformation(SpriteBatch batch, Unit unit, int map_x, int map_y) {
        drawUnitWithInformation(batch, unit, map_x, map_y, 0, 0);
    }

    public void drawUnitWithInformation(SpriteBatch batch, Unit unit, int map_x, int map_y, float offset_x, float offset_y) {
        drawUnit(batch, unit, map_x, map_y, offset_x, offset_y);
        int screen_x = screen.getXOnScreen(map_x);
        int screen_y = screen.getYOnScreen(map_y);
        //draw health points
        if (unit.getCurrentHp() < unit.getMaxHp()) {
            FontRenderer.drawSNumber(batch, unit.getCurrentHp(), screen_x, screen_y);
        }
        //draw status
        int sw = ts / 24 * ResourceManager.getStatusTexture(0).getRegionWidth();
        int sh = ts / 24 * ResourceManager.getStatusTexture(0).getRegionHeight();
        if (unit.getStatus() != null) {
            switch (unit.getStatus().getType()) {
                case Status.POISONED:
                    batch.draw(ResourceManager.getStatusTexture(0), screen_x, screen_y + ts - sh, sw, sh);
                    break;
                default:
                    //do nothing
            }
        }
        batch.flush();
    }

    private int getCurrentFrame() {
        return ((int) (state_time / 0.3f)) % 2;
    }

    public void update(float delta) {
        state_time += delta;
    }

}