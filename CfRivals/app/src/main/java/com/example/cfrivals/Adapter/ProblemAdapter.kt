package com.example.cfrivals.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cfrivals.Models.Problem
import androidx.core.net.toUri
import com.example.cfrivals.databinding.ItemProblemBinding

class ProblemAdapter(private val prblms: List<Problem>) :
    RecyclerView.Adapter<ProblemAdapter.ViewHolder>() {

        class ViewHolder(val binding: ItemProblemBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemProblemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
            val prblm = prblms[pos]
            holder.binding.txtPrblmName.text = "${prblm.index}. ${prblm.name}"
            holder.binding.txtPrblmRating.text = "Rating: ${prblm.rating ?: "Unrated"}"

            holder.itemView.setOnClickListener {
                val url = "https://codeforces.com/contest/${prblm.contestId}/problem/${prblm.index}"
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                it.context.startActivity(intent)
            }
        }

    override fun getItemCount() = prblms.size
}