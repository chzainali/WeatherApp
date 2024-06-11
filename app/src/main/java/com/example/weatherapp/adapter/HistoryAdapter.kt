package com.example.weatherapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ItemHistoryBinding
import com.example.weatherapp.models.FinalForecastModel

class HistoryAdapter(val list: MutableList<FinalForecastModel>,var onItemClick:(Int)->Unit,var onDelete:(Int)->Unit) :
    RecyclerView.Adapter<HistoryAdapter.Vh>() {
    inner class Vh(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemHistoryBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return Vh(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: Vh, position: Int) {
        val item = list[position]
        holder.binding.tvTitle.text=item.city
        holder.binding.tv.text=item.time

        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
        holder.binding.imgDelete.setOnClickListener{
            onDelete(position)
        }
    }
}