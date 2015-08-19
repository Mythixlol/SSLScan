package application.adress.view.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.examples.ToCSV;

import application.MainSSLScan;
import application.adress.model.ScanTarget;

public class RootController {

	@FXML
	Button btn_Import;
	@FXML
	VBox vBox_Thread;
	@FXML
	ListView<ScanTarget> listView_ScanTarget;

	private MainSSLScan app;
	private ArrayList<ScanTarget> urls = new ArrayList<>();
	private ObservableList<ScanTarget> scanTargets = FXCollections
			.observableArrayList(urls);

	private EventHandler<MouseEvent> mouseEvent = (MouseEvent event) -> {
		handleMouseEvent(event);
	};

	public void setMainApp(MainSSLScan app) {
		this.app = app;
	}

	private void handleMouseEvent(MouseEvent event) {

		Node n = (Node) event.getSource();

		if (n.getId().equals("targetCell")) {
			ScanTarget scanTarget = (ScanTarget) n.getUserData();
			displayDetails(scanTarget);
		}
		event.consume();
	}

	private void displayDetails(ScanTarget target) {
		// TODO Auto-generated method stub

	}

	public void importURL() {
		FileChooser fc = new FileChooser();
		fc.setTitle("Choose a File to Import.. (xls or csv");
		fc.setSelectedExtensionFilter(new ExtensionFilter("*.xls", "*.xlsx",
				"*.csv"));

		File importFile = fc.showOpenDialog(app.getPrimaryStage());

		try {
			parseFile(importFile);
		} catch (IllegalArgumentException | InvalidFormatException
				| IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		renderScanTargetList();

	}

	private void renderScanTargetList() {

		int a = 0;
		for (ScanTarget s : scanTargets) {
			System.out.println(a);
			System.out.println(s.getURL());
			a++;
		}

		listView_ScanTarget.setItems(scanTargets);

		listView_ScanTarget
				.setCellFactory(new Callback<ListView<ScanTarget>, ListCell<ScanTarget>>() {

					@Override
					public ListCell<ScanTarget> call(ListView<ScanTarget> cell) {

						// cell.setOnMouseClicked(mouseEvent);

						return new TargetCell();
					}
				});

	}

	public void parseFile(File importFile) throws IOException,
			IllegalArgumentException, InvalidFormatException {
		File csvFile = new File(importFile.getParent() + "\\csvImport.csv");
		csvFile.createNewFile();
		String extension = importFile.getName().substring(
				importFile.getName().lastIndexOf(".") + 1);

		ToCSV toCSV = new ToCSV();
		switch (extension) {
		case "xls": {
			toCSV.convertExcelToCSV(importFile.getPath(),
					importFile.getParent());
			break;
		}
		case "xlsx": {
			toCSV.convertExcelToCSV(importFile.getPath(),
					importFile.getParent());

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

		String dataRow = CSVFile.readLine(); // Read first line.
		// The while checks to see if the data is null. If
		// it is, we've hit the end of the file. If not,
		// process the data.

		while (dataRow != null) {
			String[] dataArray = dataRow.split(",");
			if (dataArray[0] != null || !dataArray[0].equals("")) {
				scanTargets.add(new ScanTarget(dataArray[0]));
			}
			dataRow = CSVFile.readLine(); // Read next line of data.

		}
		// Close the file once all data has been read.
		CSVFile.close();

	}

	static class TargetCell extends ListCell<ScanTarget> {
		static int num = 0;

		public TargetCell() {
			super();
		}

		@Override
		public void updateItem(ScanTarget target, boolean empty) {
			try {
				super.updateItem(target, empty);

				super.setText(target.getURL()); // target ist null

			} catch (Exception e) {
				System.out.println("shit" + num++);
			}
		}

	}

}
