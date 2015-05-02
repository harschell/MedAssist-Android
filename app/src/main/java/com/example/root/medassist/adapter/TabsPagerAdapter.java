package com.example.root.medassist.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.root.medassist.PastFragment;
import com.example.root.medassist.ProfileFragment;
import com.example.root.medassist.UpcomingFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Top Rated fragment activity
			return new PastFragment();
		case 1:
			// Games fragment activity
			return new UpcomingFragment();
		case 2:
			// Movies fragment activity
			return new ProfileFragment();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 3;
	}

}
