package com.example.howudoing


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController



class WelcomeFragment : Fragment() {

    private lateinit var signBu  :Button
    private lateinit var registBu :Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        signBu = view.findViewById(R.id.log_in_bu)
        registBu= view.findViewById(R.id.create_acc_bu)

        signBu.setOnClickListener { SignIn() }
        registBu.setOnClickListener { SignUp() }

    }

    fun SignIn(){
        this.findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
    }
    fun SignUp(){
        this.findNavController().navigate(R.id.action_welcomeFragment_to_registerFragment)
    }
}
