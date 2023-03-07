package com.thesis2.myapplication

import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.thesis2.myapplication.databinding.ActivityImageFullBinding
import kotlinx.coroutines.launch



class ImageFullActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageFullBinding
    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var externalStoragePhotoAdapter: SharedPhotoAdapter
    //private val deletedImageUri: Uri? = null
  //  private lateinit var externalStoragePhotoAdapter: SharedPhotoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageFullBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imagePath = intent.getStringExtra("path")
        val imageName = intent.getStringExtra("name")
        val imageId = intent.getLongExtra("id", 0)
        val imageWidth = intent.getIntExtra("width", 0)
        val imageHeight = intent.getIntExtra("height", 0)
        val contentUri = Uri.parse(intent.getStringExtra("contentUri"))


     /*   supportActionBar?.setTitle(imageName)
        Glide.with(this)
            .load(imagePath)
            .into(findViewById(R.id.imageView))*/

     //   var deleteButton = findViewById<FloatingActionButton>(R.id.delete_image)
      //  val txt = imagePath.toString()
        //val replaced = txt.replace("file", "content")

      //  val txt = "content://" + imagePath.toString()
      //  val replaced = txt.replace("file", "content")
       // var deletedImageUri = Uri.parse(txt)
       var deletedImageUri: Uri? = null

        intentSenderLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if(it.resultCode == RESULT_OK) {
                if(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                    lifecycleScope.launch {
                        deletePhotoFromExternalStorage(deletedImageUri ?: return@launch)
                    }
                }
                Toast.makeText(this@ImageFullActivity, "Photo deleted successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@ImageFullActivity, "Photo couldn't be deleted", Toast.LENGTH_SHORT).show()
            }
        }


         externalStoragePhotoAdapter = SharedPhotoAdapter {
            lifecycleScope.launch {
                deletePhotoFromExternalStorage(it.contentUri)
                Toast.makeText(this@ImageFullActivity, "I am here", Toast.LENGTH_SHORT).show()
                //deletedImageUri = it.contentUri
            }
        }

        setupExternalStorageRecyclerView()
        loadPhotosFromExternalStorageIntoRecyclerView()
    }

    private fun loadPhotosFromExternalStorageIntoRecyclerView() {
        lifecycleScope.launch {
            val photos = mutableListOf<SharedStoragePhoto>()

            val imagePath = intent.getStringExtra("path")
            val imageName = intent.getStringExtra("name").toString()
            val imageId = intent.getLongExtra("id", 0)
            val imageWidth = intent.getIntExtra("width", 0)
            val imageHeight = intent.getIntExtra("height", 0)
            val contentUri = Uri.parse(intent.getStringExtra("contentUri"))

            photos.add(SharedStoragePhoto(imageId, imageName, imageWidth, imageHeight,  contentUri))
            photos.toList()

            externalStoragePhotoAdapter.submitList(photos)
        }
    }
    private fun setupExternalStorageRecyclerView() = binding.rvPublicPhotos.apply {
        adapter = externalStoragePhotoAdapter
        layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)
    }
    private suspend fun deletePhotoFromExternalStorage(photoUri: Uri) {
        withContext(Dispatchers.IO) {
            try {
                contentResolver.delete(photoUri, null, null)
            } catch (e: SecurityException) {
                val intentSender = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                        MediaStore.createDeleteRequest(contentResolver, listOf(photoUri)).intentSender
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                        val recoverableSecurityException = e as? RecoverableSecurityException
                        recoverableSecurityException?.userAction?.actionIntent?.intentSender
                    }
                    else -> null
                }
                intentSender?.let { sender ->
                    intentSenderLauncher.launch(
                        IntentSenderRequest.Builder(sender).build()
                    )
                }
            }
        }
    }

}