package model;

public class Film {
    private int idFilm;
    private int idTmdb;
    private String titolo;

    public Film() { }


    public Film(int idTmdb, String titolo) {
        this.idTmdb = idTmdb;
        this.titolo = titolo;
    }

    public Film(int idFilm, int idTmdb, String titolo) {
        this.idFilm = idFilm;
        this.idTmdb = idTmdb;
        this.titolo = titolo;
    }


    public int getIdFilm() { return idFilm; }
    public void setIdFilm(int idFilm) { this.idFilm = idFilm; }

    public int getIdTmdb() { return idTmdb; }
    public void setIdTmdb(int idTmdb) { this.idTmdb = idTmdb; }

    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }
}