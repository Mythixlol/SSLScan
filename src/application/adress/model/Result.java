package application.adress.model;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Result {

	// rawData
	JSONObject result;
	Date date; 						// date of the Scan

	// arrays
	JSONObject details;				// details of the Result
	JSONObject suits; 				// get the List of the Suits!
	JSONArray protocols; 			// protocols of the given IP
	JSONObject certificate;

	// common
	String duration; 				// duration of the scan
	String grade; 					// grade of Scan
	String gradeTrustIgnored; 		// grade without Certificate
	String IP; 						// IP of the result
	String ServerName; 				// server Name

	// suite
	String anonSuite;				// anonymous Suites

	// protocol
	String ssl2;					// SSL2
	String ssl3;					// SSL3
	String tls10;					// TSL1.0
	String tls11;					// TLS1.1
	String tls12;					// TLS1.2

	// certificate
	String isTrusted; 				// isCertificate Trusted
	String notTrustedReason; 		// reasen if not trusted

	// Details
	boolean DHYreUse;				// DH public server param (Ys) reuse
	boolean fallBackScv; 			// Downgrade prevention TLS_FALLBACK_SCSV (Signaling Cipher Suite Value)
	int hasSct;
	boolean beastVuln; 				// has BEAST vulnerability?
	int poodleTLS;				// Has POODLE vulnerability against TLS?
	boolean poodleVuln; 				// has POODLE vulnerabilty against SSL
	boolean heartBleed; 				// has HEARTBLEED vulnerability
	boolean heartBeat;				// has HEARTBEAT vulnerability
	boolean logJam; 					// has LOGJAM vulnerability
	boolean supportRC4; 				// server supports RC4 negotiation
	boolean freakVuln; 				// has FREAK vulnerability
	int openSSLCcs; 				// has an OPEN SSL Certificate
	int forwardSecrecy; 			// supports Forwared Secrey
	int renegSupport; 			// supports secure Renogotiation
	int tlsCompression;			// TLS Compression enabled

	public Result(JSONObject result) {
		this.date = new Date(Long.parseLong("1440160623624"));
		this.result = result;

		System.out.println(result);
		try {
			setCommonFacts();
			setDetails();
			setSuites();
			setCertificate();
			setProtocols();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void setCommonFacts() throws JSONException {
		grade = result.getString("grade");
		gradeTrustIgnored = result.getString("gradeTrustIgnored");
		duration = result.getString("duration");
		IP = result.getString("iPAddress");
		ServerName = result.getString("serverName");

	}

	private void setCertificate() throws JSONException {
		certificate = details.getJSONObject("cert");

		isTrusted = (grade.equals("M")) ? "Ja" : "Nein";

	}

	private void setProtocols() throws JSONException {
		protocols = details.getJSONArray("protocols");

		ArrayList<String> supportedProtocols = new ArrayList<>();
		for (int i = 0; i < protocols.length(); i++) {
			supportedProtocols.add(protocols.getJSONObject(i).get("id").toString());

		}

		ssl2 = (supportedProtocols.contains("512")) ? "Ja" : "Nein";
		ssl3 = (supportedProtocols.contains("768")) ? "Ja" : "Nein";
		tls10 = (supportedProtocols.contains("769")) ? "Ja" : "Nein";
		tls11 = (supportedProtocols.contains("770")) ? "Ja" : "Nein";
		tls12 = (supportedProtocols.contains("771")) ? "Ja" : "Nein";

	}

	private void setSuites() throws JSONException {

		suits = details.getJSONObject("suites");

		anonSuite = "Nein";
		JSONArray list = suits.getJSONArray("list");
		for (int i = 0; i < list.length(); i++) {
			JSONObject suit = list.getJSONObject(0);
			String name = suit.getString("name");
			name.toLowerCase();
			if (name.contains("anon")) {
				anonSuite = "Ja";
				break;
			}

		}

	}

	private void setDetails() throws JSONException {

		details = result.getJSONObject("details");

		DHYreUse = details.getBoolean("dhYsReuse");
		fallBackScv = details.getBoolean("fallbackScsv");
		hasSct = details.getInt("hasSct");
		beastVuln = details.getBoolean("vulnBeast");
		poodleTLS = details.getInt("poodleTls");
		poodleVuln = details.getBoolean("poodle");
		heartBleed = details.getBoolean("hearbleed");
		heartBeat = details.getBoolean("heartbeat");
		logJam = details.getBoolean("logjam");
		supportRC4 = details.getBoolean("supportsRc4");
		freakVuln = details.getBoolean("freak");
		openSSLCcs = details.getInt("openSslCcs");
		forwardSecrecy = details.getInt("forwardSecrecy");
		renegSupport = details.getInt("renegSupport");
		tlsCompression = details.getInt("compressionMethods");
	}

	public JSONObject getResult() {
		return result;
	}

	public Date getDate() {
		return date;
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

	public JSONObject getCertificate() {
		return certificate;
	}

	public String getDuration() {
		return duration;
	}

	public String getGrade() {
		return grade;
	}

	public String getGradeTrustIgnored() {
		return gradeTrustIgnored;
	}

	public String getIP() {
		return IP;
	}

	public String getServerName() {
		return ServerName;
	}

	public String getAnonSuite() {
		return anonSuite;
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

	public String getIsTrusted() {
		return isTrusted;
	}

	public String getNotTrustedReason() {
		return notTrustedReason;
	}

	public boolean isDHYreUse() {
		return DHYreUse;
	}

	public boolean isFallBackScv() {
		return fallBackScv;
	}

	public int getHasSct() {
		return hasSct;
	}

	public boolean isBeastVuln() {
		return beastVuln;
	}

	public int getPoodleTLS() {
		return poodleTLS;
	}

	public boolean isPoodleVuln() {
		return poodleVuln;
	}

	public boolean isHeartBleed() {
		return heartBleed;
	}

	public boolean isHeartBeat() {
		return heartBeat;
	}

	public boolean isLogJam() {
		return logJam;
	}

	public boolean isSupportRC4() {
		return supportRC4;
	}

	public boolean isFreakVuln() {
		return freakVuln;
	}

	public int getOpenSSLCcs() {
		return openSSLCcs;
	}

	public int getForwardSecrecy() {
		return forwardSecrecy;
	}

	public int getRenegSupport() {
		return renegSupport;
	}

	public int getTlsCompression() {
		return tlsCompression;
	}

}
