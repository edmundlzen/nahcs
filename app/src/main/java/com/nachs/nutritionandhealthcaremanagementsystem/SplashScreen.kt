package com.nachs.nutritionandhealthcaremanagementsystem

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        if (!internetIsConnected()) {
            val builder = CustomDialog(this)
            builder.setText("This app requires an internet connection to work properly. Please connect to the internet and try again.")
            builder.setCancellable(false)
            builder.setCallback {
                finish()
            }
            builder.show()
        }
    }

    private fun internetIsConnected(): Boolean {
        return try {
            val command = "ping -c 1 google.com"
            Runtime.getRuntime().exec(command).waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }
}