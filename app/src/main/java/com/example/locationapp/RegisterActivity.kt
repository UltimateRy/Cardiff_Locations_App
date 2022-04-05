package com.example.locationapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.w3c.dom.Text


class RegisterActivity : AppCompatActivity() {

    private var mAuth = FirebaseAuth.getInstance()
    private var currentUser = mAuth.currentUser

    lateinit var btnRegister : Button
    lateinit var txtEmail : EditText
    lateinit var txtPassword : EditText
    lateinit var txtGreeting : TextView
    lateinit var txtLoginAccount : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnRegister = findViewById<Button>(R.id.btnRegister)
        txtEmail = findViewById<TextInputEditText>(R.id.textInputEditEmailRegister)
        txtPassword = findViewById<TextInputEditText>(R.id.textInputEditPasswordRegister)
        txtGreeting = findViewById<TextView>(R.id.txtGreetingRegister)
        txtLoginAccount = findViewById<TextView>(R.id.txtLoginAccount)

        btnRegister.setOnClickListener() {
            register()
        }

        txtLoginAccount.setOnClickListener() {
            val newIntent = Intent(this, LoginActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(newIntent)
        }
    }

    private fun register() {
        when {
            TextUtils.isEmpty(txtEmail.text.toString().trim { it <= ' ' }) -> {
                txtGreeting.text = "Please enter an email address"
            }

            TextUtils.isEmpty(txtPassword.text.toString().trim { it <= ' ' }) -> {
                txtGreeting.text = "Please enter a password"
            }
            else -> {
                val email: String = txtEmail.text.toString().trim { it <= ' '}
                val password : String = txtPassword.text.toString().trim {it <= ' '}

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                        OnCompleteListener<AuthResult> { task ->

                            if (task.isSuccessful) {

                                val firebaseUser : FirebaseUser = task.result!!.user!!

                                val intent =
                                    Intent(this, HomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("user_id", firebaseUser.uid)
                                intent.putExtra("email_id", email)
                                startActivity(intent)
                                finish()

                            } else {
                                txtGreeting.text = "Error: Please ensure you have entered a valid email and password"
                            }
                        }
                    )
            }
        }



    }

    private fun displayMessage(view: View, msgText : String) {
        val sb = Snackbar.make(view, msgText, Snackbar.LENGTH_SHORT)
        sb.show()
    }
}