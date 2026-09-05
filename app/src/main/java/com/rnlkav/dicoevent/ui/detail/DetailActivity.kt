package com.rnlkav.dicoevent.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.rnlkav.dicoevent.R
import com.rnlkav.dicoevent.data.Result
import com.rnlkav.dicoevent.data.local.entity.FavoriteEvent
import com.rnlkav.dicoevent.data.response.ListEventsItem
import com.rnlkav.dicoevent.databinding.ActivityDetailBinding
import com.rnlkav.dicoevent.ui.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val eventId = intent.getIntExtra(EXTRA_EVENT_ID, -1)
        if (eventId == -1) {
            finish()
            return
        }

        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]

        setupSwipeRefresh()
        observeViewModel(eventId)

        viewModel.getEventDetail(eventId)
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshDetail()
        }
    }

    private fun observeViewModel(id: Int) {
        viewModel.eventDetail.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    displayEventDetail(result.data)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.getFavoriteById(id.toString()).observe(this) { favorite ->
            isFavorite = favorite != null
            updateFavoriteIcon()
        }
    }

    private fun updateFavoriteIcon() {
        if (isFavorite) {
            binding.fabFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite))
        } else {
            binding.fabFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border))
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
                val intent = Intent(Intent.ACTION_VIEW, event.link.toUri())
                startActivity(intent)
            }

            fabFavorite.setOnClickListener {
                val favoriteEvent = FavoriteEvent(
                    id = event.id.toString(),
                    name = event.name,
                    mediaCover = event.mediaCover,
                )
                viewModel.setFavorite(favoriteEvent, !isFavorite)
            }
        }
    }

    private fun formatEventTime(time: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.forLanguageTag("id-ID"))
            val date = inputFormat.parse(time)
            date?.let { outputFormat.format(it) } ?: time
        } catch (_: Exception) {
            time
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
    }
}
