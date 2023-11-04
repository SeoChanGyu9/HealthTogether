package com.example.firebase_test

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.firebase_test.databinding.ActivityMain2Binding
import com.example.firebase_test.databinding.ActivityTestImageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


class TestImage : AppCompatActivity() {

    val REQUEST_FIRST = 1000
    val REQUEST_GET_IMAGE = 2000
    private var uId =  FirebaseAuth.getInstance().currentUser?.uid.toString()
    var fbStorage : FirebaseStorage? = null
    var uriPhoto : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityTestImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fbStorage = FirebaseStorage.getInstance()

        binding.addImageButton.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED -> {
                    //스토리지 읽기 권한이 허용이면 커스텀 앨범 띄워주기
                    //권한 있을 경우 : PERMISSION_GRANTED
                    //권한 없을 경우 : PERMISSION_DENIED
                    getImageFromAlbum()
                }

                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_MEDIA_IMAGES) -> {
                    //권한을 명시적으로 거부한 경우 : ture
                    //다시 묻지 않음을 선택한 경우 : false
                    //다이얼로그를 띄워 권한 팝업을 해야하는 이유 및 권한팝업을 허용하여야 접근 가능하다는 사실을 알려줌
                    //showPermissionAlertDialog()
                    showPermissionContextPopup()
                }

                else -> {
                    //권한 요청
                    requestPermissions(
                        //arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_CODE
                        arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES), REQUEST_FIRST
                    )
                }
            }
        }

/*        //이미지불러오기버튼
        binding.addImageButton.setOnClickListener {
            //권한
            when {
                ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                -> {
                    // 권한이 존재하는 경우
                    // TODO 이미지를 가져옴
                    getImageFromAlbum()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES) -> {
                    // 권한이 거부 되어 있는 경우
                    showPermissionContextPopup()
                }
                else -> {
                    // 처음 권한을 시도했을 때 띄움
                    requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES),REQUEST_FIRST)
                }
            }

        }*/

        //db업로드버튼
        binding.submitButton.setOnClickListener {
            var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            var imgFileName = "IMAGE_" + timeStamp + "_.png"
            var storageRef = fbStorage?.reference?.child("images")?.child(imgFileName)

            storageRef?.putFile(uriPhoto!!)?.addOnSuccessListener {
                Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show()
            }

        }
        binding.downButton.setOnClickListener {
            Log.d("로그3","downButton")

            val fireStore = FirebaseFirestore.getInstance()
            var storageRef = fbStorage?.reference?.child("images")?.child("IMAGE_20231101_055148_.png")
            val localFile = File.createTempFile("images","png")
            storageRef?.getFile(localFile)?.addOnSuccessListener { takeSnapshot ->

                Log.d("로그3","1 "+takeSnapshot.toString())
                Glide.with(getApplicationContext())
                    .load(File(localFile.absolutePath))
                    .into(binding.addImageView)


            }



        }

/*        binding.submitButton.setOnClickListener {
            val titleEditText = findViewById<EditText>(R.id.addTitleEditText).text.toString()
            val priceEditText = findViewById<EditText>(R.id.addPrcieEditText).text.toString()

            if( titleEditText.isNotEmpty() && priceEditText.isNotEmpty()){
                val articleDB = db.child(DB_ARTICLE)
                val articleModel = ArticleModel(uId,titleEditText,System.currentTimeMillis(),"${priceEditText}원","")
                articleDB.push().setValue(articleModel)
                finish()
            }
        }*/

    }

    //이미지불러오기의 결과에 따른 행동들
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK) {
                    Toast.makeText(this,"잘못된 접근입니다",Toast.LENGTH_SHORT).show()
                    return
                }
        when(requestCode){
            REQUEST_GET_IMAGE -> {
                val selectedImageURI : Uri? = data?.data
                if( selectedImageURI != null ) {
                    val imageView = findViewById<ImageView>(R.id.addImageView)
                    imageView.setImageURI(selectedImageURI)
                    uriPhoto = selectedImageURI
                    Log.d("로그2","REQUEST_GET_IMAGE: "+selectedImageURI.toString())
                }else {
                    Toast.makeText(this,"이미지를 가져오지 못했습니다1",Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this,"이미지를 가져오지 못했습니다2",Toast.LENGTH_SHORT).show()
            }
        }
    }

    //권한묻기팝업
    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다")
            .setMessage("전자액자에서 사진을 선택하려면 권한이 필요합니다.")
            .setPositiveButton("동의하기", {_, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),1000)
            })
            .setNegativeButton("취소하기",{ _,_ ->})
            .create()
            .show()
    }
    //앨범에서 이미지가져오기
    private fun getImageFromAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent,REQUEST_GET_IMAGE)
    }



}