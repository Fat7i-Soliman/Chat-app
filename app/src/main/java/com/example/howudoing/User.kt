package com.example.howudoing

 class User{
     var name:String?=null
     var status:String?=null
     var image:String?=null
     var thumb_image:String?=null


     constructor():this("","","","")

     constructor(name: String, status: String,image :String,thumb_image :String) {
         this.name = name
         this.status = status
         this.image=image
         this.thumb_image=thumb_image
     }
}