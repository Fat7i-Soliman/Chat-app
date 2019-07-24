package com.example.howudoing


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class Chats : Fragment() {

    private lateinit var recyclerView:RecyclerView
    private lateinit var reference: DatabaseReference
    private lateinit var messageref: DatabaseReference
    private lateinit var userreference: DatabaseReference
    private lateinit var currentUser: String
    private lateinit var firebaseRecycleView: FirebaseRecyclerAdapter<Conversation,ViewHolder>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setRecycleView(view)
    }

    private fun setRecycleView(view: View){
        recyclerView = view.findViewById(R.id.chats_list)

        currentUser= FirebaseAuth.getInstance().currentUser!!.uid
        reference = FirebaseDatabase.getInstance().reference.child("Chat").child(currentUser)
        messageref= FirebaseDatabase.getInstance().reference.child("Messages").child(currentUser)
        userreference= FirebaseDatabase.getInstance().reference.child("Users")
        userreference.keepSynced(true)

        val options = FirebaseRecyclerOptions.Builder<Conversation>()
            .setQuery(reference,Conversation::class.java)
            .setLifecycleOwner(this)
            .build()

        firebaseRecycleView = object : FirebaseRecyclerAdapter<Conversation,ViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view1 =inflater.inflate(R.layout.single_user,parent,false)
                return ViewHolder(view1)
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int, user: Conversation) {
                val key = getRef(position).key
                var name = ""


                userreference.child(key!!).addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        name=p0.child("name").value.toString()
                        holder.userName.text=name

                        val image = p0.child("thumb_image").value.toString()
                        if (image!="default"){
                            Picasso.get().load(image).placeholder(R.drawable.defaultimg).into(holder.userImage)
                        }
                    }

                })
                messageref.child(key).addChildEventListener(object :ChildEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                    }

                    override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    }

                    override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                        val singleMessage = p0.getValue(SingleMessage::class.java)
                        if (singleMessage!!.user==currentUser) {
                            holder.userStatus.text = "you: ${singleMessage.message}"

                        }else{
                            holder.userStatus.text = singleMessage.message
                        }
                    }

                    override fun onChildRemoved(p0: DataSnapshot) {
                    }

                })

                holder.itemView.setOnClickListener {
                    findNavController().navigate(MainFragmentDirections.actionMainFragmentToChatFragment(key,name))
                }

            }

        }
        recyclerView.adapter = firebaseRecycleView
    }

    class Conversation(val seen:Boolean?,val time:Long?){
        constructor():this(false,null)
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.username_list)
        val userStatus: TextView = itemView.findViewById(R.id.userstatus_list)
        val userImage: CircleImageView = itemView.findViewById(R.id.user_img_list)
    }
}
