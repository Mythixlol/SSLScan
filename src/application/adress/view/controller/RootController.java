package application.adress.view.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.examples.ToCSV;
import org.json.JSONException;
import org.json.JSONObject;

import application.MainSSLScan;
import application.adress.model.Api;
import application.adress.model.ScanTarget;

public class RootController {
	/*
	 * 
	 * --------------------------------------------------------------------------------------------------------------------
	 */
	@FXML
	Button btn_Import;
	@FXML
	VBox vBox_Thread;
	@FXML
	ListView<ScanTarget> listView_ScanTarget;

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	private MainSSLScan app;
	private ArrayList<ScanTarget> urls = new ArrayList<>();
	private ObservableList<ScanTarget> scanTargets = FXCollections.observableArrayList(urls);
	private ContextMenu contextMenu = new ContextMenu();
	private Thread[] threads = new Thread[5];
	private ArrayList<ScanTarget> runningScans = new ArrayList<>();
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

	/*
	 * 
	 * --------------------------------------------------------------------------------------------------
	 */

	public void setMainApp(MainSSLScan app) {
		this.app = app;
		initController();
	}

	private void handleContextMenuActionEvent(ActionEvent event) {
		MenuItem item = (MenuItem) event.getSource();

		if (item.getId().equals("startScan")) {
			startScan(listView_ScanTarget.getSelectionModel().getSelectedItem());
		} else if (item.getId().equals("stopScan")) {

		}

	}

	private void startScan(ScanTarget target) {

		JSONObject json = (scanApi.fetchHostInformation(target.getURL(), false, true, false, null, null, true));

		System.out.println(json);

		Iterator a = json.keys();

		while (a.hasNext()) {
			String value = a.next().toString();
			try {
				System.out.println(value + ": " + json.get(value));
			} catch (JSONException e) {
				System.out.println("Error");
				// e.printStackTrace();
			}

		}
	}

	public void initController() {
		setContextmenu();
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

	private void handleMouseEvent(MouseEvent event) {

		Node n = (Node) event.getSource();

		if (n.getId().equals("targetCell") || event.isSecondaryButtonDown()) {
			ScanTarget scanTarget = (ScanTarget) n.getUserData();
			displayDetails(scanTarget);
		}

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

				cell.setOnMouseClicked(mouseEvent);

				return new TargetCell(contextMenu);
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

	/*
	 * 
	 * ------------------------------------------------------------------------------------------
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

				super.setText(target.getURL());
				setContextMenu(c);
			} catch (Exception e) {

			}
		}

	}

}
