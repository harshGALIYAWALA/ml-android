package com.example.ml_android.helper

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ml_android.R
import com.example.ml_android.databinding.ActivityImageHelperBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class ImageHelperActivity : AppCompatActivity() {

    private lateinit var imageLabeler: ImageLabeler
    private val PICK_IMAGE_CODE = 1

    private val binding: ActivityImageHelperBinding by lazy {
        ActivityImageHelperBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        imageLabeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        binding.pickImgBtn.setOnClickListener{
            pickImageFromGallery()

        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            imageUri?.let {
                binding.imageView.setImageURI(it)
                analyzeImage(it)
            }
        }
    }

    private fun analyzeImage(imageUri: Uri) {
        val image = InputImage.fromFilePath(this, imageUri)
        imageLabeler.process(image)
            .addOnSuccessListener { labels ->
                val resultText = labels.joinToString("\n") { "${it.text}: ${it.confidence}" }
                binding.data.text = resultText
            }
            .addOnFailureListener { e ->
                binding.data.text = "Error: ${e.localizedMessage}"
            }
    }
}