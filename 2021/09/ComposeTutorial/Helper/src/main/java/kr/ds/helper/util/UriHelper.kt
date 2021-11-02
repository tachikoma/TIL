package kr.ds.helper.util

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.net.toFile
import androidx.lifecycle.Lifecycle
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.floor

class UriHelper(private val context: Context, private val lifecycle: Lifecycle) {

    private val deleteCacheFileHelper = DeleteCacheFileHelper(lifecycle)

    fun fileFrom(uri: Uri): File? {
        return try {
            val file = uri.toFile()
            deleteCacheFileHelper.add(file)
            file
        } catch (e: Exception) {
            getFile(uri)
        }
    }

    fun getSize(uri: Uri): Long {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
            ?: run {
                var size = 0L
                try {
                    size = uri.toFile().length()
                } catch (e: Exception) {
                }

                return size
            }

        val sizeIndex = cursor.getColumnIndexOrThrow(OpenableColumns.SIZE)
        cursor.moveToFirst()
        val size = cursor.getLong(sizeIndex)
        cursor.close()
        return size
    }

    private fun getFile(uri: Uri): File? {
        try {
            val destinationFile: File =
                File(context.cacheDir.path + File.separatorChar + queryName(uri))
            context.contentResolver.openInputStream(uri)?.use { ins ->
                createFileFromStream(
                    ins,
                    destinationFile
                )
                deleteCacheFileHelper.add(destinationFile)
                return destinationFile
            } ?: run {
                Timber.e("can't open InputStream from $uri")
                return null
            }
        } catch (ex: java.lang.Exception) {
            Timber.e(ex)
            return null
        }
    }

    private fun createFileFromStream(ins: InputStream, destination: File) {
        FileOutputStream(destination).use { os ->
            val buffer = ByteArray(4096)
            var length: Int
            while (ins.read(buffer).also { length = it } > 0) {
                os.write(buffer, 0, length)
            }
            os.flush()
        }
    }

    private fun queryName(uri: Uri): String {
        val returnCursor: Cursor = context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    fun bitmap(uri: Uri): Bitmap? {
        var bitmap: Bitmap? = null
        bitmap = makeBitmapFrom(uri)
        return bitmap
    }

    private fun getPowerOfTwoForSampleRatio(ratio: Double): Int {
        val k = Integer.highestOneBit(floor(ratio).toInt())
        return if (k == 0) 1 else k
    }

    private fun makeBitmapFrom(
        uri: Uri
    ): Bitmap? {
        var bitmap: Bitmap? = null

//            val bitmapOptions = BitmapFactory.Options()
//            val ratio = 1.0
//            bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio)
//
//            bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888 //

        try {
            val input = context.contentResolver.openInputStream(uri)
            input.use {
                bitmap = BitmapFactory.decodeStream(input)
            }
        } catch (e: java.lang.Exception) {
            Timber.e(e)
        }
        //            try {
//                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, it)
//            } catch (e: IOException) {
//                Timber.e(e)
//            }
        return bitmap
    }
}