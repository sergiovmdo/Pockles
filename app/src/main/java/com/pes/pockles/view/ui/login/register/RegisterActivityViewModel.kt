package com.pes.pockles.view.ui.login.register

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.pes.pockles.R
import com.pes.pockles.model.CreateUser
import com.pes.pockles.util.livedata.Event
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class RegisterActivityViewModel @Inject constructor() : ViewModel() {

    companion object {
        const val DEFAULT_IMAGE =
            "https://firebasestorage.googleapis.com/v0/b/pockles.appspot.com/o/default_user_profile_image.png?alt=media&token=af8dd6a0-2dec-4780-9f6d-633cb9a93ba6"
        const val DEFAULT_COLOR = "#3f51b5"
        const val DEFAULT_RADIUS = 1F
    }

    val user = MutableLiveData<CreateUser>()

    private val _nextRegister = MutableLiveData<Event<Boolean>>()
    val nextRegister: LiveData<Event<Boolean>>
        get() = _nextRegister

    private val _nextRegisterError = MutableLiveData<RegisterActivityUiModel>()
    val nextRegisterError: LiveData<RegisterActivityUiModel>
        get() = _nextRegisterError

    init {
        val u = FirebaseAuth.getInstance().currentUser
        u?.let {
            user.value =
                CreateUser(
                    id = it.uid,
                    name = it.displayName,
                    birthDate = null,
                    mail = it.email!!,
                    profileImageUrl = DEFAULT_IMAGE,
                    radiusVisibility = DEFAULT_RADIUS,
                    accentColor = DEFAULT_COLOR
                )

        }
    }

    fun next(v: View) {
        val u = user.value!!
        var error = false

        if (u.name.isNullOrEmpty()) {
            _nextRegisterError.value =
                RegisterActivityUiModel(
                    key = RegisterActivityUiFields.NAME_FIELD,
                    error = R.string.field_must_not_be_empty
                )
            error = true
        }

        if (u.birthDate.isNullOrEmpty()) {
            _nextRegisterError.value =
                RegisterActivityUiModel(
                    key = RegisterActivityUiFields.BIRTH_DATE_FIELD,
                    error = R.string.field_must_not_be_empty
                )
            error = true
        } else {
            try {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                sdf.isLenient = false
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.YEAR, -13)
                val date = sdf.parse(u.birthDate!!)
                if (date != null && date > calendar.time) {
                    _nextRegisterError.value =
                        RegisterActivityUiModel(
                            key = RegisterActivityUiFields.BIRTH_DATE_FIELD,
                            error = R.string.too_young
                        )
                    error = true
                }
            } catch (e: ParseException) {
                _nextRegisterError.value =
                    RegisterActivityUiModel(
                        key = RegisterActivityUiFields.BIRTH_DATE_FIELD,
                        error = R.string.invalid_date
                    )
                error = true
            }
        }

        if (!error) {
            _nextRegister.value = Event(true)
        }

    }

    fun setVisibility(progressFloat: Float) {
        val u = user.value
        u?.let {
            it.radiusVisibility = progressFloat
            user.value = it
        }
    }

    fun setColor(color: Int) {
        val u = user.value
        u?.let {
            it.accentColor = String.format("#%06X", 0xFFFFFF and color)
            user.value = it
        }
    }

    data class RegisterActivityUiModel(
        val key: RegisterActivityUiFields,
        val error: Int
    )

    enum class RegisterActivityUiFields {
        NAME_FIELD,
        BIRTH_DATE_FIELD
    }
}