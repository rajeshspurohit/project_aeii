package net.toyknight.aeii.screen;

import static net.toyknight.aeii.manager.MapEditor.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import net.toyknight.aeii.GameContext;
import net.toyknight.aeii.entity.*;
import net.toyknight.aeii.manager.MapEditor;
import net.toyknight.aeii.manager.MapEditorListener;
import net.toyknight.aeii.renderer.CanvasRenderer;
import net.toyknight.aeii.screen.editor.*;
import net.toyknight.aeii.screen.widgets.CircleButton;
import net.toyknight.aeii.utils.TileFactory;

/**
 * @author toyknight 6/1/2015.
 */
public class MapEditorScreen extends StageScreen implements MapCanvas, MapEditorListener {

    private final MapEditor editor;

    private final MapViewport viewport;

    private CircleButton btn_hand;
    private CircleButton btn_brush;
    private CircleButton btn_eraser;

    private float scale;

    private int pointer_x;
    private int pointer_y;
    private int cursor_map_x;
    private int cursor_map_y;

    public MapEditorScreen(GameContext context) {
        super(context);
        this.editor = new MapEditor(this);

        this.viewport = new MapViewport();
        this.viewport.width = Gdx.graphics.getWidth();
        this.viewport.height = Gdx.graphics.getHeight();

        this.initComponents();
    }

    private void initComponents() {
        TileSelector tile_selector = new TileSelector(getContext(), getEditor());
        tile_selector.setBounds(0, 0, ts * 4, Gdx.graphics.getHeight());
        this.addActor(tile_selector);

        int usw = ts * 2 + ts / 4 * 3;
        UnitSelector unit_selector = new UnitSelector(getContext(), getEditor());
        unit_selector.setBounds(Gdx.graphics.getWidth() - usw, 0, usw, Gdx.graphics.getHeight());
        this.addActor(unit_selector);

        Table button_bar = new Table();
        button_bar.setBounds(
                tile_selector.getWidth(), 0,
                Gdx.graphics.getWidth() - tile_selector.getWidth() - unit_selector.getWidth(), ts * 33 / 24);
        this.addActor(button_bar);

        btn_hand = new CircleButton(getContext(), CircleButton.LARGE, getResources().getEditorTexture("icon_hand"));
        btn_hand.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getEditor().setMode(MODE_HAND);
            }
        });
        button_bar.add(btn_hand);
        btn_brush = new CircleButton(getContext(), CircleButton.LARGE, getResources().getEditorTexture("icon_brush"));
        btn_brush.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getEditor().setMode(MODE_BRUSH);
            }
        });
        button_bar.add(btn_brush).padLeft(ts / 4);
        btn_eraser = new CircleButton(getContext(), CircleButton.LARGE, getResources().getEditorTexture("icon_eraser"));
        btn_eraser.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getEditor().setMode(MODE_ERASER);
            }
        });
        button_bar.add(btn_eraser).padLeft(ts / 4);

        CircleButton btn_resize = new CircleButton(getContext(), CircleButton.LARGE, getResources().getEditorTexture("icon_resize"));
        btn_resize.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showDialog("resize");
            }
        });
        button_bar.add(btn_resize).padLeft(ts / 4);

        CircleButton btn_save = new CircleButton(getContext(), CircleButton.LARGE, getResources().getEditorTexture("icon_save"));
        btn_save.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showDialog("save");
            }
        });
        button_bar.add(btn_save).padLeft(ts / 4);

        CircleButton btn_load = new CircleButton(getContext(), CircleButton.LARGE, getResources().getEditorTexture("icon_load"));
        btn_load.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showDialog("open");
            }
        });
        button_bar.add(btn_load).padLeft(ts / 4);

        CircleButton btn_exit = new CircleButton(getContext(), CircleButton.LARGE, getResources().getEditorTexture("icon_exit"));
        btn_exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getContext().gotoMainMenuScreen(true);
            }
        });
        button_bar.add(btn_exit).padLeft(ts / 4);
        button_bar.layout();

        int mrw = ts * 8;
        int mrh = ts * 4 + ts / 2;
        MapResizeDialog map_resize_dialog = new MapResizeDialog(this, getEditor());
        map_resize_dialog.setBounds((Gdx.graphics.getWidth() - mrw) / 2, (Gdx.graphics.getHeight() - mrh) / 2, mrw, mrh);
        this.addDialog("resize", map_resize_dialog);

        int msw = ts * 6;
        int msh = ts * 4;
        MapSaveDialog map_save_dialog = new MapSaveDialog(this, getEditor());
        map_save_dialog.setBounds((Gdx.graphics.getWidth() - msw) / 2, (Gdx.graphics.getHeight() - msh) / 2, msw, msh);
        this.addDialog("save", map_save_dialog);

        int mow = ts * 8;
        int moh = ts * 7;
        MapOpenDialog map_open_dialog = new MapOpenDialog(this, getEditor());
        map_open_dialog.setBounds((Gdx.graphics.getWidth() - mow) / 2, (Gdx.graphics.getHeight() - moh) / 2, mow, moh);
        this.addDialog("open", map_open_dialog);
    }

    public MapEditor getEditor() {
        return editor;
    }

    @Override
    public void onModeChange(int mode) {
        btn_hand.setHold(false);
        btn_brush.setHold(false);
        btn_eraser.setHold(false);
        switch (mode) {
            case MODE_HAND:
                btn_hand.setHold(true);
                break;
            case MODE_BRUSH:
                btn_brush.setHold(true);
                break;
            case MODE_ERASER:
                btn_eraser.setHold(true);
                break;
            default:
                //do nothing
        }
    }

    @Override
    public void onMapChange(Map map) {
        int map_width = map.getWidth() * ts();
        int map_height = map.getHeight() * ts();
        viewport.x = (map_width - viewport.width) / 2;
        viewport.y = (map_height - viewport.height) / 2;
    }

    @Override
    public void onMapSaved() {
        closeDialog("save");
    }

    @Override
    public void onError(String message) {
        showNotification(message, null);
    }

    @Override
    public void draw() {
        batch.begin();
        drawMap();
        drawUnits();
        batch.end();
        super.draw();
    }

    private void drawMap() {
        for (int x = 0; x < getMap().getWidth(); x++) {
            for (int y = 0; y < getMap().getHeight(); y++) {
                int sx = getXOnScreen(x);
                int sy = getYOnScreen(y);
                if (isWithinPaintArea(sx, sy)) {
                    int index = getMap().getTileIndex(x, y);
                    getRenderer().drawTile(batch, index, sx, sy);
                    Tile tile = TileFactory.getTile(index);
                    if (tile.getTopTileIndex() != -1) {
                        int top_tile_index = tile.getTopTileIndex();
                        getRenderer().drawTopTile(batch, top_tile_index, sx, sy + ts());
                    }
                }
            }
        }
    }

    private void drawUnits() {
        ObjectMap.Keys<Position> unit_positions = getMap().getUnitPositions();
        for (Position position : unit_positions) {
            Unit unit = getMap().getUnit(position.x, position.y);
            int unit_x = unit.getX();
            int unit_y = unit.getY();
            int sx = getXOnScreen(unit_x);
            int sy = getYOnScreen(unit_y);
            if (isWithinPaintArea(sx, sy)) {
                getRenderer().drawUnit(batch, unit, unit_x, unit_y);
            }
        }
    }

    @Override
    public void act(float delta) {
        getRenderer().update(delta);
        super.act(delta);
    }

    @Override
    public void show() {
        super.show();
        getEditor().initialize(getContext().getUsername());
        this.scale = 1.0f;
        locateViewport(0, 0);
    }

    @Override
    public Map getMap() {
        return getEditor().getMap();
    }

    @Override
    public boolean scrolled(int amount) {
        int previous_map_width = getMap().getWidth() * ts();
        int previous_map_height = getMap().getWidth() * ts();
        float delta = 0.05f * amount;
        scale -= delta;
        if (scale < 0.5f) {
            scale = 0.5f;
        }
        if (scale > 1.0f) {
            scale = 1.0f;
        }
        int new_map_width = getMap().getWidth() * ts();
        int new_map_height = getMap().getWidth() * ts();
        dragViewport(-(previous_map_width - new_map_width) / 2, -(previous_map_height - new_map_height) / 2);
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        boolean event_handled = super.touchDown(screenX, screenY, pointer, button);
        if (!event_handled) {
            if (button == Input.Buttons.LEFT) {
                if (0 <= screenX && screenX <= viewport.width && 0 <= screenY && screenY <= viewport.height) {
                    pointer_x = screenX;
                    pointer_y = screenY;
                    cursor_map_x = createCursorMapX(pointer_x);
                    cursor_map_y = createCursorMapY(pointer_y);
                    switch (getEditor().getMode()) {
                        case MODE_ERASER:
                            getEditor().doErase(cursor_map_x, cursor_map_y);
                            break;
                        case MODE_BRUSH:
                            getEditor().doBrush(cursor_map_x, cursor_map_y);
                            break;
                        default:
                            //do nothing
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        boolean event_handled = super.touchDragged(screenX, screenY, pointer);
        if (!event_handled) {
            if (getEditor().getMode() == MODE_HAND) {
                int delta_x = pointer_x - screenX;
                int delta_y = pointer_y - screenY;
                dragViewport(delta_x, delta_y);
            }
            pointer_x = screenX;
            pointer_y = screenY;
            cursor_map_x = createCursorMapX(pointer_x);
            cursor_map_y = createCursorMapY(pointer_y);
            switch (getEditor().getMode()) {
                case MODE_ERASER:
                    getEditor().doErase(cursor_map_x, cursor_map_y);
                    break;
                case MODE_BRUSH:
                    getEditor().doBrush(cursor_map_x, cursor_map_y);
                    break;
                default:
                    //do nothing
            }
        }
        return true;
    }

//    @Override
//    public boolean mouseMoved(int screenX, int screenY) {
//        boolean event_handled = super.mouseMoved(screenX, screenY);
//        if (!event_handled) {
//            this.pointer_x = screenX;
//            this.pointer_y = screenY;
//            this.cursor_map_x = createCursorMapX(pointer_x);
//            this.cursor_map_y = createCursorMapY(pointer_y);
//        }
//        return true;
//    }

    public void locateViewport(int map_x, int map_y) {
        int center_sx = map_x * ts;
        int center_sy = map_y * ts;
        int map_width = getMap().getWidth() * ts;
        int map_height = getMap().getHeight() * ts;
        if (viewport.width < map_width) {
            viewport.x = center_sx - (viewport.width - ts) / 2;
            if (viewport.x < 0) {
                viewport.x = 0;
            }
            if (viewport.x > map_width - viewport.width) {
                viewport.x = map_width - viewport.width;
            }
        } else {
            viewport.x = (map_width - viewport.width) / 2;
        }
        if (viewport.height < map_height) {
            viewport.y = center_sy - (viewport.height - ts) / 2;
            if (viewport.y < 0) {
                viewport.y = 0;
            }
            if (viewport.y > map_height - viewport.height) {
                viewport.y = map_height - viewport.height;
            }
        } else {
            viewport.y = (map_height - viewport.height) / 2;
        }
    }

    @Override
    public CanvasRenderer getRenderer() {
        return getContext().getCanvasRenderer();
    }

    @Override
    public void focus(int map_x, int map_y, boolean focus_viewport) {
    }

    public void dragViewport(int delta_x, int delta_y) {
        viewport.x += delta_x;
        viewport.y += delta_y;
    }

    @Override
    public int getCursorMapX() {
        return cursor_map_x;
    }

    private int createCursorMapX(int pointer_x) {
        int map_width = getMap().getWidth();
        int cursor_x = (pointer_x + viewport.x) / ts();
        if (cursor_x >= map_width) {
            return map_width - 1;
        }
        if (cursor_x < 0) {
            return 0;
        }
        return cursor_x;
    }

    @Override
    public int getCursorMapY() {
        return cursor_map_y;
    }

    @Override
    public void setOffsetX(float offset_x) {
    }

    @Override
    public void setOffsetY(float offset_y) {
    }

    private int createCursorMapY(int pointer_y) {
        int map_height = getMap().getHeight();
        int cursor_y = (pointer_y + viewport.y) / ts();
        if (cursor_y >= map_height) {
            return map_height - 1;
        }
        if (cursor_y < 0) {
            return 0;
        }
        return cursor_y;
    }

    @Override
    public int getXOnScreen(int map_x) {
        int sx = viewport.x / ts();
        sx = sx > 0 ? sx : 0;
        int x_offset = sx * ts() - viewport.x;
        return (map_x - sx) * ts() + x_offset;
    }

    @Override
    public int getYOnScreen(int map_y) {
        int screen_height = Gdx.graphics.getHeight();
        int sy = viewport.y / ts();
        sy = sy > 0 ? sy : 0;
        int y_offset = sy * ts() - viewport.y;
        return screen_height - ((map_y - sy) * ts() + y_offset) - ts();
    }

    @Override
    public int ts() {
        return (int) (ts * scale);
    }

    @Override
    public boolean isWithinPaintArea(int sx, int sy) {
        return -ts() <= sx && sx <= viewport.width && -ts() <= sy && sy <= viewport.height;
    }

    @Override
    public int getViewportWidth() {
        return viewport.width;
    }

    @Override
    public int getViewportHeight() {
        return viewport.height;
    }

}
