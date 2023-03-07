package com.thesis2.myapplication

import android.net.Uri

class Image {
     var contentUri: Uri ?=null
    var heightColumn: Int ?=null
    var widthColumn: Int ?=null
    var idColumn: Long ?=null
    var imagePath:String?=null
    var imageName:String?=null

    constructor(imagePath: String?, imageName: String?) {
        this.imagePath = imagePath
        this.imageName = imageName
    }

    constructor()
    {}
}