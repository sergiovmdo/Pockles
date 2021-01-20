package com.pes.pockles.view.ui.settings


import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceFragment
import android.view.MenuItem
import com.pes.pockles.R
import com.pes.pockles.view.ui.aboutus.AboutUsActivity
import com.pes.pockles.view.ui.base.BaseActivity


class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager
            .beginTransaction()
            .replace(android.R.id.content, SettingsFragment())
            .commit()
        // Add Top Bar
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar?.setDisplayShowHomeEnabled(true)
        setTitle(R.string.settings)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    class SettingsFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)
            findPreference("AboutUs")
                .setOnPreferenceClickListener {
                    val intent = Intent(activity, AboutUsActivity::class.java)
                    startActivity(intent)
                    return@setOnPreferenceClickListener true
                }
        }
    }

}