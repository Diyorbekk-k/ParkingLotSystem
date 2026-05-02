package CarParkingLot.models;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ParkingTicket {
    private int id;
    private String ticketNumber;
    private LocalDateTime issuedAt;
    private LocalDateTime paidAt;
    private double paidAmount;
    private TicketStatus status;
    private Vehicle vehicle;
    private ParkingSpot spot;
    private int vehicleId;
    private int spotId;

    public ParkingTicket() {}

    public ParkingTicket(String ticketNumber, Vehicle vehicle, ParkingSpot spot) {
        this.ticketNumber = ticketNumber;
        this.vehicle = vehicle;
        this.spot = spot;
        this.issuedAt = LocalDateTime.now();
        this.status = TicketStatus.ACTIVE;
    }

    public double calculateFee() {
        LocalDateTime end = (paidAt != null) ? paidAt : LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(issuedAt, end);
        long hours = (long) Math.ceil(minutes / 60.0);
        if (hours <= 0) hours = 1;

        double fee = 0;
        for (long i = 1; i <= hours; i++) {
            if (i == 1) fee += 4.0;
            else if (i <= 3) fee += 3.5;
            else fee += 2.5;
        }
        return fee;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }
    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public ParkingSpot getSpot() { return spot; }
    public void setSpot(ParkingSpot spot) { this.spot = spot; }
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    public int getSpotId() { return spotId; }
    public void setSpotId(int spotId) { this.spotId = spotId; }

    @Override
    public String toString() { return ticketNumber; }
}
