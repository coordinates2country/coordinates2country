cp data/countries-8bitgray.png src/main/resources/
tail -n +2 data/countries.csv | grep -v "^," > src/main/resources/countries.csv # TODO remove sample coordinates columns
./gradlew build
