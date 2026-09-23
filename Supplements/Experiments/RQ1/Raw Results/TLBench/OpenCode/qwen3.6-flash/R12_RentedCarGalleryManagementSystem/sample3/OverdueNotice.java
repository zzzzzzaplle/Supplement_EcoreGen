import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
        if (this.customer == null || customer == null) {
            return "No customer to send notice to";
        }
        return "Notice " + noticeId + " sent to " + customer.getName() + " " + customer.getSurname();
    }
}
