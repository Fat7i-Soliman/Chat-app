package com.example.howudoing


import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.io.File

class SettingFragment : Fragment() {

    private lateinit var reference: DatabaseReference
    private lateinit var user: FirebaseUser
    private lateinit var uName: TextView
    private lateinit var uStatus:TextView
    private lateinit var changeStatus: Button
    private lateinit var changeImg:Button
    private lateinit var mStorageRef: StorageReference
    private lateinit var imageView: CircleImageView
    private lateinit var userId: String
    private  val GALLERY_PICK = 1
    private lateinit var dialog: ProgressDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mStorageRef = FirebaseStorage.getInstance().reference

        imageView = view.findViewById(R.id.user_image)
        changeStatus = view.findViewById(R.id.change_status)
        changeImg = view.findViewById(R.id.change_img)
        uName = view.findViewById(R.id.user_name)
        uStatus = view.findViewById(R.id.user_status)
        user = FirebaseAuth.getInstance().currentUser!!
        userId = user.uid

        reference = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        reference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val name = p0.child("name").value.toString()
                val status = p0.child("status").value.toString()
                val image = p0.child("thumb_image").value.toString()

                uName.text = name
                uStatus.text = status
                if (image != "default") {
                    Picasso.get().load(image).placeholder(R.drawable.defaultimg).into(imageView)
                }
            }

        })

        changeStatus.setOnClickListener {
            this.findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToStatusFragment(uStatus.text.toString()))
        }

        changeImg.setOnClickListener {
//            val intent = Intent()
//            intent.type="image/*"
//            intent.action=Intent.ACTION_GET_CONTENT
//
//            startActivityForResult(Intent.createChooser(intent,"Choose Image"),GALLERY_PICK)

            CropImage.activity()
                .setAspectRatio(1,1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(context!!,this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode==GALLERY_PICK&&resultCode== RESULT_OK){
//            val url = data?.data
//            CropImage.activity(url)
//                .setAspectRatio(1,1)
//                .start(requireContext(),this)
//        }

        Log.i("SettingFragment","onActivityResult")

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){


            val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
            if (resultCode== RESULT_OK ){
                Log.i("SettingFragment","result code ok")

                dialog = ProgressDialog(requireContext())
                dialog.setMessage("Loading ...")
                dialog.show()
                val urlData = result.uri

                val fileThumb = File(urlData.path)

                val compressedImage: Bitmap = Compressor(context)
                    .setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(70)
                    .compressToBitmap(fileThumb)

                val byteArray = ByteArrayOutputStream()

                compressedImage.compress(Bitmap.CompressFormat.JPEG,100,byteArray)
                val data = byteArray.toByteArray()


                val path = mStorageRef.child("profile_img").child("$userId.jpg")
                val thumb_path= mStorageRef.child("profile_img").child("thumb_img").child("$userId.jpg")

                path.putFile(urlData).addOnCompleteListener{
                    if (it.isComplete){

                        val path2 = mStorageRef.child("profile_img").child("$userId.jpg")
                        path2.downloadUrl.addOnSuccessListener{url ->

                            val image_uri = url.toString()

                            val uploadTask = thumb_path.putBytes(data)

                            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                                if (!task.isSuccessful) {
                                    task.exception?.let {
                                        throw it
                                    }
                                }
                                return@Continuation thumb_path.downloadUrl
                            }).addOnCompleteListener { thumb->
                                if (thumb.isSuccessful){
                                    val thumb_url = thumb.result.toString()
                                    val map = mutableMapOf<String,String>()
                                    map["image"] = image_uri
                                    map["thumb_image"] = thumb_url
                                    reference.updateChildren(map as Map<String, Any>).addOnSuccessListener {
                                        Toast.makeText(requireContext(), "saved", Toast.LENGTH_SHORT).show()

                                        dialog.dismiss()
                                    }

                                }else{
                                    Toast.makeText(requireContext(), "not done, try again", Toast.LENGTH_SHORT).show()

                                }
                            }

                        }.addOnFailureListener {
                            dialog.dismiss()
                            Toast.makeText(requireContext(), "not done, try again", Toast.LENGTH_SHORT).show()
                        }

                    }else{
                        Log.i("SettingFragment","failed")

                    }
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error

            }
        }else{
            Log.i("SettingFragment","CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE no")
        }

    }
}
