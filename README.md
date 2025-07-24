<img width="1703" height="615" alt="image" src="https://github.com/user-attachments/assets/bda96912-17aa-4172-a62e-7bfcc1c133db" />



# real-time-cloud-native-chat-server

A cloud native chat server built using Java and other supporting technologies.

## Core dependencies 
- Java 17
- Spring Boot 3.5.3
- Docker version 28.3.0, build 38b7060
- Docker Compose version v2.38.1-desktop.1


## Technologies Used

- **Java**: Core programming language
- **Spring Boot**: Backend framework for building REST APIs and WebSocket endpoints
- **Haskell**: Used for implementing consent rules and custom message filtering logic
- **Cassandra**: Distributed NoSQL database for message storage
- **WebSockets**: Real-time bidirectional communication
- **Redis**: In-memory data store for caching and pub/sub
- **RabbitMQ**: Message broker for asynchronous communication
- **Docker**: Containerization for consistent deployment
- **Kubernetes (K8s)**: Orchestration and scaling of containers

## To Do

- [x] Initialize Spring Boot project structure
- [x] Containerize application dependencies
- [x] Connect with Keycloak for authentication delegation
- [x] Add RabbitMQ for message queuing and delivery
- [x] Set up Cassandra integration for message persistence
- [x] Implement WebSocket endpoints for real-time chat
- [x] Integrate Redis for caching and pub/sub
- [ ] Write REST APIs for user and chat management
- [ ] Write Haskell scripts for message filtering, use in Spring service
- [ ] Create Kubernetes manifests for deployment
- [ ] Add unit and integration tests
- [ ] Write documentation for setup and usage

---
Feel free to contribute or suggest improvements!
