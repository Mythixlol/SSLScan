package application.adress.model;

import java.net.IDN;
import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.json.JSONObject;

public class ScanTarget {

	private ArrayList<String> IPs;
	private StringProperty URL;
	private ArrayList<Result> results;
	private StringProperty URI;
	boolean complete = false;
	private StringProperty status;
	private JSONObject rawHostInformation;
	private int thread;

	public int getThread() {
		return thread;
	}

	public void setThread(int thread) {
		this.thread = thread;
	}

	public JSONObject getRawHostInformation() {
		return rawHostInformation;
	}

	public void setRawHostInformation(JSONObject rawHostInformation) {
		this.rawHostInformation = rawHostInformation;
	}

	public ScanTarget(String URL) {
		this.status = new SimpleStringProperty("SLEEP");
		this.URL = new SimpleStringProperty(URL);
		this.URI = new SimpleStringProperty(IDN.toASCII(URL));
		this.IPs = new ArrayList<>();
		this.results = new ArrayList<>();

	}

	public String getStatus() {
		return status.getValue();
	}

	public void setStatus(String newStatus) {
		this.status.set(newStatus);
	}

	public String getURL() {
		return URL.getValue();
	}

	public String getURI() {
		return URI.getValue();
	}

	public void setURL(String url) {
		this.URL.set(url);
		URI.set(IDN.toASCII(url));

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

	public ArrayList<Result> getResults() {
		return results;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	public boolean isComplete() {
		return complete;
	}

}
