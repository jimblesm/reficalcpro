package com.swick.reficalcpro;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class RefiCalcApplication extends Application {

	private Tracker mTracker;
	
	public synchronized Tracker getTracker() {
		if (mTracker == null) {
			mTracker = GoogleAnalytics.getInstance(this).newTracker(R.xml.google_analytics_config);
		}
		
		return mTracker;
	}
}
