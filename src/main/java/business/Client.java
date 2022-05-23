package business;

import java.io.Serializable;

public class Client implements Serializable{
    private int id;
    private String nume;
    private String password;

    public Client(int id, String nume) {
        this.id = id;
        this.nume = nume;
    }

    public Client(int id, String nume, String password) {
        this.id = id;
        this.nume = nume;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPassword() {
        return password;
    }
}
