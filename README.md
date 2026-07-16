# iACADEMY Library Management System - Developer & Integration Guide

Welcome to the unified documentation for the iACADEMY Library Management System. This guide merges all setup guides and module specifications into one comprehensive resource. It covers workspace layout, tools, database seeding, IDE setup, API contracts, and testing tips.

---

## 1. Project Overview & Layout

The project uses the classic **Eclipse Dynamic Web Project** directory structure.

```
Library-Management-System-dev/
├── src/                          # Java Source Code (Servlets, DAOs, Models, Utilities)
│   └── com.iacademy.library/
│       ├── connection/
│       ├── dao/
│       ├── model/
│       ├── service/
│       ├── servlet/
│       └── util/
├── WebContent/                   # Static & Web-facing Resources
│   ├── views/                    # JSP pages (dashboard, login, register, signup)
│   ├── css/                      # Styling
│   ├── js/                       # Front-end javascript controllers
│   ├── images/                   # Asset files
│   └── WEB-INF/
│       ├── classes/              # Compiled servlet class files
│       └── lib/                  # Bundled dependencies (.jar files)
├── database/                     # SQL Schemas
│   └── database_schema.sql       # MySQL/MariaDB schema & seed scripts
└── README.md                     # Combined project guide (this file)
```

---

## 2. Prerequisites & Installed Tools

To run and test this application locally, ensure you have the following installed:

| Tool | Purpose | Note |
|---|---|---|
| **XAMPP** | Database Server (MariaDB / MySQL) | Starts the local database server. XAMPP's Apache server is not used for running Java. |
| **Apache Tomcat 9.0** | Java Servlet Container | Hosts and runs the JSP/Servlet web application. |
| **JDK (Java 1.8)** | Compiler/Runtime | Used to compile the Java source code. |
| **IntelliJ IDEA** or **Eclipse** | Integrated Development Environment | Used to manage dependencies, compile classes, and run Tomcat. |

---

## 3. IDE Setup Instructions (IntelliJ IDEA)

The `.iml` file does not come pre-configured with the source root or classpaths. Follow these steps when importing the project:

### Mark the Sources Root
1. Open the project folder in your IDE.
2. Right-click the `Open module settings` folder in the Project Explorer window.
3. Click `Paths` , click **Use module compile path**, and type `\classes` at the end of WEB-INF.
4. Click **Apply**.

### Add Module Dependencies
1. Go to **File → Project Structure** (`Ctrl+Alt+Shift+S`) → **Modules** → select your module → **Dependencies** tab.
2. Click the **+** (plus icon) → **JARs or Directories...**
3. Select all JARs inside `WebContent/WEB-INF/lib/`:
   * `mysql-connector-j-8.0.33.jar`
   * `servlet-api.jar`
   * `taglibs-standard-spec-1.2.5.jar`
   * `taglibs-standard-impl-1.2.5.jar`
4. Click **Apply** and **OK**.

---

## 4. Database Setup & Configurations

The database used is **MariaDB** (packaged with XAMPP). 

### Setup Database & Seed Data
1. Open the **XAMPP Control Panel** and start **MySQL**.
2. Click **Admin** next to MySQL (or visit `http://localhost/phpmyadmin` in your browser).
3. Create a new database named `iacademy_library` or run the SQL script directly.
4. Go to the **SQL** tab, paste the contents of `database/database_schema.sql`, and run it. This builds the `users`, `books`, and `transactions` tables and inserts test/seed data.

### Adjust DB Credentials
If you have custom database credentials, you can adjust them in:
`src/com/iacademy/library/util/DBUtil.java`
```java
private static final String URL = "jdbc:mysql://localhost:3306/iacademy_library?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = ""; // <- Update if you set a password
```

---

## 5. Librarian Dashboard Greeting Integration

We updated `libraryManager.jsp` to display a dynamic database-driven welcome greeting instead of the hardcoded `"Maria"` name.

### How It Works
* The server retrieves the logged-in user's email via the session (`session.getAttribute("email")`).
* If logged in, it queries the database and fetches the user's first name.
* **Developer Fallback**: If no email is present in the session (e.g., when opening `libraryManager.jsp` directly without going through the login page), the code dynamically defaults to querying the database for the **first registered librarian user** (e.g., `Joseph` with initial `J`). If the database is empty, it falls back to `"Librarian"` and `"L"`.
* The HTML rendering block outputs `<%= initial %>` in the avatar bubble and `<%= firstName %>` in the greeting.
* A client-side JavaScript script in `librarian.js` reads this name dynamically and displays: `Good [morning/afternoon/evening], [Librarian Name]!` based on the time of day.

---

## 6. Transactions Module Integration Contract

The Transaction page (`TransactionServlet.java`) secures all add, delete, and update calls by validating the user's logged-in session role.

### The Role Check
```java
HttpSession session = req.getSession(false);
Object role = session.getAttribute("role");
// Must be "librarian" or "admin" to manage transactions
```

> [!IMPORTANT]
> **Authentication Notice**:
> When implementing `LoginServlet.java`, after a successful login, you must store the logged-in user's credentials in the session:
> * `session.setAttribute("role", "librarian");` (or `"admin"`)
> * `session.setAttribute("email", email);` (used by the greeting system)

### Local Dev Testing Flag (`TEST_MODE`)
For convenience, `TransactionServlet.java` has a testing flag at the top:
```java
private static final boolean TEST_MODE = false;
```
If set to `true`, authorization checks are bypassed, allowing you to debug page features without logging in first. **Ensure this is set to `false` in production or graded submissions.**

---

## 7. API Endpoints Reference

| Method | Request Endpoint | Parameters | Description |
|---|---|---|---|
| **GET** | `/TransactionServlet?action=list` | None | Returns a JSON list of all borrowers and their loan history. |
| **GET** | `/TransactionServlet?action=books` | None | Returns a JSON list of books `[{id, title, quantity}]` for dropdown selections. |
| **POST** | `/TransactionServlet` | `action=addBorrower`<br>`firstName`, `surname`, `email`, `bookIds` (repeated) | Registers or appends new loan books to a borrower. |
| **POST** | `/TransactionServlet` | `action=updateBorrower`<br>`userId`, `transactionIds` (repeated), `statuses` (repeated), `newBookIds` (repeated) | Updates book loan statuses to `borrowed` or `returned` or appends new books. |
| **POST** | `/TransactionServlet` | `action=deleteLoan`<br>`transactionId` | Deletes a loan record. Auto-returns book to stock if still borrowed. |

---

## 8. Business Logic Rules

The Transactions module enforces the following database validations:
* **Out of Stock Protection**: Books with `quantity <= 0` cannot be checked out. The Servlet responds with: `"<Title>": All copies are currently borrowed.` and skips that book.
* **Duplicate Loan Restriction**: A borrower cannot hold more than one active borrowed copy of the same book title. Re-borrowing it will flip the existing row status back to `"borrowed"` and decrement stock.
* **Existing Email Binding**: Only emails already registered in the `users` table can be checked out as borrowers. Unregistered emails return: `"No registered user was found with email ..."`.
* **Stock Synchronization**: Returning a book increments stock. Deleting a transaction that was still borrowed also auto-increments stock.

---

## 9. Troubleshooting Tips

| Issue | Likely Cause | Resolution |
|---|---|---|
| Borrowers list is empty or shows errors | Database was not seeded | Ensure `database/database_schema.sql` was executed in phpMyAdmin. |
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Database driver missing | Add the `mysql-connector` jar file in `WebContent/WEB-INF/lib` to IntelliJ dependencies. |
| `403 Forbidden` response from TransactionServlet | `TEST_MODE` is `false` | Log in first via `login.jsp` or flip `TEST_MODE = true` in `TransactionServlet.java` for local debugging. |
| Red compile errors on `src` imports in IntelliJ | Sources root not selected | Right-click the `src` folder and select **Mark Directory as → Sources Root**. |
