package com.mdiot.test.myalbums.tracks

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.mdiot.test.myalbums.R
import com.mdiot.test.myalbums.databinding.TracksFragmentBinding
import com.mdiot.test.myalbums.utils.MarginItemDecoration
import com.mdiot.test.myalbums.utils.setupSnackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Display a list of [Tracks]s.
 */
@AndroidEntryPoint
class TracksFragment : Fragment() {

    private val viewModel by viewModels<TracksViewModel>()

    private lateinit var viewDataBinding: TracksFragmentBinding

    private lateinit var listAdapter: TracksAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewDataBinding = TracksFragmentBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_refresh -> {
                viewModel.forceRefresh()
                true
            }
            else -> false
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewDataBinding.rvItemList.setHasFixedSize(true)
        viewDataBinding.rvItemList.addItemDecoration(MarginItemDecoration(resources.getDimension(R.dimen.list_item_padding).toInt()))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tracks_fragment_menu, menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Set the lifecycle owner to the lifecycle of the view
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupSnackbar()
        setupListAdapter()
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = TracksAdapter(viewModel)
            viewDataBinding.rvItemList.adapter = listAdapter
        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }

}