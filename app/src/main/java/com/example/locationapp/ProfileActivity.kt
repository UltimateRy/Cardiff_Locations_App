package com.example.locationapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class ProfileActivity : AppCompatActivity(), RecyclerViewAdapter.OnItemClickListener {

    lateinit var txtFullName : TextView
    lateinit var txtEmail : TextView

    private lateinit var recyclerView : RecyclerView
    private lateinit var locationArrayList : ArrayList<Location>
    private lateinit var myAdapter: RecyclerViewAdapter
    private lateinit var emailActionButton : FloatingActionButton
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        txtFullName = findViewById<TextView>(R.id.txtFullname)
        txtEmail = findViewById<TextView>(R.id.txtEmail)
        emailActionButton = findViewById<FloatingActionButton>(R.id.btnEmail)

        var firstName : String = ""
        var lastName : String = ""

        txtEmail.text = FirebaseAuth.getInstance().currentUser!!.email.toString()

        Firebase.firestore.collection("users")
            .whereEqualTo("EmailAddress", FirebaseAuth.getInstance().currentUser!!.email.toString())
            .get().addOnSuccessListener {  collection ->
                for (item in collection) {
                    firstName = item["FirstName"].toString()
                    lastName = item["Surname"].toString()
                    txtFullName.text = "$firstName $lastName"
                }
            }

        recyclerView = this.findViewById(R.id.rcvLandmarksVisited)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        locationArrayList = arrayListOf()
        myAdapter = RecyclerViewAdapter(locationArrayList, this)
        recyclerView.adapter = myAdapter

        emailActionButton.setOnClickListener() {

            var outputString : String = ""
            for (item in locationArrayList) {
                outputString += " - " + item.Name + "\n"
            }

            val newIntent = Intent(Intent.ACTION_SENDTO)
            newIntent.setData(Uri.parse("mailto:"))
            newIntent.putExtra(Intent.EXTRA_SUBJECT, "My Visited Locations in Cardiff")
            newIntent.putExtra(Intent.EXTRA_TEXT, outputString)

            startActivity(Intent.createChooser(newIntent, "Send Email"))
        }
    }

    override fun onResume() {
        super.onResume()
        EventChangeListener()
    }

    //This method will get the landmarks that the user has checked visited to, and update the recyclerview accordingly
    private fun EventChangeListener() {
        locationArrayList.clear()
        db = FirebaseFirestore.getInstance()
        db.collection("visited").whereEqualTo("UserEmail", FirebaseAuth.getInstance().currentUser!!.email.toString())
            .get().addOnCompleteListener { task ->
                val document = task.result
                for (item in document) {
                    db.collection("landmarks").whereEqualTo("Name", item["LandmarkName"])
                        .addSnapshotListener(object : EventListener<QuerySnapshot> {
                            override fun onEvent(
                                value: QuerySnapshot?,
                                error: FirebaseFirestoreException?
                            ) {
                                if (error != null) {
                                    Log.e("Firestore Error" , error.message.toString())
                                    return
                                }
                                for (dc : DocumentChange in value?.documentChanges!!) {
                                    if (dc.type == DocumentChange.Type.ADDED) {
                                        locationArrayList.add(dc.document.toObject(Location::class.java))
                                    }
                                }
                                myAdapter.notifyDataSetChanged()
                            }
                        })
                }
                myAdapter.notifyDataSetChanged()
            }
    }

    override fun onItemClick(position: Int) {
        val clickedItem : Location = locationArrayList[position]
        val newIntent = Intent(this, LandmarkActivity::class.java)
        newIntent.putExtra("landmark_id", clickedItem.Name.toString())
        startActivity(newIntent)
        false
        myAdapter.notifyItemChanged(position)
    }
}