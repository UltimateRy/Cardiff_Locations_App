package com.example.locationapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth


class RegisterActivity : AppCompatActivity() {

    private var mAuth = FirebaseAuth.getInstance()
    private var currentUser = mAuth.currentUser

    lateinit var btnRegister : Button
    lateinit var txtEmail : EditText
    lateinit var txtPassword : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnRegister = findViewById<Button>(R.id.btnRegister)
        txtEmail = findViewById<TextInputEditText>(R.id.textInputEditEmailRegister)
        txtPassword = findViewById<TextInputEditText>(R.id.textInputEditPasswordRegister)


        btnRegister.setOnClickListener() {



        }




    }

    private fun displayMessage(view: View, msgText : String) {
        val sb = Snackbar.make(view, msgText, Snackbar.LENGTH_SHORT)
        sb.show()
    }
}