package com.example.kotlin

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import android.view.LayoutInflater
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import com.example.kotlin.Widget.UserInformation
import com.google.android.material.bottomsheet.BottomSheetDialog

class BlogManagementDetailActivity : AppCompatActivity() {
    private lateinit var imgBlogDetail: ImageView
    private lateinit var txtTitle: TextView
    private lateinit var txtContent: TextView
    private lateinit var txtUpdateTime: TextView
    private lateinit var btnBack: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_management_detail)

        val token = UserInformation.TOKEN

        imgBlogDetail = findViewById(R.id.imgBlogDetail)
        txtTitle = findViewById(R.id.txtTitle)
        txtContent = findViewById(R.id.txtContent)
        txtUpdateTime = findViewById(R.id.txtUpdateTime)
        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }
        val blogId = intent.getStringExtra("blogId")
        val retrofit = APIServiceImpl()
        try {
            GlobalScope.launch(Dispatchers.IO) {
                val response =
                    retrofit.getBlog().getBlogById(blogId!!).awaitResponse()
                // debug response
                Log.d("Response", response.toString())
                if (response.isSuccessful) {
                    Log.d("Response", response.body().toString())
                    launch(Dispatchers.Main) {
                        val blog = response.body()
                        txtTitle.text = blog?.title
                        // replace all '&lt;' with '<'
                        val htmlContent = blog?.content?.replace("&lt;", "<")
                        txtContent.text = htmlContent?.let {
                            HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
                        }
                        txtUpdateTime.text = blog?.update_time
                        Glide.with(imgBlogDetail.context)
                            .load(blog?.thumbnail)
                            .into(imgBlogDetail)
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }

        val btnDelete = findViewById<Button>(R.id.btnDelete)
        btnDelete.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(
                this, com.google.android.material.R.style.Theme_Design_BottomSheetDialog
            )
            val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
                R.layout.layout_payment_bottom_sheet,
                findViewById<ConstraintLayout>(R.id.bottomSheet)
            )

            bottomSheetView.findViewById<TextView>(R.id.txtTitle).text = "Bạn có chắc xóa tin tức này không?"

            bottomSheetView.findViewById<TextView>(R.id.txtMessage).text = "Hành động này không thể hoàn tác. Hãy chắc chắn rằng bạn đã kiểm tra kỹ thông tin trước khi xóa tin tức này."

            bottomSheetView.findViewById<Button>(R.id.btnBack).setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            bottomSheetView.findViewById<Button>(R.id.btnPay).text = "Tiếp tục xóa tin tức"

            bottomSheetView.findViewById<Button>(R.id.btnPay).setOnClickListener {
                try {
                    GlobalScope.launch(Dispatchers.IO) {
                        val response =
                            retrofit.manipulateBlog(token.toString()).deleteBlogById(blogId!!).awaitResponse()
                        // debug response
                        Log.d("Response", response.toString())
                        if (response.isSuccessful) {
                            Log.d("Response", response.body().toString())
                            launch(Dispatchers.Main) {
                                Toast.makeText(this@BlogManagementDetailActivity, "Xóa tin tức thành công", Toast.LENGTH_SHORT).show()
                                bottomSheetDialog.dismiss()
                                finish()
                            }
                        }else{
                            launch(Dispatchers.Main) {
                                if(response.code() == 401){
                                    Toast.makeText(
                                        this@BlogManagementDetailActivity,
                                        "Phiên đăng nhập đã hết hạn.\nVui lòng đăng nhập lại.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }catch (e: Exception){
                    Toast.makeText(this@BlogManagementDetailActivity, "Đã xảy ra lỗi, xin hãy kiểm tra lại kết nối", Toast.LENGTH_SHORT).show()
                }
            }
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }
    }
}