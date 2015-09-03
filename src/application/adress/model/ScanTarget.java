package application.adress.model;

import java.net.IDN;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.json.JSONObject;

public class ScanTarget {

	private ArrayList<String> IPs;
	private ArrayList<Result> results;
	private Map<String, Result> lastRecentResult;

	private StringProperty URL;
	private StringProperty URI;
	private StringProperty status;
	private StringProperty statusMessage;

	private JSONObject rawHostInformation;

	private int thread;
	boolean complete = false;

	public ScanTarget(String URL) {
		thread = -1;
		this.status = new SimpleStringProperty("SLEEP");
		this.URL = new SimpleStringProperty(URL);
		this.statusMessage = new SimpleStringProperty();
		this.URI = new SimpleStringProperty(IDN.toASCII(URL));
		this.IPs = new ArrayList<>();
		this.results = new ArrayList<>();
		this.lastRecentResult = new HashMap<>();

	}

	public Result addLastRecent(String ip, Result result) {
		Result oldValue = lastRecentResult.put(ip, result);
		return oldValue;

	}

	public Map<String, Result> getLastRecentResults() {
		return lastRecentResult;
	}

	public String getStatus() {
		return status.getValue();
	}

	public void setStatusMessage(String message) {
		this.statusMessage.set(message);
	}

	public String getStatusMessage() {
		return statusMessage.getValue();
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

}
