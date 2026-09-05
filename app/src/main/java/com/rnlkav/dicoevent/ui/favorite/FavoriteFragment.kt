package com.rnlkav.dicoevent.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rnlkav.dicoevent.data.response.ListEventsItem
import com.rnlkav.dicoevent.databinding.FragmentFavoriteBinding
import com.rnlkav.dicoevent.ui.ViewModelFactory
import com.rnlkav.dicoevent.ui.adapter.EventAdapter
import com.rnlkav.dicoevent.ui.detail.DetailActivity

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FavoriteViewModel
    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory.getInstance(requireContext())
        viewModel = ViewModelProvider(this, factory)[FavoriteViewModel::class.java]

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter { favoriteEvent ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_EVENT_ID, favoriteEvent.id)
            startActivity(intent)
        }
        binding.rvEvents.adapter = adapter
        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        viewModel.getAllFavorite().observe(viewLifecycleOwner) { favorites ->
            val listEvents = favorites.map {
                ListEventsItem(
                    id = it.id.toInt(),
                    name = it.name,
                    summary = "",
                    description = "",
                    imageLogo = it.mediaCover ?: "",
                    mediaCover = it.mediaCover ?: "",
                    category = "",
                    ownerName = "",
                    cityName = "",
                    quota = 0,
                    registrants = 0,
                    beginTime = "",
                    endTime = "",
                    link = ""
                )
            }
            adapter.submitList(listEvents)
            binding.tvEmpty.visibility = if (listEvents.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
