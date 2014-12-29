package com.swick.reficalcpro;

import static com.swick.reficalcpro.Utils.divide;
import static com.swick.reficalcpro.Utils.newBigDecimal;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.swick.reficalcpro.DatePickerFragment.MortgageDateChangeListener;

public class RefiCalcActivity extends FragmentActivity implements MortgageDateChangeListener {

	/**
	 * Mortage/Refinance fields
	 */
	public static final String CURRENT_MORTGAGE_MONTH = "CURRENT_MORTGAGE_MONTH";
	public static final String CURRENT_MORTGAGE_YEAR = "CURRENT_MORTGAGE_YEAR";
	public static final String REFINANCED_MORTGAGE_MONTH = "REFINANCED_MORTGAGE_MONTH";
	public static final String REFINANCED_MORTGAGE_YEAR = "REFINANCED_MORTGAGE_YEAR";
	
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
	private final Map<String, Integer> mLoanDurations;

	/**
	 * Fragments
	 */
	private RefinanceFragment mRefinanceFragment;
	private ComparisonFragment mComparisonFragment;
	
	/**
	 * States
	 */
	private final MortgageState mMortgageState;
	private final RefinanceState mRefinanceState;
	private final ComparisonState mComparisonState;

	public RefiCalcActivity() {
		tabTitles = new SparseArray<String>();
		mLoanDurations = new LinkedHashMap<String, Integer>();

		mMortgageState = new MortgageState();
		mMortgageState.setDuration(30);
		mMortgageState.setInterestRate(newBigDecimal(5));
		mMortgageState.setMonth(Calendar.JANUARY);
		mMortgageState.setYear(2015);
		mMortgageState.setPrincipal(newBigDecimal(200000));
		
		mRefinanceState = new RefinanceState();
		mRefinanceState.setDuration(30);
		mRefinanceState.setInterestRate(newBigDecimal(5));
		mRefinanceState.setMonth(Calendar.FEBRUARY);
		mRefinanceState.setYear(2015);
		mRefinanceState.setCashOut(newBigDecimal(10000));
		mRefinanceState.setCost(newBigDecimal(6000));
		
		mComparisonState = new ComparisonState();
		
		calculateMortgage();
		calculateRefinance();
		calculateComparison();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		tabTitles.put(0, getResources().getString(R.string.tab_mortgage));
		tabTitles.put(1, getResources().getString(R.string.tab_refinance));
		tabTitles.put(2, getResources().getString(R.string.tab_comparison));

		mLoanDurations.put("15 years", getResources().getInteger(R.integer.duration_15_years));
		mLoanDurations.put("30 years", getResources().getInteger(R.integer.duration_30_years));
		mLoanDurations.put("40 years", getResources().getInteger(R.integer.duration_40_years));

		
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
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {

			}
		};

		for (int i = 0; i < 3; i++) {
			actionBar.addTab(actionBar.newTab().setText(tabTitles.get(i))
					.setTabListener(tabListener));
		}

		mRefiCalcPagerAdapter = new RefiCalcPagerAdapter(this, getSupportFragmentManager());

		mViewPager.setAdapter(mRefiCalcPagerAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				getActionBar().setSelectedNavigationItem(position);
			}
		});

		recalc();
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
				MortgageFragment mortgageFragment = new MortgageFragment();
				fragment = mortgageFragment;
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
	 * @param v
	 */
	public void showRefiDatePickerDialog(View v) {
		Bundle bundle = new Bundle();
		bundle.putInt(REFINANCED_MORTGAGE_YEAR, mRefinanceState.getYear());
		bundle.putInt(REFINANCED_MORTGAGE_MONTH, mRefinanceState.getMonth());

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
		String displayMonth = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
		startDateView.setText(displayMonth + " " + year);
	}

	@Override
	public void setRefinanceDate(int month, int year) {
		mRefinanceState.setMonth(month);
		mRefinanceState.setYear(year);

		TextView startDateView = (TextView) findViewById(R.id.refinance_start_date);
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, month);
		String displayMonth = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
		startDateView.setText(displayMonth + " " + year);

	}
	
	public void recalc() {
		calculateMortgage();
		calculateRefinance();
		calculateComparison();

		if (mComparisonFragment != null) {
			mComparisonFragment.setComps();
		}
	}
	
	public Map<String, Integer> getLoanDurations() {
		return mLoanDurations;
	}

	public RefinanceFragment getmRefinanceFragment() {
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
	
	/**
	 * Helper functions
	 */
	
	private void calculateMortgage() {
		calculateMortgage(mMortgageState);
	}
	
	private void calculateMortgage(MortgageState state) {
		// i
		BigDecimal monthlyInterest = divide(divide(state.getInterestRate(), newBigDecimal(100)), newBigDecimal(12));

		// 1 + i
		BigDecimal monthlyInterestPlusOne = monthlyInterest.add(BigDecimal.ONE);
		
		// n
		BigDecimal n = newBigDecimal(12).multiply(newBigDecimal(state.getDuration()));
		
		// (1 + i) ^ n
		BigDecimal monthlyInterestPlusOnePow = monthlyInterestPlusOne.pow(n.intValue());
		
		// dividend
		BigDecimal dividend = monthlyInterest.multiply(state.getPrincipal()).multiply(monthlyInterestPlusOnePow);
		
		// divisor
		BigDecimal divisor = monthlyInterestPlusOnePow.subtract(BigDecimal.ONE);

		state.setMonthlyPayment(divide(dividend, divisor));

		state.setTotalInterest(state.getMonthlyPayment().multiply(newBigDecimal(12).multiply(newBigDecimal(state.getDuration()))).subtract(state.getPrincipal()));
	}
	

	private void calculateRefinance() {
		// Interest & Principal until refinance
		// i
		BigDecimal monthlyInterest = divide(divide(mMortgageState.getInterestRate(), newBigDecimal(100)), newBigDecimal(12));

		BigDecimal balance = mMortgageState.getPrincipal();
		BigDecimal interestPaid = BigDecimal.ZERO;

		int durationUntilRefinance = (mRefinanceState.getYear() - mMortgageState.getYear()) * 12 + (mRefinanceState.getMonth() - mMortgageState.getMonth());
		for (int i = 0; i < durationUntilRefinance; i++) {
			BigDecimal newBalance = balance.multiply(monthlyInterest.add(BigDecimal.ONE));
			interestPaid = interestPaid.add(newBalance.subtract(balance));
			balance = newBalance.subtract(mMortgageState.getMonthlyPayment());
		}

		// Refinance
		mRefinanceState.setPrincipal(balance);
		calculateMortgage(mRefinanceState);
	}

	private void calculateComparison() {
		mComparisonState.setComparisonMonthlyPayment(mRefinanceState.getMonthlyPayment().subtract(mMortgageState.getMonthlyPayment()));
		mComparisonState.setComparisonInterestPaid(mRefinanceState.getTotalInterest().subtract(mMortgageState.getTotalInterest()));

		int durationUntilRefinance = (mRefinanceState.getYear()- mMortgageState.getYear()) * 12 + (mRefinanceState.getMonth() - mMortgageState.getMonth());
		mComparisonState.setComparisonDuration((mRefinanceState.getDuration() * 12 + durationUntilRefinance) - (mMortgageState.getDuration() * 12));
	}

}
