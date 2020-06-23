package com.example.android.newsapp;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_preference);

            Preference searchQuery = findPreference(getString(R.string.settings_search_key));
            bindPreferenceSummaryToValue(searchQuery);

            Preference sectionQuery = findPreference(getString(R.string.settings_sections_key));
            bindPreferenceSummaryToValue(sectionQuery);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            // The code in this method takes care of updating the displayed preference summary after it has been changed.
            String string = value.toString();

            // if the preference is an instace of a list preference, then find the index of the item selected to show in the summary.
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int preferenceIndex = listPreference.findIndexOfValue(string);
                if (preferenceIndex >= 0) {
                    CharSequence[] sectionLabels = listPreference.getEntries();
                    preference.setSummary(sectionLabels[preferenceIndex]);
                }
            }
            // Otherwise, set the string as the summary text.
            else {
                preference.setSummary(string);
            }
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }
}
