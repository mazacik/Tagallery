package user_interface.factory.stage;

import database.object.InfoObject;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import system.CommonUtil;
import user_interface.factory.NodeFactory;
import user_interface.factory.node.TitleBar;
import user_interface.factory.util.ColorUtil;
import user_interface.factory.util.enums.ColorType;

public class InfoObjectEditStage extends Stage {
    private final TextField nodeGroupEdit = new TextField();
    private final TextField nodeNameEdit = new TextField();
    private final Label nodeGroup = NodeFactory.getLabel("Group", ColorType.DEF, ColorType.DEF);
    private final Label nodeName = NodeFactory.getLabel("Name", ColorType.DEF, ColorType.DEF);
    private final Label nodeOK = NodeFactory.getLabel("OK", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
    private final Label nodeCancel = NodeFactory.getLabel("Cancel", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);

    private InfoObject infoObject = null;

    public InfoObjectEditStage(InfoObject infoObject) {
        BorderPane borderPane = new BorderPane();

        nodeGroup.setPrefWidth(60);
        nodeGroupEdit.setPrefWidth(200);
        nodeName.setPrefWidth(60);
        nodeNameEdit.setPrefWidth(200);

        nodeGroupEdit.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 1, 1, 1))));
        nodeNameEdit.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 1, 1, 1))));

        nodeGroupEdit.setFont(CommonUtil.getFont());
        nodeNameEdit.setFont(CommonUtil.getFont());

        borderPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                getValue();
                close();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                close();
            }
        });

        NodeFactory.addNodeToBackgroundManager(nodeGroupEdit, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        NodeFactory.addNodeToBackgroundManager(nodeNameEdit, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        nodeGroupEdit.requestFocus();
        if (infoObject != null) {
            nodeGroupEdit.setText(infoObject.getGroup());
            nodeNameEdit.setText(infoObject.getName());
        }

        nodeOK.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.getValue();
                this.close();
            }
        });
        nodeCancel.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.close();
            }
        });

        HBox hBoxGroup = NodeFactory.getHBox(ColorType.DEF, nodeGroup, nodeGroupEdit);
        HBox hBoxName = NodeFactory.getHBox(ColorType.DEF, nodeName, nodeNameEdit);
        VBox vBoxHelper = NodeFactory.getVBox(ColorType.DEF, hBoxGroup, hBoxName);
        double padding = CommonUtil.getPadding();
        vBoxHelper.setPadding(new Insets(padding, padding, 0, 0));
        vBoxHelper.setSpacing(padding);

        HBox hBoxBottom = NodeFactory.getHBox(ColorType.DEF, nodeCancel, nodeOK);

        borderPane.setTop(new TitleBar(this, "Create a new tag"));
        borderPane.setCenter(vBoxHelper);
        borderPane.setBottom(hBoxBottom);

        borderPane.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 1, 1, 1))));

        this.initStyle(StageStyle.UNDECORATED);
        setAlwaysOnTop(true);
        setScene(new Scene(borderPane));
        setResizable(false);
        this.setOnShown(event -> {
            this.centerOnScreen();
            CommonUtil.updateNodeProperties(this.getScene());
        });

        showAndWait();
    }
    public InfoObjectEditStage() {
        this(null);
    }

    public InfoObject getResult() {
        return infoObject;
    }
    private void getValue() {
        String group = nodeGroupEdit.getText();
        String name = nodeNameEdit.getText();
        if (!group.isEmpty() && !name.isEmpty()) {
            infoObject = new InfoObject(group, name);
        }
    }
}