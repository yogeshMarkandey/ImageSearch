package com.example.imagesearch.activities

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.imagesearch.R
import com.example.imagesearch.datas.PhotoInfo
import com.example.imagesearch.adapter.FlickrPhotoAdapter
import com.example.imagesearch.adapter.PhotoLoadStateAdapter
import com.example.imagesearch.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_box_layout.view.*


class MainActivity : AppCompatActivity(), FlickrPhotoAdapter.TapListener {
    private val TAG = "MainActivity"
    private lateinit var viewModel: MainViewModel
    private val adapter  = FlickrPhotoAdapter(this )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val progressBar = progress_bar_main_activity
        val recyclerView = recyclerview_main
        val buttonRetry = button_retry
        val textViewError = text_view_error
        val textViewEmpty = text_view_empty

        /*val fade = Fade()
        // val decor = window.decorView
        fade.excludeTarget(R.id.action_bar_container, true)
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        fade.excludeTarget(android.R.id.navigationBarBackground, true)

        window.enterTransition = fade
        window.exitTransition = fade*/

        Log.d(TAG, "onCreate: called")
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        

        
        recyclerview_main.setHasFixedSize(true)
        recyclerView.itemAnimator = null
        recyclerview_main.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PhotoLoadStateAdapter{adapter.retry()},
            footer = PhotoLoadStateAdapter{adapter.retry()},
        )

        buttonRetry.setOnClickListener{
            adapter.retry()
        }

        viewModel.photes.observe(this){
            // recycler view adapter

            adapter.submitData(this.lifecycle,it)
        }


        var layoutManager  = GridLayoutManager(this, 2)
        recyclerview_main.layoutManager = layoutManager
        viewModel.gridLayoutSpanCount.observe(this){spanCount ->

            layoutManager.spanCount = spanCount
            recyclerView.refreshDrawableState()
        }
        adapter.addLoadStateListener { loadState ->

                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error

                // empty view
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1) {
                    recyclerView.isVisible = false
                    textViewEmpty.isVisible = true
                } else {
                    textViewEmpty.isVisible = false
                }

        }

    }

    override fun onBackPressed() {

        if(viewModel.currentQuery.value != MainViewModel.DEFAULT_QUERY){
            viewModel.searchPhotos(MainViewModel.DEFAULT_QUERY)
            return
        }

        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        this.menuInflater.inflate(R.menu.menu_main, menu)
        val  searchItem  = menu?.findItem(R.id.item_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query != null){
                    recyclerview_main.scrollToPosition(0)
                    viewModel.searchPhotos(query)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        return true
    }

    override fun onTap(item: PhotoInfo, imageView: ImageView) {
        Log.d(TAG, "onTap: Called..")

        val url =
            "https://live.staticflickr.com/${item?.server}/${item?.id}_${item?.secret}_b.jpg"

        val intent  = Intent(this, FullScreenActivity::class.java)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, imageView, "transition")
        intent.putExtra("imageInfo", url)
        intent.putExtra("imageData", item)
        startActivity(intent, options.toBundle())


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.item_custom_url){
            getCustomImageUrl()
            return true
        }

        if(item.itemId == R.id.item_search){
            return true
        }


       var span = when(item.itemId){
           R.id.number_2 -> 2
           R.id.number_3 -> 3
           R.id.number_4 -> 4
           R.id.item_dummy -> viewModel.gridLayoutSpanCount.value!!
           else -> 2
       }

        viewModel.setSpanCount(span)

        return true
    }


    private fun getCustomImageUrl(){

        val builder = AlertDialog.Builder(this)
        val myView = LayoutInflater.from(this).inflate(R.layout.dialog_box_layout, null)
        val customInputEV = myView.edit_text_custom_image_dialog
        val buttonGetImage = myView.button_get_image
        //myView.textView_feedback_dialog_main.visibility = View.GONE

        builder.setView(myView)
        val dialog = builder.create()
        dialog.show()

        var canExpand = true

        buttonGetImage.setOnClickListener{
            val url = customInputEV.text.toString()
            if (canExpand) {

                if(url.trim().isEmpty()){
                    customInputEV.setText("")
                    customInputEV.hint = "Enter Valid Url!"
                    myView.textView_feedback_dialog_main.visibility = View.VISIBLE

                    myView.textView_feedback_dialog_main.text = "Enter Valid Url!"
                    return@setOnClickListener
                }

                val intent = Intent(this, FullScreenActivity::class.java)
                //val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, imageView, "transition")

                intent.putExtra("imageInfo", url)
                startActivity(intent)
                dialog.dismiss()
            }else{
                customInputEV.setText("")
                customInputEV.hint = "Image Not Found \nPlease Check the Url!"
                myView.textView_feedback_dialog_main.visibility = View.VISIBLE
                myView.textView_feedback_dialog_main.text = "Image Not Found \nPlease Check the Url!"
            }
        }

        myView.button_search_dialog_box_main.setOnClickListener{
            val url = customInputEV.text.toString().trim()
            myView.textView_feedback_dialog_main.visibility = View.GONE
            canExpand =true
            if( url.trim().isNotEmpty()){
                val option = RequestOptions.placeholderOf(R.drawable.background_image_view)
                    .error(R.drawable.background_image_view)
                Glide.with(this)
                    .load(url)
                    .apply(option)
                    .centerCrop()
                    .listener(object :RequestListener<Drawable>{
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            customInputEV.setText("")
                            customInputEV.hint = "Image Not Found \nPlease Check the Url!"
                            myView.textView_feedback_dialog_main.visibility = View.VISIBLE
                            myView.textView_feedback_dialog_main.text= "Image Not Found \nPlease Check the Url!"
                            canExpand = false
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            myView.textView_feedback_dialog_main.visibility = View.GONE
                            return false
                        }
                    })
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(myView.imageView_dialog_box_main)
            }else{
                customInputEV.setText("")
                customInputEV.hint = "Enter Valid Url!"
                myView.textView_feedback_dialog_main.visibility = View.VISIBLE
                myView.textView_feedback_dialog_main.text = "Enter Valid Url!"
                canExpand = false
            }
        }
    }
}


