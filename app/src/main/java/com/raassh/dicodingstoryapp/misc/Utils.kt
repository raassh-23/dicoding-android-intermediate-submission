package com.raassh.dicodingstoryapp.misc

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

val timeStamp: String = SimpleDateFormat(
    "yyyy-MM-dd",
    Locale.US
).format(System.currentTimeMillis())

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

fun ImageView.loadImage(url: String?, listener: RequestListener<Drawable>) {
    Glide.with(this.context)
        .load(url)
        .listener(listener)
        .centerCrop()
        .into(this)
}

fun String.withDateFormat(): String {
    val format = SimpleDateFormat(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            "yyyy-MM-dd'T'HH:mm:ss.SSSX"
        } else {
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        }, Locale.US
    )

    val date = format.parse(this) as Date
    return DateFormat.getDateInstance(DateFormat.FULL).format(date)
}

fun createTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
    val matrix = Matrix()

    return if (isBackCamera) {
        matrix.postRotate(90f)
        Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    } else {
        matrix.postRotate(-90f)
        matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }
}

fun uriToFile(selectedImage: Uri, context: Context): File {
    val contentResolver = context.contentResolver
    val tempFile = createTempFile(context)

    val inputStream = contentResolver.openInputStream(selectedImage) as InputStream
    val outputStream = FileOutputStream(tempFile)
    val buf = ByteArray(1024)
    var len: Int

    while (inputStream.read(buf).also { len = it } > 0) {
        outputStream.write(buf, 0, len)
    }

    outputStream.close()
    inputStream.close()

    return tempFile
}

fun reduceFileImage(file: File, step: Int = 5): File {
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int

    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= step
    } while (streamLength > 1000000)

    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}

fun Boolean?.isTrue() = this != null && this
