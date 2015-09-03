package application.adress.model;

import java.time.LocalDate;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Result {

	// rawData
	JSONObject result;
	LocalDate date; // date of the Scan

	// arrays
	JSONObject details; // details of the Result
	JSONObject suits; // get the List of the Suits!
	JSONArray protocols; // protocols of the given IP
	JSONObject certificate;

	// common
	Long duration; // duration of the scan
	String grade; // grade of Scan
	String gradeTrustIgnored; // grade without Certificate
	String IP; // IP of the result
	String ServerName; // server Name
	String statusMessage; // statusMessage of the Scan
	String geoLocation;

	public String getGeoLocation() {
		return geoLocation;
	}

	// suite
	String anonSuite; // anonymous Suites

	// protocol
	String ssl2; // SSL2
	String ssl3; // SSL3
	String tls10; // TSL1.0
	String tls11; // TLS1.1
	String tls12; // TLS1.2

	// certificate
	String isTrusted; // isCertificate Trusted
	String notTrustedReason; // reasen if not trusted

	// Details
	boolean DHYreUse; // DH public server param (Ys) reuse
	boolean fallBackScv; // Downgrade prevention TLS_FALLBACK_SCSV (Signaling Cipher Suite Value)
	int hasSct;
	boolean beastVuln; // has BEAST vulnerability?
	int poodleTLS; // Has POODLE vulnerability against TLS?
	boolean poodleVuln; // has POODLE vulnerabilty against SSL
	boolean heartBleed; // has HEARTBLEED vulnerability
	boolean heartBeat; // has HEARTBEAT vulnerability
	boolean logJam; // has LOGJAM vulnerability
	boolean supportRC4; // server supports RC4 negotiation
	boolean freakVuln; // has FREAK vulnerability
	int openSSLCcs; // has an OPEN SSL Certificate
	int forwardSecrecy; // supports Forwared Secrey
	int renegSupport; // supports secure Renogotiation
	int tlsCompression; // TLS Compression enabled
	String serverSig;

	public Result(JSONObject result) {
		this.date = LocalDate.now();
		this.result = result;

		setCommonFacts();
		if (statusMessage.equalsIgnoreCase("ready")) {
			setDetails();
			setSuites();
			setCertificate();
			setProtocols();
		}

	}

	public Result() {
		this.statusMessage = "Offline";
		IP = "";
	}

	private void setCommonFacts() {
		try {
			statusMessage = result.getString("statusMessage");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}
		if (!statusMessage.equals("Ready")) {
			return;
		}
		try {
			grade = result.getString("grade");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}
		try {
			gradeTrustIgnored = result.getString("gradeTrustIgnored");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}
		try {
			duration = result.getLong("duration");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}
		try {
			ServerName = result.getString("serverName");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}

	}

	public String getStatusMessage() {
		return statusMessage;
	}

	private void setCertificate() {
		try {
			certificate = details.getJSONObject("cert");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}

		isTrusted = (grade.equals("M")) ? "Ja" : "Nein";

	}

	private void setProtocols() {
		try {
			protocols = details.getJSONArray("protocols");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ArrayList<String> supportedProtocols = new ArrayList<>();
		for (int i = 0; i < protocols.length(); i++) {
			try {
				supportedProtocols.add(protocols.getJSONObject(i).get("id").toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block

			}

		}

		ssl2 = (supportedProtocols.contains("512")) ? "Ja" : "Nein";
		ssl3 = (supportedProtocols.contains("768")) ? "Ja" : "Nein";
		tls10 = (supportedProtocols.contains("769")) ? "Ja" : "Nein";
		tls11 = (supportedProtocols.contains("770")) ? "Ja" : "Nein";
		tls12 = (supportedProtocols.contains("771")) ? "Ja" : "Nein";

	}

	private void setSuites() {

		try {
			suits = details.getJSONObject("suites");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}

		anonSuite = "Nein";
		JSONArray list = new JSONArray();
		try {
			list = suits.getJSONArray("list");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}
		for (int i = 0; i < list.length(); i++) {
			JSONObject suit = null;
			try {
				suit = list.getJSONObject(0);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String name = null;
			try {
				name = suit.getString("name");
			} catch (JSONException e) {
				// TODO Auto-generated catch block

			}
			name.toLowerCase();
			if (name.contains("anon")) {
				anonSuite = "Ja";
				break;
			}

		}

	}

	private void setDetails() {

		try {
			details = result.getJSONObject("details");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}

		try {
			DHYreUse = details.getBoolean("dhYsReuse");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}
		try {
			fallBackScv = details.getBoolean("fallbackScsv");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}
		try {
			hasSct = details.getInt("hasSct");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}
		try {
			beastVuln = details.getBoolean("vulnBeast");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}
		try {
			poodleTLS = details.getInt("poodleTls");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}
		try {
			poodleVuln = details.getBoolean("poodle");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block

		}
		try {
			heartBleed = details.getBoolean("heartbleed");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}
		try {
			heartBeat = details.getBoolean("heartbeat");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}
		try {
			logJam = details.getBoolean("logjam");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}
		try {
			supportRC4 = details.getBoolean("supportsRc4");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}
		try {
			freakVuln = details.getBoolean("freak");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}
		try {
			openSSLCcs = details.getInt("openSslCcs");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block

		}
		try {
			forwardSecrecy = details.getInt("forwardSecrecy");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}
		try {
			renegSupport = details.getInt("renegSupport");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}
		try {
			tlsCompression = details.getInt("compressionMethods");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}
		try {
			serverSig = details.getString("serverSignature");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}
	}

	public JSONObject getResult() {
		return result;
	}

	public LocalDate getDate() {
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

	public Long getDuration() {
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

	public String getServerSig() {
		return serverSig;
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

	public String isDHYreUse() {
		return (DHYreUse) ? "Ja" : "Nein";

	}

	public String isFallBackScv() {
		return (fallBackScv) ? "Ja" : "Nein";
	}

	public String getHasSct() {
		return (hasSct > 0) ? "Ja" : "Nein";
	}

	public String isBeastVuln() {

		return (beastVuln) ? "Ja" : "Nein";
	}

	public int getPoodleTLS() {
		return poodleTLS;
	}

	public String isPoodleVuln() {
		return (poodleVuln || poodleTLS > 1) ? "Ja" : "Nein";
	}

	public String isHeartBleed() {
		return (heartBleed) ? "Ja" : "Nein";
	}

	public String isHeartBeat() {
		return (heartBeat) ? "Ja" : "Nein";
	}

	public String isLogJam() {
		return (logJam) ? "Ja" : "Nein";
	}

	public String isSupportRC4() {

		return (supportRC4) ? "Ja" : "Nein";
	}

	public String isFreakVuln() {
		return (freakVuln) ? "Ja" : "Nein";
	}

	public String getOpenSSLCcs() {
		return (openSSLCcs == 1) ? "Nein" : "Ja";
	}

	public String getForwardSecrecy() {
		return (forwardSecrecy > 0) ? "Ja" : "Nein";
	}

	public String getRenegSupport() {
		return (renegSupport == 2) ? "Nein" : "Ja";
	}

	public String getTlsCompression() {
		return (tlsCompression == 0) ? "Nein" : "Ja";
	}

	public void setIP(String ip) {
		this.IP = ip;

	}

	public void setGeoLocation(String geoLoc) {
		this.geoLocation = geoLoc;

	}

}
