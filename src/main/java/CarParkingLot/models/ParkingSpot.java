package CarParkingLot.models;

public class ParkingSpot {
    private int id;
    private String spotNumber;
    private int floorId;
    private String floorName;
    private ParkingSpotType spotType;
    private boolean isFree;

    public ParkingSpot() {}

    public ParkingSpot(String spotNumber, int floorId, ParkingSpotType spotType) {
        this.spotNumber = spotNumber;
        this.floorId = floorId;
        this.spotType = spotType;
        this.isFree = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSpotNumber() { return spotNumber; }
    public void setSpotNumber(String s) { this.spotNumber = s; }
    public int getFloorId() { return floorId; }
    public void setFloorId(int floorId) { this.floorId = floorId; }
    public String getFloorName() { return floorName; }
    public void setFloorName(String floorName) { this.floorName = floorName; }
    public ParkingSpotType getSpotType() { return spotType; }
    public void setSpotType(ParkingSpotType spotType) { this.spotType = spotType; }
    public boolean isFree() { return isFree; }
    public void setFree(boolean free) { isFree = free; }

    @Override
    public String toString() {
        return spotNumber + " [" + spotType + "] - " + (isFree ? "Free" : "Occupied");
    }
}
