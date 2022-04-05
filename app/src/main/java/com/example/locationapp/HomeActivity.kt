package com.example.locationapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth



class HomeActivity : AppCompatActivity() {

    private lateinit var mMap : GoogleMap

    //private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val userId = intent.getStringExtra("user_id")
        val emailId = intent.getStringExtra("email_id")


        val myToolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.main_toolbar)
        setSupportActionBar(myToolbar)

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = findViewById<ViewPager2>(R.id.pager)

        viewPager.setUserInputEnabled(false)

        val tabTitles = resources.getStringArray(R.array.tabTitles)
        viewPager.adapter = TabAdapter(this)
        TabLayoutMediator(tabLayout, viewPager)
        { tab, position ->
            when (position) {
                0 -> tab.text = tabTitles[0]
                1 -> tab.text = tabTitles[1]
                2 -> tab.text = tabTitles[2]
            }
        }.attach()
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