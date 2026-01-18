# AI Recruitment Screening Platform

An AI-powered recruitment screening system that helps organizations efficiently filter job applicants, shortlist qualified candidates, and communicate hiring decisions at scale.

This platform enables recruiters to define custom job application forms, collect resumes before a deadline, and leverage AI to evaluate candidates based on recruiter-defined criteria, drastically reducing manual screening effort.

---

## Features

### Organization & Recruiter Management
- Organization signup with email verification
- Secure authentication using JWT
- Recruiter login and job management

### Job Application Management
- Create custom job application forms:
  - Text-based forms
  - Resume upload–only forms
- Automatically generated public application URLs
- Application deadline enforcement

### Resume Handling
- Resume uploads stored securely using **Cloudinary**
- Resume text extraction using **Apache PDFBox**
- Token estimation using **jtokkit**
- Structured candidate data persistence in **PostgreSQL**

### AI-Powered Candidate Filtering
- Recruiter-defined filtering criteria via prompt input
- AI agent evaluates candidates using:
  - **LangChain4j**
  - **Ollama (LLaMA 3.2 : 1B open-source model)**
- Returns ranked or matching candidates based on requirements

### Candidate Decision Workflow
- Accepted and Rejected candidate carts
- Ability to move candidates between carts before final decision
- Human-in-the-loop AI-assisted decision making

### Automated Candidate Communication
- AI-generated personalized acceptance and rejection messages
- Email delivery via **RabbitMQ-backed mail queue**
- Scalable and non-blocking email processing

---

## Tech Stack

| Category | Technology |
|-------|------------|
| Backend | Java, Spring Boot |
| AI / LLM | LangChain4j, Ollama (LLaMA 3.2 : 1B) |
| Database | PostgreSQL |
| Authentication | JWT |
| Messaging / Queue | RabbitMQ |
| File Storage | Cloudinary |
| Resume Parsing | Apache PDFBox |
| Token Estimation | jtokkit |
| Build Tool | Maven |
| API Style | REST |

---

## How It Works

1. **Organization registers** → verifies email → logs in
2. Recruiter **creates a job opening** and custom application form
3. Platform generates a **public application URL**
4. Applicants submit applications **before deadline**
5. Recruiter provides **AI filtering prompts**
6. AI analyzes resumes and application data
7. Recruiter reviews AI-matched candidates
8. Candidates are sorted into **Accepted / Rejected carts**
9. AI generates **personalized emails**
10. Emails are delivered asynchronously via RabbitMQ

---

## Security & Reliability
- JWT-based authentication
- Deadline enforcement for fairness
- Asynchronous email processing
- Open-source AI model (no vendor lock-in)

---

## Use Cases
- High-volume recruitment
- Resume-heavy hiring pipelines
- Early-stage startups and HR teams
- Organizations seeking unbiased AI-assisted screening

---

## Future Enhancements
- Candidate scoring & ranking
- Interview scheduling integration
- Recruiter analytics dashboard
- Multi-language resume support
- Fine-tuned domain-specific AI models

---

## Author
- Samuel Mbanisi (Backend Developer | AI Systems)
