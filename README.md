# ⚡ Pokedex Search Application  
A full-stack Pokémon search engine built as part of the FinFactor coding challenge.  
This project contains:

- A **Java Spring Boot backend** that fetches Pokémon details from PokeAPI  
- A **React.js frontend** with a modern UI for searching and displaying Pokémon  
- An in-memory **cache system** to boost performance and reduce external API calls  

---

## 🚀 Project Overview

This application allows users to:

- Search for any Pokémon by **name**  
- View details like:
  - ID  
  - Classic sprite  
  - Height & weight  
  - Types  
  - Abilities  
  - Base stats  
  - Habitat  
  - Flavor text  
  - Legendary/Mythical info  
- Enjoy a modern, responsive, galaxy-themed UI  

The backend exposes a clean REST API `GET /api/pokemon/{name}` which returns a simplified JSON response tailored for the frontend.

---

# 📂 Repository Structure

```
project-root/
│
├── backend/             # Spring Boot backend (Java 17)
│   ├── src/
│   ├── pom.xml
│   └── ...
│
├── frontend/            # React.js frontend (Node + Vite/CRA)
│   ├── src/
│   ├── package.json
│   └── ...
│
└── README.md
```

---

# 🧠 Features

### ✅ Backend (Spring Boot)
- Fetch Pokémon details from PokeAPI  
- Custom response model (clean JSON only)  
- **Caching system** to avoid repeated API calls  
- Graceful handling of:
  - Not found Pokémon  
  - Upstream network failures  
  - Empty/invalid requests  
- Always returns `200 OK` with `{ error: "" }` or `{ PokémonData }`  

### ✅ Frontend (React.js)
- Attractive neon-themed UI  
- Search bar with loading + validation  
- Fully responsive card layout  
- Displays rich Pokémon info  
- Clean code structure  
- Error display for missing Pokémon  

---

# 🛠️ Tech Stack

### Backend
- Java 17  
- Spring Boot  
- Spring Web  
- RestTemplate-based PokeAPI client  
- Custom caching implementation  

### Frontend
- React.js  
- CSS3 (custom design)  
- Fetch API  
- Responsive grid layout  

---

# ▶️ Run the Project Locally

## 1️⃣ Clone the Repository
```sh
git clone https://github.com/<your-username>/<repo-name>.git
cd <repo-name>
```

---

## 2️⃣ Run Backend (Java / Spring Boot)

### Requirements:
- Java 17+
- Maven

### Commands:
```sh
cd backend
mvn spring-boot:run
```

Backend will start at:

```
http://localhost:8080
```

### Test API:
```
http://localhost:8080/api/pokemon/pikachu
```

---

## 3️⃣ Run Frontend (React)

### Requirements:
- Node.js (v16+ recommended)

### Commands:
```sh
cd frontend
npm install
npm start
```

Frontend will start at:

```
http://localhost:3000
```

---

# 🔌 API Specification

### **GET /api/pokemon/{name}**

#### Response Example:
```json
{
  "id": 25,
  "name": "Pikachu",
  "height": 4,
  "weight": 60,
  "types": ["Electric"],
  "abilities": ["Static", "Lightning-rod"],
  "stats": {
    "Hp": 35,
    "Attack": 55,
    "Defense": 40,
    "Speed": 90
  },
  "habitat": "Forest",
  "legendary": false,
  "mythical": false,
  "flavorText": "When several of these POKéMON gather...",
  "spriteUrl": "https://..."
}
```

### Error Response:
```json
{
  "error": "Pokemon not found: xyz"
}
```

---

# 🧪 Caching Details

The backend caches PokeAPI responses:

- Cache key: Pokémon name (lowercase)
- Cache expiry: configurable
- Cache entries: limited to avoid memory overflow
- Benefit: Fast responses for repeated searches

---

# 📸 Screenshots (Add after uploading)
> You can paste screenshots here after deploying.

---

# 📨 Submission Format (As Required)

To submit:

1. Upload code to **public GitHub repo**  
2. Reply to the challenge email **without changing the subject**  
3. Attach the GitHub link  
4. Do NOT attach zip files  

---

# 👨‍💻 Author

**Madhav Dhande**  
Full-Stack Developer (Java + React)  
Email: *your email here*  
GitHub: https://github.com/<your-username>  

---

# ⭐ Final Notes

This project is built with clean code, performance in mind, and follows all requirements of the assessment, including REST guidelines, caching, and UI creativity.

Happy Coding! ⚡
