package application.adress.view.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
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
import application.adress.model.Result;
import application.adress.model.ScanTarget;

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

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	private MainSSLScan app;
	private ArrayList<ScanTarget> urls = new ArrayList<>();
	private ObservableList<ScanTarget> scanTargets = FXCollections.observableArrayList(urls);
	private ContextMenu contextMenu = new ContextMenu();
	private Object[] threads = new Object[5];
	private ObservableList<ScanTarget> runningScans = FXCollections.observableArrayList();
	private Api scanApi = new Api();
	private ScanTarget selectedTarget;

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
			startScan(listView_ScanTarget.getSelectionModel().getSelectedItem());
		} else if (item.getId().equals("stopScan")) {

		}

	}

	private void handleMouseEvent(MouseEvent event) {

		Node n = (Node) event.getSource();

		if (n.getId().equals("targetCell") || event.isSecondaryButtonDown()) {
			ScanTarget scanTarget = (ScanTarget) n.getUserData();
			displayDetails(scanTarget);
		}

	}

	private void handleListChangeEvent(Change<? extends ScanTarget> event) {

		while (event.next()) {
			ScanTarget target;
			if (event.wasRemoved()) {
				target = event.getRemoved().get(0);
				startScan(target);
				event.getRemoved().remove(0);

			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////
	// scans
	// /////////////////////////////////////////////////////////////////////////
	private ScanTarget startScan(ScanTarget target) {

		Pane progressPane = createThreadViewBox();
		runningScans.add(target);

		String URL = target.getURI();

		Thread scanHost = new Thread(new Runnable() {

			boolean isPublic = false;
			boolean startNewScan = true;
			boolean fromCache = false;
			boolean certMissMatch = true;

			@Override
			public void run() {
				JSONObject hostInformation = (scanApi.fetchHostInformation(URL, isPublic, startNewScan, fromCache, null, null, certMissMatch));

				target.setRawHostInformation(hostInformation);
			}
		});

		scanHost.start();

		Thread fetchStatus = new Thread(new Runnable() {

			JSONObject hostInformation;

			@Override
			public void run() {

				try {
					scanHost.join();
				} catch (InterruptedException e1) {

					e1.printStackTrace();
					System.out.println("fetchStatusError");
				}

				String status = null;

				do {

					hostInformation = (scanApi.fetchHostInformation(URL, false, false, false, null, null, true));
					target.setRawHostInformation(hostInformation);
					try {
						status = (hostInformation.getString("status"));

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// testIt

					target.setStatus(status);
					System.out.println(status);
				} while (!status.equals("READY") && !status.equals("ERROR"));

				fetchResult(target, scanApi.fetchHostInformation(URL, false, false, false, null, null, true));

			}
		});

		fetchStatus.start();

		/*
		 * fetchStatusCodes() : statusDetails
		 * 
		 * fetchHostInformation() : engineVersion, protocol, criteriaVersion, port, host, isPublic, startTime, statusMessage, status
		 */
		ObservableList<ScanTarget> list = (ObservableList<ScanTarget>) threads[target.getThread()];
		list.remove(target);
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

			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

	}

	private void createResults(ScanTarget target, JSONObject object) {

		for (String ip : target.getIPs()) {
			target.addResult(new Result(scanApi.fetchEndpointData(target.getURI(), ip, false)));
		}

	}

	private void setContextmenu() {

		MenuItem startScanItem = new MenuItem("Scan");
		startScanItem.setId("startScan");
		startScanItem.setOnAction(actionEvent);

		MenuItem stopScanItem = new MenuItem("Scan");
		stopScanItem.setId("startScan");
		stopScanItem.setOnAction(actionEvent);

		contextMenu.getItems().add(startScanItem);
	}

	private void displayDetails(ScanTarget target) {
		// TODO Auto-generated method stub

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
				cell.setOnMouseClicked(mouseEvent);

				return tCell;
			}
		});

	}

	public void parseFile(File importFile) throws IOException, IllegalArgumentException, InvalidFormatException {
		File csvFile = new File(importFile.getParent() + "\\csvImport.csv");
		csvFile.createNewFile();
		String extension = importFile.getName().substring(importFile.getName().lastIndexOf(".") + 1);

		ToCSV toCSV = new ToCSV();
		switch (extension) {
		case "xls": {
			toCSV.convertExcelToCSV(importFile.getPath(), importFile.getParent());
			break;
		}
		case "xlsx": {
			toCSV.convertExcelToCSV(importFile.getPath(), importFile.getParent());

			break;
		}

		case "csv": {
			csvFile = importFile;
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
			String[] dataArray = dataRow.split(",");
			if (dataArray[0] != null || !dataArray[0].equals("")) {
				scanTargets.add(new ScanTarget(dataArray[0]));
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

	public void scanAll() {

		for (int i = 0; i < threads.length; i++) {
			ObservableList<ScanTarget> running = FXCollections.observableArrayList();

			for (ScanTarget target : scanTargets) {
				if (scanTargets.indexOf(target) % i == 0) {
					running.add(target);
					target.setThread(i);
				}
			}

			running.addListener(changeListener);
			threads[i] = running;

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
			try {

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
			} catch (Exception e) {

			}
		}
	}

	@FXML
	MenuItem test;

	public void doTest() {
		// createThreadViewBox();

	}

}
