package com.example.liveinpeace.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.liveinpeace.ui.onboarding.OnboardingAdapter
import com.example.liveinpeace.databinding.ActivityOnboardingBinding
import com.example.liveinpeace.MainActivity
import com.example.liveinpeace.R
import com.example.liveinpeace.data.OnboardingItem
import com.example.liveinpeace.utils.OnboardingPreferences

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingAdapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupOnboardingItems()
        setupIndicators()
        setupButtons()
        setCurrentIndicator(0)
    }

    private fun setupOnboardingItems() {
        onboardingAdapter = OnboardingAdapter(
            listOf(
                OnboardingItem(
                    R.drawable.onboarding1,
                    getString(R.string.judul_onboarding_1)
                ),
                OnboardingItem(
                    R.drawable.onboarding2,
                    getString(R.string.judul_onboarding_2)
                ),
                OnboardingItem(
                    R.drawable.onboarding3,
                    getString(R.string.judul_onboarding_3)
                )
            )
        )

        binding.viewPagerOnboarding.apply {
            adapter = onboardingAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    setCurrentIndicator(position)
                    updateButtonText(position)
                }
            })
        }
    }

    private fun setupButtons() {
        // Setup Skip button
        binding.btnSkip.setOnClickListener {
            startMainActivity()
        }

        // Setup Next button
        binding.btnNext.setOnClickListener {
            val currentPosition = binding.viewPagerOnboarding.currentItem
            if (currentPosition < onboardingAdapter.itemCount - 1) {
                binding.viewPagerOnboarding.currentItem = currentPosition + 1
            } else {
                // Ini user sudah selesai onboarding
                OnboardingPreferences.setCompleted(this, true)
                startMainActivity()
            }
        }
    }

    private fun updateButtonText(position: Int) {
        binding.btnNext.text = if (position == onboardingAdapter.itemCount - 1) {
            getString(R.string.mulai_btn)
        } else {
            getString(R.string.lanjut_btn)
        }
    }

    private fun setupIndicators() {
        // Indikator sudah diatur dalam layout XML
    }

    private fun setCurrentIndicator(position: Int) {
        val indicators = listOf(
            binding.indicator1,
            binding.indicator2,
            binding.indicator3
        )

        indicators.forEachIndexed { index, view ->
            view.setBackgroundResource(
                if (index == position) R.drawable.active_indicator
                else R.drawable.inactive_indicator
            )
        }
    }

    private fun startMainActivity() {
        // Simpan status onboarding selesai ke SharedPreferences
        OnboardingPreferences.setCompleted(this, true)

        // Mulai MainActivity
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

//package com.example.liveinpeace.ui.onboarding
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.viewpager2.widget.ViewPager2
//import com.example.liveinpeace.R
//import com.example.liveinpeace.data.OnboardingItem
//import com.example.liveinpeace.ui.auth.AuthActivity
//import com.example.liveinpeace.utils.OnboardingPreferences
//
//class OnboardingActivity : AppCompatActivity() {
//
//    private lateinit var viewPager: ViewPager2
//    private lateinit var onboardingItems: List<OnboardingItem>
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_onboarding)
//
//        onboardingItems = listOf(
//            OnboardingItem(R.drawable.onboarding1, "Tenangkan hati dan pikiran"),
//            OnboardingItem(R.drawable.onboarding2, "Kelola stres dan kecemasan dengan pendekatan Islami."),
//            OnboardingItem(R.drawable.onboarding3, "Jaga kesehatan mental & spiritualmu sekarang!")
//        )
//
//        viewPager = findViewById(R.id.viewPagerOnboarding)
//
//        // Initialize the OnboardingAdapter with the onComplete lambda
//        viewPager.adapter = OnboardingAdapter(onboardingItems, viewPager) {
//            onComplete()
//        }
//
//        val skipBtn = findViewById<TextView>(R.id.btnSkip)
//        skipBtn.setOnClickListener {
//            onComplete()  // Handle "Skip" button action
//        }
//    }
//
//    private fun onComplete() {
//        // Save the onboarding completion status
//        OnboardingPreferences.setCompleted(this, true)
//
//        // Navigate to the authentication screen (AuthActivity)
//        startActivity(Intent(this, AuthActivity::class.java))
//        finish()
//    }
//}
