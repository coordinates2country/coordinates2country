# coordinates2country

What country is at a particular latitude/longitude? This Java library tells you in 100 milliseconds, without using the Internet and without requiring any permission.

- Fast reverse geocoding
- Never needs an Internet connection

For the Android version, see https://github.com/coordinates2country/coordinates2country-android.

# Use

This [sample program that uses the library](https://github.com/coordinates2country/sample) might help.

## 1) Import the library

If using Gradle:
```
implementation("io.github.coordinates2country:coordinates2country:1.4")
```

For other build systems or for the JAR, search for the latest version on [Maven Central](https://central.sonatype.com/namespace/io.github.coordinates2country).

At the top of your Java file, after the package declaration, insert this line:
```
import io.github.coordinates2country.Coordinates2Country;
```

## 2) Call the library

`Coordinates2Country.country(-23.7, 39.8)` returns the String `France`.

If you prefer identifiers, `Coordinates2Country.countryQID(-23.7, 39.8)` returns `142`, the Wikidata [QID](https://www.wikidata.org/wiki/Q142) number of France.

# Testimonial

> _Impressed with the library! It swiftly translated coordinates to countries with precision. The developer's quick response to an issue I encountered, despite the library's age, reflects their commitment to user satisfaction. Kudos for a reliable tool and excellent support!_

Layton Berth, developer at X-Plor

# Build

Run `./gradlew build`, or `./build.sh` if you modified anything in the `data` folder.

# Generate the gray map

- Open countries.xcf in Gimp
- Image > Duplicate
- Colors > Components > Extract component > RGB Red
- File > Export As
- Filename: data/countries-8bitgray.png
- Export > 8bpc GRAY 

When modifying the map, you can modify colors to see better, as long as you keep the RGB red component.

# Info

Source image: https://commons.wikimedia.org/wiki/File:Internationalwaters.png Kvasir Creative Commons Attribution-Share Alike 3.0 Unported, 2.5 Generic, 2.0 Generic and 1.0 Generic license.

Projection: https://en.wikipedia.org/wiki/Equirectangular_projection with phi0=0 and lambda0=0

Useful maps: https://farm8.staticflickr.com/7292/10134658063_fca4fc3da2_o.jpg https://i.imgur.com/lzm0fWN.png

[Release procedure](https://github.com/coordinates2country/coordinates2country/wiki#release-procedure)
