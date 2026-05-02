package CarParkingLot.database;

import CarParkingLot.models.ParkingSpot;
import CarParkingLot.models.ParkingSpotType;
import CarParkingLot.models.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FloorSpotDAO {

    // ─── FLOORS ───────────────────────────────────────────────

    public List<ParkingFloor> getAllFloors() {
        List<ParkingFloor> list = new ArrayList<>();
        String sql = "SELECT * FROM parking_floors";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ParkingFloor f = new ParkingFloor();
                f.setId(rs.getInt("id"));
                f.setName(rs.getString("name"));
                list.add(f);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addFloor(String name) {
        String sql = "INSERT INTO parking_floors (name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteFloor(int id) {
        String sql = "DELETE FROM parking_floors WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ─── SPOTS ────────────────────────────────────────────────

    public List<ParkingSpot> getSpotsByFloor(int floorId) {
        List<ParkingSpot> list = new ArrayList<>();
        String sql = """
            SELECT ps.*, pf.name as floor_name FROM parking_spots ps
            JOIN parking_floors pf ON ps.floor_id = pf.id
            WHERE ps.floor_id = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, floorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapSpot(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<ParkingSpot> getAllSpots() {
        List<ParkingSpot> list = new ArrayList<>();
        String sql = """
            SELECT ps.*, pf.name as floor_name FROM parking_spots ps
            JOIN parking_floors pf ON ps.floor_id = pf.id
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapSpot(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<ParkingSpot> getFreeSpots() {
        List<ParkingSpot> list = new ArrayList<>();
        String sql = """
            SELECT ps.*, pf.name as floor_name FROM parking_spots ps
            JOIN parking_floors pf ON ps.floor_id = pf.id
            WHERE ps.is_free = TRUE
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapSpot(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addSpot(ParkingSpot spot) {
        String sql = "INSERT INTO parking_spots (spot_number, floor_id, spot_type, is_free) VALUES (?,?,?,TRUE)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, spot.getSpotNumber());
            ps.setInt(2, spot.getFloorId());
            ps.setString(3, spot.getSpotType().name());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteSpot(int id) {
        String sql = "DELETE FROM parking_spots WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateSpotAvailability(int spotId, boolean isFree) {
        String sql = "UPDATE parking_spots SET is_free = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isFree);
            ps.setInt(2, spotId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public int getTotalSpots() {
        String sql = "SELECT COUNT(*) FROM parking_spots";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getFreeSpotCount() {
        String sql = "SELECT COUNT(*) FROM parking_spots WHERE is_free = TRUE";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private ParkingSpot mapSpot(ResultSet rs) throws SQLException {
        ParkingSpot s = new ParkingSpot();
        s.setId(rs.getInt("id"));
        s.setSpotNumber(rs.getString("spot_number"));
        s.setFloorId(rs.getInt("floor_id"));
        s.setFloorName(rs.getString("floor_name"));
        s.setSpotType(ParkingSpotType.valueOf(rs.getString("spot_type")));
        s.setFree(rs.getBoolean("is_free"));
        return s;
    }
}
