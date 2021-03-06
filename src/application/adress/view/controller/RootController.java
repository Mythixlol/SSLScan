package application.adress.view.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.examples.ToCSV;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import application.MainSSLScan;
import application.adress.model.Api;
import application.adress.model.GetGeoLocation;
import application.adress.model.ImportExport;
import application.adress.model.Result;
import application.adress.model.ScanTarget;

import com.maxmind.geoip2.exception.GeoIp2Exception;

public class RootController {
	/*
	 * 
	 * -------------------------------------------------------------------------- ------------------------------------------
	 */
	@FXML
	Button btn_Import;
	@FXML
	VBox vBox_Thread;
	@FXML
	ListView<ScanTarget> listView_ScanTarget;
	@FXML
	Button btn_AddSingleURL;
	@FXML
	TextField textField_SingleURL;
	ArrayList<ArrayList<ScanTarget>> distibutedScans = new ArrayList<>();

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	private MainSSLScan app;
	private Api scanApi = new Api();

	private ArrayList<ScanTarget> urls = new ArrayList<>();
	private ArrayList<ScanTarget> scannedTargets = new ArrayList<>();
	private ObservableList<ScanTarget> scanTargets = FXCollections.observableArrayList(urls);
	private ObservableList<ScanTarget> runningScans = FXCollections.observableArrayList();

	private ContextMenu contextMenu = new ContextMenu();
	private int threads = 5;

	private ScanTarget selectedTarget;

	private int currentThread = 0;

	// ///////////////////////////////////////////////////////////////////////////////////////////////

	private EventHandler<MouseEvent> mouseEvent = (MouseEvent event) -> {
		handleMouseEvent(event);
	};
	private EventHandler<ActionEvent> actionEvent = (ActionEvent event) -> {
		if (event.getSource() instanceof MenuItem) {
			handleContextMenuActionEvent(event);
		}

	};

	private ListChangeListener<ScanTarget> changeListener = (ListChangeListener.Change<? extends ScanTarget> event) -> {
		handleListChangeEvent(event);

	};

	/*
	 * 
	 * -------------------------------------------------------------------------- ------------------------
	 */

	public void setMainApp(MainSSLScan app) {
		this.app = app;
		initController();
	}

	public void initController() {
		listView_ScanTarget.setOnMouseClicked(mouseEvent);
		runningScans.addListener(changeListener);

		setContextmenu();
	}

	// ////////////////////////////////////////////////////////////////
	// EventHandler
	// //////////////////////////////////////////////////////////////

	private void handleContextMenuActionEvent(ActionEvent event) {
		MenuItem item = (MenuItem) event.getSource();

		if (item.getId().equals("startScan")) {
			startSingleScan(selectedTarget);
		} else if (item.getId().equals("stopScan")) {

		}

	}

	private void handleMouseEvent(MouseEvent event) {

		Node n = (Node) event.getSource();

		if ("targetCell".equals(n.getId())) {

			ScanTarget scanTarget = listView_ScanTarget.getSelectionModel().getSelectedItem();
			displayDetails(scanTarget);
			selectedTarget = scanTarget;
		}

	}

	private void handleListChangeEvent(Change<? extends ScanTarget> event) {

		while (event.next()) {

			if (event.wasAdded()) {

			}
			if (event.wasRemoved()) {

			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////
	// Visuals
	// /////////////////////////////////////////////////////////////////////////

	private void setContextmenu() {

		MenuItem startScanItem = new MenuItem("Scan");
		startScanItem.setId("startScan");
		startScanItem.setOnAction(actionEvent);

		contextMenu.getItems().add(startScanItem);
	}

	private void displayDetails(ScanTarget target) {
		// TODO Auto-generated method stub

	}

	// ////////////////////////////////////////////////////////////////////////////
	// scans
	// /////////////////////////////////////////////////////////////////////////

	public void startSingleScan(ScanTarget target) {

		VBox box = new VBox();
		Label thread = new Label("SingleScan:");
		TextField urllbl = new TextField(target.getURL());
		urllbl.setEditable(false);
		box.getChildren().add(thread);
		box.getChildren().add(urllbl);

		vBox_Thread.getChildren().add(box);

		Service scanService = new Service() {
			@Override
			protected Task createTask() {

				return new Task() {
					@Override
					protected Object call() throws Exception {

						startScan(target);
						target.setComplete(true);
						return null;
					}
				};
			}
		};

		scanService.restart();

	}

	public void scanSingleTarget() {
		startSingleScan(selectedTarget);
	}

	public void scanAllTargets() {
		distributeTargets();
		startScanAll();
	}

	private void distributeTargets() {

		runningScans.addAll(scanTargets);

		for (int i = 0; i < threads; i++) {
			ArrayList<ScanTarget> dist = new ArrayList<ScanTarget>();
			for (ScanTarget target : scanTargets) {
				if (target.getThread() == -1) {
					if (scanTargets.indexOf(target) % 5 == i) {
						target.setThread(i);
						dist.add(target);

					}
				} else {
					continue;
				}
			}
			distibutedScans.add(i, dist);
		}

	}

	public void startScanAll() {

		for (int i = 0; i < threads; i++) {

			currentThread = i;
			ArrayList<ScanTarget> listOfThread = distibutedScans.get(i);
			Iterator<ScanTarget> targetIter = listOfThread.iterator();
			TextField urllbl = new TextField();
			urllbl.setEditable(false);
			StringProperty url = new SimpleStringProperty();
			url.addListener(new ChangeListener<String>() {

				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					if (newValue == null) {
						urllbl.setText("Idle");
					}

				}
			});
			VBox box = new VBox();

			Label thread = new Label("Scan #" + currentThread);
			box.getChildren().add(thread);

			box.getChildren().add(urllbl);
			vBox_Thread.getChildren().add(box);

			Service scanService = new Service() {

				@Override
				protected Task createTask() {
					// TODO Auto-generated method stub
					return new Task() {

						@Override
						protected Object call() throws Exception {
							final int a = currentThread;
							while (targetIter.hasNext()) {

								ScanTarget t = targetIter.next();
								updateValue(t.getURL());
								startScan(t);
								t.setComplete(true);

							}
							// vBox_Thread.getChildren().remove(box);
							return null;
						}

					};
				}

			};

			scanService.restart();

			urllbl.textProperty().bind(scanService.valueProperty().asString());

		}

	}

	// ////////////////////////////////////////////////////////////////////////////
	// scans
	// /////////////////////////////////////////////////////////////////////////

	public ScanTarget startScan(ScanTarget target) {

		String URL = target.getURI();

		boolean isPublic = false;
		boolean startNewScan = true;
		boolean fromCache = false;
		boolean certMissMatch = true;

		JSONObject hostInformation = (scanApi.fetchHostInformation(URL, isPublic, startNewScan, fromCache, null, null, certMissMatch));

		target.setRawHostInformation(hostInformation);

		fetchStatus(target);

		return target;

	}

	public ScanTarget fetchStatus(ScanTarget target) {
		String status = null;
		String URL = target.getURI();

		boolean isPublic = false;
		boolean startNewScan = false;
		boolean fromCache = false;
		boolean certMissMatch = true;
		JSONObject hostInformation = null;

		do {

			hostInformation = (scanApi.fetchHostInformation(URL, isPublic, startNewScan, fromCache, null, null, certMissMatch));
			target.setRawHostInformation(hostInformation);

			try {
				status = (hostInformation.getString("status"));

			} catch (JSONException e) {
				// e.printStackTrace();
			}

			// testIt

			target.setStatus(status);

		} while (status == null || (!status.equals("READY") && !status.equals("ERROR")));

		fetchResult(target, scanApi.fetchHostInformation(URL, isPublic, startNewScan, fromCache, null, null, certMissMatch));
		return target;

	}

	public void fetchResult(ScanTarget target, JSONObject object) {

		target.setComplete(true);

		try {
			target.setStatus(object.getString("status"));
			if (target.getStatus().equals("READY")) {
				JSONArray endPointArray = object.getJSONArray("endpoints");

				for (int i = 0; i < endPointArray.length(); i++) {
					JSONObject endPoint = (JSONObject) endPointArray.get(i);
					target.addIP(endPoint.get("ipAddress").toString());

				}

				createResults(target, object);

			} else if (target.getStatus().equalsIgnoreCase("error")) {

				Result r = new Result();
				target.addResult(r);
				scannedTargets.add(target);
				System.out.println("ERROR:    " + target.getURI() + "   " + target.getStatus());

			} else {
				System.out.println("fehlt was:    " + target.getURI() + "   " + target.getStatus());
			}
		} catch (JSONException e) {

			e.printStackTrace();
		}

	}

	private void createResults(ScanTarget target, JSONObject object) {
		scannedTargets.add(target);
		for (String ip : target.getIPs()) {
			Result result = new Result(scanApi.fetchEndpointData(target.getURI(), ip, false));
			result.setIP(ip);
			String geoLoc = "";
			try {
				geoLoc = GetGeoLocation.getGeoLocation("81.169.145.80");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GeoIp2Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			result.setGeoLocation(geoLoc);
			target.addResult(result);
			target.addLastRecent(ip, result);
		}

	}

	public void importURL() {
		FileChooser fc = new FileChooser();
		fc.setTitle("Choose a File to Import.. (xls or csv");
		fc.setSelectedExtensionFilter(new ExtensionFilter("*.xls", "*.xlsx", "*.csv"));

		File importFile = fc.showOpenDialog(app.getPrimaryStage());
		if (importFile == null) {
			return;
		}
		try {
			parseFile(importFile);
		} catch (IllegalArgumentException | InvalidFormatException | IOException e) {

			e.printStackTrace();
		}

		renderScanTargetList();

	}

	private void renderScanTargetList() {

		listView_ScanTarget.setItems(scanTargets);

		listView_ScanTarget.setCellFactory(new Callback<ListView<ScanTarget>, ListCell<ScanTarget>>() {

			@Override
			public ListCell<ScanTarget> call(ListView<ScanTarget> cell) {

				TargetCell tCell = new TargetCell(contextMenu);

				tCell.setOnMouseClicked(mouseEvent);
				return tCell;
			}
		});

	}

	public void parseFile(File importFile) throws IOException, IllegalArgumentException, InvalidFormatException {

		String extension = importFile.getName().substring(importFile.getName().lastIndexOf(".") + 1);
		String csv = importFile.getAbsolutePath().substring(0, importFile.getAbsolutePath().lastIndexOf(".")) + ".csv";
		ToCSV toCSV = new ToCSV();
		switch (extension) {
		case "xls": {

			toCSV.convertExcelToCSV(importFile.getPath(), importFile.getParent());
			importFile = new File(csv);
			break;
		}
		case "xlsx": {
			toCSV.convertExcelToCSV(importFile.getPath(), importFile.getParent());
			importFile = new File(csv);
			break;
		}

		case "csv": {

			break;
		}

		default:
		}

		parseCSV(importFile);

	}

	public void parseCSV(File importFile) throws IOException {
		BufferedReader CSVFile = new BufferedReader(new FileReader(importFile));

		String dataRow = CSVFile.readLine();
		while (dataRow != null) {
			if (!dataRow.isEmpty()) {

				String[] dataArray = dataRow.split(",");
				if (dataArray[0] != null || !dataArray[0].equals("")) {
					scanTargets.add(new ScanTarget(dataArray[0]));
				}
			}
			dataRow = CSVFile.readLine(); // Read next line of data.
		}

		CSVFile.close();

	}

	public void addSingleURL() {
		if (!textField_SingleURL.getText().trim().isEmpty()) {
			scanTargets.add(new ScanTarget(textField_SingleURL.getText()));
			renderScanTargetList();
		}
		textField_SingleURL.clear();
	}

	public Pane createThreadViewBox() {
		Pane pane_ThreadPane = new Pane();

		Label lbl_ThreadInfo = new Label("");
		lbl_ThreadInfo.setId("lblThreadInfo");
		Label lbl_Progress = new Label("-1");
		lbl_Progress.setId("lblProgress");
		ProgressIndicator pb_ThreadProgress = new ProgressBar();
		pb_ThreadProgress.setId("pbProgress");
		pane_ThreadPane.getChildren().addAll(lbl_Progress, lbl_ThreadInfo, pb_ThreadProgress);
		vBox_Thread.getChildren().add(pane_ThreadPane);
		pb_ThreadProgress.setPrefWidth(pb_ThreadProgress.getParent().getBoundsInLocal().getWidth() - 5);

		return pane_ThreadPane;

	}

	public void setPaneDetails(Pane pane, JSONObject scan) throws JSONException {

		Label lbl_ThreadInfo = null;

		for (Node n : pane.getChildren()) {

			if (n.getId().equals("lblThreadInfo"))
				lbl_ThreadInfo = (Label) n;

		}

		lbl_ThreadInfo.setText(scan.getString("status"));

	}

	public void export() {

	}

	public void exportResults() {

		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilterXLS = new FileChooser.ExtensionFilter("Excel files (*.xls)", "*.xls");

		fileChooser.getExtensionFilters().addAll(extFilterXLS);

		File f = fileChooser.showSaveDialog(app.getPrimaryStage());

		if (f == null) {
			return;
		}

		else {
			ImportExport.exportToExcelFile(scannedTargets, f);

		}
	}

	/*
	 * 
	 * -------------------------------------------------------------------------- ----------------
	 */
	static class TargetCell extends ListCell<ScanTarget> {

		ContextMenu c;

		public TargetCell(ContextMenu contextMenu) {
			super();
			c = contextMenu;
		}

		@Override
		public void updateItem(ScanTarget target, boolean empty) {
			if (target == null || empty) {
				return;
			}
			super.updateItem(target, empty);
			super.setText(target.getURI());
			if (target.getStatus().equals("IN_PROGRESS")) {
				this.setStyle("-fx-background-color: yellow");

			} else {
				if (target.isComplete() && !empty) {
					this.setStyle("-fx-background-color: lightgreen");
				} else {
					// this.setStyle("-fx-background-color: white");
				}

			}
			setContextMenu(c);
			setId("targetCell");

		}
	}

	@FXML
	MenuItem test;

	public void doTest() {

	}
}
