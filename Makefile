deploy : 
	./gradlew build
	cp -f ./dist/baritone-api-forge-1.2.10.jar ~/.minecraft/mods/1.12.2/
