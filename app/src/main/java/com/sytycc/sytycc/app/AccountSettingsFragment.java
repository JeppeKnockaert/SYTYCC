package com.sytycc.sytycc.app;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Created by Michaël on 29/03/14.
 */
public class AccountSettingsFragment extends PreferenceFragment {

    @Override
     public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_account);
        PreferenceManager.setDefaultValues(AccountSettingsFragment.this.getActivity().getBaseContext(), R.xml.preferences_account, false);
    }
}