package com.rnlkav.dicoevent.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.rnlkav.dicoevent.R
import com.rnlkav.dicoevent.data.response.ListEventsItem
import com.rnlkav.dicoevent.databinding.ActivityDetailBinding
import com.rnlkav.dicoevent.ui.MainViewModel

import java.text.SimpleDateFormat
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId = intent.getIntExtra(EXTRA_EVENT_ID, -1)
        if (eventId == -1) {
            finish()
            return
        }

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        loadData(eventId)

        setupSwipeRefresh(eventId)
        observeViewModel()
    }

    private fun loadData(id: Int) {
        viewModel.getEventDetail(id)
    }

    private fun setupSwipeRefresh(id: Int) {
        binding.swipeRefresh.setOnRefreshListener {
            loadData(id)
        }
    }

    private fun observeViewModel() {
        viewModel.eventDetail.observe(this) { event ->
            displayEventDetail(event)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            with(binding) {
                progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                if (!isLoading) {
                    swipeRefresh.isRefreshing = false
                }
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayEventDetail(event: ListEventsItem) {
        with(binding) {
            tvEventName.text = event.name
            tvOwnerName.text = getString(R.string.owner, event.ownerName)
            
            val formattedTime = formatEventTime(event.beginTime)
            tvBeginTime.text = getString(R.string.begin_time, formattedTime)

            val quotaLeft = event.quota - event.registrants
            tvQuota.text = getString(R.string.quota_left, quotaLeft)

            tvDescription.text =
                HtmlCompat.fromHtml(event.description, HtmlCompat.FROM_HTML_MODE_LEGACY)

            Glide.with(this@DetailActivity)
                .load(event.mediaCover)
                .into(imgEvent)

            btnRegister.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                startActivity(intent)
            }
        }
    }

    private fun formatEventTime(time: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
            val date = inputFormat.parse(time)
            date?.let { outputFormat.format(it) } ?: time
        } catch (e: Exception) {
            time
        }
    }

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
    }
}
