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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.*


class LocationsFragment : Fragment(), RecyclerViewAdapter.OnItemClickListener {

    private lateinit var recyclerView : RecyclerView
    private lateinit var locationArrayList : ArrayList<Location>
    private lateinit var myAdapter: RecyclerViewAdapter
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //recyclerView = findViewById(R.id.rcvLandmarks)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rcvLandmarks)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        locationArrayList = arrayListOf()

        myAdapter = RecyclerViewAdapter(locationArrayList, this)

        recyclerView.adapter = myAdapter

        EventChangeListener()
    }

    override fun onItemClick(position: Int) {
        //displayMessage(requireView(), "Item $position was clicked")
        val clickedItem : Location = locationArrayList[position]
        //clickedItem.Name = "Clicked"

        val newIntent = Intent(requireActivity(), LandmarkActivity::class.java)
        newIntent.putExtra("landmark_id", clickedItem.Name.toString())
        startActivity(newIntent)
        false

        myAdapter.notifyItemChanged(position)

    }

    private fun EventChangeListener() {

        db = FirebaseFirestore.getInstance()
        db.collection("landmarks")
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_locations, container, false)
    }

    private fun displayMessage(view: View, msgText : String) {
        val sb = Snackbar.make(view, msgText, Snackbar.LENGTH_SHORT)
        sb.show()
    }


}