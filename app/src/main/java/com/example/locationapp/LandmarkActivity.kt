package com.example.locationapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.AttributeSet
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.locationapp.databinding.ActivityLandmarkBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import org.w3c.dom.Text
import java.io.File
import java.util.*

class LandmarkActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var txtTitle : TextView
    private lateinit var txtDescription : TextView
    private lateinit var btnSpeak : FloatingActionButton

    private lateinit var cbxVisited : CheckBox
    private lateinit var cbxFavourited : CheckBox

    private var tts: TextToSpeech? = null

    lateinit var binding : ActivityLandmarkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        txtTitle = findViewById<TextView>(R.id.txtTitle)
        txtDescription = findViewById<TextView>(R.id.txtDescription)
        btnSpeak = findViewById<FloatingActionButton>(R.id.btnSpeak)

        cbxVisited = findViewById<CheckBox>(R.id.cbxVisited)
        cbxFavourited = findViewById<CheckBox>(R.id.cbxFavourited)

        tts = TextToSpeech(this, this)

        val myToolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.main_toolbar)
        setSupportActionBar(myToolbar)

        var locationId = intent.getStringExtra("landmark_id")
        txtTitle.text = locationId!!.uppercase()

        Firebase.firestore.collection("landmarks").whereEqualTo("Name", locationId)
            .get().addOnCompleteListener { task ->
                val document = task.result
                for (item in document) {
                    txtDescription.text = item.getString("Description")
                }

                Firebase.firestore.collection("favourites")
                    .whereEqualTo("LandmarkName", locationId)
                    .whereEqualTo("UserEmail", FirebaseAuth.getInstance().currentUser!!.email.toString())
                    .get().addOnCompleteListener { favouritesTask ->
                        val favourites = favouritesTask.result
                        if (favourites.count() > 0) {
                            cbxFavourited.isChecked = true
                        }
                    }
            }

        Firebase.firestore.collection("landmarks").whereEqualTo("Name", locationId)
            .get().addOnCompleteListener { task ->
                val document = task.result

                for (item in document) {
                    txtDescription.text = item.getString("Description")
                }
                Firebase.firestore.collection("visited")
                    .whereEqualTo("LandmarkName", locationId)
                    .whereEqualTo("UserEmail", FirebaseAuth.getInstance().currentUser!!.email.toString())
                    .get().addOnCompleteListener { favouritesTask ->
                        val favourites = favouritesTask.result
                        if (favourites.count() > 0) {
                            cbxVisited.isChecked = true
                        }
                    }
            }

        val storageRef = FirebaseStorage.getInstance().reference.child("images/$locationId.jpg")
        val localfile = File.createTempFile("tempImage", "jpg")

        storageRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            binding.imageView.setImageBitmap(bitmap)
        }.addOnFailureListener{
            Log.e("Image View : ", "Failed to get image")
            finish()
        }

        cbxFavourited.setOnClickListener() {
            setFavourited()
        }

        cbxVisited.setOnClickListener() {
            setVisited()
        }

        btnSpeak.setOnClickListener() { view ->
            tts!!.speak(txtDescription.text.toString(), TextToSpeech.QUEUE_FLUSH, null, "")
            Log.i("Speech started : ", "Speaking now")
        }
    }

    fun setFavourited() {

        if (cbxFavourited.isChecked) {

            val favourite = hashMapOf(
                "UserEmail" to FirebaseAuth.getInstance().currentUser!!.email.toString(),
                "LandmarkName" to intent.getStringExtra("landmark_id")
            )

            Firebase.firestore.collection("favourites").document().set(favourite)
                .addOnSuccessListener() {
                    Log.i("Add favourite", "success")
                }.addOnFailureListener() {
                    Log.e("Add favourite", "failure")
                }

        } else {

            Firebase.firestore.collection("favourites")
                .whereEqualTo("UserEmail", FirebaseAuth.getInstance().currentUser!!.email.toString())
                .whereEqualTo("LandmarkName", intent.getStringExtra("landmark_id")
                ).get().addOnSuccessListener { collection ->
                    for (item in collection) {
                        Firebase.firestore.collection("favourites").document(item.id).delete()
                    }
                }
        }
    }

    fun setVisited() {

        if (cbxVisited.isChecked) {

            val favourite = hashMapOf(
                "UserEmail" to FirebaseAuth.getInstance().currentUser!!.email.toString(),
                "LandmarkName" to intent.getStringExtra("landmark_id")
            )

            Firebase.firestore.collection("visited").document().set(favourite)
                .addOnSuccessListener() {
                    Log.i("Add visit", "success")
                }.addOnFailureListener() {
                    Log.e("Add visit", "failure")
                }

        } else {

            Firebase.firestore.collection("visited")
                .whereEqualTo("UserEmail", FirebaseAuth.getInstance().currentUser!!.email.toString())
                .whereEqualTo("LandmarkName", intent.getStringExtra("landmark_id")
                ).get().addOnSuccessListener { collection ->
                    for (item in collection) {
                        Firebase.firestore.collection("visited").document(item.id).delete()
                    }
                }
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

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {

            val result = tts!!.setLanguage(Locale.UK)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS" , "The language is not supported")
            }

        } else {
            Log.e("TTS" , "Initialisation Failed")
        }

    }

    public override fun onDestroy() {
        super.onDestroy()

        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }


    }
}