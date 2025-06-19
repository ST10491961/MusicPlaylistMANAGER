// DetailedViewActivity.kt
package com.example.musicplaylistmanager // package com.example.imadpracticum

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.imadpracticum.R
import kotlin.math.roundToInt

class DetailedViewActivity : AppCompatActivity() {

    private lateinit var tvSongList: TextView
    private lateinit var tvAverageRating: TextView

    // Arrays to hold the data passed from MainActivity
    private var songs: Array<String> = arrayOf()
    private var artists: Array<String> = arrayOf()
    private var ratings: Array<Int> = arrayOf()
    private var comments: Array<String> = arrayOf()
    private var songCount: Int = 0 // Actual count of valid songs

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_view)

        // Initialize UI elements
        tvSongList = findViewById (R.id.tvSongList)
        tvAverageRating = findViewById(R.id.tvAverageRating)
        val showSongsButton: Button = findViewById(R.id.showSongsButton)
        val calculateAverageButton: Button = findViewById(R.id.calculateAverageButton)
        val backButton: Button = findViewById(R.id.backButton)

        // Retrieve data passed from MainActivity
        intent.extras?.let {
            songs = it.getStringArray("songs") ?: arrayOf()
            artists = it.getStringArray("artists") ?: arrayOf()
            ratings = it.getIntArray("ratings")?.toTypedArray() ?: arrayOf()
            comments = it.getStringArray("comments") ?: arrayOf()
            songCount = it.getInt("songCount", 0)
        }

        // --- Q.1.3 Detailed View Screen: Add a button to display the list of songs ---
        showSongsButton.setOnClickListener {
            displaySongDetails()
        }

        // --- Q.1.3 Add a button to calculate and display the average rating ---
        calculateAverageButton.setOnClickListener {
            calculateAndDisplayAverageRating()
        }

        // --- Q.1.3 Add a button to return to the main screen ---
        backButton.setOnClickListener {
            finish() // This will destroy DetailedViewActivity and return to MainActivity
        }
    }

    /**
     * Displays the list of songs with corresponding details using a loop.
     */
    private fun displaySongDetails() {
        if (songCount == 0) {
            tvSongList.text = "No songs added to the playlist yet."
            return
        }

        val stringBuilder = StringBuilder()
        // Loop through the arrays to build the display string
        for (i in 0 until songCount) { // Loop only up to songCount to show valid entries
            // Only display songs that have been actually added/updated (rating > 0 implying valid entry)
            if (ratings[i] > 0) {
                stringBuilder.append("Song ${i + 1}:\n")
                stringBuilder.append("  Title: ${songs[i]}\n")
                stringBuilder.append("  Artist: ${artists[i]}\n")
                stringBuilder.append("  Rating: ${ratings[i]} / 5\n")
                stringBuilder.append("  Comments: ${comments[i]}\n")
                stringBuilder.append("----------------------------\n")
            }
        }
        tvSongList.text = stringBuilder.toString()
    }

    /**
     * Calculates and displays the average rating for the songs in the playlist using a loop.
     */
    private fun calculateAndDisplayAverageRating() {
        if (songCount == 0) {
            tvAverageRating.text = "No songs to calculate average rating from."
            Toast.makeText(this, "Cannot calculate average: No songs in playlist.", Toast.LENGTH_SHORT).show()
            return
        }

        var totalRating = 0.0
        var validSongCountForAverage = 0

        // Loop through the ratings array to sum up valid ratings
        for (i in 0 until songCount) { // Loop only up to songCount
            // Only include ratings that are valid (1-5) in the average calculation
            if (ratings[i] in 1..5) {
                totalRating += ratings[i]
                validSongCountForAverage++
            }
        }

        if (validSongCountForAverage == 0) {
            tvAverageRating.text = "No valid ratings to calculate average from."
            Toast.makeText(this, "Cannot calculate average: No valid ratings found.", Toast.LENGTH_SHORT).show()
            return
        }

        val averageRating = totalRating / validSongCountForAverage
        // Format to two decimal places
        val formattedAverage = (averageRating * 100).roundToInt() / 100.0

        tvAverageRating.text = "Average Rating: $formattedAverage / 5"
    }
}