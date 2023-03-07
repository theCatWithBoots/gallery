package com.thesis2.myapplication

import android.content.ContentUris
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File


class MainActivity : AppCompatActivity() {

    private var imageRecycler: RecyclerView?=null
    private var progressBar: ProgressBar?=null
    private var allPictures: ArrayList<Image>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageRecycler = findViewById(R.id.image_recycler)
        progressBar = findViewById(R.id.recycler_progress)


        imageRecycler?.layoutManager = GridLayoutManager(this, 3)
        imageRecycler?.setHasFixedSize(true)

        if(ContextCompat.checkSelfPermission(
                this@MainActivity,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
        )!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                101
            )
        }

        allPictures = ArrayList()

        if(allPictures!!.isEmpty()){
            progressBar?.visibility = View.VISIBLE
            //get all images from storage
            allPictures = getAllImages()

           // val currentImage= allPictures?.get(1)


            //set adapter to recycler
            imageRecycler?.adapter = ImageAdapter(this,allPictures!!)
            progressBar?.visibility = View.GONE

        }
    }

    private fun getAllImages(): ArrayList<Image>? {

        val images = ArrayList<Image>()
        val allImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        //Uri.fromFile((getOutputDirectory()))


        val projection =
            arrayOf(MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME,MediaStore.Images.Media._ID,
                MediaStore.Images.Media.WIDTH,MediaStore.Images.Media.HEIGHT)
        var cursor =
            this@MainActivity.contentResolver.query(allImageUri, projection, null, null, null)

        try {
            cursor!!.moveToFirst()
            do {
                val image = Image()
                image.imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                image.imageName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                image.idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID).toLong()
                image.widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                image.heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
                image.contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    image.idColumn!!
                )

                images.add(image)
            } while (cursor.moveToNext())
            cursor.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return images
    }


    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {mFile->
            File(mFile,resources.getString(R.string.app_name)).apply {
                mkdirs()
            }
        }

        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }
}