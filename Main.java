package tikape.runko;

import java.util.HashMap;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.database.DrinkkiDao;
import tikape.runko.database.KategoriaDao;
import tikape.runko.database.RaakaAineDao;
import tikape.runko.domain.Drinkki;
import tikape.runko.domain.Kategoria;
import tikape.runko.domain.RaakaAine;

public class Main {

    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:drinkit.db");
        database.init();
        
//        // herokun portille
//        if (System.getenv("PORT") != null) {
//            Spark.port(Integer.valueOf(System.getenv("PORT")));
//        }

        DrinkkiDao drinkkiDao = new DrinkkiDao(database);
        KategoriaDao kategoriaDao = new KategoriaDao(database);
        RaakaAineDao raakaAineDao = new RaakaAineDao(database);

        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        get("/drinkit", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("drinkit", drinkkiDao.findAll());

            return new ModelAndView(map, "drinkit");
        }, new ThymeleafTemplateEngine());
        
        get("/drinkit/kategoriat/:kategoria", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("drinkit", drinkkiDao.findAllbyCategory(req.params("kategoria")));

            return new ModelAndView(map, "drinkit");
        }, new ThymeleafTemplateEngine());

        get("/drinkit/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("drinkki", drinkkiDao.findOne(Integer.parseInt(req.params("id"))));

            return new ModelAndView(map, "drinkki");
        }, new ThymeleafTemplateEngine()); 
        
        get("/lisaa", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("lisaa", drinkkiDao.findAll());

            return new ModelAndView(map, "lisaa");
        }, new ThymeleafTemplateEngine());
        
        post("/lisaa", (req, res) -> {
            String nimi = req.queryParams("nimi");
            drinkkiDao.save(new Drinkki(0, nimi, ""));
            String kategoria = req.queryParams("kategoria");
            kategoriaDao.lisaaDrinkki(kategoriaDao.findOnebyName(kategoria), drinkkiDao.findOnebyName(nimi) );
            res.redirect("/");
            return "";
        });
        
        get("/raaka-aineet", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("raakaAineet", raakaAineDao.findAll());
            map.put("tilasto", raakaAineDao.findStatistics());

            return new ModelAndView(map, "raaka-aineet");
        }, new ThymeleafTemplateEngine());
        
        post("/raaka-aineet", (req, res) -> {
            String nimi = req.queryParams("nimi");
            RaakaAine raakaAine = new RaakaAine(0, nimi);
            raakaAineDao.save(raakaAine);
            res.redirect("/raaka-aineet");
            return "";
        });
    }
}