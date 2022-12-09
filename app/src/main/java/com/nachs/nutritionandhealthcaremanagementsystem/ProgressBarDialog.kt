// Custom Dialog Class

package com.nachs.nutritionandhealthcaremanagementsystem

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window

class ProgressBarDialog(context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.progress_bar_dialog)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setCancelable(false)
    }
}