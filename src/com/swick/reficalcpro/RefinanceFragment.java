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

public class RefinanceFragment extends Fragment {

	private RefiCalcActivity mActivity;
	private View rootView;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = (RefiCalcActivity) activity;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		updateState();
		updateSummary(mActivity.getWindow().getDecorView().findViewById(android.R.id.content));
		mActivity.recalc();
		Log.d("RefiCalcPro.MortgageFragment", "onPause");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d("RefiCalcPro.RefinanceFragment", "onResume");
	}

	void updateState() {
		final EditText refinanceInterestRateView = (EditText) rootView
				.findViewById(R.id.refinance_interest_rate);

		Editable editable = refinanceInterestRateView.getText();
		if (editable != null && editable.length() > 0) {
			mActivity.getRefinanceState().setInterestRate(newBigDecimal(editable.toString()));
		} else {
			refinanceInterestRateView.setText(mActivity.getRefinanceState().getInterestRate().toPlainString());
		}

		final EditText refinanceCosts = (EditText) rootView
				.findViewById(R.id.refinance_costs);
		editable = refinanceCosts.getText();
		if (editable != null && editable.length() > 0) {
			mActivity.getRefinanceState().setCost(newBigDecimal(editable.toString()));
		} else {
			refinanceCosts.setText(mActivity.getRefinanceState().getCost().toPlainString());
		}
		
		final EditText refinanceCashout = (EditText) rootView
				.findViewById(R.id.refinance_cash_out);
		editable = refinanceCashout.getText();
		if (editable != null && editable.length() > 0) {
			mActivity.getRefinanceState().setCashOut(newBigDecimal(editable.toString()));
		} else {
			refinanceCashout.setText(mActivity.getRefinanceState().getCashOut().toPlainString());
		}

		updateSummary(mActivity.getWindow().getDecorView().findViewById(android.R.id.content));
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.refinance_layout,
				container, false);
		this.rootView = rootView;

		// Interest
		final EditText refinanceInterestRateView = (EditText) rootView
				.findViewById(R.id.refinance_interest_rate);
		refinanceInterestRateView.setOnFocusChangeListener(new AbstractRecalcFocusChangeListener(mActivity) { 
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (!hasFocus) {
							updateRefinanceInterestRate(v);
							mActivity.clearFocus(v);
						} else {
							mActivity.setCurrentFocusedEditText(v);
						}
						super.onFocusChange(v, hasFocus);
					}
				});
		refinanceInterestRateView.setText(mActivity.getRefinanceState().getInterestRate().toString());
		refinanceInterestRateView.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
					updateRefinanceInterestRate(v);
				}
				return false;
			}
		});

		// Duration
		final Spinner refinanceSpinner = (Spinner) rootView
				.findViewById(R.id.refinance_duration);
		// Create an ArrayAdapter using the string array and a default
		// spinner layout
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
				mActivity, android.R.layout.simple_spinner_item,
				mActivity.getLoanDurations().keySet().toArray(new String[0]));
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		refinanceSpinner.setAdapter(adapter);
		refinanceSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						String loanDurations = ((TextView) view).getText().toString();
						mActivity.getRefinanceState().setDuration(mActivity.getLoanDurations().get(loanDurations));
						mActivity.recalc();
						updateSummary(mActivity.getWindow().getDecorView().findViewById(android.R.id.content));
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});

		refinanceSpinner.setSelection(1);
		// Start Date
		final TextView startDateView = (TextView) rootView
				.findViewById(R.id.refinance_start_date);
		String monthName = new DateFormatSymbols().getMonths()[mActivity.getRefinanceState().getMonth()];
		startDateView.setText(monthName + " " + mActivity.getRefinanceState().getYear());

		// Costs
		final EditText refinanceCosts = (EditText) rootView
				.findViewById(R.id.refinance_costs);
		refinanceCosts.setOnFocusChangeListener(new AbstractRecalcFocusChangeListener(mActivity) { 
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					updateRefinanceCosts(v);
					mActivity.clearFocus(v);
				} else {
					mActivity.setCurrentFocusedEditText(v);
				}
				super.onFocusChange(v, hasFocus);
			}
		});
		refinanceCosts.setText(mActivity.getRefinanceState().getCost().toString());
		refinanceCosts.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
					updateRefinanceCosts(v);
				}
				return false;
			}
		});

		// Cash Out Amount
		final EditText refinanceCashout = (EditText) rootView
				.findViewById(R.id.refinance_cash_out);
		refinanceCashout.setOnFocusChangeListener(new AbstractRecalcFocusChangeListener(mActivity) {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					updateRefinanceCashout(v);
					mActivity.clearFocus(v);
				} else {
					mActivity.setCurrentFocusedEditText(v);
				}
				super.onFocusChange(v, hasFocus);
			}
		});
		refinanceCashout.setText(mActivity.getRefinanceState().getCashOut().toString());
		refinanceCashout.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
					updateRefinanceCashout(v);
				}
				return false;
			}
		});

		updateSummary(rootView);
		return rootView;
	}

	private void updateSummary(View rootView) {
		MortgageState mortgageState = mActivity.getMortgageState();

		// Original principal
		TextView originalPrincipalView = (TextView) rootView.findViewById(R.id.mortgage_original_principal);
		originalPrincipalView.setText("$" + mortgageState.getPrincipal().setScale(2, RoundingMode.CEILING).toPlainString());

		// Original monthly payment
		TextView originalMonthlyPayment = (TextView) rootView.findViewById(R.id.mortgage_original_monthly_payment);
		originalMonthlyPayment.setText("$" + mortgageState.getMonthlyPayment().setScale(2, RoundingMode.CEILING).toPlainString());

		// Original payoff date
		String mortgageMonth = new DateFormatSymbols().getMonths()[mortgageState.getMonth()];
		TextView originalPayoffDate = (TextView) rootView.findViewById(R.id.mortgage_original_payoff_date);
		originalPayoffDate.setText(mortgageMonth + " " + (mortgageState.getYear().intValue() + mortgageState.getDuration().intValue()));

		// Interest paid to date
		TextView interestPaidToDate = (TextView) rootView.findViewById(R.id.mortgage_interest_paid_without_refinancing);
		interestPaidToDate.setText("$" + mortgageState.getTotalInterest().setScale(2, RoundingMode.CEILING));

		RefinanceState refinanceState = mActivity.getRefinanceState();

		// New principal
		TextView newPrincipal = (TextView) rootView.findViewById(R.id.refinance_new_principal);
		newPrincipal.setText("$" + refinanceState.getPrincipal().setScale(2, RoundingMode.CEILING));

		// New monthly payment
		TextView newMonthlyPayment = (TextView) rootView.findViewById(R.id.refinance_new_monthly_payment);
		newMonthlyPayment.setText("$" + refinanceState.getMonthlyPayment().setScale(2, RoundingMode.CEILING));

		// New payoff date
		String refinanceMonth = new DateFormatSymbols().getMonths()[refinanceState.getMonth()];
		TextView newPayoffDate = (TextView) rootView.findViewById(R.id.refinance_new_payoff_date);
		newPayoffDate.setText(refinanceMonth + " " + (refinanceState.getYear().intValue() + refinanceState.getDuration().intValue()));

		// Total interest paid after refinance
		TextView refinanceTotalInterestPaid = (TextView) rootView.findViewById(R.id.refinance_total_interest_paid);
		refinanceTotalInterestPaid.setText("$" + refinanceState.getTotalInterest().setScale(2, RoundingMode.CEILING));
	}

	private void updateRefinanceCashout(View v) {
		EditText tempEditView = (EditText) v;
		Editable editable = ((EditText) v).getText();
		if (editable != null && editable.length() > 0) {
			mActivity.getRefinanceState().setCashOut(newBigDecimal(editable.toString()));
		} else {
			tempEditView.setText(mActivity.getRefinanceState().getCashOut().toPlainString());
		}

		updateState();
		mActivity.recalc();
		updateSummary(mActivity.getWindow().getDecorView().findViewById(android.R.id.content));
	}

	private void updateRefinanceCosts(View v) {
		EditText tempEditView = (EditText) v;
		Editable editable = tempEditView.getText();
		if (editable != null && editable.length() > 0) {
			mActivity.getRefinanceState().setCost(newBigDecimal(editable.toString()));
		} else {
			tempEditView.setText(mActivity.getRefinanceState().getCost().toPlainString());
		}

		updateState();
		mActivity.recalc();
		updateSummary(mActivity.getWindow().getDecorView().findViewById(android.R.id.content));
	}

	private void updateRefinanceInterestRate(View v) {
		EditText tempEditView = (EditText) v;
		Editable editable = ((EditText) v).getText();
		if (editable != null && editable.length() > 0) {
			mActivity.getRefinanceState().setInterestRate(newBigDecimal(editable.toString()));
		} else {
			tempEditView.setText(mActivity.getRefinanceState().getInterestRate().toPlainString());
		}

		mActivity.recalc();
		updateSummary(mActivity.getWindow().getDecorView().findViewById(android.R.id.content));
	}

}