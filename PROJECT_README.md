# Real-time Data Dashboard with Google Assistant Integration

This project implements a real-time dashboard that visualizes continuously streamed data. It features a Java Spring Boot backend for data processing and WebSocket communication, and a React frontend for live chart visualization. Additionally, it integrates with Google Assistant to query metrics via voice commands.

## Features

-   **Live Data Display:** Frontend chart updates every ~2 seconds with new data from the backend.
-   **Simulated Data Source:** Backend generates simulated sensor data (e.g., temperature) periodically.
-   **WebSocket Communication:** Real-time data transfer from backend to frontend.
-   **React Frontend:** Displays data using Chart.js for a live line chart.
-   **Spring Boot Backend:** Manages data generation, WebSocket connections, and Google Assistant webhook.
-   **Google Assistant Integration:** Allows querying of current metrics (e.g., "What's the current temperature?", "How many users are online?") via voice.

## Technical Stack

-   **Backend:** Java 11, Spring Boot 2.7.x, Spring WebSocket, Jackson (for JSON)
-   **Frontend:** React 18.x, Chart.js, react-chartjs-2, WebSocket API
-   **Build Tools:** Maven (backend), npm (frontend)

## Project Structure

```
.
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/example/realtimedashboard/
│       │   │   ├── RealTimeDashboardApplication.java (Main application)
│       │   │   ├── config/WebSocketConfig.java       (WebSocket endpoint setup)
│       │   │   ├── controller/GoogleAssistantWebhookController.java (Handles Assistant requests)
│       │   │   ├── dto/DataPoint.java                (Data structure for metrics)
│       │   │   ├── google/                           (Request/Response POJOs for Assistant)
│       │   │   ├── handler/DataWebSocketHandler.java (Manages WebSocket sessions & broadcasts)
│       │   │   └── service/DataGenerationService.java  (Simulates and broadcasts data)
│       │   └── resources/
│       └── test/                                     (Unit tests)
├── frontend/
│   ├── package.json
│   ├── public/
│   │   └── index.html
│   └── src/
│       ├── App.css
│       ├── App.js                                    (Main React component, chart, WebSocket client)
│       ├── index.css
│       └── index.js
└── README.md (or PROJECT_README.md if README.md is problematic)
```

## Setup and Running

### Prerequisites

-   Java JDK 11 or later
-   Maven 3.6.x or later
-   Node.js 16.x or later (which includes npm)

### Backend

1.  **Navigate to the backend directory:**
    ```bash
    cd backend
    ```

2.  **Build the project using Maven:**
    ```bash
    mvn clean install
    ```

3.  **Run the Spring Boot application:**
    ```bash
    mvn spring-boot:run
    # Alternatively, you can run the JAR file from the target/ directory
    # java -jar target/real-time-dashboard-0.0.1-SNAPSHOT.jar
    ```
    The backend will start on `http://localhost:8080`.
    - WebSocket endpoint: `ws://localhost:8080/data-stream`
    - Google Assistant webhook: `POST http://localhost:8080/api/google-assistant/webhook`

### Frontend

1.  **Navigate to the frontend directory:**
    ```bash
    cd frontend
    ```

2.  **Install dependencies:**
    ```bash
    npm install
    ```

3.  **Start the React development server:**
    ```bash
    npm start
    ```
    The frontend will open in your browser at `http://localhost:3000`. It will attempt to connect to the backend WebSocket.

## How It Works

### Data Flow

1.  The **Spring Boot backend** (`DataGenerationService`) generates a new `DataPoint` (simulated temperature with a timestamp) every 2 seconds.
2.  This `DataPoint` is serialized to JSON and broadcasted by `DataWebSocketHandler` to all connected WebSocket clients.
3.  The **React frontend** (`App.js`) establishes a WebSocket connection to `ws://localhost:8080/data-stream`.
4.  Upon receiving a message, the frontend parses the JSON data and updates the state of its line chart, displaying the latest data points. The chart shows a rolling window of the last 20 data points.

### Google Assistant Integration

1.  **Actions on Google Setup (Manual Steps - Not covered by this codebase directly):**
    *   Create a project in the [Actions on Google Console](https://console.actions.google.com/).
    *   Define intents (e.g., `GetTemperature`, `UserCount`) that your users will say.
        *   Example invocation phrases: "Ask My Dashboard for the temperature", "Talk to My Dashboard about online users".
    *   Set up a fulfillment webhook pointing to your deployed backend's endpoint: `https://<your-publicly-accessible-backend-url>/api/google-assistant/webhook`. (For local testing, tools like `ngrok` can expose your localhost to the internet).
2.  **Backend Webhook (`GoogleAssistantWebhookController`):**
    *   When a user interacts with your Action on Google Assistant, Google sends a POST request (with a JSON payload like `GoogleAssistantRequest.java`) to your webhook.
    *   The controller identifies the intent from the request.
    *   It fetches the required data (e.g., latest temperature or user count from `DataGenerationService`).
    *   It constructs a JSON response (`GoogleAssistantResponse.java`) containing the speech text.
    *   Google Assistant speaks this response back to the user.

    **Current supported intents (as configured in `GoogleAssistantWebhookController`):**
    *   `actions.intent.MAIN`: Welcome message.
    *   `GetTemperature` (or `TemperatureCheck`, `GetCurrentValues`): "The current simulated temperature is X.YZ degrees Celsius, recorded at HH:mm:ss."
    *   `UserCount` (or `GetOnlineUsers`): "There are currently N simulated users online."

## Code Comments

Key components and complex logic within the Java and JavaScript files have been commented to explain their purpose and functionality. The Javadoc style comments are used in the backend and standard JS comments in the frontend.

## Further Development / Refinements

-   **Real Data Source:** Replace the simulated data in `DataGenerationService` with a connection to a real data provider (e.g., sensors, financial APIs, user activity database).
-   **Kafka Integration:** For higher scalability and more robust data streaming, integrate Apache Kafka as initially proposed.
-   **Enhanced Frontend:**
    *   Add more chart types or dashboard widgets.
    *   Implement user-configurable filtering options (time ranges, categories if applicable).
    *   Improve UI/UX.
-   **Advanced Google Assistant Features:**
    *   Use more complex conversation flows (scenes, parameters in intents).
    *   Store user preferences.
    *   Provide visual responses on smart displays.
-   **Security:**
    *   Secure the WebSocket endpoint (restrict origins, authentication).
    *   Verify requests to the Google Assistant webhook (e.g., JWT signature verification).
-   **Error Handling & Logging:** Implement more robust error handling and structured logging throughout the application.
-   **Testing:** Expand test coverage, especially for frontend components and end-to-end flows.
-   **Deployment:** Containerize the applications (e.g., using Docker) for easier deployment.
```
