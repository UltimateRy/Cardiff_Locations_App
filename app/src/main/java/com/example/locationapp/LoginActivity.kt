package com.example.locationapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private var mAuth = FirebaseAuth.getInstance()
    private var currentUser = mAuth.currentUser

    lateinit var btnLogin : Button
    lateinit var txtEmail : TextInputEditText
    lateinit var txtPassword : TextInputEditText
    lateinit var txtLogin : TextView
    lateinit var txtGreeting : TextView
    lateinit var txtRegisterAccount : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin = findViewById<Button>(R.id.btnLogin)
        txtEmail = findViewById<TextInputEditText>(R.id.textInputEditEmail)
        txtPassword = findViewById<TextInputEditText>(R.id.textInputEditPassword)
        txtLogin = findViewById<TextView>(R.id.txtLogin)
        txtGreeting = findViewById<TextView>(R.id.txtGreeting)
        txtRegisterAccount = findViewById<TextView>(R.id.txtRegisterAccount)

        btnLogin.setOnClickListener() {
            loginClick()
        }

        txtRegisterAccount.setOnClickListener() {
            val newIntent = Intent(this, RegisterActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(newIntent)
            finish()
        }

        mAuth.signOut()
        update()
    }

    override fun onStart() {
        super.onStart()
        update()
    }

    private fun loginClick() {
        val currentUser = mAuth.currentUser
        val currentEmail = currentUser?.email

        btnLogin.isEnabled = false

        if (txtEmail.text.toString() == "") {
            txtGreeting.text = getString(R.string.validEmail)
            btnLogin.isEnabled = true
        } else if (txtPassword.text.toString() == "") {
            txtGreeting.text = getString(R.string.validPass)
            btnLogin.isEnabled = true
        } else {

            //Checking if the user can sign in
            mAuth.signInWithEmailAndPassword(
                txtEmail.text.toString(),
                txtPassword.text.toString()
            )
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        update()
                        closeKeyboard()
                    } else {
                        closeKeyboard()
                        txtGreeting.text = getString(R.string.badCredentials)
                        btnLogin.isEnabled = true
                    }
                }
        }
    }

    private fun logoutClick() {
        mAuth.signOut()
        update()
    }

    private fun update() {
        val currentUser = mAuth.currentUser
        val currentEmail = currentUser?.email

        if (currentEmail !== null) {
            //They are already logged in and we can go to the home page
            //txtGreeting.text = "Welcome " + mAuth.currentUser?.email.toString() + "!"

            txtGreeting.text = ""

            val newIntent = Intent(this, HomeActivity::class.java)
            intent.putExtra("user_id", FirebaseAuth.getInstance().currentUser!!.uid)
            intent.putExtra("email_id", FirebaseAuth.getInstance().currentUser!!.uid)
            startActivity(newIntent)
            finish()

        } else {
            //We are not logged in and we should be at the login screen
            txtGreeting.text == ""
        }
    }

    private fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}