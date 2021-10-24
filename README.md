# coordinates2country

What country is at a particular latitude/longitude? This Java/Android library tells you in 100 milliseconds, without using the Internet and without requiring any permission.

# Build

Run `./build.sh`

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
