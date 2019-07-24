package com.example.howudoing


import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class StatusFragment : Fragment() {

    private lateinit var statusLayout: TextInputLayout
    private lateinit var saveChange: Button
    private lateinit var reference: DatabaseReference
    private lateinit var currentuser: FirebaseUser
    private lateinit var dialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        statusLayout = view.findViewById(R.id.edit_status)
        saveChange = view.findViewById(R.id.save_change)

        val args = StatusFragmentArgs.fromBundle(arguments!!)
        val currentStatus = args.currentStatus
        statusLayout.editText!!.setText(currentStatus)

        currentuser = FirebaseAuth.getInstance().currentUser!!

        val userID = currentuser.uid
        reference = FirebaseDatabase.getInstance().reference.child("Users").child(userID)
        saveChange.setOnClickListener {
            dialog = ProgressDialog(requireContext())
            dialog.setTitle("Saving")
            dialog.setMessage("wait until saving changes..")
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
            val newStatus = statusLayout.editText!!.text.toString()
            reference.child("status").setValue(newStatus).addOnCompleteListener {
                if (it.isComplete){
                    dialog.dismiss()
                    this.findNavController().navigateUp()
                    Toast.makeText(requireContext(), "saved", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireContext(), "changes not done, try again.", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }
}
