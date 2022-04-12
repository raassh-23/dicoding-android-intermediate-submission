package com.raassh.dicodingstoryapp.misc

import android.content.Context
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun showSnackbar(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        .show()
}

fun showSnackbar(view: View, message: String, actionLabel: String, action: View.OnClickListener) {
    Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
        .setAction(actionLabel, action)
        .show()
}

fun hideSoftKeyboard(activity: FragmentActivity) {
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE)
            as InputMethodManager
    imm.hideSoftInputFromWindow(
        activity.currentFocus?.windowToken,
        InputMethodManager.HIDE_NOT_ALWAYS
    )
}

fun visibility(visible: Boolean) = if (visible) {
    View.VISIBLE
} else {
    View.INVISIBLE
}

fun ImageView.loadImage(url: String?) {
    Glide.with(this.context)
        .load(url)
        .centerCrop()
        .into(this)
}

fun String.withDateFormat(): String {
    val format = SimpleDateFormat(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        "yyyy-MM-dd'T'HH:mm:ss.SSSX"
    } else {
        // need better work around
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }, Locale.US)

    val date = format.parse(this) as Date
    return DateFormat.getDateInstance(DateFormat.FULL).format(date)
}