package com.example.howudoing


import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class ChatFragment : Fragment() {

    private lateinit var sendMsg :ImageView
    private lateinit var txtMsg :EditText
    private lateinit var currentUser :String
    private lateinit var userId :String
    private lateinit var rootRef:DatabaseReference
    private lateinit var recycle :RecyclerView
    private lateinit var list :MutableList<SingleMessage>
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sendMsg = view.findViewById(R.id.send)
        txtMsg = view.findViewById(R.id.message)
        recycle= view.findViewById(R.id.message_list)
        val args = ChatFragmentArgs.fromBundle(arguments!!)
        rootRef= FirebaseDatabase.getInstance().reference

        currentUser= FirebaseAuth.getInstance().currentUser!!.uid
        userId = args.userId

        list= mutableListOf()
        messageAdapter = MessageAdapter(list)
        getMessages()

        recycle.adapter = messageAdapter
        sendMsg.setOnClickListener {
            val message= txtMsg.text.toString()
            txtMsg.text.clear()

            sendMessage(message)
        }
    }

    private fun getMessages(){
        rootRef.child("Messages").child(currentUser).child(userId).addChildEventListener(object :ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val singleMessage = p0.getValue(SingleMessage::class.java)
                list.add(singleMessage!!)
                messageAdapter.notifyDataSetChanged()
                recycle.scrollToPosition(list.lastIndex)

            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })
    }

    private fun sendMessage(message:String) {

        if (!TextUtils.isEmpty(message)){
            val messagePush = rootRef.child(currentUser).child(userId).push()
            val pushKey = messagePush.key

            val currentUserMsg = "Messages/$currentUser/$userId/$pushKey"
            val chatUserMsg = "Messages/$userId/$currentUser/$pushKey"

           val msgMap = mutableMapOf<String,Any>()
            msgMap["message"] = message
            msgMap["user"]=currentUser
            msgMap["seen"]=false
            msgMap["type"] = "text"
            msgMap["time"] = ServerValue.TIMESTAMP

            val userMap = mutableMapOf<String,Any>()
            userMap[currentUserMsg]=msgMap
            userMap[chatUserMsg]=msgMap

            rootRef.updateChildren(userMap) { p0, p1 ->
                if (p0!=null){
                    Log.i("ChatFragment:message", p0.message)
                }else{
                    createChat()
                }
            }
        }
    }

    private fun createChat(){
        rootRef.child("Chat").child(currentUser).addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (!p0.hasChild(userId)){
                    val currentUserMsg = "Chat/$currentUser/$userId"
                    val chatUserMsg = "Chat/$userId/$currentUser"
                    val chatMap = mutableMapOf<String,Any>()
                    chatMap["seen"]=false
                    chatMap["time"]=ServerValue.TIMESTAMP

                    val userChatMap = mutableMapOf<String,Any>()
                    userChatMap[currentUserMsg]=chatMap
                    userChatMap[chatUserMsg]=chatMap

                    rootRef.updateChildren(userChatMap
                    ) { p0, p1 ->
                        if (p0!=null){
                            Log.i("ChatFragment:chat", p0.message)
                        }
                    }
                }
            }
        })
    }
}
