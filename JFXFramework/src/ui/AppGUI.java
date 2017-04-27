package ui;

import apptemplate.AppTemplate;
import components.AppStyleArbiter;
import controller.FileController;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import propertymanager.PropertyManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static settings.AppPropertyType.*;
import static settings.InitializationParameters.APP_IMAGEDIR_PATH;

/**
 * This class provides the basic user interface for this application, including all the file controls, but it does not
 * include the workspace, which should be customizable and application dependent.
 *
 * @author Richard McKenna, Ritwik Banerjee
 * @author Zhi Zou
 */
public class AppGUI implements AppStyleArbiter {

    protected FileController fileController;   // to react to file-related controls
    protected Stage          primaryStage;     // the application window
    protected Scene          primaryScene;     // the scene graph
    protected BorderPane     appPane;          // the root node in the scene graph, to organize the containers
    protected VBox toolbarPane;      // the top toolbar

    protected Button         createNewProfile;
    protected Button         login;
    protected Button         professaur;
    protected MenuButton        selectMode;
    protected Button         startPlaying;
    protected String         applicationTitle; // the application title
    protected MenuItem menuItem1;
    protected MenuItem menuItem2;
    protected MenuItem menuItem3;
    protected Button home;
    protected Button logout;
    protected Button checkProfile;
    protected Button pauseOrResume;
    protected Button help;

    private int appWindowWidth;  // optional parameter for window width that can be set by the application
    private int appWindowHeight; // optional parameter for window height that can be set by the application
    private int stageNumber;
    /**
     * This constructor initializes the file toolbar for use.
     *
     * @param initPrimaryStage The window for this application.
     * @param initAppTitle     The title of this application, which
     *                         will appear in the window bar.
     * @param app              The app within this gui is used.
     */
    public AppGUI(Stage initPrimaryStage, String initAppTitle, AppTemplate app) throws IOException, InstantiationException {
        this(initPrimaryStage, initAppTitle, app, -1, -1);
    }

    public AppGUI(Stage primaryStage, String applicationTitle, AppTemplate appTemplate, int appWindowWidth, int appWindowHeight) throws IOException, InstantiationException {
        this.appWindowWidth = appWindowWidth;
        this.appWindowHeight = appWindowHeight;
        this.primaryStage = primaryStage;
        this.applicationTitle = applicationTitle;
        initializeToolbar();                    // initialize the top toolbar
        initializeToolbarHandlers(appTemplate); // set the toolbar button handlers
        initializeWindow();                     // start the app window (without the application-specific workspace)

    }

    public FileController getFileController() {
        return this.fileController;
    }

    public VBox getToolbarPane() { return toolbarPane; }

    public BorderPane getAppPane() { return appPane; }
    
    /**
     * Accessor method for getting this application's primary stage's,
     * scene.
     *
     * @return This application's window's scene.
     */
    public Scene getPrimaryScene() { return primaryScene; }
    
    /**
     * Accessor method for getting this application's window,
     * which is the primary stage within which the full GUI will be placed.
     *
     * @return This application's primary stage (i.e. window).
     */
    public Stage getWindow() { return primaryStage; }
    
    /**
     * This function initializes all the buttons in the toolbar at the top of
     * the application window. These are related to file management.
     */
    private void initializeToolbar() throws IOException {
        toolbarPane = new VBox();

        toolbarPane.setSpacing(20);
        createNewProfile = new Button("Create New Profile");
        toolbarPane.setMinWidth(230);
        login = new Button("Login");
        help = new Button("Help");
        professaur = new Button("Zhi Zou");
        professaur.setDisable(true);
        menuItem1 = new MenuItem("AG");
        menuItem2 = new MenuItem("NH");

        menuItem3 = new MenuItem("OZ");
        home = new Button("Home");
        selectMode = new MenuButton("Select Mode",null,menuItem1,menuItem2,menuItem3);
        startPlaying = new Button("Start Playing");
        logout = new Button("Logout");
        checkProfile = new Button("Check Profile");
        pauseOrResume = new Button("Pause/Resume");


        toolbarPane.getChildren().add(createNewProfile);
        toolbarPane.getChildren().add(login);




    }

    private void initializeToolbarHandlers(AppTemplate app) throws InstantiationException {
        try {
            Method getFileControllerClassMethod = app.getClass().getMethod("getFileControllerClass");
            String fileControllerClassName = (String) getFileControllerClassMethod.invoke(app);
            Class<?> klass = Class.forName("controller." + fileControllerClassName);
            Constructor<?> constructor = klass.getConstructor(AppTemplate.class);
            fileController = (FileController) constructor.newInstance(app);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        createNewProfile.setOnAction( e->{
            fileController.handleCreateNewProfile();
        });
        login.setOnAction(event -> {
            fileController.handleLogin();
        });
        menuItem1.setOnAction(event -> {
            fileController.handleWordType(0);
        });
        menuItem2.setOnAction(event -> {
            fileController.handleWordType(1);
        });
        menuItem3.setOnAction(event -> {
            fileController.handleWordType(2);
        });

        home.setOnAction(event -> {
            fileController.handleHome();
        });
        startPlaying.setOnAction(event -> {
            fileController.handleStart();
        });
        logout.setOnAction(event -> {
            fileController.handleLogout();
        });
        checkProfile.setOnAction(event -> {
            fileController.handleCheckProfile();
        });
        pauseOrResume.setOnAction(event -> {
            fileController.handlePauseResume();
        });
        help.setOnAction(event -> {
            fileController.handleHelp();
        });
    }

    public void updateWorkspaceToolbar(boolean savable) {

    }

    private void initializeWindow() throws IOException {
        PropertyManager propertyManager = PropertyManager.getManager();

        // SET THE WINDOW TITLE
        primaryStage.setTitle(applicationTitle);

        // add the toolbar to the constructed workspace
        Label head = new Label("!! Buzz Word !!");
        Font font = new Font(30);
        head.setFont(javafx.scene.text.Font.font(30));
        head.setTextFill(javafx.scene.paint.Paint.valueOf("white"));
        BorderPane header = new BorderPane();
        header.setCenter(head);
        appPane = new BorderPane();
        appPane.setLeft(toolbarPane);
        appPane.setTop(header);
        //BuzzWord
        BorderPane figurePane = new BorderPane();
        figurePane.prefHeightProperty().setValue(Integer.parseInt(propertyManager.getPropertyValue(APP_WINDOW_HEIGHT)));
        figurePane.prefWidthProperty().setValue(Integer.parseInt(propertyManager.getPropertyValue(APP_WINDOW_WIDTH))/2);
        //add Buzz Word Circle
        Circle[] circles = new Circle[16];

        for(int i = 0; i<16 ; i++){
            Text t =null;

            if(i<4) {
                circles[i] = new Circle(140 + i * 100, 100, 30);
                if(i==0){
                    t= new Text(140 + i * 100-8, 100+8,"B");
                    t.setStroke(Color.valueOf("Black"));
                    t.setFill(Color.valueOf("Black"));
                    t.setFont(new Font(25));
                }
                if(i==1){
                    t= new Text(140 + i * 100-8, 100+8,"u");
                    t.setStroke(Color.valueOf("Black"));
                    t.setFill(Color.valueOf("Black"));
                    t.setFont(new Font(25));
                }
            }
            else if (i<8){
                int j = i-4;
                circles[i] = new Circle(140 + j * 100, 200, 30);
                if(i==4){
                    t= new Text(140 + j * 100-8, 200+8,"z");
                    t.setStroke(Color.valueOf("Black"));
                    t.setFill(Color.valueOf("Black"));
                    t.setFont(new Font(25));
                }
                if(i==5){
                    t= new Text(140 + j * 100-8, 200+8,"z");
                    t.setStroke(Color.valueOf("Black"));
                    t.setFill(Color.valueOf("Black"));
                    t.setFont(new Font(25));
                }
            }
            else if (i<12){
                int j = i-8;
                circles[i] = new Circle(140 + j * 100, 300, 30);
                if(i==10){
                    t= new Text(140 + j * 100-8, 300+8,"W");
                    t.setStroke(Color.valueOf("Black"));
                    t.setFill(Color.valueOf("Black"));
                    t.setFont(new Font(25));
                }
                if(i==11){
                    t= new Text(140 + j * 100-8, 300+8,"o");
                    t.setStroke(Color.valueOf("Black"));
                    t.setFill(Color.valueOf("Black"));
                    t.setFont(new Font(25));
                }
            }
            else {
                int j = i-12;
                circles[i] = new Circle(140 + j * 100, 400, 30);
                if(i==14){
                    t= new Text(140 + j * 100-8, 400+8,"r");
                    t.setStroke(Color.valueOf("Black"));
                    t.setFill(Color.valueOf("Black"));
                    t.setFont(new Font(25));
                }
                if(i==15){
                    t= new Text(140 + j * 100-8, 400+8,"d");
                    t.setStroke(Color.valueOf("Black"));
                    t.setFill(Color.valueOf("Black"));
                    t.setFont(new Font(25));
                }
            }
            circles [i].setFill(Color.valueOf("gainsboro"));
            circles[i].setStroke(Color.valueOf("Green"));

            figurePane.getChildren().addAll(circles[i]);
            if(t!=null){
                figurePane.getChildren().addAll(t);
            }
        }




        appPane.setCenter(figurePane);

        primaryScene = appWindowWidth < 1 || appWindowHeight < 1 ? new Scene(appPane)
                                                                 : new Scene(appPane,
                                                                             appWindowWidth,
                                                                             appWindowHeight);

        URL imgDirURL = AppTemplate.class.getClassLoader().getResource(APP_IMAGEDIR_PATH.getParameter());
        if (imgDirURL == null)
            throw new FileNotFoundException("Image resrouces folder does not exist.");
        try (InputStream appLogoStream = Files.newInputStream(Paths.get(imgDirURL.toURI()).resolve(propertyManager.getPropertyValue(APP_LOGO)))) {
            primaryStage.getIcons().add(new Image(appLogoStream));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        primaryStage.setScene(primaryScene);
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            if(stageNumber==3){
                fileController.handlePauseResume();
            }

            YesNoCancelDialogSingleton yesNoCancelDialog = YesNoCancelDialogSingleton.getSingleton();

            yesNoCancelDialog.show("Notice",
                   "Are you sure about exiting the game?");

            if (yesNoCancelDialog.getSelection().equals(YesNoCancelDialogSingleton.YES))
                System.exit(0);
            if(yesNoCancelDialog.getSelection().equals(YesNoCancelDialogSingleton.NO))
                fileController.handlePauseResume();
            if(yesNoCancelDialog.getSelection().equals(YesNoCancelDialogSingleton.CANCEL))
                fileController.handlePauseResume();



        });

        primaryStage.show();
    }
    
    /**
     * This is a public helper method for initializing a simple button with
     * an icon and tooltip and placing it into a toolbar.
     *
     * @param toolbarPane Toolbar pane into which to place this button.
     * @param icon        Icon image file name for the button.
     * @param tooltip     Tooltip to appear when the user mouses over the button.
     * @param disabled    true if the button is to start off disabled, false otherwise.
     * @return A constructed, fully initialized button placed into its appropriate
     * pane container.
     */
    public Button initializeChildButton(Pane toolbarPane, String icon, String tooltip, boolean disabled) throws IOException {
        PropertyManager propertyManager = PropertyManager.getManager();

        URL imgDirURL = AppTemplate.class.getClassLoader().getResource(APP_IMAGEDIR_PATH.getParameter());
        if (imgDirURL == null)
            throw new FileNotFoundException("Image resources folder does not exist.");

        Button button = new Button();
        try (InputStream imgInputStream = Files.newInputStream(Paths.get(imgDirURL.toURI()).resolve(propertyManager.getPropertyValue(icon)))) {
            Image buttonImage = new Image(imgInputStream);
            button.setDisable(disabled);
            button.setGraphic(new ImageView(buttonImage));
            Tooltip buttonTooltip = new Tooltip(propertyManager.getPropertyValue(tooltip));
            button.setTooltip(buttonTooltip);
            toolbarPane.getChildren().add(button);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return button;
    }
    
    /**
     * This function specifies the CSS style classes for the controls managed
     * by this framework.
     */
    @Override
    public void initStyle() {
        // currently, we do not provide any stylization at the framework-level

    }

    public void changeToolStage(int stage) throws IOException, InstantiationException {
        if (stage==0){
            stageNumber = 0;
            toolbarPane.getChildren().clear();

            toolbarPane.getChildren().add(createNewProfile);
            toolbarPane.getChildren().add(login);
        }
        else if(stage == 1){
            stageNumber = 1;
           // toolbarPane.getChildren().removeAll(createNewProfile,login);
            toolbarPane.getChildren().clear();
            toolbarPane.getChildren().addAll(professaur,selectMode,logout,checkProfile,startPlaying,help);

        }
        else if(stage == 2){
            stageNumber = 2;
            toolbarPane.getChildren().clear();
            toolbarPane.getChildren().addAll(professaur,home);
        }
        else if (stage == 3){
            stageNumber = 3;
            toolbarPane.getChildren().clear();
            toolbarPane.getChildren().addAll(professaur,pauseOrResume,home);
        }
    }
}
