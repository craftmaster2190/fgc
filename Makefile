default: build-jar

build-jar:
	cd client \
	&& npm run build \
	&& npm run deploy \
	&& cd ../server \
	&& ./gradlew clean build

run: 
	java -jar server/build/libs/fgc*.jar

build-and-run: build-jar run
