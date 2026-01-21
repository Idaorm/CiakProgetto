package model;

import java.sql.Date;

public class Recensione {
    private int idRecensione;
    private int rating;
    private String text;
    private Date date;
    private int idUtente;
    private int idFilm;

    public Recensione() {}

    public Recensione(int rating, String text, Date date, int idUtente, int idFilm) {
        this.rating = rating;
        this.text = text;
        this.date = date;
        this.idUtente = idUtente;
        this.idFilm = idFilm;
    }


    public int getIdRecensione() { return idRecensione; }
    public void setIdRecensione(int idRecensione) { this.idRecensione = idRecensione; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public int getIdUtente() { return idUtente; }
    public void setIdUtente(int idUtente) { this.idUtente = idUtente; }

    public int getIdFilm() { return idFilm; }
    public void setIdFilm(int idFilm) { this.idFilm = idFilm; }
}