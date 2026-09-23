public class OverdueNotice {
    private String noticeId;
    private Customer customer;

    public OverdueNotice() {
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String sendNoticeTo(Customer customer) {
        this.customer = customer;
        return "Overdue notice " + (this.noticeId == null ? "" : this.noticeId)
                + " sent to " + (customer == null ? "" : customer.getName())
                + " " + (customer == null ? "" : customer.getSurname());
    }
}
