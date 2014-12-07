package com.swick.reficalcpro;

import android.view.View;
import android.view.View.OnFocusChangeListener;

abstract class AbstractRecalcFocusChangeListener implements OnFocusChangeListener {

	/**
	 * 
	 */
	private final RefiCalcActivity refiCalcActivity;

	/**
	 * @param refiCalcActivity
	 */
	AbstractRecalcFocusChangeListener(RefiCalcActivity refiCalcActivity) {
		this.refiCalcActivity = refiCalcActivity;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			return;
		}
		
		this.refiCalcActivity.recalc();
	}

}