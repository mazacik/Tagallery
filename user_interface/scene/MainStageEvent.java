package user_interface.scene;

import control.Reload;
import control.Select;
import control.Target;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.input.KeyEvent;
import lifecycle.InstanceManager;
import user_interface.singleton.center.BaseTileEvent;
import user_interface.utils.SceneUtil;

public class MainStageEvent {
    private SimpleBooleanProperty shiftDown = new SimpleBooleanProperty(false);

    public MainStageEvent() {
        onKeyPress();
        onKeyRelease();
    }
    private void onKeyPress() {
        InstanceManager.getMainStage().getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            Select select = InstanceManager.getSelect();
            Reload reload = InstanceManager.getReload();
            Target target = InstanceManager.getTarget();

            switch (event.getCode()) {
                case ESCAPE:
                    if (SceneUtil.isFullView()) {
                        SceneUtil.swapViewMode();
                    } else if (InstanceManager.getSelectPane().getTfSearch().isFocused()) {
                        InstanceManager.getToolbarPane().requestFocus();
                    }

                    break;
                case E:
                    BaseTileEvent.onGroupButtonPress(target.getCurrentTarget());
                    reload.doReload();
                    break;
                case R:
                    select.setRandom();
                    reload.doReload();
                    break;
                case F:
                    SceneUtil.swapViewMode();
                    reload.doReload();
                    break;
                case SHIFT:
                    shiftDown.setValue(true);
                    InstanceManager.getSelect().setShiftStart(target.getCurrentTarget());
                    break;
                case W:
                case A:
                case S:
                case D:
                    target.move(event.getCode());

                    if (event.isShiftDown()) select.shiftSelectTo(target.getCurrentTarget());
                    else select.set(target.getCurrentTarget());

                    InstanceManager.getReload().doReload();
                    break;
                default:
                    break;
            }
        });
    }
    private void onKeyRelease() {
        InstanceManager.getMainStage().getScene().addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            switch (event.getCode()) {
                case SHIFT:
                    shiftDown.setValue(false);
                    break;
                default:
                    break;
            }
        });
    }

    public boolean isShiftDown() {
        return shiftDown.get();
    }
    public SimpleBooleanProperty shiftDownProperty() {
        return shiftDown;
    }
}
