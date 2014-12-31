package com.swick.reficalcpro;


import static com.swick.reficalcpro.Utils.newBigDecimal;

import java.math.RoundingMode;
import java.text.DateFormatSymbols;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;


public class MortgageFragment extends Fragment {

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
		mActivity.getmRefinanceFragment().updateState();
		mActivity.recalc();
		updateSummary();

		Log.d("RefiCalcPro.MortgageFragment", "onPause");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("RefiCalcPro.MortgageFragment", "onResume");
	}

	private void updateSummary() {
		TextView monthlyPaymentView = (TextView) mActivity.findViewById(R.id.mortgage_monthly_payment);
		TextView mortgagePayoffDateView = (TextView) mActivity.findViewById(R.id.mortgage_payoff_date);
		TextView totalInterestPaidView = (TextView) mActivity.findViewById(R.id.mortgage_total_interest_paid);
		String monthName = new DateFormatSymbols().getMonths()[mActivity.getMortgageState().getMonth()];

		setSummaryView(monthName, monthlyPaymentView, mortgagePayoffDateView, totalInterestPaidView);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.mortgage_layout,
				container, false);

		// Principal
		final EditText mortgageLoanAmountView = (EditText) rootView.findViewById(R.id.mortgage_loan_amount);
		
		mortgageLoanAmountView.setOnFocusChangeListener(new AbstractRecalcFocusChangeListener(mActivity) { 
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					updateMortgageLoanAmount(v);
					mActivity.clearFocus(v);
				} else {
					mActivity.setCurrentFocusedEditText(v);
				}
				super.onFocusChange(v, hasFocus);
			}
		});
		mortgageLoanAmountView.setText(mActivity.getMortgageState().getPrincipal()
				.toString());
		mortgageLoanAmountView.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
					updateMortgageLoanAmount(v);
					mActivity.clearFocus(v);
				}

				return false;
			}
		});

		// Interest
		final EditText mortgageInterestRateView = (EditText) rootView.findViewById(R.id.mortgage_interest_rate);
		mortgageInterestRateView.setOnFocusChangeListener(new AbstractRecalcFocusChangeListener(mActivity) {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					updateMortgageInterest(v);
					mActivity.clearFocus(v);
				} else {
					mActivity.setCurrentFocusedEditText(v);
				}
				super.onFocusChange(v, hasFocus);
			}
		});
		mortgageInterestRateView.setText(mActivity.getMortgageState().getInterestRate()
				.toString());
		mortgageInterestRateView.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
					updateMortgageInterest(v);
				}

				return false;
			}
		});

		// Duration
		final Spinner mortgageSpinner = (Spinner) rootView
				.findViewById(R.id.mortgage_duration);
		// Create an ArrayAdapter using the string array and a default
		// spinner layout
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
				mActivity, android.R.layout.simple_spinner_item,
				mActivity.getLoanDurations().keySet().toArray(new String[0]));

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
						mActivity.getMortgageState().setDuration(mActivity.getLoanDurations()
								.get(loanDurations));
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
		String monthName = new DateFormatSymbols().getMonths()[mActivity.getMortgageState().getMonth()];
		startDateView.setText(monthName + " " + mActivity.getMortgageState().getYear());

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
		monthlyPaymentView.setText(mActivity.getMortgageState().getMonthlyPayment().setScale(2, RoundingMode.CEILING).toPlainString());
		mortgagePayoffDateView.setText(monthName + " " + String.valueOf(Integer.valueOf(mActivity.getMortgageState().getYear()) + mActivity.getMortgageState().getDuration()));
		totalInterestPaidView.setText("$" + mActivity.getMortgageState().getTotalInterest().setScale(2, RoundingMode.CEILING).toPlainString());
	}
	
	private void updateMortgageLoanAmount(View v) {
		EditText tempEditView = (EditText) v;
		Editable editable = ((EditText) v).getText();
		if (editable != null && editable.length() > 0) {
			mActivity.getMortgageState().setPrincipal(newBigDecimal(editable.toString()));
		} else {
			tempEditView.setText(mActivity.getMortgageState().getPrincipal().toPlainString());
		}
		mActivity.recalc();
		updateSummary();
	}

	private void updateMortgageInterest(View v) {
		EditText tempEditView = (EditText) v;
		Editable editable = ((EditText) v).getText();
		if (editable != null && editable.length() > 0) {
			mActivity.getMortgageState().setInterestRate(newBigDecimal(editable.toString()));
		} else {
			tempEditView.setText(mActivity.getMortgageState().getInterestRate().toPlainString());
		}
		mActivity.recalc();
		updateSummary();
	}
	
}