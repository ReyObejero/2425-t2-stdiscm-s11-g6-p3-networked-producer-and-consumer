# P3 - Networked Producer and Consumer

##  Requirements

- Java 21
- Docker

## Build/Compilation Instructions

### Consumer

1. Open a terminal and navigate to the project folder.
2. Enter the following command to build and run the Docker image:
```
docker-compose up --build
```
3. Access `http://localhost:8080` in a web browser.

### Producer

1. Open a terminal and navigate to the project folder.
2. Enter the following command to compile the source code:
```
javac producer/producer.java
```
3. Enter the following command to upload videos:
```
java producer.Producer <nThreads> <videoDir1> <videoDir2> ... <videoDirNThreads>
```
