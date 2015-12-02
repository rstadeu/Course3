/* JavaFX application used to display routes with Google Maps API
 * @author Adam Setters
 */
package application;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import application.controllers.ClientMapController;
import application.controllers.FetchController;
import application.controllers.RouteController;
import application.services.ClientService;
import application.services.GeneralService;
import application.services.RouteService;
import gmapsfx.GoogleMapView;
import gmapsfx.MapComponentInitializedListener;
import gmapsfx.javascript.object.GoogleMap;
import gmapsfx.javascript.object.LatLong;
import gmapsfx.javascript.object.MapOptions;
import gmapsfx.javascript.object.MapTypeIdEnum;

public class MapApp extends Application
					implements MapComponentInitializedListener {

	protected GoogleMapView mapComponent;
	protected GoogleMap map;
    protected BorderPane bp;
    protected Stage primaryStage;


	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		bp = new BorderPane();

        // set up map
		mapComponent = new GoogleMapView();
		mapComponent.addMapInitializedListener(this);

		// border pane for main layout

		// HBox for top panel with options
		HBox rightPanel = new HBox();
		VBox panelV = new VBox();


        Tab routeTab = new Tab("Routing");
        Tab fetchTab = new Tab("Data Fetching");

        Button fetchButton = new Button("Fetch Data");
        Button displayButton = new Button("Show Intersections");
        TextField tf = new TextField();
        ComboBox<DataSet> cb = new ComboBox<DataSet>();
        setupFetchTab(fetchTab, fetchButton, displayButton, tf, cb);
        setupRouteTab(routeTab);

		TabPane tp = new TabPane(routeTab, fetchTab);
        tp.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		Button button = new Button("Display Route");

//		rightPanel.getChildren().add(panelV);
//		panelV.getChildren().add(button);
        // initialize Services and controlelrs after map is loaded
        mapComponent.addMapReadyListener(() -> {
            ClientService cs = new ClientService();
            GeneralService gs = new GeneralService(mapComponent, this);
            RouteService rs = new RouteService(mapComponent);
            System.out.println("in map ready : " + this.getClass());
    		RouteController routeController = new RouteController(rs, this, button);
            ClientMapController userController = new ClientMapController(gs, this);
            FetchController fetchController = new FetchController(gs, tf, fetchButton, cb, displayButton);
        });

		// add components to border pane

		bp.setRight(tp);
		bp.setCenter(mapComponent);

		Scene scene = new Scene(bp);
		primaryStage.setScene(scene);
		primaryStage.show();

	}


	@Override
	public void mapInitialized() {

		// TODO Auto-generated djdlkjmet`hod stub
		LatLong center = new LatLong(38.25, -85.7667);


		// set map options
		MapOptions options = new MapOptions();
		options.center(center)
		       .mapMarker(false)
		       .mapType(MapTypeIdEnum.ROADMAP)
		       //maybe set false
		       .mapTypeControl(true)
		       .overviewMapControl(false)
		       .panControl(true)
		       .rotateControl(false)
		       .scaleControl(false)
		       .streetViewControl(false)
		       .zoom(8)
		       .zoomControl(true);

		// create map;
		map = mapComponent.createMap(options);


	}

	/**
	 * Setup layout and controls for Fetch tab
	 * @param fetchTab
	 * @param fetchButton
	 * @param displayButton
	 * @param tf
	 */
    private void setupFetchTab(Tab fetchTab, Button fetchButton, Button displayButton, TextField tf, ComboBox cb) {

        // add button to tab, rethink design and add V/HBox for content
        VBox v = new VBox();
        HBox h = new HBox();

        HBox fetchControls = new HBox();
        fetchControls.getChildren().add(tf);
        fetchControls.getChildren().add(fetchButton);


        HBox intersectionControls = new HBox();
        intersectionControls.getChildren().add(cb);
        intersectionControls.getChildren().add(displayButton);

        h.getChildren().add(v);
        v.getChildren().add(fetchControls);
        v.getChildren().add(intersectionControls);

        fetchTab.setContent(h);
    }

    /**
     * Setup layout of route tab and controls
     *
     * @param routeTab
     * @param box
     */
    private void setupRouteTab(Tab routeTab) {
        VBox v = new VBox();
        HBox h = new HBox();



    }

    public void showLoadStage(Stage loadStage, String text) {
    	loadStage.initModality(Modality.APPLICATION_MODAL);
    	loadStage.initOwner(primaryStage);
        VBox loadVBox = new VBox(20);
        loadVBox.setAlignment(Pos.CENTER);
        Text tNode = new Text(text);
        tNode.setFont(new Font(16));
        loadVBox.getChildren().add(new HBox());
        loadVBox.getChildren().add(tNode);
        loadVBox.getChildren().add(new HBox());
        Scene loadScene = new Scene(loadVBox, 300, 200);
        loadStage.setScene(loadScene);
        loadStage.show();
    }

    public static void showInfoAlert(String header, String content) {
	    Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
    }

    public static void showErrorAlert(String header, String content) {
    	Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("File Name Error");
		alert.setHeaderText(header);
		alert.setContentText(content);

		alert.showAndWait();
    }

}
