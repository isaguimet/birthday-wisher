# birthday-wisher

## Running the server

### From Command Line
* To build the project: `./gradlew build`
* To run the project: `./gradlew bootRun`

### For IntelliJ
Recommended instructions for IntelliJ:
* File > Project Structure:
    * Use SDK greater than 17? I used Oracle OpenJDK 19
    * Press the "Apply" and "OK" buttons
* Right-click the server/build.gradle file and select "Link Gradle Project". You should see a progress bar start going
  at the bottom of the IDE, wait a few mins for that to finish.
* Right-click DemoApplication.java and then "Run" to run the server.

## Running the client
* Change directory into `birthday-wisher/client` and run `npm install` to download project dependencies.
* Still from within this `client` directory, run `npm start` to start the app. Open http://localhost:3000 to view it in your browser.
