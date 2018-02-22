package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.RaakaAine;
import tikape.runko.domain.RaakaAineDrinkissa;

public class RaakaAineDao implements Dao<RaakaAine, Integer> {

    private Database database;

    public RaakaAineDao(Database database) {
        this.database = database;
    }
    
    public RaakaAine findOnebyName(String nimi) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM RaakaAine WHERE nimi = ?");
        stmt.setObject(1, nimi);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");

        RaakaAine d = new RaakaAine(id, nimi);

        rs.close();
        stmt.close();
        connection.close();

        return d;
    }

    @Override
    public RaakaAine findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM RaakaAine WHERE id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String nimi = rs.getString("nimi");

        RaakaAine r = new RaakaAine(id, nimi);

        rs.close();
        stmt.close();
        connection.close();

        return r;
    }
    
    
    
    public List<RaakaAineDrinkissa> findAllInDrink(Integer key) throws SQLException {
       Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT RaakaAine.id,"
                + "RaakaAine.nimi, DrinkkiRaakaAine.maara FROM RaakaAine, "
                + "DrinkkiRaakaAine WHERE DrinkkiRaakaAine.drinkki_id = ?"
                + "AND DrinkkiRaakaAine.raakaaine_id = RaakaAine.id ORDER BY DrinkkiRaakaAine.jarjestys");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        List<RaakaAineDrinkissa> raakaAineet = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");
            String maara = rs.getString("maara");

            raakaAineet.add(new RaakaAineDrinkissa(id, nimi, maara));
        }

        rs.close();
        stmt.close();
        connection.close();

        return raakaAineet; 
    }

    @Override
    public List<RaakaAine> findAll() throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM RaakaAine ORDER BY nimi");

        ResultSet rs = stmt.executeQuery();
        List<RaakaAine> drinkit = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");

            drinkit.add(new RaakaAine(id, nimi));
        }

        rs.close();
        stmt.close();
        connection.close();

        return drinkit;
    }

    public List<String> findStatistics() throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM RaakaAine");

        ResultSet rs = stmt.executeQuery();
        List<RaakaAine> raakaAineet = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");

            raakaAineet.add(new RaakaAine(id, nimi));
        }
        
        List<String> tilasto = new ArrayList<>();

        for (int i = 0; i < raakaAineet.size(); i++) {
            PreparedStatement stmt2 = connection.prepareStatement("SELECT COUNT(*) AS total FROM DrinkkiRaakaAine WHERE raakaAine_id = ?");
            stmt2.setInt(1, raakaAineet.get(i).getId());
            ResultSet rs2 = stmt2.executeQuery();
            while (rs2.next()) {
                Integer maara = rs2.getInt("total");
                tilasto.add(raakaAineet.get(i).getNimi() + ", käytössä yhteensä " + maara + " drinkissä");
            }
            rs2.close();
            stmt2.close();
        }

        rs.close();
        stmt.close();
        connection.close();

        return tilasto;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        Connection conn = database.getConnection();
        
        conn.setAutoCommit(false);
        
        PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM RaakaAine WHERE id = ?");
        PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM DrinkkiRaakaAine WHERE raakaaine_id = ?");

        stmt1.setInt(1, key);
        stmt2.setInt(1, key);
        
        int rowAffected = stmt1.executeUpdate();
        
        if (rowAffected != 1) {
            conn.rollback();
        }
        
        stmt2.executeUpdate();

        conn.commit();

        stmt1.close();
        stmt2.close();
        conn.close();
    }

    @Override
    public void save(RaakaAine raakaAine) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO RaakaAine"
                + " (nimi)"
                + " VALUES (?)");
        stmt.setString(1, raakaAine.getNimi());

        stmt.executeUpdate();
        stmt.close();

        conn.close();

    }
}
