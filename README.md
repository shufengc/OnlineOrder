# OnlineOrder

A full-stack online food ordering application built with **Spring Boot**, **React.js**, and **AWS Cloud Deployment**.  
Users can register, browse restaurants, add menu items to carts, and checkout orders in a secure cloud-hosted environment.

---

## 🚀 Features

### 🔐 User Authentication
- User registration & login with **Spring Security**
- Session-based authentication

### 🛒 Food Ordering Workflow
- Restaurant browsing and menu filtering
- Add items to shopping cart
- View cart and perform checkout

### 🧩 Backend (Spring Boot)
- RESTful APIs for registration, login, restaurant menus, orders, and carts
- Layered architecture: **Controller → Service → Repository**
- Spring Data JDBC for database operations
- Password encryption and security configuration
- Caching with **Caffeine**
- Unit testing with mocked repositories

### 🎨 Frontend (React + Ant Design)
- Clean UI with Ant Design components
- Registration & login forms
- Restaurant dropdown selection
- Menu browsing with item images
- Shopping cart drawer with real-time total price

### 🗄️ Database
- MySQL hosted on **AWS RDS**
- Restaurant, menu items, customers, carts, and orders

### ☁️ Cloud Deployment (AWS)
- Backend containerized and deployed via **AWS ECR** + **AWS App Runner**
- Persistent data storage with RDS
- Exposed public API endpoints for frontend usage

---

## 📸 Demo Screenshots

| Register | Login |
|---------|--------|
| ![](docs/register.png) | ![](docs/login.png) |

| Menu Browsing | Shopping Cart |
|---------------|----------------|
| ![](docs/menu.png) | ![](docs/cart.png) |

*(Note: Replace with real paths or remove this section if you want.)*

---

## 📂 Project Structure
_Current repo currently contains the full structure (backend + frontend + infra):_

```text
backend/
  ├── src/main/java/com/laioffer/onlineorder/...
  ├── src/main/resources/application.yml
  └── build.gradle

frontend/
  ├── src/
  ├── public/
  └── package.json

infra/
  ├── Dockerfile
  ├── ECR_push.sh
  └── app-runner.json
```

## 👥 Collaborators

This repository is maintained by:

@shufengc

@hammerniu

## 📜 License

This project is for educational and demonstration purposes.

