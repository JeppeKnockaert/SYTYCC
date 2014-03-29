package com.sytycc.sytycc.app;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Michaël on 29/03/14.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}