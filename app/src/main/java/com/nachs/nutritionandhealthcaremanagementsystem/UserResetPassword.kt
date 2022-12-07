package com.nachs.nutritionandhealthcaremanagementsystem

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class UserResetPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_reset_password)
    }

    fun onClickBackButton(view: View) {
        onBackPressed()
    }

    fun onClickConfirmButton(view: View) {
        val builder: CustomDialog = CustomDialog(this)
        builder.setText("Kindly check your inbox email address to reset the password.")
        builder.setCancellable(false)
        builder.setCallback { onBackPressed() }
        builder.show()
    }
}