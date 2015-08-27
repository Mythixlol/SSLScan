package application.adress.model;

import java.util.ArrayList;

import javafx.collections.ObservableList;

public class RunningScans {

	ArrayList<ObservableList<ScanTarget>> scans;

	public RunningScans() {
		scans = new ArrayList<>();

	}

	public void addNewScans(ObservableList<ScanTarget> list, int i) {

		scans.add(i, list);
	}

	public ObservableList<ScanTarget> getList(int i) {
		return scans.get(i);
	}

}
