public class SztukaTeatralna {
    private int id;
    private String tytul;
    private Rezyser rezyser;
    private String informator;
    private String data_realizacji;
    private String miejsce_realizacji;
    public int ilosc_biletow;
    public int ulgowy_cena;
    public int normalny_cena;

    public SztukaTeatralna(int id, String t, String info, Rezyser rez, String d, String miejsce, int ilosc, int ulgowy, int normalny){
        this.id = id;
        tytul = t;
        rezyser = rez;
        informator = info;
        data_realizacji = d;
        miejsce_realizacji = miejsce;
        ilosc_biletow = ilosc;
        ulgowy_cena = ulgowy;
        normalny_cena = normalny;
    };

    public String getData(){
        return  data_realizacji;
    }

    public String getInformator(){
        return informator;
    }
    public int getId() {
        return id;
    }

    public Rezyser getRezyser() {
        return rezyser;
    }

    public String getTytul() {
        return tytul;
    }

    public String getMiejsce(){
        return miejsce_realizacji;
    }

    public int getIlosc_biletow() {
        return ilosc_biletow;
    }

    public int getNormalny_cena() {
        return normalny_cena;
    }

    public int getUlgowy_cena() {
        return ulgowy_cena;
    }
}
