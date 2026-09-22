# GamesHub Backend

A Spring Boot backend for GamesHub, a Steam-style mobile game platform.

## Tech Stack

- Java 21
- Spring Boot 4.1.0
- MongoDB
- Spring Security + JWT Authentication
- WebSocket (STOMP)
- Steam Web API Integration
- Stripe Payment
- Swagger UI

## Features

- Game store with Steam API integration (auto-sync & scheduled refresh)
- JWT-based authentication
- Friend system
- Community servers with real-time chat (WebSocket)
- Order & payment processing (Stripe)
- Game reviews
- Genre filtering & search

## API Endpoints

### Games
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /games | Get all games |
| GET | /games/paged | Get games with pagination |
| GET | /games/popular | Get popular games |
| GET | /games/search | Search games by keyword |
| GET | /games/filter | Filter games by genre |
| GET | /games/genres | Get all available genres |
| POST | /games/import/{steamAppId} | Import game from Steam |

### Users
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /users | Register |
| POST | /users/login | Login |
| GET | /users/{id} | Get user by ID |

### Friendships
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /friendships/user/{userId} | Get user's friendships |
| POST | /friendships | Add friend |
| PUT | /friendships/{id} | Update friendship status |
| DELETE | /friendships/{id} | Remove friend |

### Orders
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /orders | Create order |
| POST | /orders/{id}/payment | Process payment |
| GET | /orders/{id}/confirm | Confirm order |

## Setup

### Prerequisites
- Java 21
- MongoDB running on localhost:27017
- Steam API Key
- Stripe API Key


spring.data.mongodb.uri=mongodb://localhost:27017/gameshub
steam.api.key=YOUR_STEAM_API_KEY
stripe.api.key=YOUR_STRIPE_API_KEY



### Run
```bash
mvn spring-boot:run -DskipTests
```

### API Documentation
After running, visit:

http://localhost:8080/swagger-ui/index.html


## Scheduled Tasks

| Task | Schedule | Description |
|------|----------|-------------|
| Steam App List Sync | Every 12 hours | Sync Steam game list to MongoDB |
| Process Unprocessed Games | Every 2 minutes | Fetch full game data from Steam |
| Refresh Existing Games | Every 2 min (Tue-Wed) | Update existing game data |

### Environment Variables
Create a `.env` or set in `application.properties`:
