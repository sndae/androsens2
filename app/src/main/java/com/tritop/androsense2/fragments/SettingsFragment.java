/*
    Copyright (C) 2013-2014 Christian Schneider
    christian.d.schneider@googlemail.com
    
    This file is part of Androsens 2.

    Androsens 2 is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Androsens 2 is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Androsens 2.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.tritop.androsense2.fragments;


import com.tritop.androsense2.LicenseActivity;
import com.tritop.androsense2.R;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		/*Create a button to send me a mail*/
		Preference button = (Preference)findPreference("emaildeveloper");
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
		                @Override
		                public boolean onPreferenceClick(Preference arg) { 
		                	Intent intent = new Intent(Intent.ACTION_SEND);
						    intent.setType("plain/text");
						    intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"christian.d.schneider@googlemail.com"});
							startActivity(Intent.createChooser(intent, ""));
		                    return true;
		                }
		            });
		
		/*Create a button to send the user to the google play site of this app*/
		Preference rateButton = (Preference)findPreference("rateApp");
		rateButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
            public boolean onPreferenceClick(Preference arg) { 	
				Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
				Intent market = new Intent(Intent.ACTION_VIEW, uri);
				try {
					startActivity(market);
				} catch (ActivityNotFoundException e) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
				}
				return true;
			}
		});
		
		
		
		Preference licenseButton = (Preference)findPreference("showLicense");
		licenseButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
            public boolean onPreferenceClick(Preference arg) {
				Intent licenseActivity = new Intent(getActivity(),LicenseActivity.class);
					startActivity(licenseActivity);
				return true;
			}
		});
		
		
		
		
		
	}

}
