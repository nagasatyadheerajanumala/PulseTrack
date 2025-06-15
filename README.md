# ⚡ PulseTrack – Uptime & Log Monitoring as a Service

PulseTrack is a full-stack monitoring tool that helps developers, startups, and small teams track the uptime and performance of their APIs, websites, or backend services — with real-time alerts, logs, public status pages, and analytics.

> Built for builders who want reliability without SaaS pricing or bloat.

---

## 🔧 Tech Stack

| Layer       | Tech Used                                   |
|-------------|---------------------------------------------|
| Backend     | Java, Spring Boot, MySQL, JWT, REST APIs    |
| Frontend    | React, TailwindCSS, Axios, Chart.js         |
| Notifications | Email (SMTP), Slack Webhooks              |
| Real-time   | WebSocket (Spring + Socket.IO)              |
| Deployment  | Docker (planned), AWS (target platform)     |

---

## 📦 Features

### ✅ Core Functionality
- Monitor any public/private **URL or API** at custom intervals
- **Retry logic** before triggering alerts to reduce noise
- Store logs for every health check (status, response time)
- **Email & Slack alerts** on consistent failures
- Public-facing **status pages** for transparency
- JWT-authenticated REST API for secure access

### 📊 Analytics & Real-time (Backend Done, Frontend In Progress)
- Uptime percentage over time
- Average response time
- Live WebSocket dashboard updates (in progress)
- Export logs (CSV planned)

---

## 🚀 Getting Started

### 🐳 Project Structure

```
pulsetrack/
│
├── backend/                   # Java Spring Boot backend
│   ├── src/main/java/com/… # Controllers, Services, Models
│   ├── resources/             # application.yml
│
├── frontend/                  # React frontend
│   ├── src/                   # Components, Pages, Utils
│   ├── public/
│
├── README.md
└── .gitignore
```

---

### 🧠 Backend Setup (Spring Boot)

1. **Set up MySQL:**

```sql
CREATE DATABASE pulsetrack;
```
2.	Edit your DB credentials:

In backend/src/main/resources/application.yml:
```
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pulsetrack
    username: root
    password: root
```

3. Run the backend:
```
cd backend
./mvnw spring-boot:run
```
💻 Frontend Setup (React)
```
cd frontend
npm install
npm run dev  # or npm run start
```

🔐 Authentication (JWT)
	•	After login or registration, you receive a JWT token
	•	Send it in every API request:
```
Authorization: Bearer <token>
```

## 📡 API Endpoints

### 🔐 Authentication

| Method | Endpoint         | Description              |
|--------|------------------|--------------------------|
| POST   | `/auth/register` | Register a new user      |
| POST   | `/auth/login`    | Login and receive a JWT  |

> After login/register, use the token in the `Authorization` header:  
> `Authorization: Bearer <your-token>`

---

### 📊 Monitor Management (Protected Routes)

| Method | Endpoint       | Description               |
|--------|----------------|---------------------------|
| POST   | `/monitors`    | Create a new monitor      |
| GET    | `/monitors`    | Get all monitors for user |

**Request Body for Creating a Monitor:**
```json
{
  "name": "My API Monitor",
  "url": "https://example.com/api/health",
  "intervalInMinutes": 15
}
```
 
### 🌐 Public Status Pages

| Method | Endpoint                                | Description                          |
|--------|-----------------------------------------|--------------------------------------|
| GET    | `/status-pages/public/{publicKey}`      | View the public status of a monitor  |

**Example:**
   GET /status-pages/public/abcd1234xyz

   This endpoint is **unauthenticated** and is designed to be shared with clients, users, or team members.

---

### 📈 Uptime & Analytics

Included in the public status page response:

| Field              | Description                                        |
|--------------------|----------------------------------------------------|
| `uptimePercentage` | Uptime percentage based on historical checks       |
| `avgResponseTime`  | Average response time in milliseconds              |
| `lastChecked`      | Timestamp of the most recent health check          |
| `lastStatusCode`   | HTTP status code from the latest monitor execution |

**Sample JSON Response:**

```json
{
  "name": "My API Monitor",
  "url": "https://example.com/api/health",
  "intervalInMinutes": 15,
  "publicKey": "abcd1234xyz",
  "uptimePercentage": 99.1,
  "avgResponseTime": 210,
  "lastChecked": "2025-06-14T14:02:30",
  "lastStatusCode": 200
}
```



---

## 📤 Export & Logs (Upcoming)

| Feature           | Description                                      |
|-------------------|--------------------------------------------------|
| CSV Export        | Download monitor logs as `.csv` from frontend    |
| Log Filters       | Filter logs by date range, status code, etc.     |
| Pagination        | Server-side pagination for large log sets        |

---

## 🔒 Security Notes

- All monitor-related APIs are protected via **JWT-based authentication**.
- Sensitive credentials (auth tokens, passwords) are **never exposed** in responses.
- Public status pages expose **only non-sensitive** monitor metadata.
- You can revoke or regenerate public keys at any time (planned).

---

## 💡 Use Cases

- 📈 Developers tracking uptime of microservices or APIs
- 🛠️ Startups needing public status pages without paying for SaaS
- 🚨 Teams wanting email/Slack alerts for incidents
- 👨‍💻 Hackers and indie makers monitoring side projects

---

## 🙌 Contributing

Have an idea or found a bug? Contributions are welcome!

```bash
git clone https://github.com/nagasatyadheerajanumala/PulseTrack.git
cd PulseTrack
```


📬 Contact

Made with 💻 by Naga Satya Dheeraj
Project updates coming soon — stay tuned!
