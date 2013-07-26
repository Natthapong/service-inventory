package th.co.truemoney.serviceinventory.persona.bills;

public enum KnownBills {
    AEON(new AEON_Bill()), CAL(new CAL_Bill()),
    GLC(new GLC_Bill()), SG(new SG_Bill()),
    TCG(new TCG_Bill()), TI(new TI_Bill()),
    TLI(new TLI_Bill()), UMBRELLA_CORP(new UmbrellaCorp_Bill());

    private BillTemplate billTemplate;

    KnownBills(BillTemplate template) {
        this.billTemplate = template;
    }

    public BillTemplate getBillTemplate() {
        return billTemplate;
    }

    public static KnownBills getBillTemplateFromBarcode(String taxID, String ref1, String ref2, String amount) {
        for (KnownBills b : values()) {
            if (b.getBillTemplate().isMyBillBarcode(taxID, ref1, ref2, amount)) {
                return b;
            }
        }

        throw new RuntimeException("unknown barcode : " + taxID);
    }

    public static KnownBills getBillTemplateFromBillCode(String billCode) {
        for (KnownBills b : values()) {
            if (b.getBillTemplate().isMyBillBillCode(billCode)) {
                return b;
            }
        }

        throw new RuntimeException("unknown billcode : " + billCode);
    }
}
