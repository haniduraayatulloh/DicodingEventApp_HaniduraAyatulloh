package com.example.dicodingevent.ui.upcoming

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dicodingevent.R
import com.example.dicodingevent.data.model.EventItem
import com.example.dicodingevent.di.ViewModelFactory
import com.example.dicodingevent.ui.detail.DetailEventActivity
import com.example.dicodingevent.ui.list.EventAdapter
import com.example.dicodingevent.ui.viewmodel.EventResult
import com.example.dicodingevent.ui.viewmodel.EventViewModel

class UpcomingEventFragment : Fragment() {

    private lateinit var rvEventList: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvErrorMessage: TextView

    private val viewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_event_list, container, false)
        rvEventList = view.findViewById(R.id.rv_event_list)
        progressBar = view.findViewById(R.id.progress_bar)
        tvErrorMessage = view.findViewById(R.id.tv_error_message)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val upcomingStatusText = getString(R.string.status_label) + " Aktif"
        adapter = EventAdapter({ event -> navigateToDetail(event) }, upcomingStatusText)

        rvEventList.layoutManager = LinearLayoutManager(requireContext())
        rvEventList.adapter = adapter

        observeUpcomingEvents()
    }

    private fun navigateToDetail(event: EventItem) {
        val intent = Intent(activity, DetailEventActivity::class.java)
        intent.putExtra(DetailEventActivity.EXTRA_EVENT_ID, event.id.toString())
        startActivity(intent)
    }

    private fun observeUpcomingEvents() {
        viewModel.upcomingEvents.observe(viewLifecycleOwner) { result ->
            when (result) {
                is EventResult.Loading -> {
                    showLoading(true)
                }
                is EventResult.Success -> {
                    showLoading(false)
                    val data = result.data

                    if (data.isNullOrEmpty()) {
                        showStatusMessage(isVisible = true, message = getString(R.string.status_no_events_found))
                        adapter.submitList(emptyList())
                    } else {
                        showStatusMessage(isVisible = false, message = null)
                        adapter.submitList(data)
                    }
                }
                is EventResult.Error -> {
                    showLoading(false)

                    showStatusMessage(isVisible = true, message = "Error memuat acara aktif: ${result.exception.message}")
                    adapter.submitList(emptyList())
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        rvEventList.visibility = if (isLoading) View.GONE else View.VISIBLE
        if (isLoading) showStatusMessage(false, null)
    }

    private fun showStatusMessage(isVisible: Boolean, message: String?) {
        tvErrorMessage.visibility = if (isVisible) View.VISIBLE else View.GONE
        tvErrorMessage.text = message
        rvEventList.visibility = if (isVisible) View.GONE else View.VISIBLE
    }
}