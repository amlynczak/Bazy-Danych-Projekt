public class SztukaTeatralna {
    private int id;
    private String tytul;
    private Rezyser rezyser;
    private String informator;
    private String data_realizacji;
    private String miejsce_realizacji;
    private Obsada obsada;

    public SztukaTeatralna(int id, String t, String info, Rezyser rez, String d, String miejsce){
        id = id;
        tytul = t;
        rezyser = rez;
        informator = info;
        data_realizacji = d;
        miejsce_realizacji = miejsce;
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

}
