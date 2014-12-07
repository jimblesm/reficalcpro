package com.swick.reficalcpro;

import java.math.BigDecimal;

public class RefinanceState extends MortgageState {
	
	private BigDecimal mRefinanceCosts;
	private BigDecimal mRefinanceCashOut;

	public BigDecimal getCost() {
		return mRefinanceCosts;
	}
	public void setCost(BigDecimal refinanceCosts) {
		this.mRefinanceCosts = refinanceCosts;
	}
	public BigDecimal getCashOut() {
		return mRefinanceCashOut;
	}
	public void setCashOut(BigDecimal refinanceCashOut) {
		this.mRefinanceCashOut = refinanceCashOut;
	}
	
}
