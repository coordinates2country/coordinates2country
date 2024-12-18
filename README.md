# coordinates2country

What country is at a particular latitude/longitude? This Java library (less than 100kB) tells you in 50 milliseconds, without using the Internet and without requiring any permission.

- Fast reverse geocoding.
- Never needs an Internet connection, not even the first time.

For the Android version, see https://github.com/coordinates2country/coordinates2country-android.

# Use

This [sample program that uses the library](https://github.com/coordinates2country/sample) might help.

## 1) Import the library

If using Gradle:
```
implementation("io.github.coordinates2country:coordinates2country:1.9")
```

For other build systems or for the JAR, search for the latest version on [Maven Central](https://central.sonatype.com/namespace/io.github.coordinates2country).

At the top of your Java file, after the package declaration, insert this line:
```
import io.github.coordinates2country.Coordinates2Country;
```

## 2) Call the library

`Coordinates2Country.country(-23.7, 39.8)` returns the String `France`.

Alternatives:
- `Coordinates2Country.country(-23.7, 39.8, Locale.SPANISH)` returns the String `Francia`.
- `Coordinates2Country.countryCode(-23.7, 39.8)` returns `FR`, the [ISO 3166-1 alpha-2](https://en.wikipedia.org/wiki/List_of_ISO_3166_country_codes) code of France, used by Java.
- `Coordinates2Country.countryQID(-23.7, 39.8)` returns `142`, the Wikidata [QID](https://www.wikidata.org/wiki/Q142) number of France.

# Testimonial

> _Impressed with the library! It swiftly translated coordinates to countries with precision. The developer's quick response to an issue I encountered, despite the library's age, reflects their commitment to user satisfaction. Kudos for a reliable tool and excellent support!_

Layton Berth, developer at X-Plor

_[Release procedure](https://github.com/coordinates2country/coordinates2country/wiki#release-procedure)_
