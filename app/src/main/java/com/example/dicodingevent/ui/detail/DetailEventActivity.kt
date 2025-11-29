package com.example.dicodingevent.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.dicodingevent.R
import com.example.dicodingevent.data.model.EventItem
import com.example.dicodingevent.databinding.ActivityDetailEventBinding
import com.example.dicodingevent.di.ViewModelFactory
import com.example.dicodingevent.ui.viewmodel.EventResult
import com.example.dicodingevent.ui.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.*

class DetailEventActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
        private const val TAG = "DetailEventActivity"
    }

    private lateinit var binding: ActivityDetailEventBinding


    private val viewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.detail_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val eventId = intent.getStringExtra(EXTRA_EVENT_ID)

        if (eventId.isNullOrEmpty()) {
            showError(getString(R.string.error_no_event_id))
            return
        }

        viewModel.fetchDetailEvent(eventId)
        observeDetailEvent()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observeDetailEvent() {
        viewModel.detailEvent.observe(this) { result ->
            when (result) {
                is EventResult.Loading -> {
                    showLoading(true)
                }
                is EventResult.Success -> {
                    showLoading(false)
                    val event = result.data
                    if (event != null) {
                        showDetailEvent(event)
                    } else {
                        showError(getString(R.string.error_event_not_found))
                    }
                }
                is EventResult.Error -> {
                    Log.e(TAG, "Error: ${result.exception.message}")
                    showError("Gagal memuat detail acara: ${result.exception.message}")
                }
            }
        }
    }

    private fun showDetailEvent(event: EventItem) {
        binding.apply {


            val imageUrl = event.getImageUrl()

            if (!imageUrl.isNullOrEmpty()) {

                ivDetailImage.visibility = View.VISIBLE
                ivDetailImage.load(imageUrl) {
                    crossfade(true)

                    listener(
                        onError = { _, _ ->

                            ivDetailImage.visibility = View.GONE
                        },
                        onSuccess = { _, _ ->

                            ivDetailImage.visibility = View.VISIBLE
                        }
                    )
                }
            } else {

                ivDetailImage.visibility = View.GONE
            }

            tvDetailName.text = event.name ?: getString(R.string.not_available)
            tvDetailOwner.text = event.ownerName ?: getString(R.string.not_available)
            tvDetailTime.text = formatTime(event.beginTime)


            if (event.quota != null && event.registered != null) {
                val remaining = event.getRemainingQuota() ?: 0
                tvDetailQuota.text = getString(
                    R.string.quota_format,
                    event.registered,
                    event.quota,
                    remaining
                )
                tvDetailQuota.visibility = View.VISIBLE
            } else {
                tvDetailQuota.text = getString(R.string.quota_unknown)
                tvDetailQuota.visibility = View.VISIBLE
            }


            tvDetailSummary.text = event.summary ?: getString(R.string.not_available)


            val rawDescriptionText = event.description.orEmpty()
            val cleanDescription = cleanHtmlText(rawDescriptionText)

            tvDetailDescription.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(cleanDescription, Html.FROM_HTML_MODE_COMPACT)
            } else {
                @Suppress("DEPRECATION")
                Html.fromHtml(cleanDescription)
            }

            if (!event.link.isNullOrEmpty()) {
                btnRegister.visibility = View.VISIBLE
                btnRegister.setOnClickListener {
                    openRegistrationLink(event.link)
                }
            } else {
                btnRegister.visibility = View.GONE
            }

            detailContentLayout.visibility = View.VISIBLE
        }
    }


    private fun openRegistrationLink(link: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error saat membuka link: $link", e)
            Toast.makeText(this, getString(R.string.error_invalid_link), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.detailContentLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.detailContentLayout.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun formatTime(time: String?): String {
        return try {
            if (time.isNullOrEmpty()) return getString(R.string.time_unknown)

            val apiFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val displayFormat = SimpleDateFormat("EEEE, dd MMMM yyyy, HH:mm", Locale("in", "ID"))

            val date = apiFormat.parse(time)
            if (date != null) {
                displayFormat.format(date)
            } else {
                getString(R.string.time_unknown)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error format waktu: $time", e)
            getString(R.string.time_unknown)
        }
    }


    private fun cleanHtmlText(htmlText: String): String {
        var cleanedText = htmlText

            .replace("\\u0003", "")

            .replace("Rundown Acara:", "<br><br><b>Rundown Acara:</b><br>")
            .replace("FAQ:", "<br><br><b>FAQ:</b><br>")

            .replace(";", "<br>")

            .replace("<br><br><br>", "<br><br>")
            .replace("\n\n\n", "\n\n")

            .replace("  ", " ")

        return cleanedText
    }
}