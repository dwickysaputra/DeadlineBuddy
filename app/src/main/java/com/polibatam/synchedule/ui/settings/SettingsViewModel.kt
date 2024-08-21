package com.polibatam.synchedule.ui.settings

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel


class SettingsViewModel : ViewModel() {
    var firstName : String = ""
    var lastName : String = ""
    var pictureUri : Uri? = null

    fun setProfileName(firstName: String?, lastName: String?){
        this.firstName = firstName!!
        this.lastName = lastName!!
    }
}