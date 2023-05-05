package com.example.kotlin.Admin.Screen.BusOperator

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import com.example.kotlin.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import retrofit2.awaitResponse

class AdminBusOperatorCreateActivity:AppCompatActivity() {
    lateinit var busOperatorNameEt: com.google.android.material.textfield.TextInputEditText
    lateinit var busOperatorPhoneEt: com.google.android.material.textfield.TextInputEditText
    lateinit var createBtn: Button
    lateinit var busOperatorData: BusOperatorBody
    lateinit var imgThumbnail: ImageView
    private var photoChosen = false
    private var photoUri: Uri? = null
    private var fileUpload = UploadFile()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_bus_operator_create)
        val retrofit = APIServiceImpl()
        val token = " BEARER " + UserInformation.TOKEN

        busOperatorNameEt = findViewById(R.id.adminBusOperatorNameCreateEt)
        busOperatorPhoneEt = findViewById(R.id.adminBusOperatorPhoneCreateEt)
        createBtn = findViewById(R.id.adminBusOperatorCreateBtn)
        imgThumbnail = findViewById(R.id.imageAdminBusOperatorThumbnail)

        val btnBack = findViewById<ImageButton>(R.id.adminBusOperatorCreateBackBtn)
        btnBack.setOnClickListener {
            finish()
        }

        val btnUpload = findViewById<Button>(R.id.btnAdminBusOperatorUpload)
        btnUpload.setOnClickListener {
            selectImage()
        }

        createBtn.setOnClickListener {
            var busOperatorName = busOperatorNameEt.text.toString()
            var busOperatorPhone = busOperatorPhoneEt.text.toString()


            if (!photoChosen || photoUri == null) {
                Toast.makeText(this, "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            if(busOperatorName.isEmpty() || busOperatorPhone.isEmpty()){
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

            bottomSheetView.findViewById<TextView>(R.id.txtTitle).text = "Bạn có chắc thêm nhà xe này không?"

            bottomSheetView.findViewById<TextView>(R.id.txtMessage).text = "Hành động này không thể hoàn tác. Hãy chắc chắn rằng bạn đã kiểm tra kỹ thông tin trước khi thêm nhà xe này."

            bottomSheetView.findViewById<Button>(R.id.btnBack).setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            bottomSheetView.findViewById<Button>(R.id.btnPay).text = "Tiếp tục thêm nhà xe"


            bottomSheetView.findViewById<Button>(R.id.btnPay).setOnClickListener {
                val dialog = ProgressDialog(this)
                dialog.setMessage("Đang thêm nhà xe...")
                dialog.setCancelable(false)
                dialog.show()


                try {
                    // wait for the upload task to complete
                    // turn on progress bar



                val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
                    throwable.printStackTrace()

                }


                GlobalScope.launch (Dispatchers.IO + coroutineExceptionHandler) {

                    val downloadUri = fileUpload.uploadImageToFirebase(photoUri!!).await()
                    busOperatorData = BusOperatorBody(downloadUri.toString(),busOperatorPhone,busOperatorName)
                    var response = retrofit.adminCreateBusOperator().createBusOperator(token,busOperatorData).awaitResponse()
                    Log.d("Response", "vui 1" + response.message())
                    // debug response
                    Log.d("Response", response.toString())
                    if(response.isSuccessful){
                        Log.d("Response", "vui 2")
                        val data = response.body()!!
                        Log.d("Response", data.toString())
                        launch(Dispatchers.Main) {
                            bottomSheetDialog.dismiss()
                            dialog.dismiss()
                            Toast.makeText(this@AdminBusOperatorCreateActivity, "Thêm tin tức thành công", Toast.LENGTH_SHORT).show()
                            val intent = Intent()
                            intent.putExtra("id", data.id)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    }
                    else {
                            launch(Dispatchers.Main) {
                                dialog.dismiss()
                                Toast.makeText(this@AdminBusOperatorCreateActivity, "Đã xảy ra lỗi, xin hãy kiểm tra lại kết nối", Toast.LENGTH_SHORT).show()
                            }
                    }


                }


                // Send information to list

            }catch (e: Exception){
                    dialog.dismiss()
                    Toast.makeText(this@AdminBusOperatorCreateActivity, "Đã xảy ra lỗi, xin hãy kiểm tra lại kết nối", Toast.LENGTH_SHORT).show()
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

