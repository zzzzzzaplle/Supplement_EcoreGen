public class Trainer extends Person {
    private boolean contractor;

    public Trainer(String id, boolean contractor) {
        super(id);
        this.contractor = contractor;
    }

    public Trainer() {
        super();
        this.contractor = false;
    }

    public boolean isContractor() {
        return contractor;
    }

    public void setContractor(boolean contractor) {
        this.contractor = contractor;
    }
}
