# DROP DATABASE CiakProgetto;

CREATE DATABASE IF NOT EXISTS CiakProgetto;
USE CiakProgetto;

CREATE TABLE UtenteRegistrato (
    Id_utente INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20) NOT NULL,
    email VARCHAR(320) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    photo VARCHAR(255),
    bio VARCHAR(200),
    watchlistVisibility BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE Film (
    Id_film INT AUTO_INCREMENT PRIMARY KEY,
    Id_tmdb INT NOT NULL UNIQUE,
    titolo VARCHAR(100) NOT NULL
);

CREATE TABLE Recensione (
    Id_recensione INT AUTO_INCREMENT PRIMARY KEY,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    text VARCHAR(200) NOT NULL,
    date DATE NOT NULL,
    Id_utente INT NOT NULL,
    Id_film INT NOT NULL,
    FOREIGN KEY (Id_utente) REFERENCES UtenteRegistrato(Id_utente) ON DELETE CASCADE,
    FOREIGN KEY (Id_film) REFERENCES Film(Id_film) ON DELETE CASCADE
);

CREATE TABLE WatchlistItem (
    Id_item INT AUTO_INCREMENT PRIMARY KEY,
    status BOOLEAN NOT NULL DEFAULT FALSE,
    date_added DATETIME DEFAULT CURRENT_TIMESTAMP,
    Id_utente INT NOT NULL,
    Id_film INT NOT NULL,
    FOREIGN KEY (Id_utente) REFERENCES UtenteRegistrato(Id_utente) ON DELETE CASCADE,
    FOREIGN KEY (Id_film) REFERENCES Film(Id_film) ON DELETE CASCADE,
    UNIQUE(Id_utente, Id_film)
);
