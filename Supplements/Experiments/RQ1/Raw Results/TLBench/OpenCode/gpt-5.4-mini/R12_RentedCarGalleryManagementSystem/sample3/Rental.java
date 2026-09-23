public class Rental {
    private java.util.Date rentalDate;
    private java.util.Date dueDate;
    private java.util.Date backDate;
    private double totalPrice;
    private String leasingTerms;
    private Car car;
    private Customer customer;

    public Rental() {
    }

    public java.util.Date getRentalDate() {
        return this.rentalDate;
    }

    public void setRentalDate(java.util.Date rentalDate) {
        this.rentalDate = rentalDate;
    }

    public java.util.Date getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(java.util.Date dueDate) {
        this.dueDate = dueDate;
    }

    public java.util.Date getBackDate() {
        return this.backDate;
    }

    public void setBackDate(java.util.Date backDate) {
        this.backDate = backDate;
    }

    public double getTotalPrice() {
        return this.totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getLeasingTerms() {
        return this.leasingTerms;
    }

    public void setLeasingTerms(String leasingTerms) {
        this.leasingTerms = leasingTerms;
    }

    public Car getCar() {
        return this.car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
