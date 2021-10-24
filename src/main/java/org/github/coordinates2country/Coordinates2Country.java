package org.github.coordinates2country;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Coordinates2Country {

    private static BufferedImage map = null;

    public static String country(double latitude, double longitude) {
	return country(latitude, longitude, false);
    }
    public static String countryQID(double latitude, double longitude) {
	return country(latitude, longitude, true);
    }
    private static String country(double latitude, double longitude, boolean qidOrNot) {
	int WIDTH = 2400;
	int HEIGHT = 949;

	int GREENWICH_X = 939;
	int EQUATOR_Y = 555;
	double MIN_LATITUDE = -58.55; // South tip of Sandwich Islands
	double MAX_LATITUDE = 83.64; // North tip of Canada

	if (longitude < -180
		|| longitude > 180
		|| latitude < MIN_LATITUDE
		|| latitude > MAX_LATITUDE) {
		return null;
	}

	// https://en.wikipedia.org/wiki/Equirectangular_projection
	int x = (WIDTH + (int)(GREENWICH_X + longitude*WIDTH/360)) % WIDTH;
	int y = (int)(EQUATOR_Y - latitude*HEIGHT/(MAX_LATITUDE-MIN_LATITUDE));

	try {
	    map = ImageIO.read(Coordinates2Country.class.getResourceAsStream("/countries-8bitgray.png"));
	}
	catch(IOException e) {
            e.printStackTrace();
            return "";
	}
  
        String country = nearestCountry(x, y, qidOrNot);
        System.out.println("For latitude=" + latitude + " longitude=" + longitude + " (x=" + x + " y=" + y + ") found country " + country);
        return country;
    }

    private static String nearestCountry(int x, int y, boolean qidOrNot) {
        String country = countryFromPixel(x, y, qidOrNot);
        if (country == null) {
            // We are in the sea or right on a border. Check surrounding pixels.
            int radius = 1;
            while (country == null) {
                country = countryAtDistance(x, y, radius, qidOrNot);
                radius++;
            }
        }
        return country;
    }

    private static String countryAtDistance(int centerX, int centerY, int radius, boolean qidOrNot) {
        System.out.println("radius=" + radius);
        int x1 = centerX - radius;
        int x2 = centerX + radius;
        int y1 = centerY - radius;
        int y2 = centerY + radius;
        Map<String, Integer> countriesOccurrences = new HashMap<>(); // Key: Country, Value: Number of occurrences of that country in the pixels at said distance.
        
        for (int x=x2; x>=x1; x--) {
            for (int y = y1; y <= y2; y += y2-y1) { // y1 then y2 then end the loop.
            System.out.println("vertical, radius=" + radius + " x=" + x + " y=" +y);
                String country = countryFromPixel(x, y, qidOrNot);
                if (country != null) {
                    int occurrences = 0;
                    if (countriesOccurrences.containsKey(country)) {
                        occurrences = countriesOccurrences.get(country);
                    }
                    countriesOccurrences.put(country, occurrences);
                }
           }
        }
        for (int y=y2-1; y>=y1+1; y--) {
            for (int x = x1; x <= x2; x += x2-x1) { // x1 then x2 then end the loop.
            System.out.println("horizontal, radius=" + radius + " x=" + x + " y=" +y);
                String country = countryFromPixel(x, y, qidOrNot);
                if (country != null) {
                    int occurrences = 0;
                    if (countriesOccurrences.containsKey(country)) {
                        occurrences = countriesOccurrences.get(country);
                    }
                    countriesOccurrences.put(country, occurrences);
                }
           }
        }

        if (countriesOccurrences.size() < 1) {
            return null;
        }
        Entry<String, Integer> maxEntry = Collections.max(
            countriesOccurrences.entrySet(),
            (Entry<String, Integer> e1, Entry<String, Integer> e2) -> e1.getValue().compareTo(e2.getValue()));
        return maxEntry.getKey();
    }

    private static String countryFromPixel(int x, int y, boolean qidOrNot) {
        int grayshade = map.getRaster().getSample(x, y, 0);
        return countryFromGrayshade(grayshade, qidOrNot);
   }

    private static String countryFromGrayshade(int wantedGrayshade, boolean qidOrNot) {
        // Get the country that has the grayshade we want.
        Map<Integer, String> countriesByGrayshade = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Coordinates2Country.class.getResourceAsStream("/countries.csv")));
            String line = br.readLine();
            while (line != null) {
                String[] parts = line.split(",");
                int grayshade = Integer.parseInt(parts[0]);
                String country = parts[1]; // TODO or QID
                countriesByGrayshade.put(grayshade, country);
                line = br.readLine();
           }
           br.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        String country = countriesByGrayshade.get(wantedGrayshade);
        return country;
    }


public static void main(String[] args) {
 System.out.println(country(Double.parseDouble(args[0]), Double.parseDouble(args[1])));
 //System.out.println(countryQID(Double.parseDouble(args[0]), Double.parseDouble(args[1])));
}
}
