package miun.fl.dt142g.projektkok.json;

import java.io.Serializable;

public class Order implements Serializable {
    private Booking booking;
    private Dish dish;
    private Employee employee;
    private String notes;
    private int id;
    private String time;
    private boolean status;
    private boolean served;

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isStatus() {
        return status;
    }

    public boolean isServed() {
        return served;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getServed() {
        return served;
    }

    public void setServed(boolean status) {
        this.served = status;
    }
}