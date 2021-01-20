package com.pes.pockles.view.ui.editpock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.pes.pockles.data.Resource
import com.pes.pockles.data.storage.StorageManager
import com.pes.pockles.data.storage.StorageTask
import com.pes.pockles.data.storage.StorageTaskBitmap
import com.pes.pockles.domain.usecases.EditPockUseCase
import com.pes.pockles.model.EditedPock
import com.pes.pockles.model.Pock
import com.pes.pockles.util.livedata.AbsentLiveData
import javax.inject.Inject

class EditPockViewModel @Inject constructor(
    private var useCase: EditPockUseCase,
    private val storageManager: StorageManager
) : ViewModel() {

    private val _errorHandler = MutableLiveData<Boolean>()
    val chatEnabled = MutableLiveData<Boolean>()
    private val _pockToUpdate = MutableLiveData<EditedPock?>()
    val pockContent = MutableLiveData<String>()
    private val _pockCategory = MutableLiveData<String>()
    private var pockId: String = ""
    private var hasImages = false

    private val _image1 = MutableLiveData<StorageTaskBitmap>()
    private val _image2 = MutableLiveData<StorageTaskBitmap>()
    private val _image3 = MutableLiveData<StorageTaskBitmap>()
    private val _image4 = MutableLiveData<StorageTaskBitmap>()

    //Number of images that the pock has
    private val _nImg = MutableLiveData<Int>()
    val nImg: LiveData<Int>
        get() = _nImg

    //Image that the user wants to act on
    private val _actImg = MutableLiveData<Int>()
    val actImg: LiveData<Int>
        get() = _actImg

    //Number of images that the user could add at that moment
    val availableImgSpace: Int
        get() {
            return if (_oldImages.value != null) 4 - _oldImages.value!!.size
            else 4
        }

    private var allOldImages: List<String> = listOf()
    private val _oldImages = MutableLiveData<List<String>>()
    val oldImages: LiveData<List<String>>
        get() = _oldImages

    private val _errorSavingImages = MutableLiveData<Boolean>()
    val errorSavingImages: LiveData<Boolean>
        get() = _errorSavingImages

    val networkCallback: LiveData<Resource<Pock>?>
        get() = Transformations.switchMap(_pockToUpdate) { value: EditedPock? ->
            if (value != null) useCase.execute(pockId, value) else AbsentLiveData.create()
        }

    val errorHandlerCallback: LiveData<Boolean>
        get() = _errorHandler

    init {
        _image1.value = null
        _image2.value = null
        _image3.value = null
        _image4.value = null
        _actImg.value = 0
        _nImg.value = 0
        //chatEnabled.value = false
        _errorSavingImages.value = false
    }

    fun updatePock() {
        val category: String =
            if (_pockCategory.value == null) "General" else _pockCategory.value.toString()

        if (pockContent.value == null) {
            _errorHandler.value = true
        } else {
            if (hasImages) {
                //Store in storageTask the images saved locally
                val storageTask = StorageTask.create(storageManager)

                _image1.value?.let {
                    storageTask.addBitmap(_image1.value!!)
                }
                _image2.value?.let {
                    storageTask.addBitmap(_image2.value!!)
                }
                _image3.value?.let {
                    storageTask.addBitmap(_image3.value!!)
                }
                _image4.value?.let {
                    storageTask.addBitmap(_image4.value!!)
                }

                //Try to insert a pock when the images are upload in firebase
                storageTask.upload({
                    var newMedia = it
                    if (_oldImages.value != null && _oldImages.value!!.isNotEmpty()) newMedia =
                        _oldImages.value!! + newMedia
                    _pockToUpdate.value = EditedPock(
                        message = pockContent.value!!,
                        category = category,
                        chatAccess = chatEnabled.value!!,
                        media = newMedia
                    )
                }, {
                    _errorSavingImages.value = true
                }, "pockImages")
            } else {
                _pockToUpdate.value = EditedPock(
                    message = pockContent.value!!,
                    category = category,
                    chatAccess = chatEnabled.value!!,
                    media = _oldImages.value
                )
            }
        }
    }

    fun onSaveImage(k: Int) {
        _actImg.value = k
        if (_nImg.value == k - 1) _nImg.value = k
        hasImages = true
    }

    //Local storage of pock images
    fun setBm(bm: ByteArray, fileExtension: String = "png") {
        when (_actImg.value) {
            1 -> _image1.value = StorageTaskBitmap(bm, fileExtension)
            2 -> _image2.value = StorageTaskBitmap(bm, fileExtension)
            3 -> _image3.value = StorageTaskBitmap(bm, fileExtension)
            4 -> _image4.value = StorageTaskBitmap(bm, fileExtension)
        }
    }

    fun deleteOldImage(imgNumber: Int) {
        //Deleting image from list is done in this way to force a change on oldImages and execute the code associated to the observer
        val temp: MutableList<String> = _oldImages.value!!.toMutableList()
        temp.remove(allOldImages[imgNumber - 1])
        _oldImages.value = temp.toList()
    }

    fun fillFieldsIfEmpty(id: String, editableContent: EditedPock) {
        if (pockId == "") {
            pockId = id
            pockContent.value = editableContent.message
            _pockCategory.value = editableContent.category
            chatEnabled.value = editableContent.chatAccess
            if (editableContent.media != null) {
                allOldImages = editableContent.media
                _oldImages.value = editableContent.media.toMutableList()
            }
        }
    }

    fun setCategory(cat: String) {
        _pockCategory.value = cat
    }

    fun getCategory(): String {
        return _pockCategory.value!!
    }
}