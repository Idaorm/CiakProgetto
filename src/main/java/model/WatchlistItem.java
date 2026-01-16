package model;

public class WatchlistItem {
    private int idItem;
    private boolean status;
    private int idUtente;
    private int idFilm;

    public WatchlistItem() {}

    public int getIdItem() { return idItem; }
    public void setIdItem(int idItem) { this.idItem = idItem; }
    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
    public int getIdUtente() { return idUtente; }
    public void setIdUtente(int idUtente) { this.idUtente = idUtente; }
    public int getIdFilm() { return idFilm; }
    public void setIdFilm(int idFilm) { this.idFilm = idFilm; }
}
