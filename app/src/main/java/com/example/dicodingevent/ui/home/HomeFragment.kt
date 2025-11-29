package com.example.dicodingevent.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.R
import com.example.dicodingevent.data.model.EventItem
import com.example.dicodingevent.databinding.FragmentHomeBinding
import com.example.dicodingevent.di.ViewModelFactory
import com.example.dicodingevent.ui.detail.DetailEventActivity
import com.example.dicodingevent.ui.list.EventAdapter
import com.example.dicodingevent.ui.viewmodel.EventResult
import com.example.dicodingevent.ui.viewmodel.EventViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var activeAdapter: EventAdapter
    private lateinit var finishedAdapter: EventAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val upcomingStatusText = getString(R.string.status_label) + " Aktif"
        activeAdapter = EventAdapter({ event -> navigateToDetail(event) }, upcomingStatusText)

        val finishedStatusText = getString(R.string.status_label) + " Selesai"
        finishedAdapter = EventAdapter({ event -> navigateToDetail(event) }, finishedStatusText)


        binding.rvActiveHorizontal.layoutManager = LinearLayoutManager(requireContext())
        binding.rvActiveHorizontal.adapter = activeAdapter


        binding.rvFinishedVertical.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFinishedVertical.adapter = finishedAdapter

        observeEvents()
    }

    private fun observeEvents() {
        viewModel.upcomingEvents.observe(viewLifecycleOwner) { result ->
            when (result) {
                is EventResult.Loading -> showLoading(true)
                is EventResult.Success -> {
                    showLoading(false)
                    val data = result.data ?: emptyList()

                    activeAdapter.submitList(data.take(5))
                }
                is EventResult.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(),
                        "Gagal memuat event aktif: ${result.exception.message}",
                        Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.finishedEvents.observe(viewLifecycleOwner) { result ->
            when (result) {
                is EventResult.Loading -> showLoading(true)
                is EventResult.Success -> {
                    showLoading(false)
                    val data = result.data ?: emptyList()

                    finishedAdapter.submitList(data.take(5))
                }
                is EventResult.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(),
                        "Gagal memuat event selesai: ${result.exception.message}",
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun navigateToDetail(event: EventItem) {
        val intent = Intent(activity, DetailEventActivity::class.java)


        intent.putExtra(DetailEventActivity.EXTRA_EVENT_ID, event.id.toString())
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarHome.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvActiveHorizontal.adapter = null
        binding.rvFinishedVertical.adapter = null
        _binding = null
    }
}