# Operation Alchemy

REST API made with **Java 17 + Spring Boot 3** that finds the location of
Alchemy's laboratory (trilateration) and rebuilds the intercepted message.
It uses **Domain-Driven Design** and **Hexagonal Architecture (Ports and Adapters)**.
The information is stored only in memory (no database).

## Requirements

- Java 17 or newer (`java -version`)
- Maven, or an IDE that already includes it (IntelliJ IDEA, Eclipse, VS Code with the Java extension)
- Internet the first time, so Maven can download Spring Boot

## How to run

**Option 1 - IntelliJ IDEA / Eclipse / VS Code**

1. Open the `operation-alchemy` folder as a Maven project.
2. Wait until the dependencies are downloaded.
3. Run the class `OperationAlchemyApplication`.

**Option 2 - Terminal (with Maven installed)**

```
cd operation-alchemy
mvn spring-boot:run
```

The API runs on `http://localhost:8080`.

## How to run the tests

```
mvn test
```

From the IDE: right click on `src/test/java` -> Run tests.

## Project structure

```
src/main/java/com/flash/alchemy/
├── OperationAlchemyApplication.java        # Starts Spring Boot
├── domain/                                 # Business rules (plain Java, no Spring, no HTTP)
│   ├── exception/                          # Domain errors
│   ├── model/
│   │   ├── Position.java                   # Value object (x, y)
│   │   ├── Device.java                     # Entity with its validations
│   │   ├── KnownDevices.java               # The three devices and their positions
│   │   └── DecodeResult.java               # Location + message
│   └── service/
│       ├── LocationCalculator.java         # Trilateration
│       ├── MessageDecoder.java             # Message reconstruction
│       └── TransmissionDecoder.java        # Joins location + message
├── application/                            # Use cases
│   ├── port/in/InterceptionUseCase.java    # Input port (F1 to F6)
│   ├── port/in/DeviceCommand.java          # Input data, independent of HTTP
│   ├── port/out/DeviceRepository.java      # Output port (storage)
│   └── service/InterceptionService.java    # Implementation of the use cases
└── infrastructure/                         # Adapters
    ├── config/BeanConfiguration.java       # Connects the pieces (dependency injection)
    ├── persistence/InMemoryDeviceRepository.java   # Output adapter (memory)
    └── web/                                # Input adapter (REST)
        ├── ProcessController.java
        ├── DeviceController.java
        ├── GlobalExceptionHandler.java     # Domain errors -> HTTP codes
        └── dto/                            # JSON request and response classes
```

Dependencies only go inward: `infrastructure -> application -> domain`.
The domain and the application do not have Spring annotations; Spring is only
used in the infrastructure layer.

## Devices

| Name             | Position     |
|------------------|--------------|
| `star_labs`      | (-500, -200) |
| `ccpd`           | (100, -100)  |
| `star_satellite` | (500, 100)   |

## Endpoints

| Req. | Method | URL                | Description                               |
|------|--------|--------------------|-------------------------------------------|
| F1   | POST   | `/process`         | Process the three devices at once         |
| F2   | POST   | `/devices/{name}`  | Register one device                       |
| F3   | GET    | `/devices`         | List registered devices                   |
| F3   | GET    | `/devices/{name}`  | See one registered device                 |
| F3   | GET    | `/process`         | Process the accumulated information       |
| F4   | PUT    | `/devices/{name}`  | Replace all the data of a device          |
| F5   | PATCH  | `/devices/{name}`  | Update distance, message or both          |
| F6   | DELETE | `/devices/{name}`  | Delete a device                           |

### Status codes

- `200` OK / `201` registered
- `400` invalid data, invalid JSON or unknown device
- `404` device not registered, or not enough information to process
- `409` device already registered (use PUT or PATCH)

## Examples (curl)

F1 - complete information:

```
curl -X POST http://localhost:8080/process -H "Content-Type: application/json" -d "{\"devices\":[{\"name\":\"star_labs\",\"distance\":640.31,\"message\":[\"the\",\"\",\"is\",\"\",\"the\",\"\",\"factory\"]},{\"name\":\"ccpd\",\"distance\":447.21,\"message\":[\"\",\"laboratory\",\"\",\"under\",\"\",\"old\",\"\"]},{\"name\":\"star_satellite\",\"distance\":632.46,\"message\":[\"\",\"the\",\"\",\"is\",\"under\",\"\",\"old\",\"factory\"]}]}"
```

Response:

```json
{"position": {"x": -99.99, "y": 299.97}, "message": "the laboratory is under the old factory"}
```

F2 - register one device:

```
curl -X POST http://localhost:8080/devices/ccpd -H "Content-Type: application/json" -d "{\"distance\":447.21,\"message\":[\"\",\"laboratory\",\"\",\"under\",\"\",\"old\",\"\"]}"
```

F3 - consult and process:

```
curl http://localhost:8080/devices
curl http://localhost:8080/process
```

F4 - replace (both fields are required):

```
curl -X PUT http://localhost:8080/devices/ccpd -H "Content-Type: application/json" -d "{\"distance\":450,\"message\":[\"\",\"laboratory\",\"\",\"under\",\"\",\"old\",\"\"]}"
```

F5 - partial update (only the fields you send are changed):

```
curl -X PATCH http://localhost:8080/devices/ccpd -H "Content-Type: application/json" -d "{\"distance\":447.21}"
```

F6 - delete:

```
curl -X DELETE http://localhost:8080/devices/ccpd
```

You can also use Postman or Insomnia. In PowerShell use `curl.exe` instead of `curl`.
