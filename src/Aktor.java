public class Aktor {
    private int id;
    private String imie;
    private String nazwisko;

    public Aktor(String name, String lname, int ID){
        imie = name;
        nazwisko = lname;
        id = ID;
    }

    public String getImie(){
        return imie;
    }

    public String getNazwisko(){
        return nazwisko;
    }

    public int getId(){
        return id;
    }

    public String toString(){
        return imie+" "+nazwisko;
    }
}
