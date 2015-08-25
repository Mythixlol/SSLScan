package application.adress.model;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Result {

	// rawData
	JSONObject result;

	// arrays
	JSONObject details;				// details of the Result
	JSONObject suits; 				// get the List of the Suits!
	JSONArray protocols; 			// protocols of the given IP
	JSONObject certificate;

	// Details
	Date date; 						// date of the Scan
	String IP; 						// IP of the result
	String ServerName; 				// server Name
	String grade; 					// grade of Scan
	String gradeTrustIgnored; 		// grade without Certificate
	String fallBackScv; 			// Downgrade prevention TLS_FALLBACK_SCSV (Signaling Cipher Suite Value)
	String hasScSV;
	String beastVuln; 				// has BEAST vulnerability?
	String poodleTLS;				// Has POODLE vulnerability against TLS?
	String poodleVuln; 				// has POODLE vulnerabilty against SSL
	String heartBleed; 				// has HEARTBLEED vulnerability
	String heartBeat;				// has HEARTBEAT vulnerability
	String logJam; 					// has LOGJAM vulnerability
	String supportRC4; 				// server supports RC4 negotiation
	String freakVuln; 				// has FREAK vulnerability
	String openSSLCcs; 				// has an OPEN SSL Certificate
	String forwardSecrecy; 			// supports Forwared Secrey
	String renegSupport; 			// supports secure Renogotiation
	String DHYreUse;				// DH public server param (Ys) reuse
	String duration; 				// duration of the scan
	String anonSuite;				// anonymous Suites
	String tlsCompression;			// TLS Compression enabled
	boolean isTrustet; 				// isCertificate Trusted
	String notTrustedReason; 		// reasen if not trusted
	String ssl2;					// SSL2
	String ssl3;					// SSL3
	String tls10;					// TSL1.0
	String tls11;					// TLS1.1
	String tls12;					// TLS1.2

	public Result(JSONObject result) {
		this.date = new Date(Long.parseLong("1440160623624"));
		this.result = result;

		try {
			setDetails();
			setSuites();
			setCertificate();
			setProtocols();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void setCertificate() throws JSONException {
		certificate = details.getJSONObject("cert");

	}

	private void setProtocols() throws JSONException {
		protocols = details.getJSONArray("protocols");

		ArrayList<String> supportedProtocols = new ArrayList<>();
		for (int i = 0; i < protocols.length(); i++) {
			supportedProtocols.add(protocols.getJSONObject(i).get("id").toString());
			System.out.println(protocols.getJSONObject(i).get("id").toString());
		}

		ssl2 = (supportedProtocols.contains("512")) ? "Ja" : "Nein";
		ssl3 = (supportedProtocols.contains("768")) ? "Ja" : "Nein";
		tls10 = (supportedProtocols.contains("769")) ? "Ja" : "Nein";
		tls11 = (supportedProtocols.contains("770")) ? "Ja" : "Nein";
		tls12 = (supportedProtocols.contains("771")) ? "Ja" : "Nein";

	}

	private void setSuites() throws JSONException {

		suits = details.getJSONObject("suites");

	}

	private void setDetails() throws JSONException {

		details = result.getJSONObject("details");

	}

	public JSONObject getResult() {
		return result;
	}

	public JSONObject getDetails() {
		return details;
	}

	public JSONObject getSuits() {
		return suits;
	}

	public JSONArray getProtocols() {
		return protocols;
	}

	public Date getDate() {
		return date;
	}

	public String getIP() {
		return IP;
	}

	public String getServerName() {
		return ServerName;
	}

	public String getGrade() {
		return grade;
	}

	public String getGradeTrustIgnored() {
		return gradeTrustIgnored;
	}

	public String getFallBackServerCert() {
		return fallBackScv;
	}

	public String getBeastVuln() {
		return beastVuln;
	}

	public String getPoodleTLS() {
		return poodleTLS;
	}

	public String getPoodleVuln() {
		return poodleVuln;
	}

	public String getHeartBleed() {
		return heartBleed;
	}

	public String getHeartBeat() {
		return heartBeat;
	}

	public String getLogJam() {
		return logJam;
	}

	public String getSupportRC4() {
		return supportRC4;
	}

	public String getFreakVuln() {
		return freakVuln;
	}

	public String getOpenSSLCcs() {
		return openSSLCcs;
	}

	public String getForwardSecrecy() {
		return forwardSecrecy;
	}

	public String getRenegSupport() {
		return renegSupport;
	}

	public String getDHYreUse() {
		return DHYreUse;
	}

	public String getDuration() {
		return duration;
	}

	public String getAnonSuite() {
		return anonSuite;
	}

	public String getTlsCompression() {
		return tlsCompression;
	}

	public boolean isTrustet() {
		return isTrustet;
	}

	public String getNotTrustedReason() {
		return notTrustedReason;
	}

	public String getSsl2() {
		return ssl2;
	}

	public String getSsl3() {
		return ssl3;
	}

	public String getTls10() {
		return tls10;
	}

	public String getTls11() {
		return tls11;
	}

	public String getTls12() {
		return tls12;
	}

}
