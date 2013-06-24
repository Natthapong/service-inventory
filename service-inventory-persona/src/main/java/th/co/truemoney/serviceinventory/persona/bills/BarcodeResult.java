package th.co.truemoney.serviceinventory.persona.bills;

public class BarcodeResult {

    private String taxID;
    private String ref1;
    private String ref2;
    private String amount;

    BarcodeResult(String taxID, String ref1, String ref2, String amount) {
        this.taxID = taxID;
        this.ref1 = ref1;
        this.ref2 = ref2;
        this.amount = amount;
    }

    public String getTaxID() {
        return taxID;
    }

    public String getRef1() {
        return ref1;
    }

    public String getRef2() {
        return ref2;
    }

    public String getAmount() {
        return amount;
    }
}
