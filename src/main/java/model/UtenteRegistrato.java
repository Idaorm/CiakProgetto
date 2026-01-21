package model;

public class UtenteRegistrato {

    private int idUtente;
    private String username;
    private String email;
    private String password;
    private String photo;
    private String bio;
    private boolean watchlistVisibility;

    // Costruttori
    public UtenteRegistrato() {}

    public UtenteRegistrato(int idUtente, String username, String email, String password,
                  String photo, String bio, boolean watchlistVisibility) {
        this.idUtente = idUtente;
        this.username = username;
        this.email = email;
        this.password = password;
        this.photo = photo;
        this.bio = bio;
        this.watchlistVisibility = watchlistVisibility;
    }

    // Getter & Setter
    public int getIdUtente() { return idUtente; }
    public void setIdUtente(int idUtente) { this.idUtente = idUtente; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public boolean isWatchlistVisibility() { return watchlistVisibility; }
    public void setWatchlistVisibility(boolean watchlistVisibility) {
        this.watchlistVisibility = watchlistVisibility;
    }

}
