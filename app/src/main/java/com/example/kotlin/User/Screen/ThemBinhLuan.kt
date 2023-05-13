package com.example.kotlin.User.Screen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageButton
import android.widget.RatingBar
import com.example.kotlin.APIServiceImpl
import com.example.kotlin.DataClass.ReviewItem
import com.example.kotlin.R
import com.example.kotlin.utils.UserInformation
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ThemBinhLuan : AppCompatActivity() {
    private lateinit var rating: RatingBar
    private lateinit var review: TextInputEditText
    private lateinit var send: Button
    private lateinit var back: ImageButton

    private var boId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_them_binh_luan)

        review = findViewById(R.id.user_own_review)
        rating = findViewById(R.id.review_rating)
        send = findViewById(R.id.send_review)
        back = findViewById(R.id.them_binh_luan_back)

        boId = intent.getStringExtra("boId")
        if(boId == null)
            finish()

        rating.setOnRatingBarChangeListener { ratingBar, fl, b ->
            if(b){
                send.isEnabled = true
            }
        }

        send.setOnClickListener {
            sendReview()
        }

        back.setOnClickListener {
            finish()
        }

    }
    private fun sendReview(){
        if(review.text != null && review.text!!.isNotBlank()) {
            var item = ReviewItem(comment = review.text.toString(), rate = rating.rating.toInt())
            var callCreate = APIServiceImpl.busOperatorService().createReviews(boId!!, item, "Bearer ${UserInformation.TOKEN!!}")
            callCreate.enqueue(object : Callback<ReviewItem> {
                override fun onResponse(call: Call<ReviewItem>, response: Response<ReviewItem>) {
                    finish()
                }

                override fun onFailure(call: Call<ReviewItem>, t: Throwable) {
                    TODO("Not yet implemented")
                    finish()
                }
            })
        }
    }
}