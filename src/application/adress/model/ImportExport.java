package application.adress.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ImportExport {

	public static void exportToExcelFile(ArrayList<ScanTarget> targets, File output) {

		Workbook wb;
		int k = 0;
		if (output.getName().contains("xls")) {

			wb = new HSSFWorkbook();
		} else {
			wb = new XSSFWorkbook();
		}
		int count = 0;
		Sheet sheet = wb.createSheet("Export Scans");
		Row firstLine = sheet.createRow(count++);
		firstLine.createCell(k++).setCellValue("URL");
		firstLine.createCell(k++).setCellValue("DNS");
		firstLine.createCell(k++).setCellValue("IP");
		firstLine.createCell(k++).setCellValue("Geo-Location");
		firstLine.createCell(k++).setCellValue("Online");
		firstLine.createCell(k++).setCellValue("Status");
		firstLine.createCell(k++).setCellValue("Server \n Signatur");
		firstLine.createCell(k++).setCellValue("Rating");
		firstLine.createCell(k++).setCellValue("noCert-Rating");
		firstLine.createCell(k++).setCellValue("Cert-Fehler");
		firstLine.createCell(k++).setCellValue("SSLv2");
		firstLine.createCell(k++).setCellValue("SSLv3");
		firstLine.createCell(k++).setCellValue("TLS 1.0");
		firstLine.createCell(k++).setCellValue("TLS 1.1");
		firstLine.createCell(k++).setCellValue("TLS 1.2");
		firstLine.createCell(k++).setCellValue("Insecure \n Negotiation");
		firstLine.createCell(k++).setCellValue("Forward \n Secrecy");
		firstLine.createCell(k++).setCellValue("RC4");
		firstLine.createCell(k++).setCellValue("Heartbleed");
		firstLine.createCell(k++).setCellValue("openSSLCcs");
		firstLine.createCell(k++).setCellValue("LogJam");
		firstLine.createCell(k++).setCellValue("Poodle");
		firstLine.createCell(k++).setCellValue("TLS-Fallback");
		firstLine.createCell(k++).setCellValue("Freak");
		firstLine.createCell(k++).setCellValue("TLS Compression");
		firstLine.createCell(k++).setCellValue("anon-Suite");
		firstLine.createCell(k++).setCellValue("Datum");

		for (int m = 0; m < targets.size(); m++) {

			Row row;
			ArrayList<Object> exportStrings = new ArrayList<>();
			ScanTarget target = targets.get(m);

			String URL = target.getURL();
			String URI = target.getURI();
			String status = target.getStatus();
			String online = "Nein";

			exportStrings.add(0, URL);
			System.out.println(target.getURI());
			// get the last recent Results
			Map<String, Result> lastRecentResult = target.getLastRecentResults();

			if (!target.getStatus().equalsIgnoreCase("Ready")) {
				row = sheet.createRow(count++);
				row.createCell(0).setCellValue(URL);
				row.createCell(1).setCellValue(URI);
				if (target.getStatus().equalsIgnoreCase("error")) {
					row.createCell(4).setCellValue("Nein");
				} else {
					row.createCell(4).setCellValue("Ja");
				}
				row.createCell(5).setCellValue(target.getStatusMessage());

				continue;
			} else {
				online = "Ja";
			}

			// split in in every IP
			for (String ip : target.getIPs()) {
				Result r = lastRecentResult.get(ip);
				int i = 0;
				row = sheet.createRow(count++);
				Cell cell;
				CellStyle styleGreen = (wb.createCellStyle());
				CellStyle stylered = (wb.createCellStyle());
				CellStyle styleyellow = (wb.createCellStyle());

				styleGreen.setFillBackgroundColor(HSSFColor.LEMON_CHIFFON.index);
				styleGreen.setFillBackgroundColor(HSSFColor.CORAL.index);
				styleGreen.setFillBackgroundColor(HSSFColor.ORANGE.index);

				cell = row.createCell(i++);
				cell.setCellValue(target.getURL());

				cell = row.createCell(i++);
				cell.setCellValue(target.getURI());

				cell = row.createCell(i++);
				cell.setCellValue(r.getIP());

				cell = row.createCell(i++);
				cell.setCellValue("");

				cell = row.createCell(i++);
				cell.setCellValue(online);
				if (online.equals("Nein")) {
					cell.setCellStyle(stylered);
				} else {
					cell.setCellStyle(styleGreen);
				}

				cell = row.createCell(i++);
				cell.setCellValue(r.getStatusMessage());

				if (!r.getStatusMessage().equalsIgnoreCase("ready")) {
					continue;
				}
				;
				cell = row.createCell(i++);
				cell.setCellValue(r.getServerSig());

				cell = row.createCell(i++);
				cell.setCellValue(r.getGrade());
				if (r.getGrade().equals("C") || r.getGrade().equals("D") || r.getGrade().equals("E")) {
					cell.setCellStyle(styleyellow);
				} else if (r.getGrade().equals("F") || r.getGrade().equals("T") || r.getGrade().equals("M")) {
					cell.setCellStyle(stylered);
				} else {
					cell.setCellStyle(styleGreen);
				}

				cell = row.createCell(i++);
				cell.setCellValue(r.getGradeTrustIgnored());
				if (online.equals("Nein")) {
					cell.setCellStyle(stylered);
				} else {
					cell.setCellStyle(styleGreen);
				}

				cell = row.createCell(i++);
				cell.setCellValue(r.getNotTrustedReason());

				cell = row.createCell(i++);
				cell.setCellValue(r.getSsl2());
				if (online.equals("Nein")) {
					cell.setCellStyle(stylered);
				} else {
					cell.setCellStyle(styleGreen);
				}

				cell = row.createCell(i++);
				cell.setCellValue(r.getSsl3());
				if (online.equals("Nein")) {
					cell.setCellStyle(stylered);
				} else {
					cell.setCellStyle(styleGreen);
				}

				cell = row.createCell(i++);
				cell.setCellValue(r.getTls10());
				if (online.equals("Nein")) {
					cell.setCellStyle(stylered);
				} else {
					cell.setCellStyle(styleGreen);
				}

				cell = row.createCell(i++);
				cell.setCellValue(r.getTls11());
				if (online.equals("Nein")) {
					cell.setCellStyle(stylered);
				} else {
					cell.setCellStyle(styleGreen);
				}

				cell = row.createCell(i++);
				cell.setCellValue(r.getTls12());
				if (online.equals("Nein")) {
					cell.setCellStyle(stylered);
				} else {
					cell.setCellStyle(styleGreen);
				}

				cell = row.createCell(i++);
				cell.setCellValue(r.getRenegSupport());
				if (online.equals("Nein")) {
					cell.setCellStyle(stylered);
				} else {
					cell.setCellStyle(styleGreen);
				}

				cell = row.createCell(i++);
				cell.setCellValue(r.getForwardSecrecy());
				if (online.equals("Nein")) {
					cell.setCellStyle(stylered);
				} else {
					cell.setCellStyle(styleGreen);
				}

				cell = row.createCell(i++);
				cell.setCellValue(r.isSupportRC4());
				if (online.equals("Nein")) {
					cell.setCellStyle(stylered);
				} else {
					cell.setCellStyle(styleGreen);
				}

				cell = row.createCell(i++);
				cell.setCellValue(r.isHeartBleed());
				if (online.equals("Nein")) {
					cell.setCellStyle(stylered);
				} else {
					cell.setCellStyle(styleGreen);
				}

				cell = row.createCell(i++);
				cell.setCellValue(r.getOpenSSLCcs());
				if (online.equals("Nein")) {
					cell.setCellStyle(stylered);
				} else {
					cell.setCellStyle(styleGreen);
				}

				cell = row.createCell(i++);
				cell.setCellValue(r.isLogJam());
				if (online.equals("Nein")) {
					cell.setCellStyle(stylered);
				} else {
					cell.setCellStyle(styleGreen);
				}

				cell = row.createCell(i++);
				cell.setCellValue(r.isPoodleVuln());
				if (online.equals("Nein")) {
					cell.setCellStyle(stylered);
				} else {
					cell.setCellStyle(styleGreen);
				}

				cell = row.createCell(i++);
				cell.setCellValue(r.isFallBackScv());
				if (online.equals("Nein")) {
					cell.setCellStyle(stylered);
				} else {
					cell.setCellStyle(styleGreen);
				}

				cell = row.createCell(i++);
				cell.setCellValue(r.isFreakVuln());
				if (online.equals("Nein")) {
					cell.setCellStyle(stylered);
				} else {
					cell.setCellStyle(styleGreen);
				}

				cell = row.createCell(i++);
				cell.setCellValue(r.getTlsCompression());
				if (online.equals("Nein")) {
					cell.setCellStyle(stylered);
				} else {
					cell.setCellStyle(styleGreen);
				}

				cell = row.createCell(i++);
				cell.setCellValue(r.getAnonSuite());
				if (online.equals("Nein")) {
					cell.setCellStyle(stylered);
				} else {
					cell.setCellStyle(styleGreen);
				}

				cell = row.createCell(i++);
				cell.setCellValue(r.getDate().toString());
				if (online.equals("Nein")) {
					cell.setCellStyle(stylered);
				} else {
					cell.setCellStyle(styleGreen);
				}

			}

		}

		FileOutputStream out;
		try {
			out = new FileOutputStream(output);
			wb.write(out);
			out.close();
			wb.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
