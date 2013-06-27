package th.co.truemoney.serviceinventory.bill.domain;

public enum DebtStatus {
	Debt, NoDebt;
	
	private static final String DEBT_CODE = "0";

	public static DebtStatus valueFromCode(String code) {
		return DEBT_CODE.equals(code) ? NoDebt : Debt;
	}
	
}
