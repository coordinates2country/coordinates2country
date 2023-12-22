package io.github.coordinates2country;

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

/**
 * Converts coordinates (example: 50.1, 10.2) into a country identifier (example: "Germany").
 */
public class Coordinates2Country {

    /**
     * Converts coordinates (example: 50.1, 10.2) into a country name in English (example: "Germany").
     */
    public static String country(double latitude, double longitude) {
        return country(latitude, longitude, false);
    }

    /**
     * Converts coordinates (example: 50.1, 10.2) into a the numerical part of a Wikidata QID identifier (example: 183, meaning http://www.wikidata.org/entity/Q183).
     */
    public static String countryQID(double latitude, double longitude) {
        return country(latitude, longitude, true);
    }

    /**
     * Converts coordinates (example: 50.1, 10.2) into a country identifier (example: "Germany").
     * @param wikidataOrNot Whether to return the result as a Wikidata QID number or a country name in English.
     */
    private static String country(double latitude, double longitude, boolean wikidataOrNot) {
        int WIDTH = 2400; // Width of the map image.
        int HEIGHT = 949; // Height of the map image.

        int GREENWICH_X = 939; // At what pixel is the Greenwich longitude.
        int EQUATOR_Y = 555; // At what pixel is the Equator latitude.
        double MIN_LATITUDE = -58.55; // South tip of Sandwich Islands
        double MAX_LATITUDE = 83.64; // North tip of Canada

        if (longitude < -180
            || longitude > 180
            || latitude < MIN_LATITUDE
            || latitude > MAX_LATITUDE) {
            return null; // TODO return Russia or Canada or Chile/etc based on longitude and pole.
        }

        // https://en.wikipedia.org/wiki/Equirectangular_projection
        int x = (WIDTH + (int)(GREENWICH_X + longitude*WIDTH/360)) % WIDTH;
        int y = (int)(EQUATOR_Y - latitude*HEIGHT/(MAX_LATITUDE-MIN_LATITUDE));

        // Each country is a shade of gray in this image which is a map of the world using the https://en.wikipedia.org/wiki/Equirectangular_projection with phi0=0 and lambda0=0.
        // Load it within this method and do not cache it, in order to allow garbage collection between each call, because we consider that low memory usage is more important than speed.
        BufferedImage bitmap = null;
        try {
            bitmap = ImageIO.read(Coordinates2Country.class.getResourceAsStream("/countries-8bitgray.png"));
        }
        catch(IOException e) {
            e.printStackTrace();
            return null;
        }

        String country = nearestCountry(x, y, wikidataOrNot, bitmap);
        //System.out.println("For latitude=" + latitude + " longitude=" + longitude + " (x=" + x + " y=" + y + ") found country " + country);
        return country;
    }

    /**
     * Finds the nearest country, centered on the given pixel, in the given map.
     */
    private static String nearestCountry(int x, int y, boolean wikidataOrNot, BufferedImage bitmap) {
        String country = countryFromPixel(x, y, wikidataOrNot, bitmap);
        if (country == null) {
            // We are in the sea or right on a border. Check surrounding pixels.
            int radius = 1;
            while (country == null) {
                country = countryAtDistance(x, y, radius, wikidataOrNot, bitmap);
                radius++;
            }
        }
        return country;
    }

    /**
     * Finds the most represented country in the pixels situated at given distance ("radius") of given pixel.
     * Distance is currently not implemented as real radius, but as a rectangle.
     */
    private static String countryAtDistance(int centerX, int centerY, int radius, boolean wikidataOrNot, BufferedImage bitmap) {
        //System.out.println("radius=" + radius);
        int x1 = centerX - radius;
        int x2 = centerX + radius;
        int y1 = centerY - radius;
        int y2 = centerY + radius;
        Map<String, Integer> countriesOccurrences = new HashMap<>(); // Key: Country, Value: Number of occurrences of that country in the pixels at said distance.
       
        // Horizontal parts of the rectangle. 
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y += y2 - y1) { // y1 then y2 then end the loop.
            //System.out.println("vertical, radius=" + radius + " x=" + x + " y=" +y);
                String country = countryFromPixel(x, y, wikidataOrNot, bitmap);
                if (country != null) {
                    int occurrences = 0;
                    if (countriesOccurrences.containsKey(country)) {
                        occurrences = countriesOccurrences.get(country);
                    }
                    countriesOccurrences.put(country, occurrences);
                }
           }
        }
        // Vertical parts of the rectangle, excluding corners. 
        for (int y = y1 + 1; y <= y2 - 1; y++) {
            for (int x = x1; x <= x2; x += x2 - x1) { // x1 then x2 then end the loop.
            //System.out.println("horizontal, radius=" + radius + " x=" + x + " y=" +y);
                String country = countryFromPixel(x, y, wikidataOrNot, bitmap);
                if (country != null) {
                    int occurrences = 0;
                    if (countriesOccurrences.containsKey(country)) {
                        occurrences = countriesOccurrences.get(country);
                    }
                    countriesOccurrences.put(country, occurrences);
                }
           }
        }

        // None of the searched pixels contained a country.
        if (countriesOccurrences.size() < 1) {
            return null;
        }

        // Return the country with most pixels.
        Entry<String, Integer> maxEntry = Collections.max(
            countriesOccurrences.entrySet(),
            (Entry<String, Integer> e1, Entry<String, Integer> e2) -> e1.getValue().compareTo(e2.getValue()));
        return maxEntry.getKey();
    }

    /**
     * Finds the country under a given pixel.
     */
    private static String countryFromPixel(int x, int y, boolean wikidataOrNot, BufferedImage bitmap) {
        int grayshade = bitmap.getRaster().getSample(x, y, 0);
        return countryFromGrayshade(grayshade, wikidataOrNot);
   }

    /**
     * Finds the country represented by a given gray shade.
     * The shades are stored in a CSV file.
     */
    private static String countryFromGrayshade(int wantedGrayshade, boolean wikidataOrNot) {
        Map<Integer, String> countriesByGrayshade = new HashMap<>(); // Key: Gray value from 0 to 255. Value: Country.
        try {
            // Load it within this method and do not cache it, in order to allow garbage collection between each call, because we consider that low memory usage is more important than speed. This method is called only once per country(...) call.
            BufferedReader br = new BufferedReader(new InputStreamReader(Coordinates2Country.class.getResourceAsStream("/countries.csv")));
            String line = br.readLine();
            while (line != null) {
                String[] parts = line.split(",");
                int grayshade = Integer.parseInt(parts[0]);
                String country = wikidataOrNot ? parts[2] : parts[1];
                countriesByGrayshade.put(grayshade, country);
                line = br.readLine();
           }
           br.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return countriesByGrayshade.get(wantedGrayshade);
    }


    /**
     * Command line utility, mostly for testing purposes.
     *
     * Usage example:
     * java -cp build/libs/coordinates2country.jar io.github.coordinates2country.Coordinates2Country 50.1 10.2
     * Output: Germany
     */
    public static void main(String[] args) {
        System.out.println(country(Double.parseDouble(args[0]), Double.parseDouble(args[1])));
    }
}
