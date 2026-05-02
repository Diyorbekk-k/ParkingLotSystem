package CarParkingLot.models;

public class Vehicle {
    private int id;
    private String licenseNumber;
    private VehicleType vehicleType;

    public Vehicle() {}

    public Vehicle(String licenseNumber, VehicleType vehicleType) {
        this.licenseNumber = licenseNumber;
        this.vehicleType = vehicleType;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public VehicleType getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }

    @Override
    public String toString() {
        return licenseNumber + " (" + vehicleType + ")";
    }
}
