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

import com.swick.reficalcpro.beta.R;

public class ComparisonFragment extends Fragment {
    private RefiCalcActivity mActivity;
    private TextView comparisonMonthlyPayment;
    private TextView comparisonMonthlyPaymentLessMore;
    private TextView comparisonDuration;
    private TextView comparisonShorterLonger;
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

        comparisonMonthlyPaymentLessMore = (TextView) rootView
                .findViewById(R.id.comparison_monthly_payment_less_more);

        // Duration
        comparisonDuration = (TextView) rootView
                .findViewById(R.id.comparison_duration);
        comparisonShorterLonger = (TextView) rootView
                .findViewById(R.id.comparison_longer_shorter);

        // Interest
        comparisonInterest = (TextView) rootView
                .findViewById(R.id.comparison_interest);

        setComps();
        return rootView;
    }

    void setComps() {
        String monthlyPaymentText = mActivity.getResources().getString(
                R.string.comparison_monthly_payment_format);
        String monthlyPaymentLessMoreText = mActivity.getResources().getString(
                R.string.comparison_monthly_payment_less_more);
        if (mActivity.getComparisonState().getComparisonMonthlyPayment()
                .compareTo(BigDecimal.ZERO) <= 0) {
            float monthlyPayment = mActivity.getComparisonState()
                    .getComparisonMonthlyPayment().abs().floatValue();
            monthlyPaymentText = String.format(monthlyPaymentText,
                    monthlyPayment);
            comparisonMonthlyPayment.setTextColor(getResources().getColor(
                    R.color.positive_green));
            monthlyPaymentLessMoreText = String.format(
                    monthlyPaymentLessMoreText, "less");
        } else if (mActivity.getComparisonState().getComparisonMonthlyPayment()
                .compareTo(BigDecimal.ZERO) > 0) {
            float monthlyPayment = mActivity.getComparisonState()
                    .getComparisonMonthlyPayment().floatValue();
            monthlyPaymentText = String.format(monthlyPaymentText,
                    monthlyPayment);
            comparisonMonthlyPayment.setTextColor(getResources().getColor(
                    R.color.negative_red));
            monthlyPaymentLessMoreText = String.format(
                    monthlyPaymentLessMoreText, "more");
        }
        comparisonMonthlyPayment.setText(monthlyPaymentText);
        comparisonMonthlyPaymentLessMore.setText(monthlyPaymentLessMoreText);

        String comparisonDurationText = mActivity.getResources().getString(
                R.string.comparison_duration_format);
        String comparisonShorterLongerText = mActivity.getResources()
                .getString(R.string.comparison_shorter_longer_format);
        if (mActivity.getComparisonState().getComparisonDuration() < 0) {
            int duration = Math.abs(mActivity.getComparisonState()
                    .getComparisonDuration());
            int years = duration / 12;
            int months = duration % 12;
            comparisonDurationText = String.format(comparisonDurationText,
                    years, months);
            comparisonDuration.setTextColor(getResources().getColor(
                    R.color.positive_green));
            comparisonShorterLongerText = String.format(
                    comparisonShorterLongerText, "shorter");
        } else if (mActivity.getComparisonState().getComparisonDuration() >= 0) {
            int years = mActivity.getComparisonState().getComparisonDuration() / 12;
            int months = mActivity.getComparisonState().getComparisonDuration() % 12;
            comparisonDurationText = String.format(comparisonDurationText,
                    years, months);
            comparisonDuration.setTextColor(getResources().getColor(
                    R.color.negative_red));
            comparisonShorterLongerText = String.format(
                    comparisonShorterLongerText, "longer");
        }
        comparisonDuration.setText(comparisonDurationText);
        comparisonShorterLonger.setText(comparisonShorterLongerText);

        String comparisonInterestText = mActivity.getResources().getString(
                R.string.comparison_interest_format);
        if (mActivity.getComparisonState().getComparisonInterestPaid()
                .compareTo(BigDecimal.ZERO) < 0) {
            float interestPaid = mActivity.getComparisonState()
                    .getComparisonInterestPaid().abs().floatValue();
            comparisonInterestText = String.format(comparisonInterestText,
                    interestPaid, "less");
            comparisonInterest.setTextColor(getResources().getColor(
                    R.color.positive_green));
        } else if (mActivity.getComparisonState().getComparisonInterestPaid()
                .compareTo(BigDecimal.ZERO) > 0) {
            float interestPaid = mActivity.getComparisonState()
                    .getComparisonInterestPaid().floatValue();
            comparisonInterestText = String.format(comparisonInterestText,
                    interestPaid, "more");
            comparisonInterest.setTextColor(getResources().getColor(
                    R.color.negative_red));
        } else {
            comparisonInterestText = "You pay the same amount in interest";
        }

        comparisonInterest.setText(comparisonInterestText);
    }
}