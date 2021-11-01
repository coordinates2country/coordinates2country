cp data/countries-8bitgray.png src/main/resources/
tail -n +2 data/countries.csv | grep -v "^," | sed -e "s/http:\/\/www\.wikidata\.org\/entity\/Q\([0-9]*\).*/\\1/" | sed -E "s/[0-9.-]+,[0-9.-]+,//g" > src/main/resources/countries.csv
./gradlew build
