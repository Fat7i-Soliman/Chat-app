package com.example.howudoing


import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterFragment : Fragment() {
    private lateinit var userName: TextInputLayout
    private lateinit var userMail: TextInputLayout
    private lateinit var pass: TextInputLayout
    private lateinit var create: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(activity)
        userName = view.findViewById(R.id.user_name)
        userMail = view.findViewById(R.id.user_mail)
        pass = view.findViewById(R.id.user_phone)
        create = view.findViewById(R.id.register_bu)

        create.setOnClickListener {
            val name = userName.editText!!.text.toString()
            val email = userMail.editText!!.text.toString()
            val password = pass.editText!!.text.toString()

            if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                progressDialog.setTitle("Loading ..")
                progressDialog.setMessage("creating new account")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()
                createUser(name,email,password)
            }
        }

    }


    fun createUser(name: String, mail: String, pass: String) {
        mAuth.createUserWithEmailAndPassword(mail, pass)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {

                    val uID = mAuth.currentUser!!.uid
                    database = FirebaseDatabase.getInstance().reference.child("Users").child(uID)
                    val map = HashMap<String,String>()
                    map["name"] = name
                    map["status"] = "hey there, iam using chat app."
                    map["image"] = "default"
                    map["thumb_image"] = "default"

                    database.setValue(map).addOnCompleteListener(requireActivity()){
                        if (it.isSuccessful){
                            progressDialog.dismiss()
                            this.findNavController().navigate(R.id.action_registerFragment_to_mainFragment,null,NavOptions.Builder().setPopUpTo(R.id.welcomeFragment,true).build())

                        }else{
                            progressDialog.dismiss()
                            Toast.makeText(requireContext(),"Failed ,try again",Toast.LENGTH_SHORT).show()
                        }
                    }

                                    } else {
                    // If sign in fails, display a message to the user.
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(),"Failed ,try again",Toast.LENGTH_SHORT).show()
                }

            }
    }
}
