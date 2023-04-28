package com.example.kotlin

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.awaitResponse
import java.util.concurrent.ExecutionException

class AdminBlogCreateActivity : AppCompatActivity() {
    private var photoChosen = false
    private var photoUri: Uri? = null
    private lateinit var imgThumbnail : ImageView
    private var fileUpload = UploadFile()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_blog_create)

//        val token = UserInformation.TOKEN
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJjMTE4ZjY5My04NzIyLTQ0NjEtYTc5ZC1kNzY5OTFiOTZiY2QiLCJpYXQiOjE2ODI2NDU2NzksImV4cCI6MTg2MjY0NTY3OSwidHlwZSI6ImFjY2VzcyJ9.CfPy4FMZvqM3tbNV4E3z4dy6_tkv0scMJF3ynM5Lw4I"


        imgThumbnail = findViewById(R.id.imgThumbnail)
        val retrofit = APIServiceImpl()


        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val btnUpload = findViewById<Button>(R.id.btnUpload)
        btnUpload.setOnClickListener {
            selectImage()
        }

        val btnAdd = findViewById<Button>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            if (!photoChosen || photoUri == null) {
                Toast.makeText(this, "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val title = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtTitle).text.toString()
            val content = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.edtContent).text.toString()
            if(title.isEmpty() || content.isEmpty()){
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val bottomSheetDialog = BottomSheetDialog(
                this, com.google.android.material.R.style.Theme_Design_BottomSheetDialog
            )
            val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
                R.layout.layout_payment_bottom_sheet,
                findViewById<ConstraintLayout>(R.id.bottomSheet)
            )

            bottomSheetView.findViewById<TextView>(R.id.txtTitle).text = "Bạn có chắc thêm tin tức này không?"

            bottomSheetView.findViewById<TextView>(R.id.txtMessage).text = "Hanh động này không thể hoàn tác. Hãy chắc chắn rằng bạn đã kiểm tra kỹ thông tin trước khi thêm tin tức này."

            bottomSheetView.findViewById<Button>(R.id.btnBack).setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            bottomSheetView.findViewById<Button>(R.id.btnPay).text = "Tiếp tục thêm tin tức"

            bottomSheetView.findViewById<Button>(R.id.btnPay).setOnClickListener {
                val dialog = ProgressDialog(this)
                dialog.setMessage("Đang thêm tin tức...")
                dialog.setCancelable(false)
                dialog.show()
                try {
                    GlobalScope.launch(Dispatchers.IO) {
                        // wait for the upload task to complete
                        // turn on progress bar
                        val downloadUri = fileUpload.uploadImageToFirebase(photoUri!!).await()
                        Log.d("Download URI", downloadUri.toString())
                        val blogData = BlogData(downloadUri.toString(),title, content)
                        val response =
                            retrofit.manipulateBlog(token.toString()).createBlog(blogData).awaitResponse()
                        // debug response
                        Log.d("Response 1", response.toString())
                        if (response.isSuccessful) {
                            Log.d("Response 2", response.body().toString())
                            launch(Dispatchers.Main) {
                                bottomSheetDialog.dismiss()
                                dialog.dismiss()
                                Toast.makeText(this@AdminBlogCreateActivity, "Thêm tin tức thành công", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        } else {
                            launch(Dispatchers.Main) {
                                dialog.dismiss()
                                Toast.makeText(this@AdminBlogCreateActivity, "Đã xảy ra lỗi, xin hãy kiểm tra lại kết nối", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }catch (e: Exception){
                    dialog.dismiss()
                    Toast.makeText(this@AdminBlogCreateActivity, "Đã xảy ra lỗi, xin hãy kiểm tra lại kết nối", Toast.LENGTH_SHORT).show()
                }
            }
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }
    }
    private fun selectImage(){
        // select image from local storage
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    // onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100){
            // get image from local storage
            val imageUri = data?.data
            if(imageUri != null) {
                imgThumbnail.setImageURI(imageUri)
                photoUri = imageUri
                photoChosen = true
            }
        }
    }
}