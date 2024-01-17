public class Rezyser {
    private int id;
    private String imie;
    private String nazwisko;

    public Rezyser(String imie, String nazwisko, int ID){
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.id = ID;
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
