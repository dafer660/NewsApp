package com.example.danie.newsapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener{

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.activity_settings_main);

            Preference orderBy = findPreference(getString(R.string.settings_orderby_key));
            Preference section = findPreference(getString(R.string.settings_section_key));
            Preference query = findPreference(getString(R.string.settings_query_key));

            bindPreferenceSummaryToValue(orderBy);
            bindPreferenceSummaryToValue(section);
            bindPreferenceSummaryToValue(query);
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());

            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            // The code in this method takes care of updating the displayed preference summary after it has been changed
             String stringValue = newValue.toString();
             preference.setSummary(stringValue);
             if (preference instanceof ListPreference) {
                 ListPreference listPreference = (ListPreference) preference;
                 int prefIndex = listPreference.findIndexOfValue(stringValue);
                 if (prefIndex >= 0) {
                     CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary((labels[prefIndex]));
                    }
             }
             else {
                preference.setSummary(stringValue);
             }
             return true;
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen()
                    .getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen()
                    .getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case "order_by":
                    Preference orderBy_pref = findPreference(key);
                    orderBy_pref.setSummary(sharedPreferences.getString(key, ""));
                    break;
                case "section":
                    Preference section = findPreference(key);
                    section.setSummary(sharedPreferences.getString(key, ""));
                    break;
                case "query":
                    Preference query = findPreference(key);
                    query.setSummary(sharedPreferences.getString(key, ""));
                    break;
            }
        }
    }
}
