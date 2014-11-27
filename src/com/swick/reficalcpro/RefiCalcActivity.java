package com.swick.reficalcpro;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.swick.reficalcpro.DatePickerFragment.MortgageDateChangeListener;

public class RefiCalcActivity extends FragmentActivity implements
		MortgageDateChangeListener {

	private static final int NUM_TABS = 3;
	public static final String CURRENT_MORTGAGE_MONTH = "CURRENT_MORTGAGE_MONTH";
	public static final String CURRENT_MORTGAGE_YEAR = "CURRENT_MORTGAGE_YEAR";
	public static final String REFINANCED_MORTGAGE_MONTH = "REFINANCED_MORTGAGE_MONTH";
	public static final String REFINANCED_MORTGAGE_YEAR = "REFINANCED_MORTGAGE_YEAR";

	private RefiCalcPagerAdapter mRefiCalcPagerAdapter;

	private ViewPager mViewPager;
	private Map<Integer, String> tabTitles;
	private Map<String, Integer> loanDurations;

	private RefinanceFragment mRefinanceFragment;
	private ComparisonFragment mComparisonFragment;

	/*
	 * Mortgage
	 */
	private BigDecimal mMortgagePrincipal = newBigDecimal(250000);
	private BigDecimal mMortageInterestRate = newBigDecimal(5);
	private Integer mMortgageYear = 2014;
	private Integer mMortgageMonth = 0;
	private Integer mMortgageDuration = 30;
	private BigDecimal mMortgageMonthlyPayment;
	private BigDecimal mMortgageTotalInterest;

	/*
	 * Refinance
	 */
	private BigDecimal mRefinancePrincipal = newBigDecimal(200000);
	private BigDecimal mRefinanceInterestRate = newBigDecimal(5);
	private Integer mRefinanceMortgageYear = 2014;
	private Integer mRefinanceMortgageMonth = 1;
	private Integer mRefinanceDuration = 30;
	private BigDecimal mRefinanceCosts = newBigDecimal(6000);
	private BigDecimal mRefinanceCashOut = newBigDecimal(10000);
	private BigDecimal mRefinanceMonthlyPayment;
	private BigDecimal mRefinanceTotalInterest;

	/*
	 * Comparison
	 */
	private BigDecimal mComparisonMonthlyPayment;

	private Integer mComparisonDuration;

	private BigDecimal mComparisonInterestPaid;

	public RefiCalcActivity() {
		tabTitles = new HashMap<Integer, String>(NUM_TABS);
		tabTitles.put(0, "Current Mortgage");
		tabTitles.put(1, "Refinancing");
		tabTitles.put(2, "Comparison");

		loanDurations = new LinkedHashMap<String, Integer>();
		loanDurations.put("15 years", 15);
		loanDurations.put("30 years", 30);
		loanDurations.put("40 years", 40);

		calculateMortgage();
		calculateRefinance();
		calculateComparison();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

	public static class MortgageFragment extends Fragment {

		private RefiCalcActivity mActivity;

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
			mActivity.mRefinanceFragment.updateState();
			mActivity.recalc();
			updateSummary();
		}
		
		private void updateSummary() {
			TextView monthlyPaymentView = (TextView) mActivity.findViewById(R.id.mortgage_monthly_payment);
			TextView mortgagePayoffDateView = (TextView) mActivity.findViewById(R.id.mortgage_payoff_date);
			TextView totalInterestPaidView = (TextView) mActivity.findViewById(R.id.mortgage_total_interest_paid);
			String monthName = new DateFormatSymbols().getMonths()[mActivity.mMortgageMonth];
			
			setSummaryView(monthName, monthlyPaymentView, mortgagePayoffDateView, totalInterestPaidView);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.mortgage_layout,
					container, false);

			// Principal
			final EditText mortgageLoanAmountView = (EditText) rootView.findViewById(R.id.mortgage_loan_amount);
			
			mortgageLoanAmountView.setOnFocusChangeListener(mActivity.new AbstractRecalcFocusChangeListener() { 
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (!hasFocus) {
						EditText tempEditView = (EditText) v;
						Editable editable = ((EditText) v).getText();
						if (editable != null && editable.length() > 0) {
							mActivity.mMortgagePrincipal = newBigDecimal(editable.toString());
						} else {
							tempEditView.setText(mActivity.mMortgagePrincipal.toPlainString());
						}
						mActivity.recalc();
						updateSummary();
					}
					super.onFocusChange(v, hasFocus);
				}
			});

			mortgageLoanAmountView.setText(mActivity.mMortgagePrincipal
					.toString());

			// Interest
			final EditText mortgageInterestRateView = (EditText) rootView.findViewById(R.id.mortgage_interest_rate);
			mortgageInterestRateView.setOnFocusChangeListener(mActivity.new AbstractRecalcFocusChangeListener() { 
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (!hasFocus) {
						EditText tempEditView = (EditText) v;
						Editable editable = ((EditText) v).getText();
						if (editable != null && editable.length() > 0) {
							mActivity.mMortageInterestRate = newBigDecimal(editable.toString());
						} else {
							tempEditView.setText(mActivity.mMortageInterestRate.toPlainString());
						}
						mActivity.recalc();
						updateSummary();
					}
					super.onFocusChange(v, hasFocus);
				}
			});
			mortgageInterestRateView.setText(mActivity.mMortageInterestRate
					.toString());

			// Duration
			final Spinner mortgageSpinner = (Spinner) rootView
					.findViewById(R.id.mortgage_duration);
			// Create an ArrayAdapter using the string array and a default
			// spinner layout
			ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
					mActivity, android.R.layout.simple_spinner_item,
					mActivity.loanDurations.keySet().toArray(new String[0]));

			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			mortgageSpinner.setAdapter(adapter);
			mortgageSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							String loanDurations = ((TextView) view).getText()
									.toString();
							mActivity.mMortgageDuration = mActivity.loanDurations
									.get(loanDurations);
							mActivity.recalc();
							updateSummary();
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {

						}
					});

			mortgageSpinner.setSelection(1);
			// Start Date
			final TextView startDateView = (TextView) rootView
					.findViewById(R.id.mortgage_start_date);
			String monthName = new DateFormatSymbols().getMonths()[mActivity.mMortgageMonth];
			startDateView.setText(monthName + " " + mActivity.mMortgageYear);

			TextView monthlyPaymentView = (TextView) rootView.findViewById(R.id.mortgage_monthly_payment);
			TextView mortgagePayoffDateView = (TextView) rootView.findViewById(R.id.mortgage_payoff_date);
			TextView totalInterestPaidView = (TextView) rootView.findViewById(R.id.mortgage_total_interest_paid);
			
			setSummaryView(monthName, monthlyPaymentView,
					mortgagePayoffDateView, totalInterestPaidView);
			
			return rootView;
		}

		private void setSummaryView(String monthName,
				TextView monthlyPaymentView, TextView mortgagePayoffDateView,
				TextView totalInterestPaidView) {
			monthlyPaymentView.setText(mActivity.mMortgageMonthlyPayment.setScale(2, RoundingMode.CEILING).toPlainString());
			mortgagePayoffDateView.setText(monthName + " " + String.valueOf(Integer.valueOf(mActivity.mMortgageYear) + mActivity.mMortgageDuration));
			totalInterestPaidView.setText("$" + mActivity.mMortgageTotalInterest.setScale(2, RoundingMode.CEILING).toPlainString());
		}

	}

	public static class RefinanceFragment extends Fragment {

		private RefiCalcActivity mActivity;
		private View rootView;

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
			updateState();
			mActivity.recalc();
		}
		
		private void updateState() {
			final EditText refinanceInterestRateView = (EditText) rootView
					.findViewById(R.id.refinance_interest_rate);

			Editable editable = refinanceInterestRateView.getText();
			if (editable != null && editable.length() > 0) {
				mActivity.mRefinanceInterestRate = newBigDecimal(editable.toString());
			} else {
				refinanceInterestRateView.setText(mActivity.mRefinanceInterestRate.toPlainString());
			}

			final EditText refinanceCosts = (EditText) rootView
					.findViewById(R.id.refinance_costs);
			editable = refinanceCosts.getText();
			if (editable != null && editable.length() > 0) {
				mActivity.mRefinanceCosts = newBigDecimal(editable.toString());
			} else {
				refinanceCosts.setText(mActivity.mRefinanceCosts.toPlainString());
			}
			
			final EditText refinanceCashout = (EditText) rootView
					.findViewById(R.id.refinance_cash_out);
			editable = refinanceCashout.getText();
			if (editable != null && editable.length() > 0) {
				mActivity.mRefinanceCashOut = newBigDecimal(editable.toString());
			} else {
				refinanceCashout.setText(mActivity.mRefinanceCashOut.toPlainString());
			}

		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.refinance_layout,
					container, false);
			this.rootView = rootView;

			// Interest
			final EditText refinanceInterestRateView = (EditText) rootView
					.findViewById(R.id.refinance_interest_rate);
			refinanceInterestRateView.setOnFocusChangeListener(mActivity.new AbstractRecalcFocusChangeListener() { 
						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							if (!hasFocus) {
								EditText tempEditView = (EditText) v;
								Editable editable = ((EditText) v).getText();
								if (editable != null && editable.length() > 0) {
									mActivity.mRefinanceInterestRate = newBigDecimal(editable.toString());
								} else {
									tempEditView.setText(mActivity.mRefinanceInterestRate.toPlainString());
								}
							}
							super.onFocusChange(v, hasFocus);
						}
					});
			refinanceInterestRateView.setText(mActivity.mRefinanceInterestRate
					.toString());

			// Duration
			final Spinner refinanceSpinner = (Spinner) rootView
					.findViewById(R.id.refinance_duration);
			// Create an ArrayAdapter using the string array and a default
			// spinner layout
			ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
					mActivity, android.R.layout.simple_spinner_item,
					mActivity.loanDurations.keySet().toArray(new String[0]));
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			refinanceSpinner.setAdapter(adapter);
			refinanceSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							String loanDurations = ((TextView) view).getText()
									.toString();
							mActivity.mRefinanceDuration = mActivity.loanDurations
									.get(loanDurations);
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {

						}
					});

			refinanceSpinner.setSelection(1);
			// Start Date
			final TextView startDateView = (TextView) rootView
					.findViewById(R.id.refinance_start_date);
			String monthName = new DateFormatSymbols().getMonths()[mActivity.mRefinanceMortgageMonth];
			startDateView.setText(monthName + " "
					+ mActivity.mRefinanceMortgageYear);

			// Costs
			final EditText refinanceCosts = (EditText) rootView
					.findViewById(R.id.refinance_costs);
			refinanceCosts.setOnFocusChangeListener(mActivity.new AbstractRecalcFocusChangeListener() { 
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (!hasFocus) {
						EditText tempEditView = (EditText) v;
						Editable editable = tempEditView.getText();
						if (editable != null && editable.length() > 0) {
							mActivity.mRefinanceCosts = newBigDecimal(editable.toString());
						} else {
							tempEditView.setText(mActivity.mRefinanceCosts.toPlainString());
						}
					}
					super.onFocusChange(v, hasFocus);
				}
			});
			refinanceCosts.setText(mActivity.mRefinanceCosts.toString());

			// Cash Out Amount
			final EditText refinanceCashout = (EditText) rootView
					.findViewById(R.id.refinance_cash_out);
			refinanceCashout.setOnFocusChangeListener(mActivity.new AbstractRecalcFocusChangeListener() { 
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (!hasFocus) {
						EditText tempEditView = (EditText) v;
						Editable editable = ((EditText) v).getText();
						if (editable != null && editable.length() > 0) {
							mActivity.mRefinanceCashOut = newBigDecimal(editable.toString());
						} else {
							tempEditView.setText(mActivity.mRefinanceCashOut.toPlainString());
						}
					}
					super.onFocusChange(v, hasFocus);
				}
			});
			refinanceCashout.setText(mActivity.mRefinanceCashOut.toString());

			return rootView;
		}

	}

	public static class ComparisonFragment extends Fragment {
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

		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.comparison_layout,
					container, false);

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

		private void setComps() {
			String monthlyPaymentText = mActivity.getResources().getString(
					R.string.comparison_monthly_payment_format);
			if (mActivity.mComparisonMonthlyPayment.compareTo(BigDecimal.ZERO) < 0) {
				float monthlyPayment = mActivity.mComparisonMonthlyPayment
						.abs().floatValue();
				monthlyPaymentText = String.format(monthlyPaymentText,
						monthlyPayment, "less");
			} else if (mActivity.mComparisonMonthlyPayment
					.compareTo(BigDecimal.ZERO) > 0) {
				float monthlyPayment = mActivity.mComparisonMonthlyPayment
						.floatValue();
				monthlyPaymentText = String.format(monthlyPaymentText,
						monthlyPayment, "more");
			} else {
				monthlyPaymentText = "Your monthly payment is the same";
			}
			comparisonMonthlyPayment.setText(monthlyPaymentText);

			String comparisonDurationText = mActivity.getResources().getString(
					R.string.comparison_duration_format);
			if (mActivity.mComparisonDuration < 0) {
				int duration = Math.abs(mActivity.mComparisonDuration);
				int years = duration / 12;
				int months = duration % 12;
				comparisonDurationText = String.format(comparisonDurationText,
						years, months, "shorter");
			} else if (mActivity.mComparisonDuration > 0) {
				int years = mActivity.mComparisonDuration / 12;
				int months = mActivity.mComparisonDuration % 12;
				comparisonDurationText = String.format(comparisonDurationText,
						years, months, "longer");
			} else {
				comparisonDurationText = "Your total duration is the same";
			}
			comparisonDuration.setText(comparisonDurationText);

			String comparisonInterestText = mActivity.getResources().getString(
					R.string.comparison_interest_format);
			if (mActivity.mComparisonInterestPaid.compareTo(BigDecimal.ZERO) < 0) {
				float interestPaid = mActivity.mComparisonInterestPaid.abs()
						.floatValue();
				comparisonInterestText = String.format(comparisonInterestText,
						interestPaid, "less");
			} else if (mActivity.mComparisonInterestPaid
					.compareTo(BigDecimal.ZERO) > 0) {
				float interestPaid = mActivity.mComparisonInterestPaid
						.floatValue();
				comparisonInterestText = String.format(comparisonInterestText,
						interestPaid, "more");
			} else {
				comparisonInterestText = "You pay the same amount in interest";
			}

			comparisonInterest.setText(comparisonInterestText);

		}
	}

	public void showMortgagePickerDialog(View v) {
		Bundle bundle = new Bundle();
		bundle.putInt(CURRENT_MORTGAGE_YEAR, mMortgageYear);
		bundle.putInt(CURRENT_MORTGAGE_MONTH, mMortgageMonth);

		DatePickerFragment datePickerFragment = new DatePickerFragment();
		datePickerFragment.setArguments(bundle);
		datePickerFragment.show(getSupportFragmentManager(), "datePicker");
		datePickerFragment.onAttach(this);
	}

	public void showRefiDatePickerDialog(View v) {
		Bundle bundle = new Bundle();
		bundle.putInt(REFINANCED_MORTGAGE_YEAR, mRefinanceMortgageYear);
		bundle.putInt(REFINANCED_MORTGAGE_MONTH, mRefinanceMortgageMonth);

		DatePickerFragment datePickerFragment = new DatePickerFragment();
		datePickerFragment.setArguments(bundle);
		datePickerFragment.show(getSupportFragmentManager(), "datePicker");
		datePickerFragment.onAttach(this);
	}

	@Override
	public void setMortgageDate(int month, int year) {
		mMortgageMonth = month;
		mMortgageYear = year;

		TextView startDateView = (TextView) findViewById(R.id.mortgage_start_date);
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, month);
		String displayMonth = c.getDisplayName(Calendar.MONTH, Calendar.LONG,
				Locale.US);
		startDateView.setText(displayMonth + " " + year);
	}

	@Override
	public void setRefinanceDate(int month, int year) {
		mRefinanceMortgageMonth = month;
		mRefinanceMortgageYear = year;

		TextView startDateView = (TextView) findViewById(R.id.refinance_start_date);
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, month);
		String displayMonth = c.getDisplayName(Calendar.MONTH, Calendar.LONG,
				Locale.US);
		startDateView.setText(displayMonth + " " + year);

	}

	private static BigDecimal newBigDecimal(String arg) {
		return new BigDecimal(arg).setScale(2, RoundingMode.CEILING);
	}

	private static BigDecimal newBigDecimal(int arg) {
		return new BigDecimal(arg).setScale(2, RoundingMode.CEILING);
	}

	// private static BigDecimal newBigDecimal(BigDecimal arg) {
	// return arg.setScale(2, RoundingMode.CEILING);
	// }

	private static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
		return dividend.divide(divisor, 4, RoundingMode.CEILING);
	}

	private void calculateMortgage() {
		// i
		BigDecimal monthlyInterest = divide(divide(mMortageInterestRate, newBigDecimal(100)), newBigDecimal(12));

		// 1 + i
		BigDecimal monthlyInterestPlusOne = monthlyInterest.add(BigDecimal.ONE);
		
		// n
		BigDecimal n = newBigDecimal(12).multiply(newBigDecimal(mMortgageDuration));
		
		// (1 + i) ^ n
		BigDecimal monthlyInterestPlusOnePow = monthlyInterestPlusOne.pow(n.intValue());
		
		// dividend
		BigDecimal dividend = monthlyInterest.multiply(mMortgagePrincipal).multiply(monthlyInterestPlusOnePow);
		
		// divisor
		BigDecimal divisor = monthlyInterestPlusOnePow.subtract(BigDecimal.ONE);

		mMortgageMonthlyPayment = divide(dividend, divisor);

		mMortgageTotalInterest = mMortgageMonthlyPayment.multiply(newBigDecimal(12).multiply(newBigDecimal(mMortgageDuration))).subtract(mMortgagePrincipal);
	}

	private void calculateRefinance() {
		// Interest & Principal until refinance
		// i
		BigDecimal monthlyInterest = divide(divide(mMortageInterestRate, newBigDecimal(100)), newBigDecimal(12));

		BigDecimal balance = mMortgagePrincipal;
		BigDecimal interestPaid = BigDecimal.ZERO;

		int durationUntilRefinance = (mRefinanceMortgageYear - mMortgageYear) * 12 + (mRefinanceMortgageMonth - mMortgageMonth);
		for (int i = 0; i < durationUntilRefinance; i++) {
			BigDecimal newBalance = balance.multiply(monthlyInterest.add(BigDecimal.ONE));
			interestPaid = interestPaid.add(newBalance.subtract(balance));
			balance = newBalance.subtract(mMortgageMonthlyPayment);
		}

		// Refinance
		// i
		BigDecimal refinanceMonthlyInterest = divide(divide(mRefinanceInterestRate, newBigDecimal(100)), newBigDecimal(12));

		// 1 + i
		BigDecimal refinanceMonthlyInterestPlusOne = refinanceMonthlyInterest.add(BigDecimal.ONE);
		
		// n
		BigDecimal n = newBigDecimal(12).multiply(newBigDecimal(mRefinanceDuration));
		
		// (1 + i) ^ n
		BigDecimal refinanceMonthlyInterestPlusOnePow = refinanceMonthlyInterestPlusOne.pow(n.intValue());
		
		// dividend
		BigDecimal dividend = monthlyInterest.multiply(balance).multiply(refinanceMonthlyInterestPlusOnePow);
		
		// divisor
		BigDecimal divisor = refinanceMonthlyInterestPlusOnePow.subtract(BigDecimal.ONE);

		mRefinanceMonthlyPayment = divide(dividend, divisor);

		mRefinanceTotalInterest = mRefinanceMonthlyPayment.multiply(newBigDecimal(12).multiply(newBigDecimal(mRefinanceDuration))).subtract(balance).add(interestPaid);
	}

	private void calculateComparison() {
		mComparisonMonthlyPayment = mRefinanceMonthlyPayment.subtract(mMortgageMonthlyPayment);
		mComparisonInterestPaid = mRefinanceTotalInterest.subtract(mMortgageTotalInterest);

		int durationUntilRefinance = (mRefinanceMortgageYear - mMortgageYear) * 12 + (mRefinanceMortgageMonth - mMortgageMonth);
		mComparisonDuration = (mRefinanceDuration * 12 + durationUntilRefinance) - (mMortgageDuration * 12);
	}
	
	private abstract class AbstractRecalcFocusChangeListener implements OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				return;
			}
			
			RefiCalcActivity.this.recalc();
		}

	}
	
	private void recalc() {
		calculateMortgage();
		calculateRefinance();
		calculateComparison();

		if (mComparisonFragment != null) {
			mComparisonFragment.setComps();
		}
	}

}
