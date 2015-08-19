package application.adress.model;

import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import sun.net.idn.Punycode;

public class ScanTarget {

	private ArrayList<String> IPs;
	private StringProperty URL;
	private StringProperty name;
	private StringProperty ID;
	private ArrayList<Result> results = new ArrayList<>();
	private StringProperty punyCode;
	boolean complete = false;

	public ScanTarget(String URL) {
		Punycode puny = new Punycode();

		punyCode = new SimpleStringProperty();
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
