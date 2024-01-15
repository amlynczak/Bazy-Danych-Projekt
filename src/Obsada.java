import java.util.ArrayList;

public class Obsada {
    private int id;
    public String tytul;
    public ArrayList<Aktor> aktorzy;
    public ArrayList<String> role;

    public Obsada(int i, String t){
        id = i;
        tytul = t;
        this.aktorzy = new ArrayList<>();
        this.role = new ArrayList<>();
    }

    public void dodajAktora(Aktor a, String rola){
        aktorzy.add(a);
        role.add(rola);
    }

    public String getTytul() {
        return tytul;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Aktor> getAktorzy() {
        return aktorzy;
    }

    public ArrayList<String> getRole() {
        return role;
    }
}
