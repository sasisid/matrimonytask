package com.matrimony.test

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.matrimony.test.adapter.ImageViewAdapter
import com.matrimony.test.databinding.ActivityMainBinding
import com.matrimony.test.interfaces.OnClickListener
import com.matrimony.test.model.ImageDetails
import com.matrimony.test.utils.Baseutils
import com.matrimony.test.viewmodel.MainActivityViewModel
import java.io.File


class MainActivity : AppCompatActivity() {

    companion object {
        private const val CAMERA_PERMISSION_CODE = 101
        private const val STORAGE_PERMISSION_CODE = 102
        const val TAG = "MainActivity"
        var uploadFile: File? = null
        private var imageUri: Uri? = null

    }

    private lateinit var viewModel: MainActivityViewModel
    var binding: ActivityMainBinding? = null
    private var viewManager = LinearLayoutManager(this)
    var imagedata: ArrayList<ImageDetails> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        binding!!.addphoto.setOnClickListener {
            showPictureDialog()
        }
        binding!!.addimage.setOnClickListener{
            showPictureDialog()
        }
        binding!!.addimagetext.setOnClickListener{
            showPictureDialog()
        }

        initialiseAdapter()

    }

    private fun initialiseAdapter(){

        binding!!.recycler.layoutManager = viewManager
        observeData()
    }

    private fun observeData(){
        viewModel.lst.observe(this, Observer{
            Log.i("data",it.toString())
            binding!!.recycler.layoutManager = GridLayoutManager(this,2,
                GridLayoutManager.VERTICAL,false)
            binding!!.recycler.adapter= ImageViewAdapter( it, this,object : OnClickListener {
                override fun onClickItem(position: Int) {

                    MaterialAlertDialogBuilder(this@MainActivity)
                        .setTitle("Delete Photo")
                        .setMessage("Members with multiple photos get better responses.Do you still want to delete this photo?")
                        .setPositiveButton("Yes") { dialog, which ->
                            // Respond to positive button press
                            imagedata.remove(it[position])
                            viewModel.remove(it[position])

                            binding!!.recycler.adapter?.notifyItemRemoved(position)
                            if (imagedata.size == 0){
                                binding!!.addphotoCard.visibility = View.VISIBLE
                            }
                        }
                        .setNegativeButton("No") { dialog, which ->
                            // Respond to negative button press

                        }

                        .setCancelable(true)
                        .show()
                }
            })

        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "setOnActivityResult: "+requestCode+"--"+ resultCode+"=="+ data?.extras)
        when (requestCode) {
            Baseutils.GALLERY_INTENT,//gallery
            -> if (resultCode == Activity.RESULT_OK) {
                handleGallery(data)

            }
            Baseutils.CAMERA_INTENT,//camera
            -> if (resultCode == Activity.RESULT_OK) {
                handleCamera()

            }

        }


    }

    private fun handleGallery(data: Intent?) {

        if (data != null) {
            imageUri = data.data
            Baseutils.imageUploadPath = Baseutils.getRealPathFromURI(this, imageUri!!).toString()
            var imageDetails= ImageDetails(Baseutils.imageUploadPath)
            if (imagedata.size < 6){
                viewModel.add(imageDetails)
                imagedata.add(imageDetails)
                binding!!.addphotoCard.visibility = View.GONE
                binding!!.recycler.adapter?.notifyDataSetChanged()

            }else{
                Toast.makeText(this,"You Reached Your Max Limit",Toast.LENGTH_LONG).show()
            }


        }
    }

    private fun handleCamera() {
        Log.d("dfcgchjkl",Baseutils.imageUploadPath)
        var imageDetails= ImageDetails(Baseutils.imageUploadPath)

        if (imagedata.size < 6){
            viewModel.add(imageDetails)
            imagedata.add(imageDetails)
            binding!!.addphotoCard.visibility = View.GONE
            binding!!.recycler.adapter?.notifyDataSetChanged()

        }else{
            Toast.makeText(this,"You Reached Your Max Limit",Toast.LENGTH_LONG).show()
        }
        Log.d(TAG, "handleCamera: "+imagedata.size)

    }

    private fun showPictureDialog() {

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.modal_bottom_sheet_content)
        val galleryupload= dialog.findViewById<MaterialTextView>(R.id.galleryupload)
        val cameraupload= dialog.findViewById<MaterialTextView>(R.id.cameraupload)
        val whatsappupload= dialog.findViewById<MaterialTextView>(R.id.whatsapp)

        galleryupload?.setOnClickListener {
            galleryCheckPermission()
            dialog.dismiss()

        }
        cameraupload?.setOnClickListener {
            cameraCheckPermission()
            dialog.dismiss()

        }
        whatsappupload?.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data =
                Uri.parse("http://api.whatsapp.com/send?phone=+919042663799")
            startActivity(intent)
        }
        dialog.show()
    }

    private fun galleryCheckPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, STORAGE_PERMISSION_CODE)
            } else{
                Baseutils.openGallery(this);
            }
        }else{
            Baseutils.openGallery(this);
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Baseutils.openGallery(this);
                }else{
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Baseutils.openCamera(this);
                }else{
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cameraCheckPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED) &&
                (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED) &&
                (checkSelfPermission(Manifest.permission.CAMERA)==PackageManager.PERMISSION_DENIED)){
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                requestPermissions(permissions, CAMERA_PERMISSION_CODE)
            } else{
                Baseutils.openCamera(this);
            }
        }else{
            Baseutils.openCamera(this);
        }
    }


}