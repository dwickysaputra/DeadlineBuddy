package com.polibatam.synchedule.ui.settings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.polibatam.synchedule.R
import com.polibatam.synchedule.databinding.ActivitySettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SettingsActivity"
        private const val GALERY_CODE_REQUEST = 100
    }

    private var _binding : ActivitySettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var firstNameTxt : EditText
    private lateinit var lastNameTxt : EditText
    private val settingsViewModel by viewModels<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivitySettingsBinding.inflate(layoutInflater)
        initAppBar()
        initWidget()
        getProfileData()
        setContentView(binding.root)
        setInputListener()
        setClickListener()
    }

    private fun initAppBar(){
        binding.appBarLayout.topAppBar.title = getString(R.string.settings_title)
        binding.appBarLayout.topAppBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initWidget(){
        firstNameTxt = binding.settingFirstNameTextField.editText!!
        lastNameTxt = binding.settingLastNameTextField.editText!!
    }

    private fun setClickListener(){
        binding.settingsSaveButton.setOnClickListener {
            if(checkInputValid()){
                setProfileData()
            }
        }

        binding.settingsProfilePicBtn.setOnClickListener {
            if(!checkPermission()){
                return@setOnClickListener
            }

            intent = Intent(Intent.ACTION_OPEN_DOCUMENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, GALERY_CODE_REQUEST)
        }
    }

    private fun checkPermission() : Boolean{
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            return false
        }
        return true
    }

    private fun setInputListener(){
        binding.settingLastNameTextField.editText?.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                settingsViewModel.lastName = s?.toString().toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.settingFirstNameTextField.editText?.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                settingsViewModel.firstName = s?.toString().toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun checkInputValid() : Boolean {
        if(firstNameTxt.text.isNullOrEmpty()){
            firstNameTxt.error = "Firstname is required"
            return false
        }

        if(lastNameTxt.text.isNullOrEmpty()){
            lastNameTxt.error = "Lastname is required"
            return false
        }
        return true
    }

    private fun getProfileData(){
        val sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_profile), Context.MODE_PRIVATE)
        val firstName = sharedPreferences.getString(getString(R.string.shared_pref_profile_first_name), "")
        val lastName = sharedPreferences.getString(getString(R.string.shared_pref_profile_last_name), "")
        val picture = sharedPreferences.getString(getString(R.string.shared_pref_profile_pic), null)

        if(picture != null){
            settingsViewModel.pictureUri = Uri.parse(picture)
        }
        settingsViewModel.setProfileName(firstName, lastName)

        firstNameTxt.setText(settingsViewModel.firstName)
        lastNameTxt.setText(settingsViewModel.lastName)

        if(settingsViewModel.pictureUri == null){
            binding.settingsProfilePic.setImageResource(R.drawable.ic_user_pic)
        }else {
            binding.settingsProfilePic.setImageURI(settingsViewModel.pictureUri)
        }

    }

    private fun setProfileData(){
        val sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_profile), Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(getString(R.string.shared_pref_profile_first_name), firstNameTxt.text.toString()).apply()
        sharedPreferences.edit().putString(getString(R.string.shared_pref_profile_last_name), lastNameTxt.text.toString()).apply()
        sharedPreferences.edit().putString(getString(R.string.shared_pref_profile_pic), settingsViewModel.pictureUri.toString()).apply()
        settingsViewModel.setProfileName(firstNameTxt.text.toString(), lastNameTxt.text.toString())
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imageUri = data?.data
        settingsViewModel.pictureUri = imageUri
        binding.settingsProfilePic.setImageURI(imageUri)
    }
}