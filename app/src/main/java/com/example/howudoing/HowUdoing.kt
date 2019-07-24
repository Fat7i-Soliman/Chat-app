package com.example.howudoing

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HowUdoing : Application() {

    private lateinit var ref : DatabaseReference
    private lateinit var currentUser:FirebaseAuth
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        currentUser = FirebaseAuth.getInstance()

        val current = currentUser.currentUser?.uid
        if (current!=null){
            ref= FirebaseDatabase.getInstance().reference.child("Users").child(current)

            ref.addValueEventListener(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    ref.child("online").onDisconnect().setValue(false)

                }

            })
        }

    }
}