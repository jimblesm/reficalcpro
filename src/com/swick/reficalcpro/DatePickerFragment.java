package com.swick.reficalcpro;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment implements
        OnDateSetListener {

    private MortgageDateChangeListener listener;
    private boolean isRefi;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();

        int year = bundle.getInt(RefiCalcActivity.CURRENT_MORTGAGE_YEAR, -1);
        int monthOfYear;
        if (year == -1) {
            year = bundle.getInt(RefiCalcActivity.REFINANCED_MORTGAGE_YEAR);
            monthOfYear = bundle
                    .getInt(RefiCalcActivity.REFINANCED_MORTGAGE_MONTH);
            isRefi = true;
        } else {
            monthOfYear = bundle
                    .getInt(RefiCalcActivity.CURRENT_MORTGAGE_MONTH);
        }

        int dayOfMonth = 1;

        return new DatePickerDialog(getActivity(), this, year, monthOfYear,
                dayOfMonth);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
            int dayOfMonth) {
        if (!isRefi) {
            listener.setMortgageDate(monthOfYear, year);
        } else {
            listener.setRefinanceDate(monthOfYear, year);
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (MortgageDateChangeListener) activity;
    }

    public interface MortgageDateChangeListener {
        void setMortgageDate(int month, int year);

        void setRefinanceDate(int month, int year);
    }

}