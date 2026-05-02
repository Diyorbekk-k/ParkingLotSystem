package CarParkingLot.models;

import java.util.List;

public class ParkingFloor {
    private int id;
    private String name;
    private List<ParkingSpot> spots;

    public ParkingFloor() {}

    public ParkingFloor(String name) {
        this.name = name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<ParkingSpot> getSpots() { return spots; }
    public void setSpots(List<ParkingSpot> spots) { this.spots = spots; }

    public long getFreeSpotCount() {
        if (spots == null) return 0;
        return spots.stream().filter(ParkingSpot::isFree).count();
    }

    @Override
    public String toString() { return name; }
}
