package com.example.locationapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val myToolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.main_toolbar)
        setSupportActionBar(myToolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_layout, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val myView = findViewById<View>(R.id.main_toolbar)
        when (item.itemId) {
            R.id.action_refresh -> {
                val sb = Snackbar.make(myView, "Refresh button clicked", Snackbar.LENGTH_LONG)
                sb.show()
                return true
            }
            R.id.action_logout -> {
                val sb = Snackbar.make(myView, "Logout button clicked", Snackbar.LENGTH_LONG)
                sb.show()

                var mAuth = FirebaseAuth.getInstance()
                mAuth.signOut()

                val newIntent = Intent(this, LoginActivity::class.java)
                startActivity(newIntent)
                finish()
                return true
            }
            R.id.action_profile -> {
                val newIntent = Intent(this, ProfileActivity::class.java)
                startActivity(newIntent)
            }
            R.id.action_about -> {
                val newIntent = Intent(this, AboutActivity::class.java)
                startActivity(newIntent)
            }
            R.id.action_settings -> {
                val newIntent = Intent(this, SettingsActivity::class.java)
                startActivity(newIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}