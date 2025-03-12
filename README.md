# 📱 Aplicație de Rețea Socială 🤝

## 📝Descriere
Acest proiect este o simulare a unei aplicații de rețea socială, construită utilizând Java și JavaFX pentru interfața grafică. PostgreSQL este folosit ca sistem de gestionare a bazei de date. Aplicația permite utilizatorilor să își creeze un cont, să își gestioneze profilul, să se conecteze cu prietenii și să comunice cu aceștia.

## 🔧Funcționalități
- **Autentificare utilizator**: Înregistrare, conectare și deconectare în siguranță.
- **Gestionarea profilului**: Actualizare sau ștergere a informațiilor din profil.
- **Sistem de prietenie**:
  - Trimitere și primire cereri de prietenie.
  - Vizualizare și gestionare a listei de prieteni.
- **Sistem de mesagerie**: Conversații cu prietenii.
  **Notă** Aplicația nu are useri predefiniti. Ei trebuie creați prin Sign Up.

## 🛠️Tehnologii utilizate
- **Java**: Limbajul principal de programare.
- **JavaFX**: Pentru crearea interfeței grafice.
- **PostgreSQL**: Sistem de gestionare a bazei de date, instalat local.
- **Gradle**: Utilizat pentru configurarea proiectului și gestionarea dependențelor.

## 📥Instalare
1. Asigurați-vă că aveți instalate **Java JDK** (versiunea minimă 11), **JavaFX**, **PostgreSQL** și **Gradle** pe sistemul dvs.
2. Configurați baza de date PostgreSQL:
   - Creați o nouă bază de date.
   - Editați fișierul `SocialNetworkStart.java`. Modificați variabilele: `url`, `username`, `password`.
   - Executați scriptul SQL furnizat pentru a inițializa schema.

### 📜 Script SQL pentru crearea tabelelor:
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

3. Construiți și rulați aplicația utilizând Gradle.

## 🎮Utilizare
- Lansați aplicația.
- Înregistrați un nou cont sau autentificați-vă dacă aveți deja unul.
- Have fun exploring! 🚀

## 📜 Licență
Acest proiect este licențiat sub Licența MIT. Consultați fișierul `LICENSE` pentru detalii.

## 👤 Autor
[Numele Tău]

## 🤝 Contribuții
Contribuțiile sunt binevenite! Puteți face fork la depozit și trimite cereri de pull.

