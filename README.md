# FlickFinder - COMP6000 Project

FlickFinder is a movie recommendation app built with **Java**, **Swing**, and **Spring MVC**, 
integrating with the **TMDb API** for real-time movie data.

---

## 📦 Project Overview

- **UI**: Java Swing (Login, Registration, Search, Recommendations, Account views)
- **Backend / Logic Layer**: Spring MVC style with `@Service` classes (e.g., `MovieService`, `UserService`)
- **Persistence**: Flat file (`users.txt`) for user accounts (no SQL database required)
- **External Data Source**: The Movie Database (TMDb) REST API

---

## ✅ Requirements

- Java 17 or higher
- Maven or Gradle (to manage Spring & JUnit dependencies)
- Internet connection (for TMDb API calls)
- An IDE such as IntelliJ IDEA / Eclipse / NetBeans / Visual Studio

---

## 🧰 Spring Setup

This project uses Spring annotations (e.g., `@Service`) for structure. If you are not starting a full Spring container, they act as markers only. If you do want Spring features like dependency injection:

### Maven (`pom.xml`)
```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>6.0.12</version>
</dependency>
```

### Gradle (`build.gradle`)
```groovy
implementation 'org.springframework:spring-context:6.0.12'
```

> Ensure your IDE has annotation processing enabled if you expand Spring usage.

---

## ▶️ Run the Application

1. **Clone the repository**

```bash
git clone https://git.cs.kent.ac.uk/dc637/comp6000-flickfinder.git
cd comp6000-flickfinder
```

2. **Open in your IDE** and let Maven/Gradle import dependencies.

3. **Build the project** (Maven: `mvn package`, Gradle: `./gradlew build`, or IDE build).

4. **Run the main class** (look for a class with `public static void main` that creates `LoginController`). Example from terminal:

```bash
java -cp target/classes flickfinder.Main
```
Or you can run it from App.java

5. **Login / Register** when the Swing window appears.
   - New users are written to `users.txt` in the project root.
   - After login, the search and recommendation UI loads.

---

## 🧪 Running JUnit Tests

JUnit 5 tests live under `src/test/java`.

### Option A – IDE
- Right-click the `test` folder or individual test class → **Run Tests**

### Option B – Maven 

```bash
mvn test
```

### Option C – Gradle

```bash
./gradlew test
```

### Option D – JUnit Console Launcher (if configured)

```bash
java -jar junit-platform-console-standalone.jar   -cp target/classes:target/test-classes   --scan-class-path
```

> ⚠️ Some tests (e.g., in `MovieServiceModelTest`) call TMDb live. Be online or mock those methods if offline. You can temporarily annotate with `@Disabled` to skip.

---

## 🧪 Test Classes Included

- **`UserServiceModelTest`** – registration, login validation, file persistence, edge cases
- **`LoginControllerTest`** – controller logic: success/fail login, registration validations, UI flow (mocked Swing & services)
- **`MovieServiceModelTest`** – search, trending, genres, movie details/raw JSON, recommendations (with Mockito spy for logic isolation)

---

## 🔮 Future Improvements

- Swap `users.txt` for a SQL/NoSQL database (persistent favorites/watchlists per user)
- Cache recommendations to reduce API calls
- Improve GUI theming and add poster thumbnails everywhere

---

## 👥 Contributors

- **Joshua Turkson** – Lead Developer

---

## 📜 License


Educational use for COMP6000. Not licensed for commercial distribution.
