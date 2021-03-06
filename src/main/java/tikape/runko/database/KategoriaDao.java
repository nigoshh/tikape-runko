
package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Kategoria;
import tikape.runko.domain.Drinkki;

public class KategoriaDao implements Dao<Kategoria, Integer> {
    
    private Database database;

    public KategoriaDao(Database database) {
        this.database = database;
    }

    @Override
    public Kategoria findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Kategoria WHERE id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String nimi = rs.getString("nimi");

        Kategoria k = new Kategoria(id, nimi);

        rs.close();
        stmt.close();
        connection.close();

        return k;
    }

    @Override
    public List<Kategoria> findAll() throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Kategoria");

        ResultSet rs = stmt.executeQuery();
        List<Kategoria> kategoriat = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");

            kategoriat.add(new Kategoria(id, nimi));
        }

        rs.close();
        stmt.close();
        connection.close();

        return kategoriat;
    }
    
    public Kategoria findOnebyName(String nimi) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Kategoria WHERE nimi = ?");
        stmt.setObject(1, nimi);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        

        Kategoria d = new Kategoria(id, nimi);

        rs.close();
        stmt.close();
        connection.close();

        return d;
    }
    
    public List<Kategoria> findAllInDrink(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT Kategoria.id,"
                + "Kategoria.nimi FROM Kategoria, "
                + "DrinkkiKategoria WHERE DrinkkiKategoria.drinkki_id = ?"
                + "AND DrinkkiKategoria.kategoria_id = Kategoria.Id ORDER BY Kategoria.nimi");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        List<Kategoria> kategoriat = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");

            kategoriat.add(new Kategoria(id, nimi));
        }

        rs.close();
        stmt.close();
        connection.close();

        return kategoriat;
    }
    
    public List<Kategoria> findAllNotInDrink(Integer key) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Kategoria "
                + "WHERE id NOT IN (SELECT DISTINCT kategoria_id from DrinkkiKategoria "
                + "WHERE DrinkkiKategoria.drinkki_id = ?) ORDER BY nimi");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        List<Kategoria> kategoriat = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");

            kategoriat.add(new Kategoria(id, nimi));
        }

        rs.close();
        stmt.close();
        conn.close();

        return kategoriat;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Kategoria WHERE id = ?");

        stmt.setInt(1, key);
        stmt.executeUpdate();

        stmt.close();
        conn.close();
    }
    
    @Override
    public void save(Kategoria kategoria) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Kategoria"
                + " (nimi)"
                + " VALUES (?)");
        stmt.setString(1, kategoria.getNimi());

        stmt.executeUpdate();
        stmt.close();


        conn.close();

    }
    
        public void lisaaDrinkki(Kategoria kategoria, Drinkki drinkki) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO DrinkkiKategoria"
                + " (drinkki_id, kategoria_id)"
                + " VALUES (?, ?)");
        stmt.setInt(1, drinkki.getId());
        stmt.setInt(2, kategoria.getId());

        stmt.executeUpdate();
        stmt.close();


        conn.close();
    }
        
        public void poistaDrinkki(Integer kategoria_id, int drinkki_id) throws SQLException {
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM DrinkkiKategoria "
                + "WHERE drinkki_id = ? AND kategoria_id = ?");
            stmt.setInt(1, drinkki_id);
            stmt.setInt(2, kategoria_id);

            stmt.executeUpdate();
            stmt.close();

            conn.close();
        }
}
