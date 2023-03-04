package com.thesis2.myapplication

class Image {
    var imagePath:String?=null
    var imageName:String?=null

    constructor(imagePath: String?, imageName: String?) {
        this.imagePath = imagePath
        this.imageName = imageName
    }

    constructor()
    {}
}