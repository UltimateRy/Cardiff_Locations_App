package com.example.locationapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.locationapp.databinding.ActivityLandmarkBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class LandmarkActivity : AppCompatActivity() {

    private lateinit var txtTitle : TextView
    private lateinit var cbxVisited : CheckBox
    private lateinit var cbxFavourited : CheckBox

    lateinit var binding : ActivityLandmarkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        txtTitle = findViewById<TextView>(R.id.txtTitle)

        val myToolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.main_toolbar)
        setSupportActionBar(myToolbar)


        var locationId = intent.getStringExtra("landmark_id")

        txtTitle.text = locationId!!.uppercase()

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading")
        progressDialog.setCancelable(false)
        progressDialog.show()


        val storageRef = FirebaseStorage.getInstance().reference.child("images/$locationId.jpg")

        val localfile = File.createTempFile("tempImage", "jpg")

        storageRef.getFile(localfile).addOnSuccessListener {

            if (progressDialog.isShowing) {
                progressDialog.dismiss()
            }

            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            binding.imageView.setImageBitmap(bitmap)

        }.addOnFailureListener{

            Log.e("Image View : ", "Failed to get image")

            if (progressDialog.isShowing) {
                progressDialog.dismiss()
            }

            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_layout, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val myView = findViewById<View>(R.id.main_toolbar)
        when (item.itemId) {
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

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)

        cbxVisited = findViewById<CheckBox>(R.id.cbxVisited)
        cbxFavourited = findViewById<CheckBox>(R.id.cbxFavourited)

    }
}