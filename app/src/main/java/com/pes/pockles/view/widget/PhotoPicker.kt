package com.pes.pockles.view.widget

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.*
import com.pes.pockles.R
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream

public const val TAKE_PICTURE_ID = 112
public const val GET_PICTURE_ID = 111

class PhotoPicker constructor(val activity: Activity) {

    /**
     * Creates a photo picker with the provided extraItems. It calls the callback when one of the
     * extraItems had been clicked, otherwise it handles the 2 main actions by itself
     *
     * In order to listen to the result of one of the main actions, the [Activity] must implement
     * the method [Activity.onActivityResult] and call [processResult] if they want to let the
     * [PhotoPicker] to process the result (extract the data from the payload and convert it to
     * a [ByteArray] with the correct compression format). It can be handled by the [Activity] too,
     * it is not mandatory to call [processResult]
     */
    fun createPhotoPicker(
        title: String,
        callback: ((Int) -> Unit)? = {},
        vararg extraItems: GridItem
    ) {
        val items = listOf(
            BasicGridItem(
                R.drawable.ic_icon_camera,
                activity.getString(R.string.take_photo_dialog_option)
            ),
            BasicGridItem(
                R.drawable.ic_image,
                activity.getString(R.string.select_photo_dialog_option)
            ),
            *extraItems
        )
        MaterialDialog(activity, BottomSheet()).show {
            title(text = title)
            cornerRadius(16f)
            setPeekHeight(res = R.dimen.register_menu_peek_height)
            gridItems(items) { _, index, _ ->
                run {
                    when (index) {
                        0 -> {
                            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                                takePictureIntent.resolveActivity(activity.packageManager)?.also {
                                    activity.startActivityForResult(
                                        takePictureIntent,
                                        TAKE_PICTURE_ID
                                    )
                                }
                            }
                        }
                        1 -> {
                            val photoPickerIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                            photoPickerIntent.addCategory(Intent.CATEGORY_OPENABLE)
                            photoPickerIntent.type = "image/*"
                            activity.startActivityForResult(photoPickerIntent, GET_PICTURE_ID)
                        }
                        else -> {
                            callback!!(index)
                        }
                    }
                }
            }
        }
    }

    /**
     * Processes the result obtained from the call of one of the 2 main options of [createPhotoPicker].
     *
     * This obtains the data from the payload received in the [Intent] android provides and parse it
     * into a [ByteArray] with the correct compression format.
     *
     */
    fun processResult(
        reqCode: Int,
        resultCode: Int,
        data: Intent?,
        success: (ByteArray, String) -> Unit,
        error: (String) -> Unit
    ) {
        if (reqCode == 111) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    val imageUri: Uri? = data?.data
                    val imageStream: InputStream? =
                        activity.contentResolver.openInputStream(imageUri!!)
                    val extension = if (imageUri.path?.contains("gif")!!) "gif" else "png"
                    imageStream?.readBytes()?.let { success(it, extension) }
                    imageStream?.close()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    error(activity.getString(R.string.error_selecting_image))
                }
            }
        } else if (reqCode == 112) {
            if (resultCode == Activity.RESULT_OK) {
                val imageBitmap = data?.extras?.get("data") as Bitmap
                val blob = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 0, blob)
                success(blob.toByteArray(), "png")
            }
        }
    }


}