package CarParkingLot.database;

import CarParkingLot.models.Account;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public Account login(String username, String password) {
        String sql = "SELECT * FROM accounts WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapAccount(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Account> getAllAccounts() {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapAccount(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addAccount(Account a) {
        String sql = "INSERT INTO accounts (username, password, role, name, email, phone) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getUsername());
            ps.setString(2, a.getPassword());
            ps.setString(3, a.getRole());
            ps.setString(4, a.getName());
            ps.setString(5, a.getEmail());
            ps.setString(6, a.getPhone());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteAccount(int id) {
        String sql = "DELETE FROM accounts WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private Account mapAccount(ResultSet rs) throws SQLException {
        Account a = new Account();
        a.setId(rs.getInt("id"));
        a.setUsername(rs.getString("username"));
        a.setPassword(rs.getString("password"));
        a.setRole(rs.getString("role"));
        a.setName(rs.getString("name"));
        a.setEmail(rs.getString("email"));
        a.setPhone(rs.getString("phone"));
        return a;
    }
}
