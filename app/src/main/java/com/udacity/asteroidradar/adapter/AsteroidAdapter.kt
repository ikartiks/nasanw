package com.udacity.asteroidradar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.TemplateAsteroidBinding

class AsteroidAdapter : RecyclerView.Adapter<AsteroidViewHolder>() {

    private var asteroids: List<Asteroid> = ArrayList()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): AsteroidViewHolder {
        val employeeListItemBinding: TemplateAsteroidBinding = DataBindingUtil.inflate(
            LayoutInflater.from(viewGroup.context),
            R.layout.template_asteroid,
            viewGroup,
            false
        )
        return AsteroidViewHolder(employeeListItemBinding)
    }

    override fun getItemCount(): Int {
        return asteroids.size
    }

    fun setAsteroidList(list: List<Asteroid>) {
        this.asteroids = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        holder.employeeListItemBinding.asteroid = asteroids[position]
        holder.employeeListItemBinding.root.setOnClickListener {
            mClickCallback?.invoke(asteroids[position], it, position)
        }
    }

    var mClickCallback: ((Asteroid, View, Int) -> Unit)? = null

    fun onItemClick(l: (model: Asteroid, v: View?, p: Int) -> Unit) {
        this.mClickCallback = l
    }
}