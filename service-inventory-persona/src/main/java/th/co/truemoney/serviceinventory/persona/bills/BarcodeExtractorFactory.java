package th.co.truemoney.serviceinventory.persona.bills;

public class BarcodeExtractorFactory {

    public static BarcodeExtractor getInstance(String barcode) {
        String[] barcodeSplit = barcode.split(" ");

        if (barcodeSplit.length == 2) {
            return new NoRef2BarcodeExtractor();
        }

        return new StandardBarcodeExtractor();
    }
}
