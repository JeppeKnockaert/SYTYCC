package com.sytycc.sytycc.app;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Roel on 30/03/14.
 */
public class NotificationSettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_notifications);
    }
}
