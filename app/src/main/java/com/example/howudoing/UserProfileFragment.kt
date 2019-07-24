package com.example.howudoing


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.DateFormat
import java.util.*


class UserProfileFragment : Fragment() {

    private lateinit var profile_image:CircleImageView
    private lateinit var profile_name:TextView
    private lateinit var profile_Status:TextView
    private lateinit var send_add:Button
    private lateinit var reject:Button
    private lateinit var refFriends:DatabaseReference
    private lateinit var ref :DatabaseReference
    private lateinit var currentUser:String
    private lateinit var state :String
    private lateinit var refRequest :DatabaseReference
    private lateinit var refChat :DatabaseReference

    private lateinit var args: UserProfileFragmentArgs
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         args = UserProfileFragmentArgs.fromBundle(arguments!!)
        ref =FirebaseDatabase.getInstance().reference.child("Users").child(args.userId)
        refRequest= FirebaseDatabase.getInstance().reference.child("friend_req")
        refFriends= FirebaseDatabase.getInstance().reference.child("friends")
        refChat= FirebaseDatabase.getInstance().reference


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        profile_Status=view.findViewById(R.id.profile_status)
        profile_name=view.findViewById(R.id.profile_name)
        profile_image= view.findViewById(R.id.profile_image)
        send_add=view.findViewById(R.id.add_friend)
        reject= view.findViewById(R.id.reject_friend)
        state= "not friend"

        currentUser = FirebaseAuth.getInstance().currentUser!!.uid

        ref.keepSynced(true)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                val name = p0.child("name").value.toString()
                val status = p0.child("status").value.toString()
                val image = p0.child("thumb_image").value.toString()

                profile_name.text=name
                profile_Status.text=status
                if (image != "default") {
                    Picasso.get().load(image).placeholder(R.drawable.defaultimg).into(profile_image)
                }
            }

        })



        refFriends.child(currentUser).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChild(args.userId)){
                    state= "friend"
                    send_add.text="UnFriend"
                }else{
                    checkRequest()
                }
            }

        })

        send_add.setOnClickListener {
            send_add.isEnabled=false
            if (state=="not friend"){
                sendRequest()
            }

            if (state=="req_sent"){
                cancelRequest()
            }

            if (state=="req_receive"){
                acceptRequest()
                reject.visibility= View.INVISIBLE
            }

            if (state=="friend"){
                unFriend()
            }
        }

        reject.setOnClickListener {
            cancelRequest()
            reject.visibility= View.INVISIBLE
        }
    }

    fun sendRequest(){
        val reqSend = mutableMapOf<String,String>()
        reqSend["request_type"] = "sent"
        reqSend["from"]=currentUser

        val reqReceive = mutableMapOf<String,String>()
        reqReceive["request_type"] = "received"
        reqReceive["from"]=currentUser

        refRequest.child(currentUser).child(args.userId).setValue(reqSend)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    refRequest.child(args.userId).child(currentUser).setValue(reqReceive)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(),"Request sent successfully",Toast.LENGTH_SHORT).show()
                            send_add.isEnabled=true
                            state = "req_sent"
                            send_add.text="Cancel Request"
                        }
                }else{
                    send_add.isEnabled=true
                    Toast.makeText(requireContext(),"Request not send ",Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun cancelRequest(){
        refRequest.child(currentUser).child(args.userId).removeValue().addOnCompleteListener {
            if (it.isSuccessful){
                refRequest.child(args.userId).child(currentUser).removeValue().addOnSuccessListener {
                    state="not friend"
                    send_add.isEnabled=true
                    send_add.text="Send friend Request"
                }
            }else{
                send_add.isEnabled=true
                Toast.makeText(requireContext(),"Request not canceled",Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun acceptRequest(){
        val currentDate = DateFormat.getDateTimeInstance().format(Date())
        refFriends.child(currentUser).child(args.userId).child("date").setValue(currentDate).addOnSuccessListener {
            refFriends.child(args.userId).child(currentUser).child("date").setValue(currentDate).addOnSuccessListener {
                Toast.makeText(requireContext(),"Request accepted successfully",Toast.LENGTH_SHORT).show()
                send_add.isEnabled=true
                state = "friend"
                send_add.text="UnFriend"

                refRequest.child(currentUser).child(args.userId).removeValue().addOnSuccessListener {
                    refRequest.child(args.userId).child(currentUser).removeValue().addOnSuccessListener{}
                }
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(),"Request not accepted",Toast.LENGTH_SHORT).show()
        }
    }

    fun unFriend(){
        refFriends.child(currentUser).child(args.userId).removeValue().addOnCompleteListener {
            if (it.isSuccessful){
                refFriends.child(args.userId).child(currentUser).removeValue().addOnCompleteListener {
                    Toast.makeText(requireContext(),"not friend any more",Toast.LENGTH_SHORT).show()
                    cancelRequest()
                    removeChat()
                    send_add.isEnabled=true
                    state = "not friend"
                    send_add.text="Send friend Request"
                }

            }else{
                Toast.makeText(requireContext(),"something wrong.. try later",Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun removeChat(){

        refChat.child("Chat").child(currentUser).child(args.userId).removeValue().addOnCompleteListener {
            if (it.isSuccessful){
                refChat.child("Chat").child(args.userId).child(currentUser).removeValue().addOnCompleteListener {done->
                    if (done.isSuccessful){

                    }
                }
            }
        }
    }


    fun checkRequest(){
        refRequest.child(currentUser).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChild(args.userId)){
                    val requestType= p0.child(args.userId).child("request_type").value
                    if (requestType=="sent"){
                        state = "req_sent"
                        send_add.text="Cancel Request"
                    }else if (requestType=="received"){
                        state = "req_receive"
                        reject.visibility=View.VISIBLE
                        send_add.text="Accept friend request"

                    }
                }else{
                    state="not friend"
                }
            }

        })
    }
}
