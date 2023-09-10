package com.ichsnn.myreactivesearch

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.ichsnn.myreactivesearch.model.UiState
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val edPlace = findViewById<AutoCompleteTextView>(R.id.ed_place)
        edPlace.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                lifecycleScope.launch {
                    viewModel.queryChannel.value = s.toString()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        viewModel.searchResult.observe(this, Observer { uiState ->
            when (uiState) {
                is UiState.Error -> {
                    Toast.makeText(this@MainActivity, uiState.message, Toast.LENGTH_SHORT).show()
                }

                is UiState.Success -> {
                    val placesName = uiState.data.map { it.placeName }
                    val adapter =
                        ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.select_dialog_item,
                            placesName
                        )
                    adapter.notifyDataSetChanged()
                    edPlace.setAdapter(adapter)
                }
            }
        })
    }
}