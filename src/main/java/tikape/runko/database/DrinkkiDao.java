
package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Drinkki;
import tikape.runko.domain.Kategoria;
import tikape.runko.domain.RaakaAine;

public class DrinkkiDao implements Dao<Drinkki, Integer> {
    
    private Database database;

    public DrinkkiDao(Database database) {
        this.database = database;
    }

    @Override
    public Drinkki findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Drinkki WHERE id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String nimi = rs.getString("nimi");
        String ohje = rs.getString("ohje");

        Drinkki d = new Drinkki(id, nimi, ohje);

        rs.close();
        stmt.close();
        connection.close();

        return d;
    }
    
    public Drinkki findOnebyName(String nimi) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Drinkki WHERE nimi = ?");
        stmt.setObject(1, nimi);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String ohje = rs.getString("ohje");

        Drinkki d = new Drinkki(id, nimi, ohje);

        rs.close();
        stmt.close();
        connection.close();

        return d;
    }

    @Override
    public List<Drinkki> findAll() throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Drinkki ORDER BY nimi");

        ResultSet rs = stmt.executeQuery();
        List<Drinkki> drinkit = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");
            String ohje = rs.getString("ohje");

            drinkit.add(new Drinkki(id, nimi, ohje));
        }

        rs.close();
        stmt.close();
        connection.close();

        return drinkit;
    }
    
    public List<Drinkki> findAllbyCategory(String kategoria) throws SQLException {
        
        if (kategoria.equals("kaikki")) {
            return this.findAll();
        }

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Drinkki, DrinkkiKategoria, Kategoria WHERE Drinkki.id = DrinkkiKategoria.drinkki_id AND Kategoria.id = DrinkkiKategoria.kategoria_id AND Kategoria.nimi = ? ORDER BY Drinkki.nimi");
        stmt.setString(1, kategoria);

        ResultSet rs = stmt.executeQuery();
        List<Drinkki> drinkit = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");
            String ohje = rs.getString("ohje");

            drinkit.add(new Drinkki(id, nimi, ohje));
        }

        rs.close();
        stmt.close();
        connection.close();

        return drinkit;
    }
    

    @Override
    public void delete(Integer key) throws SQLException {
        Connection conn = database.getConnection();
        
        conn.setAutoCommit(false);
        
        PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM Drinkki WHERE id = ?");
        
        PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM DrinkkiRaakaAine WHERE drinkki_id  = ?");
        
        PreparedStatement stmt3 = conn.prepareStatement("DELETE FROM DrinkkiKategoria WHERE drinkki_id  = ?");

        stmt1.setInt(1, key);
        stmt2.setInt(1, key);
        stmt3.setInt(1, key);
        
        int rowAffected = stmt1.executeUpdate();
        
        if (rowAffected != 1) {
            conn.rollback();
        }
        
        stmt2.executeUpdate();
        stmt3.executeUpdate();
        
        conn.commit();

        stmt1.close();
        stmt2.close();
        stmt3.close();
        conn.close();
    }
    
    @Override
    public void save(Drinkki drinkki) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Drinkki"
                + " (nimi, ohje)"
                + " VALUES (?, ?)");
        stmt.setString(1, drinkki.getNimi());
        stmt.setString(2, drinkki.getOhje());
        
        stmt.executeUpdate();
        stmt.close();


        conn.close();

    }
    
    public void lisaaRaakaAine(Drinkki drinkki, RaakaAine raakaAine, int jarjestys, String maara) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO DrinkkiRaakaAine"
                + " (raakaAine_id, drinkki_id, jarjestys, maara)"
                + " VALUES (?, ?, ?, ?)");
        stmt.setInt(1, raakaAine.getId());
        stmt.setInt(2, drinkki.getId());
        stmt.setInt(3, jarjestys);
        stmt.setString(4, maara);

        stmt.executeUpdate();
        stmt.close();


        conn.close();
    }
    
    public void poistaRaakaAine(Integer drinkki_id, Integer raakaaine_id) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM DrinkkiRaakaAine "
                + "WHERE drinkki_id = ? AND raakaaine_id = ?");

        stmt.setInt(1, drinkki_id);
        stmt.setInt(2, raakaaine_id);
        
        stmt.executeUpdate();

        stmt.close();
        conn.close();
    } 
    
    public void lisaaOhje(String ohje, Integer drinkki_id) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("UPDATE Drinkki SET ohje = ? "
                + "WHERE id = ?");

        stmt.setString(1, ohje);
        stmt.setInt(2, drinkki_id);

        stmt.executeUpdate();

        stmt.close();
        conn.close();
    }
    
    
}
