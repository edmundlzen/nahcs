// Custom Dialog Class

package com.nachs.nutritionandhealthcaremanagementsystem

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView

class CustomDialog(context: Context) : Dialog(context) {
    private var text: String? = null
    private var callback: (() -> Unit)? = null
    private var cancellable: Boolean = true

    fun setText(text: String?) {
        this.text = text
    }

    fun setCallback(callback: (() -> Unit)?) {
        this.callback = callback
    }

    fun setCancellable(cancellable: Boolean) {
        this.cancellable = cancellable
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setCancelable(cancellable)
        val dialogText = findViewById<TextView>(R.id.tvDialogText)

        dialogText.text = text
        val okButton = findViewById<Button>(R.id.btnOk)
        okButton.setOnClickListener {
            callback?.invoke()
            dismiss()
        }

        val closeButton = findViewById<ImageButton>(R.id.ibClose)
        if (cancellable) {
            closeButton.setOnClickListener {
                dismiss()
            }
        } else {
            closeButton.visibility = View.GONE
            val layoutParams =
                dialogText.layoutParams as LayoutParams
            layoutParams.topMargin = dpToPx(16)
            dialogText.layoutParams = layoutParams
        }
    }

    fun dpToPx(dp: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
}