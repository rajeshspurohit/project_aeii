package net.toyknight.aeii.screen.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import net.toyknight.aeii.GameContext;
import net.toyknight.aeii.ResourceManager;
import net.toyknight.aeii.manager.MapEditor;
import net.toyknight.aeii.screen.widgets.AEIIContainer;
import net.toyknight.aeii.utils.UnitFactory;

/**
 * @author toyknight 7/9/2015.
 */
public class UnitSelector extends AEIIContainer {

    private final MapEditor editor;

    public UnitSelector(GameContext context, MapEditor editor) {
        super(context);
        this.editor = editor;
        this.initComponents();
    }

    private void initComponents() {
        ImageButton[] btn_team = new ImageButton[4];
        for (int i = 0; i < 4; i++) {
            TextureRegionDrawable team_bg = ResourceManager.createDrawable(getResources().getTeamBackground(i));
            team_bg.setMinWidth(ts);
            team_bg.setMinHeight(ts);
            btn_team[i] = new ImageButton(team_bg);
        }
        Table unit_table = new Table();
        unit_table.padBottom(ts / 4);
        btn_team[0].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedTeam(0);
            }
        });
        unit_table.add(btn_team[0]).padTop(ts / 4);
        btn_team[1].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedTeam(1);
            }
        });
        unit_table.add(btn_team[1]).padLeft(ts / 4).padTop(ts / 4).row();
        btn_team[2].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedTeam(2);
            }
        });
        unit_table.add(btn_team[2]).padTop(ts / 4);
        btn_team[3].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedTeam(3);
            }
        });
        unit_table.add(btn_team[3]).padLeft(ts / 4).padTop(ts / 4).row();

        //add units
        int index = 0;
        for (int i = 0; i < UnitFactory.getUnitCount(); i++) {
            UnitButton btn_unit = new UnitButton(getContext(), editor, UnitFactory.getSample(i));
            if (index % 2 == 0) {
                unit_table.add(btn_unit).padTop(ts / 4);
            } else {
                unit_table.add(btn_unit).padLeft(ts / 4).padTop(ts / 4).row();
            }
            index++;
        }

        ScrollPane sp_unit_table = new ScrollPane(unit_table);
        sp_unit_table.setScrollBarPositions(false, true);
        sp_unit_table.setFadeScrollBars(false);
        sp_unit_table.setBounds(
                Gdx.graphics.getWidth() - ts * 2 - ts / 4 * 3, ts,
                ts * 2 - ts / 4 * 3, Gdx.graphics.getHeight() - ts);
        this.setActor(sp_unit_table);
    }

    public void setSelectedTeam(int team) {
        editor.setSelectedTeam(team);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(getResources().getPanelBackground(), getX(), getY(), getWidth(), getHeight());
        super.draw(batch, parentAlpha);
        getContext().getBorderRenderer().drawBorder(batch, getX(), getY(), getWidth(), getHeight());
        batch.flush();
    }

}
