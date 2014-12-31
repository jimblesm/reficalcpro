package com.swick.reficalcpro;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class RefiCalcApplication extends Application {

	private Tracker mAppTracker;

	@Override
	public void onCreate() {
		GoogleAnalytics.getInstance(this).enableAutoActivityReports(this);
	}
	
	public synchronized Tracker getTracker() {
		if (mAppTracker == null) {
			mAppTracker = GoogleAnalytics.getInstance(this).newTracker(R.xml.ga_app_config);
		}

		return mAppTracker;
	}
}
