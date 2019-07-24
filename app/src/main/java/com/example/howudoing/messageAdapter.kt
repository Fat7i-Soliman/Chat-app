package com.example.howudoing

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import android.widget.RelativeLayout



class MessageAdapter() : RecyclerView.Adapter<MessageViewHolder>(){

    private lateinit var currentUser:String
    private lateinit var params : RelativeLayout.LayoutParams
    var list = listOf<SingleMessage>()
    constructor( list: List<SingleMessage>):this(){
        this.list=list
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view1 =inflater.inflate(R.layout.single_msg,parent,false)

        return MessageViewHolder(view1)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val item = list.get(position)
         params =holder.message.layoutParams as RelativeLayout.LayoutParams
        currentUser=FirebaseAuth.getInstance().currentUser!!.uid
        if (item.user!! == currentUser) {
            holder.message.setTextColor(Color.BLACK)
            holder.message.setBackgroundResource(R.drawable.my_msg_style)
            holder.message.text = item.message

            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,0)
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            holder.message.layoutParams=params
        }else{
            holder.message.setBackgroundResource(R.drawable.msg_style)
            holder.message.setTextColor(Color.WHITE)
            holder.message.text = item.message

            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,0)
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            holder.message.layoutParams=params
        }
    }

}

 class SingleMessage(val message:String?,val user:String?,val seen:Boolean?,val type:String?,val time:Long?){
     constructor():this("","",null,"",null)

 }



class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val message: TextView = itemView.findViewById(R.id.single_message)
}