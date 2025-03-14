# 📱 Social Network Application 🤝

## 📝 Description
This project is a simulation of a social network application, built using Java and JavaFX for the graphical interface. PostgreSQL is used as the database management system. The application allows users to create an account, manage their profile, connect with friends, and communicate with them.

## 🔧 Features
- **User Authentication**: Secure registration, login, and logout.
- **Profile Management**: Update or delete profile information.
- **Friendship System**:
  - Send and receive friend requests.
  - View and manage the friends list.
- **Messaging System**: Conversations with friends.
  **Note**: The application does not have predefined users. They must be created through Sign Up.

## 🛠️ Technologies Used
- **Java**: The main programming language.
- **JavaFX**: Used for creating the graphical interface.
- **PostgreSQL**: Database management system, installed locally.
- **Gradle**: Used for project configuration and dependency management.

## 👅 Installation
1. Ensure you have **Java JDK** (minimum version 11), **JavaFX**, **PostgreSQL**, and **Gradle** installed on your system.
2. Configure the PostgreSQL database:
   - Create a new database.
   - Edit the `SocialNetworkStart.java` file. Modify the variables: `url`, `username`, `password`.
   - Execute the provided SQL script to initialize the schema.

### 📝 SQL Script for Creating Tables:
```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    profilepicture BYTEA NOT NULL
);

CREATE TABLE friendships (
    id SERIAL PRIMARY KEY,
    user1 INTEGER NOT NULL,
    user2 INTEGER NOT NULL,
    friendssince TIMESTAMP,
    pending BOOLEAN
);

CREATE TABLE messages (
    id SERIAL PRIMARY KEY,
    senderid INTEGER NOT NULL,
    receiverid INTEGER NOT NULL,
    date TIMESTAMP,
    message TEXT NOT NULL,
    reply_id INTEGER
);
```

3. Build and run the application using Gradle.

## 🎮 Usage
- Launch the application.
- Register a new account or log in if you already have one.
