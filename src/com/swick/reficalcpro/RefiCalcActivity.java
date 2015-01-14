package com.swick.reficalcpro;

import static com.swick.reficalcpro.Utils.divide;
import static com.swick.reficalcpro.Utils.getNextMonth;
import static com.swick.reficalcpro.Utils.getPreviousMonth;
import static com.swick.reficalcpro.Utils.newBigDecimal;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.swick.reficalcpro.DatePickerFragment.MortgageDateChangeListener;

public class RefiCalcActivity extends FragmentActivity implements
        MortgageDateChangeListener {

    /**
     * Mortgage/Refinance fields
     */
    public static final String CURRENT_MORTGAGE_MONTH = "CURRENT_MORTGAGE_MONTH";
    public static final String CURRENT_MORTGAGE_YEAR = "CURRENT_MORTGAGE_YEAR";
    public static final String REFINANCED_MORTGAGE_MONTH = "REFINANCED_MORTGAGE_MONTH";
    public static final String REFINANCED_MORTGAGE_YEAR = "REFINANCED_MORTGAGE_YEAR";
    public static final String REFINANCED_EARLIEST_DATE = "REFINANCED_EARLIEST_DATE";
    public static final String REFINANCED_LATEST_LATEST_DATE = "REFINANCED_LATEST_LATEST_DATE";

    /**
     * Constants for saving/restoring app state
     */
    private static final String mortgagePrefix = "MORTGAGE-";
    private static final String refinancePrefix = "REFI-";

    private static final String principleSuffix = "PRINCIPLE";
    private static final String interestSuffix = "INTEREST";
    private static final String yearSuffix = "YEAR";
    private static final String monthSuffix = "MONTH";
    private static final String durationSuffix = "DURATION";
    private static final String taxesSuffix = "TAXES";
    private static final String insuranceSuffix = "INSURANCE";
    private static final String costSuffix = "COST";
    private static final String cashOutSuffix = "CASHOUT";

    /**
     * Adapter
     */
    private RefiCalcPagerAdapter mRefiCalcPagerAdapter;

    /**
     * Pager
     */
    private ViewPager mViewPager;

    /**
     * Tab Titles
     */
    private final SparseArray<String> tabTitles;

    /**
     * Durations
     */
    private final Map<String, Integer> mLoanDurationLabels;
    private final Map<Integer, Integer> mLoanDurationLabelIndexes;

    /**
     * Fragments
     */
    private MortgageFragment mMortgageFragment;
    private RefinanceFragment mRefinanceFragment;
    private ComparisonFragment mComparisonFragment;

    /**
     * States
     */
    private final MortgageState mMortgageState;
    private final RefinanceState mRefinanceState;
    private final ComparisonState mComparisonState;

    /**
     * Metrics
     */
    private final SparseArray<String> mMetricsTitles;

    /**
     * Current focus. Should be cleared between tab swipes.
     */
    private View mCurrentFocusedView;

    public RefiCalcActivity() {
        tabTitles = new SparseArray<String>();
        mMetricsTitles = new SparseArray<String>();

        mMetricsTitles.put(0, "Mortgage");
        mMetricsTitles.put(1, "Refinance");
        mMetricsTitles.put(2, "Comparison");

        mLoanDurationLabels = new LinkedHashMap<String, Integer>();
        mLoanDurationLabelIndexes = new HashMap<Integer, Integer>();

        mMortgageState = new MortgageState();
        mRefinanceState = new RefinanceState();
        mComparisonState = new ComparisonState();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mMortgageState.setDuration(30);
            mMortgageState.setInterestRate(newBigDecimal(5));
            mMortgageState.setMonth(Calendar.JANUARY);
            mMortgageState.setYear(2015);
            mMortgageState.setPrincipal(newBigDecimal(200000));
            mMortgageState.setTaxes(BigDecimal.ZERO);
            mMortgageState.setInsurance(BigDecimal.ZERO);

            mRefinanceState.setDuration(30);
            mRefinanceState.setInterestRate(newBigDecimal(5));
            mRefinanceState.setMonth(Calendar.FEBRUARY);
            mRefinanceState.setYear(2015);
            mRefinanceState.setCashOut(newBigDecimal(10000));
            mRefinanceState.setCost(newBigDecimal(6000));
        } else {
            mMortgageState.setPrincipal(newBigDecimal(savedInstanceState
                    .getString(mortgagePrefix + principleSuffix)));
            mMortgageState.setInterestRate(newBigDecimal(savedInstanceState
                    .getString(mortgagePrefix + interestSuffix)));
            mMortgageState.setDuration(savedInstanceState.getInt(mortgagePrefix
                    + durationSuffix));
            mMortgageState.setMonth(savedInstanceState.getInt(mortgagePrefix
                    + monthSuffix));
            mMortgageState.setYear(savedInstanceState.getInt(mortgagePrefix
                    + yearSuffix));
            mMortgageState.setTaxes(newBigDecimal(savedInstanceState
                    .getString(mortgagePrefix + taxesSuffix)));
            mMortgageState.setInsurance(newBigDecimal(savedInstanceState
                    .getString(mortgagePrefix + insuranceSuffix)));

            mRefinanceState.setPrincipal(newBigDecimal(savedInstanceState
                    .getString(refinancePrefix + principleSuffix)));
            mRefinanceState.setInterestRate(newBigDecimal(savedInstanceState
                    .getString(refinancePrefix + interestSuffix)));
            mRefinanceState.setDuration(savedInstanceState
                    .getInt(refinancePrefix + durationSuffix));
            mRefinanceState.setMonth(savedInstanceState.getInt(refinancePrefix
                    + monthSuffix));
            mRefinanceState.setYear(savedInstanceState.getInt(refinancePrefix
                    + yearSuffix));
            mRefinanceState.setCost(newBigDecimal(savedInstanceState
                    .getString(refinancePrefix + costSuffix)));
            mRefinanceState.setCashOut(newBigDecimal(savedInstanceState
                    .getString(refinancePrefix + cashOutSuffix)));
        }

        calculateMortgage();
        calculateRefinance();
        calculateComparison();

        ((RefiCalcApplication) getApplication()).getTracker();

        tabTitles.put(0, getResources().getString(R.string.tab_mortgage));
        tabTitles.put(1, getResources().getString(R.string.tab_refinance));
        tabTitles.put(2, getResources().getString(R.string.tab_comparison));

        mLoanDurationLabels.put("15 years",
                getResources().getInteger(R.integer.duration_15_years));
        mLoanDurationLabels.put("30 years",
                getResources().getInteger(R.integer.duration_30_years));
        mLoanDurationLabels.put("40 years",
                getResources().getInteger(R.integer.duration_40_years));

        int durationLabelIndex = 0;
        for (String label : mLoanDurationLabels.keySet()) {
            mLoanDurationLabelIndexes.put(mLoanDurationLabels.get(label),
                    durationLabelIndex++);
        }

        setContentView(R.layout.refi_calc_layout);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {

            @Override
            public void onTabUnselected(Tab tab, FragmentTransaction ft) {

            }

            @Override
            public void onTabSelected(Tab tab, FragmentTransaction ft) {
                mViewPager.setCurrentItem(tab.getPosition());

                dismissKeyboard();

                Tracker tracker = ((RefiCalcApplication) getApplication())
                        .getTracker();
                tracker.setScreenName(mMetricsTitles.get(tab.getPosition()));
                tracker.send(new HitBuilders.AppViewBuilder().build());
            }

            @Override
            public void onTabReselected(Tab tab, FragmentTransaction ft) {

            }
        };

        for (int i = 0; i < 3; i++) {
            actionBar.addTab(actionBar.newTab().setText(tabTitles.get(i))
                    .setTabListener(tabListener));
        }

        mRefiCalcPagerAdapter = new RefiCalcPagerAdapter(this,
                getSupportFragmentManager());

        mViewPager.setAdapter(mRefiCalcPagerAdapter);
        mViewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        getActionBar().setSelectedNavigationItem(position);
                    }
                });

        recalc();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(mortgagePrefix + principleSuffix, mMortgageState
                .getPrincipal().toPlainString());
        outState.putString(mortgagePrefix + interestSuffix, mMortgageState
                .getInterestRate().toPlainString());
        outState.putInt(mortgagePrefix + yearSuffix, mMortgageState.getYear());
        outState.putInt(mortgagePrefix + monthSuffix, mMortgageState.getMonth());
        outState.putInt(mortgagePrefix + durationSuffix,
                mMortgageState.getDuration());
        outState.putString(mortgagePrefix + taxesSuffix, mMortgageState
                .getTaxes().toPlainString());
        outState.putString(mortgagePrefix + insuranceSuffix, mMortgageState
                .getInsurance().toPlainString());

        outState.putString(refinancePrefix + principleSuffix, mRefinanceState
                .getPrincipal().toPlainString());
        outState.putString(refinancePrefix + interestSuffix, mRefinanceState
                .getInterestRate().toPlainString());
        outState.putInt(refinancePrefix + yearSuffix, mRefinanceState.getYear());
        outState.putInt(refinancePrefix + monthSuffix,
                mRefinanceState.getMonth());
        outState.putInt(refinancePrefix + durationSuffix,
                mRefinanceState.getDuration());
        outState.putString(refinancePrefix + costSuffix, mRefinanceState
                .getCost().toPlainString());
        outState.putString(refinancePrefix + cashOutSuffix, mRefinanceState
                .getCashOut().toPlainString());
    }

    /**
     * Adapter for tabbed views.
     */
    public static class RefiCalcPagerAdapter extends FragmentPagerAdapter {

        private RefiCalcActivity refiCalcActivity;

        public RefiCalcPagerAdapter(RefiCalcActivity refiCalcActivity,
                FragmentManager supportFragmentManager) {
            super(supportFragmentManager);

            this.refiCalcActivity = refiCalcActivity;
        }

        @Override
        public Fragment getItem(int itemIndex) {
            Fragment fragment;
            if (itemIndex == 0) {
                refiCalcActivity.mMortgageFragment = new MortgageFragment();
                fragment = refiCalcActivity.mMortgageFragment;
            } else if (itemIndex == 1) {
                refiCalcActivity.mRefinanceFragment = new RefinanceFragment();
                fragment = refiCalcActivity.mRefinanceFragment;
            } else {
                refiCalcActivity.mComparisonFragment = new ComparisonFragment();
                fragment = refiCalcActivity.mComparisonFragment;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    /**
     * Date picker for Mortgage tab.
     *
     * @param v
     */
    public void showMortgagePickerDialog(View v) {
        Bundle bundle = new Bundle();
        bundle.putInt(CURRENT_MORTGAGE_YEAR, mMortgageState.getYear());
        bundle.putInt(CURRENT_MORTGAGE_MONTH, mMortgageState.getMonth());

        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setArguments(bundle);
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
        datePickerFragment.onAttach(this);
    }

    /**
     * Date picker for Refinance tab.
     *
     * @param v
     */
    public void showRefiDatePickerDialog(View v) {
        Bundle bundle = new Bundle();
        bundle.putInt(REFINANCED_MORTGAGE_YEAR, mRefinanceState.getYear());
        bundle.putInt(REFINANCED_MORTGAGE_MONTH, mRefinanceState.getMonth());

        Calendar earliestDate = Calendar.getInstance();
        earliestDate.set(mMortgageState.getYear(),
                getNextMonth(mMortgageState), 1);
        long earliestMillis = earliestDate.getTimeInMillis();
        bundle.putLong(REFINANCED_EARLIEST_DATE, earliestMillis);

        Calendar latest = Calendar.getInstance();
        latest.set(mMortgageState.getYear() + mMortgageState.getDuration(),
                getPreviousMonth(mMortgageState), 1);
        long latestMillis = latest.getTimeInMillis();
        bundle.putLong(REFINANCED_LATEST_LATEST_DATE, latestMillis);

        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setArguments(bundle);
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
        datePickerFragment.onAttach(this);
    }

    @Override
    public void setMortgageDate(int month, int year) {
        mMortgageState.setMonth(month);
        mMortgageState.setYear(year);

        TextView startDateView = (TextView) findViewById(R.id.mortgage_start_date);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, month);
        String displayMonth = c.getDisplayName(Calendar.MONTH, Calendar.LONG,
                Locale.US);
        startDateView.setText(displayMonth + " " + year);
        mMortgageFragment.updateSummary();
    }

    @Override
    public void setRefinanceDate(int month, int year) {
        mRefinanceState.setMonth(month);
        mRefinanceState.setYear(year);

        TextView startDateView = (TextView) findViewById(R.id.refinance_start_date);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, month);
        String displayMonth = c.getDisplayName(Calendar.MONTH, Calendar.LONG,
                Locale.US);
        startDateView.setText(displayMonth + " " + year);
        mRefinanceFragment.updateState();
    }

    public void recalc() {
        calculateMortgage();
        calculateRefinance();
        calculateComparison();

        if (mComparisonFragment != null) {
            mComparisonFragment.setComps();
        }
    }

    public Map<String, Integer> getLoanDurationLabels() {
        return mLoanDurationLabels;
    }

    public Map<Integer, Integer> getLoanDurationLabelIndexes() {
        return mLoanDurationLabelIndexes;
    }

    public RefinanceFragment getRefinanceFragment() {
        if (mRefinanceFragment == null) {
            mRefiCalcPagerAdapter.getItem(1);
        }

        return mRefinanceFragment;
    }

    public MortgageState getMortgageState() {
        return mMortgageState;
    }

    public RefinanceState getRefinanceState() {
        return mRefinanceState;
    }

    public ComparisonState getComparisonState() {
        return mComparisonState;
    }

    public void setCurrentFocusedEditText(View v) {
        if (v == null) {
            return;
        }

        mCurrentFocusedView = v;
    }

    public boolean clearFocus(View v) {
        boolean cleared = false;
        if (v == mCurrentFocusedView) {
            mCurrentFocusedView = null;
            cleared = true;
        }

        return cleared;
    }

    /**
     * Helper functions
     */

    private void calculateMortgage() {
        calculateMortgage(mMortgageState);
    }

    private void calculateMortgage(MortgageState state) {
        // i
        BigDecimal monthlyInterest = divide(
                divide(state.getInterestRate(), newBigDecimal(100)),
                newBigDecimal(12));

        if (BigDecimal.ZERO.equals(monthlyInterest)) {
            monthlyInterest = newBigDecimal("0.01");
        }

        // 1 + i
        BigDecimal monthlyInterestPlusOne = monthlyInterest.add(BigDecimal.ONE);

        // n
        BigDecimal n = newBigDecimal(12).multiply(
                newBigDecimal(state.getDuration()));

        // (1 + i) ^ n
        BigDecimal monthlyInterestPlusOnePow = monthlyInterestPlusOne.pow(n
                .intValue());

        // dividend
        BigDecimal dividend = monthlyInterest.multiply(state.getPrincipal())
                .multiply(monthlyInterestPlusOnePow);

        // divisor
        BigDecimal divisor = monthlyInterestPlusOnePow.subtract(BigDecimal.ONE);

        // monthly taxes
        BigDecimal monthlyTaxes = divide(state.getTaxes(), newBigDecimal(12));

        state.setMonthlyPayment(divide(dividend, divisor).add(monthlyTaxes)
                .add(state.getInsurance()));

        state.setTotalInterest(state
                .getMonthlyPayment()
                .multiply(
                        newBigDecimal(12).multiply(
                                newBigDecimal(state.getDuration())))
                .subtract(state.getPrincipal()));
    }

    private void calculateRefinance() {
        // Interest & Principal until refinance
        // i
        BigDecimal monthlyInterest = divide(
                divide(mMortgageState.getInterestRate(), newBigDecimal(100)),
                newBigDecimal(12));

        BigDecimal balance = mMortgageState.getPrincipal();
        BigDecimal interestPaid = BigDecimal.ZERO;

        int durationUntilRefinance = (mRefinanceState.getYear() - mMortgageState
                .getYear())
                * 12
                + (mRefinanceState.getMonth() - mMortgageState.getMonth());
        for (int i = 0; i < durationUntilRefinance; i++) {
            BigDecimal newBalance = balance.multiply(monthlyInterest
                    .add(BigDecimal.ONE));
            interestPaid = interestPaid.add(newBalance.subtract(balance));
            balance = newBalance.subtract(mMortgageState.getMonthlyPayment());
        }

        // Refinance
        mRefinanceState.setPrincipal(balance.add(mRefinanceState.getCost())
                .add(mRefinanceState.getCashOut()));
        mRefinanceState.setTaxes(mMortgageState.getTaxes());
        mRefinanceState.setInsurance(mMortgageState.getInsurance());
        calculateMortgage(mRefinanceState);
    }

    private void calculateComparison() {
        mComparisonState.setComparisonMonthlyPayment(mRefinanceState
                .getMonthlyPayment().subtract(
                        mMortgageState.getMonthlyPayment()));
        mComparisonState
                .setComparisonInterestPaid(mRefinanceState.getTotalInterest()
                        .subtract(mMortgageState.getTotalInterest()));

        int durationUntilRefinance = (mRefinanceState.getYear() - mMortgageState
                .getYear())
                * 12
                + (mRefinanceState.getMonth() - mMortgageState.getMonth());
        mComparisonState
                .setComparisonDuration((mRefinanceState.getDuration() * 12 + durationUntilRefinance)
                        - (mMortgageState.getDuration() * 12));
    }

    private void dismissKeyboard() {
        if (mCurrentFocusedView == null) {
            return;
        }

        mCurrentFocusedView.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            try {
                imm.hideSoftInputFromWindow(
                        mCurrentFocusedView.getWindowToken(), 0);
            } catch (NullPointerException e) {
                Log.w("RefiCalcPro.RefiCalcActivity",
                        "Caught NPE, mCurrentFocusedView="
                                + mCurrentFocusedView + " imm=" + imm, e);
            }
        }
        mCurrentFocusedView = null;
    }

}
