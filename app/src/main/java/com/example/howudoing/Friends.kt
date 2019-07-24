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


class Friends : Fragment() {

    private lateinit var recyclerView:RecyclerView
    private lateinit var reference: DatabaseReference
    private lateinit var userreference: DatabaseReference
    private lateinit var currentUser: String


    private lateinit var firebaseRecycleView: FirebaseRecyclerAdapter<Friend, ViewHolder>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setRecycleView(view)
    }

    private fun setRecycleView(view: View){
        recyclerView = view.findViewById(R.id.friends_list)

        currentUser= FirebaseAuth.getInstance().currentUser!!.uid
        reference = FirebaseDatabase.getInstance().reference.child("friends").child(currentUser)
        userreference=FirebaseDatabase.getInstance().reference.child("Users")
        userreference.keepSynced(true)

        val options = FirebaseRecyclerOptions.Builder<Friend>()
            .setQuery(reference,Friend::class.java)
            .setLifecycleOwner(this)
            .build()

        firebaseRecycleView = object : FirebaseRecyclerAdapter<Friend,ViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view1 =inflater.inflate(R.layout.single_user,parent,false)
                return ViewHolder(view1)
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int, user: Friend) {
                val key = getRef(position).key
                var name = ""
                holder.userStatus.text=user.date

                userreference.child(key!!).addValueEventListener(object :ValueEventListener{
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

                holder.itemView.setOnClickListener {
                    findNavController().navigate(MainFragmentDirections.actionMainFragmentToChatFragment(key,name))
                }

            }

        }

        recyclerView.adapter = firebaseRecycleView
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.username_list)
        val userStatus: TextView = itemView.findViewById(R.id.userstatus_list)
        val userImage: CircleImageView = itemView.findViewById(R.id.user_img_list)
    }


}


