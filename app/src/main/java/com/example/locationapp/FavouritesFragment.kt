package com.example.locationapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class FavouritesFragment : Fragment(), RecyclerViewAdapter.OnItemClickListener {

    private lateinit var recyclerView : RecyclerView
    private lateinit var locationArrayList : ArrayList<Location>
    private lateinit var myAdapter: RecyclerViewAdapter
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        EventChangeListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Preparing the recycler view for populating

        recyclerView = view.findViewById(R.id.rcvLandmarksFav)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        locationArrayList = arrayListOf()

        myAdapter = RecyclerViewAdapter(locationArrayList, this)

        recyclerView.adapter = myAdapter

        EventChangeListener()
    }

    override fun onItemClick(position: Int) {
        val clickedItem : Location = locationArrayList[position]
        val newIntent = Intent(requireActivity(), LandmarkActivity::class.java)
        newIntent.putExtra("landmark_id", clickedItem.Name.toString())
        startActivity(newIntent)
        false
        myAdapter.notifyItemChanged(position)

    }

    private fun EventChangeListener() {
        locationArrayList.clear()
        //This is getting the favourite locations of the particular user that is logged in
        db = FirebaseFirestore.getInstance()
        db.collection("favourites").whereEqualTo("UserEmail", FirebaseAuth.getInstance().currentUser!!.email.toString())
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }

}