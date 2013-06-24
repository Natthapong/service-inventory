package th.co.truemoney.serviceinventory.persona.bills;

public interface BarcodeExtractor {
    public BarcodeResult extract(String barcode);
}
