package com.example.howudoing


import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
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


class LoginFragment : Fragment() {

    private lateinit var userMail: TextInputLayout
    private lateinit var userPass: TextInputLayout
    private lateinit var logIn: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(activity)
        userPass = view.findViewById(R.id.enter_password)
        userMail = view.findViewById(R.id.enter_mail)
        logIn = view.findViewById(R.id.sign_in)

        logIn.setOnClickListener {
            val email = userMail.editText!!.text.toString()
            val password = userPass.editText!!.text.toString()

            if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                progressDialog.setTitle("Loading ..")
                progressDialog.setMessage("signing in ")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()
                signIn(email,password)
            }
        }
    }

    private fun signIn(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) {
                if (it.isSuccessful) {
                    progressDialog.dismiss()
                    this.findNavController().navigate(
                        R.id.action_loginFragment_to_mainFragment, null,
                        NavOptions.Builder().setPopUpTo(R.id.welcomeFragment, true).build()
                    )
                } else {
                    // If sign in fails, display a message to the user.
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), "Failed ,try again", Toast.LENGTH_SHORT).show()
                }
            }

    }
}
