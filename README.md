# birthday-wisher

## Running the proxy
1. Change directory into `birthday-wisher/proxy`.
2. Build the project: `./gradlew build -x test`.
3. Run the project: (note that terminal output will indicate that it only runs to 80% as it waits for requests)
    * To run a proxy instance on 8080: `./gradlew bootRun --args='--spring.profiles.active=inst1'`
    * To run a proxy instance on 8081: `./gradlew bootRun --args='--spring.profiles.active=inst2'`

## Running the server
1. Change directory into `birthday-wisher/server`.
2. Build the project: `./gradlew build -x test`.
3. Run the project: (note that terminal output will indicate that it only runs to 80% as it waits for requests)
   * To run a server instance on 8082: `./gradlew bootRun --args='--spring.profiles.active=inst1'`
   * To run a server instance on 8083: `./gradlew bootRun --args='--spring.profiles.active=inst2'`
   * To run a server instance on 8084: `./gradlew bootRun --args='--spring.profiles.active=inst3'`
   * To run a server instance on 8085: `./gradlew bootRun --args='--spring.profiles.active=inst4'`
   * To run a server instance on 8086: `./gradlew bootRun --args='--spring.profiles.active=inst5'`
   * To run a server instance on 8087: `./gradlew bootRun --args='--spring.profiles.active=inst6'`

## Running the client
1. Change directory into `birthday-wisher/client`. 
2. Run `npm install` to download project dependencies. 
3. Run `npm start` to start the app.
4. Open http://localhost:3000 to view the app in your browser.
