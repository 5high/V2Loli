package com.v2ray.loli.ui

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.EditTextPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import com.v2ray.loli.AppConfig
import com.v2ray.loli.BuildConfig
import com.v2ray.loli.R
import com.v2ray.loli.extension.defaultDPreference
import com.v2ray.loli.extension.onClick
import com.v2ray.loli.util.Utils
import libv2ray.Libv2ray
import org.jetbrains.anko.act
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class SettingsActivity : BaseActivity() {
    companion object {
        //        const val PREF_BYPASS_MAINLAND = "pref_bypass_mainland"
        //        const val PREF_START_ON_BOOT = "pref_start_on_boot"
        const val PREF_PER_APP_PROXY = "pref_per_app_proxy"
        const val PREF_MUX_ENABLED = "pref_mux_enabled"
        const val PREF_SPEED_ENABLED = "pref_speed_enabled"
        const val PREF_REMOTE_DNS = "pref_remote_dns"
        const val PREF_LANCONN_PORT = "pref_lanconn_port"
//        const val PREF_SPEEDUP_DOMAIN = "pref_speedup_domain"

        const val PREF_ROUTING_MODE = "pref_routing_mode"
        const val PREF_ROUTING = "pref_routing"
        const val PREF_TG_GROUP = "pref_tg_group"
        const val PREF_VERSION = "pref_version"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
        val perAppProxy by lazy { findPreference(PREF_PER_APP_PROXY) as CheckBoxPreference }
        //        val autoRestart by lazy { findPreference(PREF_AUTO_RESTART) as CheckBoxPreference }
        val remoteDns by lazy { findPreference(PREF_REMOTE_DNS) as EditTextPreference }
        val lanconnPort by lazy { findPreference(PREF_LANCONN_PORT) as EditTextPreference }

        val routing: Preference by lazy { findPreference(PREF_ROUTING) }
        val tgGroup: Preference by lazy { findPreference(PREF_TG_GROUP) }
        val version: Preference by lazy { findPreference(PREF_VERSION) }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_settings)

            routing.onClick {
                startActivity<RoutingSettingsActivity>()
            }

            tgGroup.onClick {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("tg:resolve?domain=${AppConfig.TG_GROUP_NAME}"))
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    toast(R.string.toast_tg_app_not_found)
                }
            }

            perAppProxy.setOnPreferenceClickListener {
                startActivity<PerAppProxyActivity>()
                perAppProxy.isChecked = true
                false
            }

            remoteDns.setOnPreferenceChangeListener { preference, any ->
                remoteDns.summary = any as String
                true
            }

            lanconnPort.setOnPreferenceChangeListener { preference, any ->
                lanconnPort.summary = any as String
                true
            }

            version.summary = "${BuildConfig.VERSION_NAME} (${Libv2ray.checkVersionX()})"
        }

        override fun onStart() {
            super.onStart()

            perAppProxy.isChecked = defaultSharedPreferences.getBoolean(PREF_PER_APP_PROXY, false)
            remoteDns.summary = defaultSharedPreferences.getString(PREF_REMOTE_DNS, "")
            lanconnPort.summary = defaultSharedPreferences.getString(PREF_LANCONN_PORT, "")

            defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onStop() {
            super.onStop()
            defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
            when (key) {
//                PREF_AUTO_RESTART ->
//                    act.defaultDPreference.setPrefBoolean(key, sharedPreferences.getBoolean(key, false))

                PREF_PER_APP_PROXY ->
                    act.defaultDPreference.setPrefBoolean(key, sharedPreferences.getBoolean(key, false))
            }
        }
    }

}