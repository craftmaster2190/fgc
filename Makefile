default: build-jar

build-jar:
	npm run build
	npm run deploy
	./gradlew clean build

run: 
	java -jar build/libs/fgc*.jar

build-and-run: build-jar run
