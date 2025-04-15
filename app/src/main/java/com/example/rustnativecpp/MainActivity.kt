package com.example.rustnativecpp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.rustnativecpp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example of a call to a native method
        binding.sampleText.text = stringFromRust()
    }

    /**
     * A native method that is implemented by the 'rustnativecpp' native library,
     * which is packaged with this application.
     */
    external fun stringFromRust(): String

    companion object {
        // Used to load the 'rustnativecpp' library on application startup.
        init {
            System.loadLibrary("rusty_android")
        }
    }
}