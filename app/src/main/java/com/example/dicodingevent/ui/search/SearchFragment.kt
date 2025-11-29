package com.example.dicodingevent.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.R
import com.example.dicodingevent.data.model.EventItem
import com.example.dicodingevent.databinding.FragmentSearchBinding
import com.example.dicodingevent.di.ViewModelFactory
import com.example.dicodingevent.ui.detail.DetailEventActivity
import com.example.dicodingevent.ui.list.EventAdapter
import com.example.dicodingevent.ui.viewmodel.EventResult
import com.example.dicodingevent.ui.viewmodel.EventViewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var searchAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.rvSearchResults.layoutManager = LinearLayoutManager(requireContext())


        setupSearchView()


        observeSearchResults()


        showStatusMessage(isVisible = true, message = "Silakan masukkan kata kunci untuk mencari acara.")
    }

    private fun setupSearchView() {

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    viewModel.searchEvents(query.trim())
                    binding.searchView.clearFocus()
                } else {
                    Toast.makeText(context, "Kata kunci tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank() && searchAdapter.currentList.isNotEmpty()) {
                    showStatusMessage(isVisible = true, message = "Silakan masukkan kata kunci untuk mencari acara.")
                    searchAdapter.submitList(emptyList())
                }
                return false
            }
        })
    }

    private fun navigateToDetail(event: EventItem) {
        val intent = Intent(activity, DetailEventActivity::class.java)
        intent.putExtra(DetailEventActivity.EXTRA_EVENT_ID, event.id)
        startActivity(intent)
    }


    private fun observeSearchResults() {
        viewModel.searchResults.observe(viewLifecycleOwner) { result ->
            when (result) {
                is EventResult.Loading -> {
                    showLoading(true)
                }
                is EventResult.Success -> {
                    showLoading(false)
                    val data = result.data ?: emptyList()
                    searchAdapter.submitList(data)

                    val isNotFound = data.isEmpty() && binding.searchView.query?.isNotBlank() == true

                    if (isNotFound) {
                        showStatusMessage(isVisible = true, message = getString(R.string.status_no_events_found))
                    } else if (data.isNotEmpty()){
                        showStatusMessage(isVisible = false, message = null)
                    } else {
                        showStatusMessage(isVisible = true, message = "Silakan masukkan kata kunci untuk mencari acara.")
                    }
                }
                is EventResult.Error -> {
                    showLoading(false)
                    showStatusMessage(isVisible = true, message = getString(R.string.error_failed_to_load_data))
                    Toast.makeText(context, "Error Pencarian: ${result.exception.message}", Toast.LENGTH_LONG).show()
                    searchAdapter.submitList(emptyList())
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvSearchResults.visibility = if (isLoading) View.GONE else View.VISIBLE
        if (isLoading) showStatusMessage(false, null)
    }

    private fun showStatusMessage(isVisible: Boolean, message: String?) {
        binding.tvNotFound.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.tvNotFound.text = message

        if (isVisible) {
            binding.rvSearchResults.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}