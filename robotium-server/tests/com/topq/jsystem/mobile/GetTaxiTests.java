package com.topq.jsystem.mobile;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class GetTaxiTests extends ActivityInstrumentationTestCase2{
	private static final String TAG = "ANDROID CLIENT";

	private static final String TARGET_PACKAGE_ID = "com.gettaxi";
	private static final String LAUNCHER_ACTIVITY_FULL_CLASSNAME = "com.gettaxi.android.activities.login.LoadingActivity";
	private static Class launcherActivityClass;
	static {

		try {
			launcherActivityClass = Class
					.forName(LAUNCHER_ACTIVITY_FULL_CLASSNAME);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public GetTaxiTests() throws ClassNotFoundException {
		super(TARGET_PACKAGE_ID, launcherActivityClass);
	}

	private Solo solo;

	@Override
	protected void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	
	

}
