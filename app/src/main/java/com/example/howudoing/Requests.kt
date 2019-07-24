package com.example.howudoing


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import com.example.howudoing.requestsAdapter.Request


class Requests : Fragment() {

    private lateinit var recyclerView:RecyclerView
    private lateinit var requestsAdapter: requestsAdapter
    private lateinit var list: MutableList<Request>
    private lateinit var refRequest :DatabaseReference
    private lateinit var currentUser: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView= view.findViewById(R.id.requests_list)
        currentUser= FirebaseAuth.getInstance().currentUser!!.uid
        refRequest= FirebaseDatabase.getInstance().reference.child("friend_req")

        list= mutableListOf()
        requestsAdapter = requestsAdapter(list,this)

        getRequests()
        recyclerView.adapter=requestsAdapter
    }

    private fun getRequests() {
        refRequest.child(currentUser).addChildEventListener(object :ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val request= p0.getValue(Request::class.java)
                if (request!!.request_type=="received"){
                    list.add(request)
                    requestsAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
    }
}





class requestsAdapter() : RecyclerView.Adapter<requestsAdapter.ViewHolder>(){

    private lateinit var userreference: DatabaseReference
    var list = mutableListOf<Request>()
    private lateinit var currentUser: String
    private lateinit var fragment: Fragment

    constructor( list: MutableList<Request>,fragment :Fragment):this(){
        this.list=list
        this.fragment = fragment
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        currentUser= FirebaseAuth.getInstance().currentUser!!.uid
        userreference= FirebaseDatabase.getInstance().reference.child("Users")
        val view1 =inflater.inflate(R.layout.single_user,parent,false)

        return ViewHolder(view1)    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Request=list.get(position)
        var name = ""

            userreference.child(item.from!!).addValueEventListener(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    name=p0.child("name").value.toString()
                    holder.userName.text="$name send a friend request "
                    val image = p0.child("thumb_image").value.toString()
                    if (image!="default"){
                        Picasso.get().load(image).placeholder(R.drawable.defaultimg).into(holder.userImage)
                    }

                    holder.userStatus.text= "open to confirm or reject"
                }

            })

            holder.itemView.setOnClickListener {
                fragment.findNavController().navigate(MainFragmentDirections.actionMainFragmentToUserProfileFragment(item.from,name))
            }


    }


    class Request(val request_type:String?,val from:String?){
        constructor():this("","")
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.username_list)
        val userStatus: TextView = itemView.findViewById(R.id.userstatus_list)
        val userImage: CircleImageView = itemView.findViewById(R.id.user_img_list)
    }
}
