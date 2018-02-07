package tikape.runko.domain;

public class Drinkki {

    private Integer id;
    private String nimi;
    private List<RaakaAine> raakaAineet;
    private List<Kategoria> kategoriat;

    public Drinkki(Integer id, String nimi) {
        this.id = id;
        this.nimi = nimi;
        this.raakaAineet = new ArrayList<>();
        this.kategoriat = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
    
    public List<RaakaAine> getRaakaAineet()  {
        return this.raakaAineet;
    }
    
    public List<RaakaAine> getKategoriat()  {
        return this.kategoriat;
    }
    
    public void lisaaRaakaAine(RaakaAine raakaAine) {
        this.raakaAineet.add(raakaAine);
    }
    
    public void lisaaKategoria(Kategoria kategoria) {
        this.kategoriat.add(kategoria);
    }

}