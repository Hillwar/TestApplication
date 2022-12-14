package olimp.stays.game

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.KeyEvent
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import olimp.stays.game.databinding.ActivityMainBinding
import com.google.firebase.ktx.BuildConfig
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import java.util.*


class MainActivity : AppCompatActivity() {
    private val APP_SETTINGS: String = "settings"
    private val APP_URL: String = "url"
    private lateinit var binding: ActivityMainBinding
    private var localUrl: String = ""
    private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig
    private lateinit var mySettings: SharedPreferences
    private val DEFAULTS: HashMap<String, Any> =
        hashMapOf(APP_URL to "")
    private val TAG = "RemoteConfig"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (isEmulator() || !isSIMInserted(applicationContext)) {
            val intent = Intent(this@MainActivity, PlugActivity::class.java)
            startActivity(intent)
            finish()
        }

        mySettings = getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE)
        if (mySettings.contains(APP_URL)) {
            localUrl = mySettings.getString(APP_URL, "")!!
        }

        val copyOfUrl = localUrl
        if (!isNetworkConnected()) {
            val text = "An internet connection is required"
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(this, text, duration)
            toast.show()
            return
        }
        if (copyOfUrl != "") {
            runWebView(copyOfUrl)
        } else {
            try {
                firebaseRemoteConfig = Firebase.remoteConfig
                val configSettings = remoteConfigSettings {
                    minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) {
                        0
                    } else {
                        3600
                    }
                }
                firebaseRemoteConfig.apply {
                    setConfigSettingsAsync(configSettings)
                    setDefaultsAsync(DEFAULTS)
                    fetchAndActivate().addOnCompleteListener {
                        Log.d(TAG, "Remote Config Fetch Complete")
                        val url: String = getUrl()
                        if (url == "") {
                            val intent = Intent(this@MainActivity, PlugActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        localUrl = url
                        editUrl(url)
                        runWebView(url)
                    }
                }

            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, ("${e.message}"), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun editUrl(url: String) {
        val editor = mySettings.edit()
        editor.putString(APP_URL, url)
        editor.apply()
    }

    private fun runWebView(copyOfUrl: String) {
        binding.webView.loadUrl(copyOfUrl)
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        val webSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.domStorageEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.databaseEnabled = true
        webSettings.setSupportZoom(false)
        webSettings.allowFileAccess = true
        webSettings.allowContentAccess = true
        webSettings.useWideViewPort = true

        binding.webView.webViewClient = MyWebViewClient()
    }

    private fun getUrl(): String = firebaseRemoteConfig.getString(APP_URL)

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && binding.webView.canGoBack()) {
            binding.webView.goBack()
            return true
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && !binding.webView.canGoBack()) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (url != null && (url.startsWith("http") || url.startsWith("https"))) {
                return false
            }

            Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                startActivity(this)
            }
            return true
        }

        override fun onReceivedError(
            view: WebView?,
            errorCode: Int,
            description: String,
            failingUrl: String?
        ) {
            Log.d(TAG, failingUrl.toString())
            Toast.makeText(this@MainActivity, ("Oh no! $description"), Toast.LENGTH_SHORT).show()
            editUrl("")
            localUrl = ""
            val intent = Intent(this@MainActivity, PlugActivity::class.java)
            startActivity(intent)
            finish()
        }

        override fun onPageFinished(webView: WebView?, url: String?) {
            CookieManager.getInstance().flush()
        }
    }

    private fun isNetworkConnected(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    private fun isEmulator(): Boolean {
        if (BuildConfig.DEBUG) return false // when developer use this build on emulator

        val phoneModel = Build.MODEL
        val buildProduct = Build.PRODUCT
        val buildHardware = Build.HARDWARE
        val brand = Build.BRAND


        var result = (Build.FINGERPRINT.startsWith("generic")
                || phoneModel.contains("google_sdk")
                || phoneModel.lowercase(Locale.getDefault()).contains("droid4x")
                || phoneModel.contains("Emulator")
                || phoneModel.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || buildHardware == "goldfish"
                || Build.BRAND.contains("google")
                || buildHardware == "vbox86"
                || buildProduct == "sdk"
                || buildProduct == "google_sdk"
                || buildProduct == "sdk_x86"
                || buildProduct == "vbox86p"
                || Build.BOARD.lowercase(Locale.getDefault()).contains("nox")
                || Build.BOOTLOADER.lowercase(Locale.getDefault()).contains("nox")
                || buildHardware.lowercase(Locale.getDefault()).contains("nox")
                || buildProduct.lowercase(Locale.getDefault()).contains("nox"))

        if (result) return true
        result = result or (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
        if (result) return true
        result = result or ("google_sdk" == buildProduct)
        return result
    }

    fun isSIMInserted(context: Context): Boolean {
        return TelephonyManager.SIM_STATE_ABSENT != (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simState
    }
}