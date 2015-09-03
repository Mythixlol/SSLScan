package application.adress.model;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;
import com.maxmind.geoip2.record.Postal;
import com.maxmind.geoip2.record.Subdivision;

public class GetGeoLocation {

	public static String getGeoLocation(String ip) throws IOException, GeoIp2Exception {

		// A File object pointing to your GeoIP2 or GeoLite2 database
		File database = new File("C:\\Users\\Mythizzle\\git\\SSLScan\\src\\GeoLite2-City.mmdb");

		// This creates the DatabaseReader object, which should be reused across
		// lookups.
		DatabaseReader reader = new DatabaseReader.Builder(database).build();

		InetAddress ipAddress = InetAddress.getByName(ip);

		// Replace "city" with the appropriate method for your database, e.g.,
		// "country".
		CityResponse response = reader.city(ipAddress);

		Country country = response.getCountry();
		System.out.println(country.getIsoCode()); // 'US'
		System.out.println(country.getName()); // 'United States'

		Subdivision subdivision = response.getMostSpecificSubdivision();
		System.out.println(subdivision.getName()); // 'Minnesota'
		System.out.println(subdivision.getIsoCode()); // 'MN'

		City city = response.getCity();
		System.out.println(city.getName()); // 'Minneapolis'

		Postal postal = response.getPostal();
		System.out.println(postal.getCode()); // '55455'

		Location location = response.getLocation();
		System.out.println(location.getLatitude()); // 44.9733
		System.out.println(location.getLongitude()); // -93.2323

		return null;

	}
}