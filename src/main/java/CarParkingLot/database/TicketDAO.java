package CarParkingLot.database;

import CarParkingLot.models.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TicketDAO {

    public ParkingTicket issueTicket(String licenseNumber, VehicleType vehicleType, int spotId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Insert or get vehicle
            int vehicleId = getOrCreateVehicle(conn, licenseNumber, vehicleType);

            // Mark spot as occupied
            String updateSpot = "UPDATE parking_spots SET is_free = FALSE WHERE id = ?";
            PreparedStatement ps2 = conn.prepareStatement(updateSpot);
            ps2.setInt(1, spotId);
            ps2.executeUpdate();

            // Create ticket
            String ticketNum = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String insertTicket = "INSERT INTO parking_tickets (ticket_number, issued_at, status, vehicle_id, spot_id) VALUES (?,?,?,?,?)";
            PreparedStatement ps3 = conn.prepareStatement(insertTicket, Statement.RETURN_GENERATED_KEYS);
            ps3.setString(1, ticketNum);
            ps3.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps3.setString(3, "ACTIVE");
            ps3.setInt(4, vehicleId);
            ps3.setInt(5, spotId);
            ps3.executeUpdate();

            ResultSet keys = ps3.getGeneratedKeys();
            int ticketId = 0;
            if (keys.next()) ticketId = keys.getInt(1);

            conn.commit();
            return getTicketById(ticketId);

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return null;
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private int getOrCreateVehicle(Connection conn, String licenseNumber, VehicleType type) throws SQLException {
        String select = "SELECT id FROM vehicles WHERE license_number = ?";
        PreparedStatement ps = conn.prepareStatement(select);
        ps.setString(1, licenseNumber);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt("id");

        String insert = "INSERT INTO vehicles (license_number, vehicle_type) VALUES (?,?)";
        PreparedStatement ps2 = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
        ps2.setString(1, licenseNumber);
        ps2.setString(2, type.name());
        ps2.executeUpdate();
        ResultSet keys = ps2.getGeneratedKeys();
        if (keys.next()) return keys.getInt(1);
        throw new SQLException("Could not create vehicle");
    }

    public ParkingTicket getTicketByNumber(String ticketNumber) {
        String sql = "SELECT * FROM parking_tickets WHERE ticket_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ticketNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return buildFullTicket(conn, rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public ParkingTicket getTicketById(int id) {
        String sql = "SELECT * FROM parking_tickets WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return buildFullTicket(conn, rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<ParkingTicket> getAllActiveTickets() {
        List<ParkingTicket> list = new ArrayList<>();
        String sql = "SELECT * FROM parking_tickets WHERE status = 'ACTIVE'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(buildFullTicket(conn, rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<ParkingTicket> getAllTickets() {
        List<ParkingTicket> list = new ArrayList<>();
        String sql = "SELECT * FROM parking_tickets ORDER BY issued_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(buildFullTicket(conn, rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean payTicket(int ticketId, double amount, String paymentType, String nameOnCard, double cashTendered) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Get spot id from ticket
            String getSpot = "SELECT spot_id FROM parking_tickets WHERE id = ?";
            PreparedStatement ps1 = conn.prepareStatement(getSpot);
            ps1.setInt(1, ticketId);
            ResultSet rs = ps1.executeQuery();
            int spotId = 0;
            if (rs.next()) spotId = rs.getInt("spot_id");

            // Update ticket
            String updateTicket = "UPDATE parking_tickets SET status='PAID', paid_at=?, paid_amount=? WHERE id=?";
            PreparedStatement ps2 = conn.prepareStatement(updateTicket);
            ps2.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps2.setDouble(2, amount);
            ps2.setInt(3, ticketId);
            ps2.executeUpdate();

            // Free the spot
            if (spotId > 0) {
                String freeSpot = "UPDATE parking_spots SET is_free = TRUE WHERE id = ?";
                PreparedStatement ps3 = conn.prepareStatement(freeSpot);
                ps3.setInt(1, spotId);
                ps3.executeUpdate();
            }

            // Insert payment record
            String insertPayment = "INSERT INTO payments (ticket_id, amount, payment_type, created_at, name_on_card, cash_tendered) VALUES (?,?,?,?,?,?)";
            PreparedStatement ps4 = conn.prepareStatement(insertPayment);
            ps4.setInt(1, ticketId);
            ps4.setDouble(2, amount);
            ps4.setString(3, paymentType);
            ps4.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps4.setString(5, nameOnCard);
            ps4.setDouble(6, cashTendered);
            ps4.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private ParkingTicket buildFullTicket(Connection conn, ResultSet rs) throws SQLException {
        ParkingTicket t = new ParkingTicket();
        t.setId(rs.getInt("id"));
        t.setTicketNumber(rs.getString("ticket_number"));
        t.setIssuedAt(rs.getTimestamp("issued_at").toLocalDateTime());
        Timestamp paidAt = rs.getTimestamp("paid_at");
        if (paidAt != null) t.setPaidAt(paidAt.toLocalDateTime());
        t.setPaidAmount(rs.getDouble("paid_amount"));
        t.setStatus(TicketStatus.valueOf(rs.getString("status")));
        t.setVehicleId(rs.getInt("vehicle_id"));
        t.setSpotId(rs.getInt("spot_id"));

        // Load vehicle
        String vSql = "SELECT * FROM vehicles WHERE id = ?";
        PreparedStatement vPs = conn.prepareStatement(vSql);
        vPs.setInt(1, t.getVehicleId());
        ResultSet vRs = vPs.executeQuery();
        if (vRs.next()) {
            Vehicle v = new Vehicle();
            v.setId(vRs.getInt("id"));
            v.setLicenseNumber(vRs.getString("license_number"));
            v.setVehicleType(VehicleType.valueOf(vRs.getString("vehicle_type")));
            t.setVehicle(v);
        }

        // Load spot
        String sSql = "SELECT ps.*, pf.name as floor_name FROM parking_spots ps JOIN parking_floors pf ON ps.floor_id = pf.id WHERE ps.id = ?";
        PreparedStatement sPs = conn.prepareStatement(sSql);
        sPs.setInt(1, t.getSpotId());
        ResultSet sRs = sPs.executeQuery();
        if (sRs.next()) {
            ParkingSpot spot = new ParkingSpot();
            spot.setId(sRs.getInt("id"));
            spot.setSpotNumber(sRs.getString("spot_number"));
            spot.setFloorId(sRs.getInt("floor_id"));
            spot.setFloorName(sRs.getString("floor_name"));
            spot.setSpotType(ParkingSpotType.valueOf(sRs.getString("spot_type")));
            spot.setFree(sRs.getBoolean("is_free"));
            t.setSpot(spot);
        }

        return t;
    }
}
