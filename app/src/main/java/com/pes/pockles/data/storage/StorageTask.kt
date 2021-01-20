package com.pes.pockles.data.storage

import android.graphics.Bitmap
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pes.pockles.data.Resource
import java.util.*
import kotlin.collections.ArrayList

/**
 * Creates a task for uploading multiple [Bitmap]s to the storage server.
 *
 * Usage:
 *
 * StorageTask.of(bitmap).upload(
 * { uris: List<String> -> /* files already uploaded*/},
 * {error -> /* Optional, handle errors*/},
 * "childReference")  -  Optional
 */
class StorageTask private constructor(private val manager: StorageManager) {

    companion object {
        fun of(manager: StorageManager, vararg list: StorageTaskBitmap): StorageTask {
            return StorageTask(manager).addBitmaps(*list)
        }

        fun of(manager: StorageManager, bitmap: StorageTaskBitmap): StorageTask {
            return StorageTask(manager).addBitmap(bitmap)
        }

        fun create(manager: StorageManager): StorageTask {
            return StorageTask(manager)
        }
    }

    private val bitmaps: Deque<StorageTaskBitmap>

    init {
        bitmaps = LinkedList()
    }

    fun addBitmap(bitmap: StorageTaskBitmap): StorageTask {
        bitmaps.add(bitmap)
        return this
    }

    fun addBitmaps(vararg list: StorageTaskBitmap): StorageTask {
        list.forEach { addBitmap(it) }
        return this
    }

    /**
     * Uploads the given [StorageTaskBitmap] and posts the results once all of them has been
     * uploaded
     */
    fun upload(
        success: (List<String>) -> Unit,
        failure: ((Throwable) -> Unit)? = null,
        childReference: String = "other"
    ) {
        //ensureThread()
        if (bitmaps.isEmpty()) {
            throw UnsupportedOperationException("Upload must be called with at least one bitmap to upload")
        }
        val resources: MutableList<String> = ArrayList()
        val initialSize = bitmaps.size
        do {
            val b: StorageTaskBitmap? = bitmaps.poll()
            b?.let {
                manager.uploadMedia(
                    it.data,
                    { uri ->
                        run {
                            resources.add(uri)
                            if (initialSize == resources.size) {
                                success(resources)
                            }
                        }
                    },
                    { t -> failure?.let { f -> f(t) } },
                    it.fileExtension,
                    childReference,
                    bitmaps.size.toString()
                )
            }
        } while (!bitmaps.isEmpty())
    }

    /**
     * Wraps the [StorageTask] into a [LiveData]
     */
    fun uploadAsLiveData(
        childReference: String = "other"
    ): LiveData<Resource<List<String>>> {
        val result = MutableLiveData<Resource<List<String>>>()
        result.value = Resource.Loading<Nothing>()

        upload({
            result.value = Resource.Success(it)
        }, {
            result.value = Resource.Error(it)
        }, childReference)

        return result
    }

    private fun ensureThread(msg: String = "upload() must be called outside UI thread") {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw RuntimeException(msg)
        }
    }
}

data class StorageTaskBitmap(val data: ByteArray, val fileExtension: String = "png")