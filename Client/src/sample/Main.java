package sample;

import UserModule.UserHelper;
import client.ToDoListClient;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Item;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Main extends Application {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");//×¢ÒâÔÂ·ÝÊÇMM
    private final TableView<Item> tableView = new TableView<>();
    private final ObservableList<Item> data = FXCollections.observableArrayList();

    public Main() throws ParseException {
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        ToDoListClient.init();

//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Login");
//        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();

        // gridPane before login
        GridPane loginGridPane = new GridPane();
        loginGridPane.setAlignment(Pos.CENTER);
        loginGridPane.setHgap(10);
        loginGridPane.setVgap(10);
        loginGridPane.setPadding(new Insets(25, 25, 25, 25));

        // loginGridPane row 0, welcome msg
        Text sceneTitle = new Text("Welcome to TO-DO-LIST system!");
        sceneTitle.setFont(Font.font("Microsoft YaHei", FontWeight.NORMAL, 20));
        loginGridPane.add(sceneTitle, 0, 0, 2, 1);

        // loginGridPane row 1, username
        final Label username = new Label("ÕËºÅ");
        loginGridPane.add(username, 0, 1);
        final TextField usernameTextField = new TextField();
        loginGridPane.add(usernameTextField, 1, 1);

        // loginGridPane row 2, password
        Label pwd = new Label("ÃÜÂë");
        loginGridPane.add(pwd, 0, 2);
        final PasswordField pwdPwdField = new PasswordField();
        loginGridPane.add(pwdPwdField, 1, 2);

        // loginGridPane row 3, register-btn
        Button registerButton = new Button("×¢²á");
        HBox hBoxReg = new HBox(1);
        hBoxReg.setAlignment(Pos.BOTTOM_LEFT);
        hBoxReg.getChildren().add(registerButton);
        loginGridPane.add(hBoxReg, 0, 4);

        // loginGridPane row 3, login-btn
        Button loginButton = new Button("µÇÂ¼");
        HBox hBoxLog = new HBox(1);
        hBoxLog.setAlignment(Pos.BASELINE_RIGHT);
        hBoxLog.getChildren().add(loginButton);
        loginGridPane.add(hBoxLog, 1, 4);

        // loginGridPane row 6, tips
        final Text tipText = new Text();
        loginGridPane.add(tipText, 1, 6);

        // scene of login
        Scene loginScene = new Scene(loginGridPane, 400, 275);
        primaryStage.setScene(loginScene);

        final Scene tableScene = new Scene(new Group());

        tableView.setEditable(false);

        final TableColumn startTimeCol = new TableColumn("Start Time");
        startTimeCol.setMinWidth(300);
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));

        TableColumn endTimeCol = new TableColumn("End Time");
        endTimeCol.setMinWidth(300);
        endTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));

        TableColumn markCol = new TableColumn("Mark");
        markCol.setMinWidth(300);
        markCol.setCellValueFactory(new PropertyValueFactory<>("label"));

        tableView.setItems(data);
        tableView.getColumns().addAll(startTimeCol, endTimeCol, markCol);

        final Text mainSceneTitle = new Text("Welcome, ");
        mainSceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        final TextField addStartTime = new TextField();
        addStartTime.setPromptText("start time");
        addStartTime.setMaxWidth(startTimeCol.getPrefWidth());
        addStartTime.setMinWidth(200);

        final TextField addEndTime = new TextField();
        addEndTime.setPromptText("end time");
        addEndTime.setMaxWidth(endTimeCol.getPrefWidth());
        addEndTime.setMinWidth(200);

        final TextField addMark = new TextField();
        addMark.setPromptText("marks");
        addMark.setMaxWidth(markCol.getPrefWidth());
        addMark.setMinWidth(250);

        final Button addBtn = new Button("Ìí¼Ó");
        final Button searchBtn = new Button("²éÑ¯");
        final Button delBtn = new Button("É¾³ý");
        final Button clearBtn = new Button("Çå³ý");

        final HBox addHBox = new HBox();
        addHBox.getChildren().addAll(addStartTime, addEndTime, addMark, addBtn, searchBtn, delBtn, clearBtn);
        addHBox.setSpacing(3);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(mainSceneTitle, tableView, addHBox);

        ((Group) tableScene.getRoot()).getChildren().addAll(vbox);

        clearBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ToDoListClient.user.clear();
                data.clear();
            }
        });

        delBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                Item itemToDel = tableView.getSelectionModel().getSelectedItem();
                int index = tableView.getSelectionModel().getSelectedIndex();
                ToDoListClient.user.delete(String.valueOf(index));
                data.remove(index);
            }
        });

        searchBtn.setOnAction((ActionEvent e) -> {
            try {
                String startTime = addStartTime.getText();
                String endTime = addEndTime.getText();

                String jsonStr = ToDoListClient.user.query(startTime, endTime);
                List lists = JSONObject.parseArray(jsonStr);
                data.clear();
                for (int i = 0; i < lists.size(); i++) {
                    JSONObject jsonObject = (JSONObject) lists.get(i);
                    String startTime1 = jsonObject.getString("startTime");
                    String endTime1 = jsonObject.getString("endTime");
                    String mark = jsonObject.getString("label");
                    data.add(new Item(new Date(Long.parseLong(startTime1)),
                            new Date(Long.parseLong(endTime1)), mark));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        addBtn.setOnAction((ActionEvent e) -> {
            try {
                String startTime = addStartTime.getText();
                String endTime = addEndTime.getText();
                String mark = addMark.getText();
                if (ToDoListClient.user.add(startTime, endTime, mark)) {
                    data.add(new Item(simpleDateFormat.parse(startTime),
                            simpleDateFormat.parse(endTime), mark));
                    addStartTime.clear();
                    addEndTime.clear();
                    addMark.clear();
                }
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        });

        // handle register event
        registerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (ToDoListClient.creator.register(usernameTextField.getText(), pwdPwdField.getText())) {
                    tipText.setFill(Color.GREEN);
                    tipText.setText("×¢²á³É¹¦");
                } else {
                    tipText.setFill(Color.FIREBRICK);
                    tipText.setText("×¢²áÊ§°Ü");
                }
            }
        });

        // handle login even --> will open new page if login successfully
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {

                    if (ToDoListClient.creator.login(usernameTextField.getText(), pwdPwdField.getText())) {
                        try {
                            ToDoListClient.user = UserHelper.narrow(
                                    ToDoListClient.ncRef.resolve_str(usernameTextField.getText()));
                        } catch (NotFound | InvalidName | CannotProceed e) {
                            e.printStackTrace();
                        }
                        tipText.setFill(Color.GREEN);
                        tipText.setText("µÇÂ¼³É¹¦");
                        primaryStage.setTitle("To-DO-LIST Homepage");
                        primaryStage.setWidth(950);
                        primaryStage.setHeight(600);
                        mainSceneTitle.setText(mainSceneTitle.getText() + usernameTextField.getText() + "!");
                        primaryStage.setScene(tableScene);

                        String jsonStr = ToDoListClient.user.show();

                        List lists = JSONObject.parseArray(jsonStr);
                        for (int i = 0; i < lists.size(); i++) {
                            JSONObject jsonObject = (JSONObject) lists.get(i);
                            String startTime = jsonObject.getString("startTime");
                            String endTime = jsonObject.getString("endTime");
                            String mark = jsonObject.getString("label");
                            data.add(new Item(new Date(Long.parseLong(startTime)),
                                    new Date(Long.parseLong(endTime)), mark));
                        }

                    } else {
                        tipText.setFill(Color.FIREBRICK);
                        tipText.setText("ÕË»§»òÃÜÂë´íÎó£¬µÇÂ¼Ê§°Ü");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    tipText.setFill(Color.FIREBRICK);
                    tipText.setText("µÇÂ¼Ê§°Ü");
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
