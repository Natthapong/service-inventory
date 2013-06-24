package th.co.truemoney.serviceinventory.persona.bills;

public class NoRef2BarcodeExtractor implements BarcodeExtractor {

    @Override
    public BarcodeResult extract(String barcode) {

        String[] barcodeSplit = barcode.split(" ");

        String taxID = extractTaxID(barcodeSplit[0]);

        return new BarcodeResult(taxID, barcodeSplit[1], null, barcodeSplit[2]);
    }

    private String extractTaxID(String barcodeFirstPart) {

        if(barcodeFirstPart.startsWith("|")) {
            return barcodeFirstPart.substring(1);
        }

        return barcodeFirstPart;
    }

}
