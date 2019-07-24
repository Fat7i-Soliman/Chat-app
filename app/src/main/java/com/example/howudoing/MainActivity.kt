package com.example.howudoing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var ref : DatabaseReference
    private lateinit var currentUser:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currentUser= FirebaseAuth.getInstance()

        ref= FirebaseDatabase.getInstance().reference.child("Users")

        val navController = this.findNavController(R.id.myNavHostFragment)
        NavigationUI.setupActionBarWithNavController(this,navController)
    }


    override fun onStart() {
        super.onStart()
        val user = currentUser.currentUser
        if (user!=null){
            ref.child(user.uid).child("online").setValue(true)

        }
    }

    override fun onStop() {
        super.onStop()
        val user = currentUser.currentUser
        if (user!=null){
            ref.child(user.uid).child("online").setValue(false)

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return if (navController.currentDestination!!.id != R.id.welcomeFragment){
            navController.navigateUp()
        }else{
            false
        }
    }
}
