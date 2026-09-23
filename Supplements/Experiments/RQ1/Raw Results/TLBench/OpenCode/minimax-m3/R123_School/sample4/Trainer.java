public class Trainer extends Person {
    private boolean contractor;

    public Trainer() {
        super();
        this.contractor = false;
    }

    public Trainer(String id, boolean contractor) {
        super(id);
        this.contractor = contractor;
    }

    public boolean isContractor() {
        return contractor;
    }

    public void setContractor(boolean contractor) {
        this.contractor = contractor;
    }
}
