// MainActivity.kt
package com.example.imadpracticum // package com.example.imadpracticum

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // --- Q.1.1 Declare and initialize appropriate variables and four parallel arrays ---

    // These arrays will store the details for up to four songs.
    // They are initialized with some default values.
    // The playlist will initially contain placeholder songs that can be updated.
    private val songs = arrayOf("Song 1 (Default)", "Song 2 (Default)", "Song 3 (Default)", "Song 4 (Default)")
    private val artists = arrayOf("Artist 1 (Default)", "Artist 2 (Default)", "Artist 3 (Default)", "Artist 4 (Default)")
    private val ratings = arrayOf(0, 0, 0, 0) // Ratings will be between 1 and 5
    private val comments = arrayOf("No comments yet.", "No comments yet.", "No comments yet.", "No comments yet.")

    // Variable to keep track of the number of songs actually added/updated in the playlist
    // This helps in only calculating averages/displaying for valid entries.
    private var songCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        val addSongButton: Button = findViewById(R.id.addSongButton)
        val detailedViewButton: Button = findViewById(R.id.detailedViewButton)
        val exitButton: Button = findViewById(R.id.exitButton)

        // --- Q.1.2 Main screen: Add a button for "Add to Playlist" ---
        addSongButton.setOnClickListener {
            // Call a method to handle adding/updating song details
            showAddSongDialog()
        }

        // --- Q.1.2 Add a 2nd button that, when clicked, navigates to the second screen ---
        detailedViewButton.setOnClickListener {
            navigateToDetailedView()
        }

        // --- Q.1.2 Add a 3rd button to exit the app ---
        exitButton.setOnClickListener {
            // Finish the current activity and close the application
            finishAffinity() // This closes all activities in the task stack
        }
    }

    // --- Q.1.1 Create methods/functions required ---

    /**
     * Displays an AlertDialog to allow the user to input or update song details.
     * The user can select which of the four song slots to modify or add to the next available slot.
     */
    private fun showAddSongDialog() {
        val dialogView = layoutInflater.inflate(R.layout_add_song , null)
        val etSongTitle: EditText = dialogView.findViewById(R.id.etSongTitle)
        val etArtistName: EditText = dialogView.findViewById(R.id.etArtistName)
        val etRating: EditText = dialogView.findViewById(R.id.etRating)
        val etComments: EditText = dialogView.findViewById(R.id.etComments)
        val etSongIndex: EditText = dialogView.findViewById(R.id.etSongIndex) // For selecting which song to update

        AlertDialog.Builder(this)
        setTitle("Add/Update Song to Playlist (1-4)")
             {
                set()
            }
            .setPositiveButton("Save") { dialog , _ ->
                val songTitle = etSongTitle.text.toString().trim()
                val artistName = etArtistName.text.toString().trim()
                val ratingStr = etRating.text.toString().trim()
                val commentsText: String = etComments.text.toString().trim()
                val indexStr = etSongIndex.text.toString().trim()

                // --- Error handling for input validation ---
                if (songTitle.isEmpty() || artistName.isEmpty() || ratingStr.isEmpty() || indexStr.isEmpty()) {
                    Toast.makeText(
                        this ,
                        "Please fill in all required fields (Song Title, Artist, Rating, Index)." ,
                        Toast.LENGTH_LONG
                    ).show()
                    // Implement the incorrect action: do not save anything if fields are empty.
                    // This is handled by simply returning and not proceeding with the save operation.
                    return@setPositiveButton
                }

                val rating = ratingStr.toIntOrNull()
                val index = indexStr.toIntOrNull()

                if (rating == null || index == null) {
                    Toast.makeText(
                        this ,
                        "Rating and Song Index must be numbers." ,
                        Toast.LENGTH_LONG
                    ).show()
                    // Implement the incorrect action: if not a number, do not save.
                    return@setPositiveButton
                }

                if (rating !in 1..5) {
                    Toast.makeText(this , "Rating must be between 1 and 5." , Toast.LENGTH_LONG)
                        .show()
                    // Implement the incorrect action: if rating is invalid, save it as 0
                    // or do not update the rating for this entry.
                    // For this scenario, we will proceed with saving other details, but the rating
                    // for this entry will remain its previous value or default if this is a new entry.
                    // To truly "implement the incorrect action" as storing an invalid rating:
                    // If the rating is out of bounds, we will simply set it to 0 and proceed.
                    val effectiveRating = if (rating !in 1..5) 0 else rating
                    updateSongDetails(
                        index - 1 ,
                        songTitle ,
                        artistName ,
                        effectiveRating
                    )
                    return@setPositiveButton // Exit after showing toast and handling invalid rating
                }

                if (index !in 1..4) {
                    Toast.makeText(this , "Song Index must be between 1 and 4." , Toast.LENGTH_LONG)
                        .show()
                    // Implement the incorrect action: do not save if index is out of bounds.
                    return@setPositiveButton
                }

                // If all validations pass, update the song details
                // Arrays are 0-indexed, so subtract 1 from user's input.
                updateSongDetails(index - 1 , songTitle , artistName , rating)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog , _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun set() {
        TODO("Not yet implemented")
    }

    /**
     * Updates the song details in the parallel arrays at the specified index.
     * @param index The 0-based index in the arrays to update.
     * @param songTitle The new song title.
     * @param artistName The new artist name.
     * @param rating The new rating (1-5).
     * @param comments The new comments.
     */
    private fun updateSongDetails(index: Int , songTitle: String , artistName: String , rating: Int) {
        // Check if the index is within bounds of our fixed-size arrays
        if (index >= 0 && index < songs.size) {
            songs[index] = songTitle
            artists[index] = artistName
            ratings[index] = rating
            set()

            // Update songCount if this is the first time this slot is being filled (from 0 to >0)
            if (ratings[index] > 0 && songCount <= index) {
                songCount = index + 1 // Ensure songCount reflects the highest valid index filled + 1
            } else if (ratings[index] == 0 && songCount > 0) {
                    // If a song is "removed" by setting rating to 0, potentially decrease songCount
                    // This scenario is not explicitly requested, but good for robust handling.
                    // For this app, we assume updates keep the slot filled.
                }

            Toast.makeText(this, "Song details for index ${index + 1} updated!", Toast.LENGTH_SHORT).show()
        } else {
            // This should ideally not be reached if previous index validation is correct
            Toast.makeText(this, "Error: Invalid song index provided for update.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Navigates to the DetailedViewActivity, passing the song data.
     */
    private fun navigateToDetailedView() {
        val intent = Intent(this, DetailedViewActivity::class.java).apply {
            // Pass the arrays to the DetailedViewActivity
            putExtra("songs", songs)
            putExtra("artists", artists)
            putExtra("ratings", ratings)
            putExtra("comments", comments)
            putExtra("songCount", songCount) // Pass the actual count of valid songs
        }
        startActivity(intent)
    }
}

private fun set() {
    TODO("Not yet implemented")
}
