package com.swick.reficalcpro;

import java.math.BigDecimal;

public class MortgageState {
    private BigDecimal mMortgagePrincipal;
    private BigDecimal mMortageInterestRate;
    private Integer mMortgageYear;
    private Integer mMortgageMonth;
    private Integer mMortgageDuration;
    private BigDecimal mMortgageTaxes;
    private BigDecimal mMortgageInsurance;
    private BigDecimal mMortgageMonthlyPayment;
    private BigDecimal mMortgageTotalInterest;

    public MortgageState() {
        this.mMortgagePrincipal = new BigDecimal(200000);
        this.mMortageInterestRate = new BigDecimal(5);
        this.mMortgageYear = 2015;
        this.mMortgageMonth = 0;
        this.mMortgageDuration = 30;
    }

    public BigDecimal getPrincipal() {
        return mMortgagePrincipal;
    }

    public void setPrincipal(BigDecimal mortgagePrincipal) {
        this.mMortgagePrincipal = mortgagePrincipal;
    }

    public BigDecimal getInterestRate() {
        return mMortageInterestRate;
    }

    public void setInterestRate(BigDecimal mortageInterestRate) {
        this.mMortageInterestRate = mortageInterestRate;
    }

    public Integer getYear() {
        return mMortgageYear;
    }

    public void setYear(Integer mortgageYear) {
        this.mMortgageYear = mortgageYear;
    }

    public Integer getMonth() {
        return mMortgageMonth;
    }

    public void setMonth(Integer mortgageMonth) {
        this.mMortgageMonth = mortgageMonth;
    }

    public Integer getDuration() {
        return mMortgageDuration;
    }

    public void setDuration(Integer mortgageDuration) {
        this.mMortgageDuration = mortgageDuration;
    }

    public BigDecimal getTaxes() {
        return mMortgageTaxes;
    }

    public void setTaxes(BigDecimal mMortgageTaxes) {
        this.mMortgageTaxes = mMortgageTaxes;
    }

    public BigDecimal getInsurance() {
        return mMortgageInsurance;
    }

    public void setInsurance(BigDecimal mMortgageInsurance) {
        this.mMortgageInsurance = mMortgageInsurance;
    }

    public BigDecimal getMonthlyPayment() {
        return mMortgageMonthlyPayment;
    }

    public void setMonthlyPayment(BigDecimal mortgageMonthlyPayment) {
        this.mMortgageMonthlyPayment = mortgageMonthlyPayment;
    }

    public BigDecimal getTotalInterest() {
        return mMortgageTotalInterest;
    }

    public void setTotalInterest(BigDecimal mortgageTotalInterest) {
        this.mMortgageTotalInterest = mortgageTotalInterest;
    }
}