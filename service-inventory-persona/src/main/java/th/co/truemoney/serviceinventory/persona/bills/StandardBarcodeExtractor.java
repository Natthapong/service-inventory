package th.co.truemoney.serviceinventory.persona.bills;

public class StandardBarcodeExtractor implements BarcodeExtractor {

    @Override
    public BarcodeResult extract(String barcode) {

        String[] barcodeSplit = barcode.split(" ");
        assert barcodeSplit.length == 3;

        String taxID = extractTaxID(barcodeSplit[0]);

        return new BarcodeResult(taxID, barcodeSplit[1], barcodeSplit[2], barcodeSplit[3]);
    }

    private String extractTaxID(String barcodeFirstPart) {

        if(barcodeFirstPart.startsWith("|")) {
            return barcodeFirstPart.substring(1);
        }

        return barcodeFirstPart;
    }

}
