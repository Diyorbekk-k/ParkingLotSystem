package CarParkingLot.models;

import java.time.LocalDateTime;

public class Payment {
    private int id;
    private int ticketId;
    private double amount;
    private String paymentType; // CASH or CREDIT_CARD
    private LocalDateTime createdAt;
    private String nameOnCard;
    private double cashTendered;

    public Payment() {}

    public Payment(int ticketId, double amount, String paymentType) {
        this.ticketId = ticketId;
        this.amount = amount;
        this.paymentType = paymentType;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getNameOnCard() { return nameOnCard; }
    public void setNameOnCard(String nameOnCard) { this.nameOnCard = nameOnCard; }
    public double getCashTendered() { return cashTendered; }
    public void setCashTendered(double cashTendered) { this.cashTendered = cashTendered; }
}
