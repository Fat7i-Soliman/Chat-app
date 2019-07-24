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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UsersFragment : Fragment() {

    private lateinit var recyclerView:RecyclerView
    private lateinit var reference: DatabaseReference
    private lateinit var firebaseRecycleView:FirebaseRecyclerAdapter<User,ViewHolder>
    private lateinit var currentUser: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        currentUser= FirebaseAuth.getInstance().currentUser!!.uid
        setRecycleView(view)
    }


    fun setRecycleView(view: View){
        recyclerView = view.findViewById(R.id.users_list)

        reference = FirebaseDatabase.getInstance().reference.child("Users")
        reference.keepSynced(true)

        val options = FirebaseRecyclerOptions.Builder<User>()
            .setQuery(reference,User::class.java)
            .setLifecycleOwner(this)
            .build()

        firebaseRecycleView = object : FirebaseRecyclerAdapter<User,ViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view1 =inflater.inflate(R.layout.single_user,parent,false)
                return ViewHolder(view1)
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int, user: User) {
                val userKey = getRef(position).key

                    holder.userName.text = user.name
                    holder.userStatus.text = user.status



                    if (user.thumb_image != "default") {
                        Picasso.get().load(user.thumb_image).placeholder(R.drawable.defaultimg).into(holder.userImage)
                    }

                    holder.itemView.setOnClickListener {
                        if (userKey==currentUser){
                            findNavController().navigate(UsersFragmentDirections.actionUsersFragmentToSettingFragment())
                        }else {
                            findNavController().navigate(
                                UsersFragmentDirections.actionUsersFragmentToUserProfileFragment(
                                    userKey!!,
                                    user.name!!
                                )
                            )
                        }
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
