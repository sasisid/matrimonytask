package com.matrimony.test.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Baseutils {

    companion object {
        const val CAMERA_INTENT = 101
        const val GALLERY_INTENT = 102
        var imageUploadPath = ""
        private const val TAG ="BaseUtil"

        fun openGallery(activity: Activity) {

            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            activity.startActivityForResult(intent, GALLERY_INTENT)
        }

        fun openCamera(activity: Activity) {
            val uri: Uri

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    // Ensure that there's a camera activity to handle the intent
                    takePictureIntent.resolveActivity(activity.packageManager)?.also {
                        // Create the File where the photo should go
                        val photoFile: File? = try {
                            getFileTostoreImage(activity)
                        } catch (ex: IOException) {
                            // Error occurred while creating the File
                            null
                        }
                        // Continue only if the File was successfully created
                        photoFile?.also {
                            val photoURI: Uri = FileProvider.getUriForFile(
                                activity,
                                "com.matrimony.test.fileprovider",
                                it
                            )
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            activity.startActivityForResult(takePictureIntent,
                                CAMERA_INTENT)
                        }
                    }
                }

            } else {
            val file = getFileTostoreImage(activity)

                Log.d("ertyuio", "poiunmn ")
                uri = Uri.fromFile(file)
                imageUploadPath = file.absolutePath
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                activity.startActivityForResult(takePictureIntent,
                    CAMERA_INTENT)
            }
        }

        private fun getFileTostoreImage(activity: Activity): File {
            // Create an image file name
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val storageDir: File? = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
            ).apply {
                Log.d("loliuytre", "zcvxc")

                // Save a file: path for use with ACTION_VIEW intents
                imageUploadPath = absolutePath
                Log.d(TAG, "getFileTostoreImage: " + imageUploadPath)
            }
        }



        fun getRealPathFromURI(context: Context, contentURI: Uri): String? {
            val result: String?
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(contentURI, filePathColumn, null, null, null)

            if (cursor == null) {
                result = contentURI.path
            } else {
                cursor.moveToFirst()
                val idx = cursor
                    .getColumnIndex(filePathColumn[0])
                result = cursor.getString(idx)
                cursor.close()
            }
            return result
        }



    }

    }