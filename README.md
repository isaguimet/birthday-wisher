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

## App Interface

#### Profile Page (can see birthday wishes sent to user logged in)
<img width="829" alt="image" src="https://user-images.githubusercontent.com/38235844/233361568-cc0b226d-826c-4175-a826-4d8062533956.png">
    
#### Friends Page (can send birthday wishes to friend)
<img width="598" alt="image" src="https://user-images.githubusercontent.com/38235844/233361704-99e81b5c-8aff-48cf-93f5-2355ec4c1a05.png">
