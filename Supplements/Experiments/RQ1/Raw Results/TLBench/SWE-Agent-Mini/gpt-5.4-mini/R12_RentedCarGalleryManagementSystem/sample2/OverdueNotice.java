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
        if (customer == null) {
            return "Notice cannot be sent";
        }
        String name = customer.getName() == null ? "" : customer.getName();
        String surname = customer.getSurname() == null ? "" : customer.getSurname();
        return ("Notice sent to " + name + " " + surname).trim();
    }
}
