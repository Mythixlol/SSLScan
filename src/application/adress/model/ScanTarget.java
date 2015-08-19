package application.adress.model;

import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ScanTarget {

	private ArrayList<String> IPs;
	private StringProperty URL;
	private StringProperty name;
	private StringProperty ID;
	private ArrayList<Result> results = new ArrayList<>();
	private StringProperty URI;
	boolean complete = false;

	public ScanTarget(String URL) {
		URI = new SimpleStringProperty(URLEncoder.encode(URL));
		this.URL = new SimpleStringProperty(URL);

	}

	public String getURL() {
		return URL.getValue();
	}

	public void setURL(String url) {
		this.URL.set(url);
	}

	public void addResult(Result r) {
		results.add(r);
	}

	public void addIP(String ip) {
		IPs.add(ip);
	}

	public ArrayList<String> getIPs() {
		return IPs;
	}

}
