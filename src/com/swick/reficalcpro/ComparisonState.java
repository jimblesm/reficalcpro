package com.swick.reficalcpro;

import java.math.BigDecimal;

public class ComparisonState {
	private BigDecimal mComparisonMonthlyPayment;
	private Integer mComparisonDuration;
	private BigDecimal mComparisonInterestPaid;

	public ComparisonState() {
	}

	public BigDecimal getComparisonMonthlyPayment() {
		return mComparisonMonthlyPayment;
	}

	public void setComparisonMonthlyPayment(
			BigDecimal mComparisonMonthlyPayment) {
		this.mComparisonMonthlyPayment = mComparisonMonthlyPayment;
	}

	public Integer getComparisonDuration() {
		return mComparisonDuration;
	}

	public void setComparisonDuration(Integer mComparisonDuration) {
		this.mComparisonDuration = mComparisonDuration;
	}

	public BigDecimal getComparisonInterestPaid() {
		return mComparisonInterestPaid;
	}

	public void setComparisonInterestPaid(BigDecimal mComparisonInterestPaid) {
		this.mComparisonInterestPaid = mComparisonInterestPaid;
	}
}