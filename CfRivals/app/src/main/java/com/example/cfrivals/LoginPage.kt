package com.example.cfrivals

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.imageview.ShapeableImageView

class LoginPage : AppCompatActivity() {
    private lateinit var profileImg: ShapeableImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_login_page)

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etAge = findViewById<EditText>(R.id.etAge)
        val btnContinue = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnSave)

        profileImg = findViewById(R.id.profileImage)

        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                profileImg.setImageURI(it)
            }
        }

        profileImg.setOnClickListener {
            pickImage.launch("image/*")
        }


        btnContinue.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val age = etAge.text.toString().trim()

            if (name.isEmpty()) {
                etName.error = "Please enter your Name"
                etName.requestFocus()
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                etEmail.error = "Please enter your Email"
                etEmail.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Please enter a valid Email address"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (phone.isEmpty()) {
                etPhone.error = "Please enter your Phone Number"
                etPhone.requestFocus()
                return@setOnClickListener
            }
            if (phone.length != 10 || !Patterns.PHONE.matcher(phone).matches()) {
                etPhone.error = "Please enter a valid Phone Number"
                etPhone.requestFocus()
                return@setOnClickListener
            }

            val ageInt = age.toIntOrNull()
            if (ageInt == null || ageInt !in 1..120) {
                etAge.error = "Please enter a valid Age"
                etAge.requestFocus()
                return@setOnClickListener
            }

            prefs.edit().putBoolean("isLoggedIn", true).apply()

            val cfPrefs = getSharedPreferences("CF_PREFS", MODE_PRIVATE)
            cfPrefs.edit().clear().apply()

            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("NAME", name)
                putExtra("EMAIL", email)
                putExtra("PHONE", phone)
                putExtra("AGE", ageInt)
            }
            startActivity(intent)
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}