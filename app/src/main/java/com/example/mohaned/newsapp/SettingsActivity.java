package com.example.mohaned.newsapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Mohaned on 5/17/2018.
 */

/**
 * Step 2: Create the SettingsActivity
 We’ll need to create the new activity and call it SettingsActivity.java. Remember that in the Fragments concept earlier, we discussed that we will use a PreferenceFragment inside our SettingsActivity like this:
 */

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    /**
     * Setup OnPreferenceChangeListener Interface
     In the SettingsActivity inside the EarthquakePreferenceFragment, implement the Preference.OnPreferenceChangeListener interface. Remember that classes that implement this interface are setup to listen for any Preference changes made by the user.
     */
    public static class ArticlePreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        /**
         * Update Preference Summary in onCreate
         Next, we’ll need to update the preference summary (the UI) when the settings activity is launched in onCreate(). To do so, we’ll need to first find the preference we’re interested in and then bind the current preference value to be displayed.
         Since we are already inside the PreferenceFragment (which is inside the SettingsActivity), we can use its findPreference() method to get the Preference object. To help us with binding the value that’s in SharedPreferences to what will show up in the preference summary, we’ll create a help method and call it bindPreferenceSummaryToValue().
         */
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference orderBy = findPreference(getString(R.string.settings_search_by_key));
            bindPreferenceSummaryToValue(orderBy);
        }

        /**
         * Setup OnPreferenceChange Method
         In the SettingsActivity, set up an empty onPreferenceChange method right under the onCreate() method that we’ll be filling in. Remember that this method will be called when the user has changed a Preference, so inside of it we should add whatever action we want to happen after this change. In this case, we’ll want to update the displayed preference summary (the UI) after it’s been changed:
         */
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            // The code in this method takes care of updating the displayed preference summary after it has been changed
            String stringValue = newValue.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                } else {
                    preference.setSummary(stringValue);
                }
            }
            return true;
        }

        /**
         * Complete bindPreferenceSummaryToValue() Helper Method
         Now we need to define the bindPreferenceSummaryToValue() helper method. This method takes in a Preference as its parameter, and we use setOnPreferenceChangeListener to set the current EarthquakePreferenceFragment instance to listen for changes to the preference we pass in using:
         */
        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }
}
