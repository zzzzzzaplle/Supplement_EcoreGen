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
        if (customer != null) {
            return "Notice " + (noticeId == null ? "" : noticeId)
                    + " sent to " + customer.getName() + " " + customer.getSurname();
        }
        return "Notice " + (noticeId == null ? "" : noticeId) + " sent to unknown customer";
    }
}
