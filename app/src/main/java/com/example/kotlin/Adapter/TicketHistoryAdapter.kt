package com.example.kotlin.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin.R
import com.example.kotlin.DataClass.HistoryItem
import java.text.DecimalFormat

class TicketHistoryAdapter(private var type: Int, private var items: MutableList<HistoryItem>)
    : RecyclerView.Adapter<TicketHistoryAdapter.TicketViewHolder>(){
    var onItemClick: ((HistoryItem) -> Unit)? = null
    var onDiscard: ((HistoryItem, Int) -> Unit)? = null

    inner class TicketViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tinh_trang: TextView = itemView.findViewById(R.id.tinh_trang)
        val gia_ve: TextView = itemView.findViewById(R.id.gia_ve)
        val bus_icon: ImageView = itemView.findViewById(R.id.bus_icon)
        val gio_bat_dau: TextView = itemView.findViewById(R.id.gio_bat_dau)
        val ngay_bat_dau: TextView = itemView.findViewById(R.id.ngay_bat_dau)
        val the_thoi_gian: LinearLayout = itemView.findViewById(R.id.the_thoi_gian)
        val tinh_den: TextView = itemView.findViewById(R.id.tinh_den)
        val tinh_di: TextView = itemView.findViewById(R.id.tinh_di)
        val ten_nha_xe: TextView = itemView.findViewById(R.id.ten_nha_xe)
        val ma_dat_cho: TextView = itemView.findViewById(R.id.ma_dat_cho)
        val thanh_btn: LinearLayout = itemView.findViewById(R.id.thanh_chinh_sua_ve)
        val huy: Button = itemView.findViewById(R.id.btn_huy_ve)
        val thanh_toan: Button = itemView.findViewById(R.id.btn_thanh_toan)
        val ve: LinearLayout = itemView.findViewById(R.id.item_ve_id)
        val showMaDatCho: LinearLayout = itemView.findViewById(R.id.show_ma_dat_cho)
        init{
            ve.setOnClickListener{onItemClick?.invoke(items[adapterPosition])}
            huy.setOnClickListener { onDiscard?.invoke(items[adapterPosition], adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.item_ve, parent, false)

        return TicketViewHolder(contactView)

    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        var item = items[position]
        if(type == 0){
            holder.thanh_btn.visibility = VISIBLE
        }else
            holder.thanh_btn.visibility = GONE

        if(item.status == 0) {
            holder.tinh_trang.text = "Chưa thanh toán"
        }
        else if(item.status == 1) {
            holder.thanh_toan.visibility = GONE
            holder.tinh_trang.text = "Đã thanh toán"
        }
        else if(item.status == 2) {
            holder.bus_icon.setBackgroundColor(holder.bus_icon.context.resources.getColor(R.color.gray_icon))
            holder.the_thoi_gian.setBackgroundColor(holder.the_thoi_gian.context.resources.getColor(R.color.gray_panel))
            holder.tinh_trang.text = "Đã hủy"
            holder.showMaDatCho.visibility = GONE
        }

        var format = DecimalFormat("#,###")

        holder.gia_ve.text = "${format.format(item.price * item.so_luong)}đ"
        holder.gio_bat_dau.text = item.start_time
        holder.ngay_bat_dau.text = "item.start_date"
        holder.tinh_den.text = item.tinh_don
        holder.tinh_di.text = item.tinh_tra
        holder.ten_nha_xe.text = item.ten_nha_xe
        holder.ma_dat_cho.text = item.id


    }

    override fun getItemCount(): Int {
        return items.size
    }
}