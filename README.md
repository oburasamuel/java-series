# 📧 Email Writer SB — Spring Boot + Groq API

A Spring Boot application that generates professional email replies using **Groq's LLM models** with failover support.  
The service takes an input email and optional tone, then uses the Groq API to produce a natural, contextually relevant reply.

---

## 🚀 Features
- **Multi-model failover** — Try multiple Groq models in priority order until one succeeds.
- **Customizable tone** — Choose between professional, casual, friendly, etc.
- **Simple REST API** — Easy to integrate with any frontend or automation pipeline.
- **Environment-based configuration** — Secure API keys via environment variables.

---

## 🛠️ Tech Stack
- **Java 17+**
- **Spring Boot**
- **WebClient (Spring WebFlux)** for non-blocking API calls
- **Groq API** for LLM responses
- **Jackson** for JSON parsing

---

## 📂 Project Structure



---

## ⚙️ Configuration

Edit `application.properties` (or use environment variables):

```properties
spring.application.name=email-writer-sb

# Groq settings
groq.api.url=${GROQ_URI}
groq.api.key=${GROQ_KEY}

# Comma-separated models in priority order
groq.models=meta-llama/llama-4-scout-17b-16e-instruct,llama3-70b-8192,mixtral-8x7b-32768,gemma2-9b-it


Environment Variables:
export GROQ_URI="https://api.groq.com/openai/v1/chat/completions"
export GROQ_KEY="your_api_key_here"


▶️ Running the Project
With Maven
mvn spring-boot:run

With JAR
mvn clean package
java -jar target/email-writer-sb-0.0.1-SNAPSHOT.jar


📬 API Usage

Endpoint:
POST /generate-email-reply

Request Body:
{
  "emailContent": "Hi team, I wanted to follow up on the proposal I sent last week. Can you provide an update?",
  "tone": "professional"
}

Response Example:
{
  "reply": "Hello, thank you for following up. We are currently reviewing your proposal and will get back to you by the end of the week."
}

🧪 Testing with Postman

1.Open Postman

2. Create a new POST request:
http://localhost:8080/generate-email-reply

3. Set header:
Content-Type: application/json

4. Add body (raw → JSON):
{
  "emailContent": "Thanks for the meeting yesterday. Can you send me the final report?",
  "tone": "friendly"
}

🔄 Sequence Diagram
sequenceDiagram
    autonumber
    participant Client as Client App / Postman
    participant API as EmailController (Spring Boot)
    participant Service as EmailGeneratorService
    participant Groq as Groq API

    Client->>API: POST /generate-email-reply (emailContent, tone)
    API->>Service: processRequest()
    Service->>Groq: POST /chat/completions (model, prompt)
    Groq-->>Service: AI-generated email reply
    Service-->>API: return generated reply
    API-->>Client: JSON { reply: "...email text..." }


📄 License

MIT License.