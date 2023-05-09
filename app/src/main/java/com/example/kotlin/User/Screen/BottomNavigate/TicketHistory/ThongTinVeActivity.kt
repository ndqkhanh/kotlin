package com.example.kotlin.User.Screen.BottomNavigate.TicketHistory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.example.kotlin.R
import com.example.kotlin.UserInformation
import com.example.kotlin.jsonConvert.History
import java.text.DecimalFormat

class ThongTinVeAcivity : AppCompatActivity() {
    private lateinit var tuyen: TextView
    private lateinit var nhaXe: TextView
    private lateinit var chuyen: TextView
    private lateinit var loaiXe: TextView
    private lateinit var giaVe: TextView
    private lateinit var tenDiemDon: TextView
    private lateinit var diaChiDiemDon: TextView
    private lateinit var tenDiemTra: TextView
    private lateinit var diaChiDiemTra: TextView
    private lateinit var thoiGianDuKienDon: TextView
    private lateinit var thoiGianDuKienTra: TextView
    private lateinit var tenKH: TextView
    private lateinit var sdtKH: TextView
    private lateinit var emailKH: TextView
    private lateinit var note: TextView
    private lateinit var back: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thong_tin_ve)
        val itemHistory = intent.getSerializableExtra("item") as History

        tuyen = findViewById(R.id.txtTuyen)
        nhaXe = findViewById(R.id.txtNhaXe)
        chuyen = findViewById(R.id.txtChuyen)
        loaiXe = findViewById(R.id.txtLoaiXe)
        giaVe = findViewById(R.id.txtGiaVe)
        tenDiemDon = findViewById(R.id.txtPickUpName)
        diaChiDiemDon = findViewById(R.id.txtPickUpLocation)
        tenDiemTra = findViewById(R.id.txtDropDownName)
        diaChiDiemTra = findViewById(R.id.txtDropDownLocation)
        thoiGianDuKienDon = findViewById(R.id.txtPickUpTime)
        thoiGianDuKienTra = findViewById(R.id.txtDropDownTime)
        tenKH = findViewById(R.id.txtHoTen)
        sdtKH = findViewById(R.id.txtDienThoai)
        emailKH = findViewById(R.id.txtEmail)
        note = findViewById(R.id.txtNote)
        back = findViewById(R.id.back)

        tuyen.text = itemHistory.tinh_don + " - " + itemHistory.tinh_tra
        nhaXe.text = itemHistory.ten_nha_xe
        chuyen.text = itemHistory.start_time + " • " + itemHistory.start_date
        loaiXe.text = if (itemHistory.type == 0) "Ghế ngồi"
        else if (itemHistory.type == 1) "Giường nằm"
        else "Giường nằm đôi"

        var format = DecimalFormat("#,###")
        giaVe.text = "${format.format(itemHistory.price)}đ"
        tenDiemDon.text = itemHistory.ten_diem_don
        tenDiemTra.text = itemHistory.ten_diem_tra
        diaChiDiemDon.text = itemHistory.dia_chi_diem_don
        diaChiDiemTra.text = itemHistory.dia_chi_diem_tra
        thoiGianDuKienDon.text = "Dự kiến đón lúc " + itemHistory.start_time + " • " + itemHistory.start_date
        thoiGianDuKienTra.text = "Dự kiến trả lúc " + itemHistory.end_time + " • " + itemHistory.end_date
        tenKH.text = UserInformation.USER!!.display_name
        sdtKH.text = itemHistory.phone
        emailKH.text = UserInformation.USER!!.email_contact
        note.text = itemHistory.note

        back.setOnClickListener{
            finish()
        }

    }
}