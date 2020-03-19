package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.adapter.AsteroidAdapter
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.repo.observeX

class MainFragment : Fragment() {

    lateinit var asteroidAdapter: AsteroidAdapter
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        asteroidAdapter = AsteroidAdapter()

        binding.asteroidRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = asteroidAdapter.apply {
                onItemClick { model, view, position ->
                    findNavController().navigate(MainFragmentDirections.actionShowDetail(model))
                }
            }
        }

        binding.viewModel = viewModel
        viewModel.fetchAsteroids()
        viewModel.getAsteroids(this).observeX(this) {
            it?.let { list ->
                asteroidAdapter.setAsteroidList(list)
                asteroidAdapter.notifyDataSetChanged()
            }
        }
        viewModel.fetchTodaysImage(binding.activityMainImageOfTheDay)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }
}
