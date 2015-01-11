package com.swick.reficalcpro;

import java.math.BigDecimal;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ComparisonFragment extends Fragment {
    private RefiCalcActivity mActivity;
    private TextView comparisonMonthlyPayment;
    private TextView comparisonDuration;
    private TextView comparisonInterest;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (RefiCalcActivity) activity;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("RefiCalcPro.ComparisonFragment", "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("RefiCalcPro.ComparisonFragment", "onResume");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.comparison_layout, container,
                false);

        // Monthly Payment
        comparisonMonthlyPayment = (TextView) rootView
                .findViewById(R.id.comparison_monthly_payment);

        // Duration
        comparisonDuration = (TextView) rootView
                .findViewById(R.id.comparison_duration);

        // Interest
        comparisonInterest = (TextView) rootView
                .findViewById(R.id.comparison_interest);

        setComps();
        return rootView;
    }

    void setComps() {
        String monthlyPaymentText = mActivity.getResources().getString(
                R.string.comparison_monthly_payment_format);
        if (mActivity.getComparisonState().getComparisonMonthlyPayment()
                .compareTo(BigDecimal.ZERO) < 0) {
            float monthlyPayment = mActivity.getComparisonState()
                    .getComparisonMonthlyPayment().abs().floatValue();
            monthlyPaymentText = String.format(monthlyPaymentText,
                    monthlyPayment, "less");
        } else if (mActivity.getComparisonState().getComparisonMonthlyPayment()
                .compareTo(BigDecimal.ZERO) > 0) {
            float monthlyPayment = mActivity.getComparisonState()
                    .getComparisonMonthlyPayment().floatValue();
            monthlyPaymentText = String.format(monthlyPaymentText,
                    monthlyPayment, "more");
        } else {
            monthlyPaymentText = "Your monthly payment is the same";
        }
        comparisonMonthlyPayment.setText(monthlyPaymentText);

        String comparisonDurationText = mActivity.getResources().getString(
                R.string.comparison_duration_format);
        if (mActivity.getComparisonState().getComparisonDuration() < 0) {
            int duration = Math.abs(mActivity.getComparisonState()
                    .getComparisonDuration());
            int years = duration / 12;
            int months = duration % 12;
            comparisonDurationText = String.format(comparisonDurationText,
                    years, months, "shorter");
        } else if (mActivity.getComparisonState().getComparisonDuration() > 0) {
            int years = mActivity.getComparisonState().getComparisonDuration() / 12;
            int months = mActivity.getComparisonState().getComparisonDuration() % 12;
            comparisonDurationText = String.format(comparisonDurationText,
                    years, months, "longer");
        } else {
            comparisonDurationText = "Your total duration is the same";
        }
        comparisonDuration.setText(comparisonDurationText);

        String comparisonInterestText = mActivity.getResources().getString(
                R.string.comparison_interest_format);
        if (mActivity.getComparisonState().getComparisonInterestPaid()
                .compareTo(BigDecimal.ZERO) < 0) {
            float interestPaid = mActivity.getComparisonState()
                    .getComparisonInterestPaid().abs().floatValue();
            comparisonInterestText = String.format(comparisonInterestText,
                    interestPaid, "less");
        } else if (mActivity.getComparisonState().getComparisonInterestPaid()
                .compareTo(BigDecimal.ZERO) > 0) {
            float interestPaid = mActivity.getComparisonState()
                    .getComparisonInterestPaid().floatValue();
            comparisonInterestText = String.format(comparisonInterestText,
                    interestPaid, "more");
        } else {
            comparisonInterestText = "You pay the same amount in interest";
        }

        comparisonInterest.setText(comparisonInterestText);
    }
}