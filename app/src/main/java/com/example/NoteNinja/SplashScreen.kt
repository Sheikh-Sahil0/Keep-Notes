package com.example.NoteNinja

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {

    private lateinit var imgLowerCircle: ImageView
    private lateinit var imgUpperCircle: ImageView
    private lateinit var imgRightLogo: ImageView
    private lateinit var txtKeep: TextView
    private lateinit var txtNotes: TextView

    private val sharedPrefFile = "com.example.keepnotes.preferences"
    private val isFirstTimeKey = "isFirstTime"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        // Initializing views
        init()

        val sharedPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE)
        val isFirstTime = sharedPreferences.getBoolean(isFirstTimeKey, true)

        if (isFirstTime) {
            // Show full animation
            showFullAnimation()
            sharedPreferences.edit().putBoolean(isFirstTimeKey, false).apply()
        } else {
            // Show only logo without animation
            showLogoOnly()
        }
    }

    private fun init() {
        imgLowerCircle = findViewById(R.id.img_lower_circle)
        imgUpperCircle = findViewById(R.id.img_upper_circle)
        imgRightLogo = findViewById(R.id.img_right_logo)
        txtKeep = findViewById(R.id.txt_keep)
        txtNotes = findViewById(R.id.txt_notes)
    }

    private fun showFullAnimation() {
        // Declaring animations
        val lowerCircleAppearingAnimation = AnimationUtils.loadAnimation(this, R.anim.lower_circle_appearance_animation)
        val upperCircleRotationAnimation = AnimationUtils.loadAnimation(this, R.anim.upper_circle_rotation_animation)
        val rightLogoScalingAnimation = AnimationUtils.loadAnimation(this, R.anim.right_logo_animation)

        // Setting Handler to use postDelay
        Handler(Looper.getMainLooper()).postDelayed({
            // Making our imageView visible
            imgLowerCircle.visibility = View.VISIBLE
            // Setting our appearing animation on the imgLowerCircle ImageView
            imgLowerCircle.startAnimation(lowerCircleAppearingAnimation)

            // Setting another delay for our UpperCircle to start rotate after completion of LowerCircles animation
            Handler(Looper.getMainLooper()).postDelayed({
                // Making our imageView visible
                imgUpperCircle.visibility = View.VISIBLE
                // Setting our rotation animation on the imgUpperCircle ImageView
                imgUpperCircle.startAnimation(upperCircleRotationAnimation)

                // Setting another delay for our RightLogo imageView to start scaling after completion of UpperCircles animation
                Handler(Looper.getMainLooper()).postDelayed({
                    // Making our ImageView visible
                    imgRightLogo.visibility = View.VISIBLE
                    // Setting our scaling animation on the imgRightLogo ImageView
                    imgRightLogo.startAnimation(rightLogoScalingAnimation)

                    // Setting another delay for our keep notes text view to start
                    Handler(Looper.getMainLooper()).postDelayed({
                        // Making our TextView visible
                        txtKeep.visibility = View.VISIBLE
                        txtNotes.visibility = View.VISIBLE
                        // Setting our appearing animation on the textView
                        txtKeep.startAnimation(lowerCircleAppearingAnimation)
                        txtNotes.startAnimation(lowerCircleAppearingAnimation)

                        // Setting another delay to start the new activity after completion of animation
                        Handler(Looper.getMainLooper()).postDelayed({
                            val mainActivityIntent = Intent(this@SplashScreen, MainActivity::class.java)
                            startActivity(mainActivityIntent)
                            finish()
                        }, 1000)
                    }, 1200)

                }, 1200)

            }, 900)

        }, 700)
    }

    private fun showLogoOnly() {
        // Make logo visible immediately
        imgRightLogo.visibility = View.VISIBLE
        imgLowerCircle.visibility = View.VISIBLE
        imgUpperCircle.visibility = View.VISIBLE
        txtNotes.visibility = View.VISIBLE
        txtKeep.visibility = View.VISIBLE

        // Start the MainActivity immediately after a short delay
        Handler(Looper.getMainLooper()).postDelayed({
            val mainActivityIntent = Intent(this@SplashScreen, MainActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        }, 1000)
    }
}
