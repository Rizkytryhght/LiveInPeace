package com.example.liveinpeace.ui.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.liveinpeace.R
import com.example.liveinpeace.data.OnboardingItem

class OnboardingAdapter(private val onboardingItems: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        return OnboardingViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.onboarding_item, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(onboardingItems[position])
    }

    override fun getItemCount(): Int {
        return onboardingItems.size
    }

    inner class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageOnboarding = view.findViewById<ImageView>(R.id.imageOnboarding)
        private val textTitle = view.findViewById<TextView>(R.id.textTitle)

        fun bind(onboardingItem: OnboardingItem) {
            imageOnboarding.setImageResource(onboardingItem.imageRes)
            textTitle.text = onboardingItem.title
        }
    }
}

//package com.example.liveinpeace.ui.onboarding
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.view.animation.AnimationUtils
//import android.widget.Button
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import androidx.viewpager2.widget.ViewPager2
//import com.example.liveinpeace.R
//import com.example.liveinpeace.data.OnboardingItem
//
//class OnboardingAdapter(
//    private val items: List<OnboardingItem>,
//    private val viewPager: ViewPager2,
//    private val onComplete: () -> Unit // Add the onComplete lambda here
//) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {
//
//    inner class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val image: ImageView = view.findViewById(R.id.imageOnboarding)
//        val text: TextView = view.findViewById(R.id.textTitle)
//        val button: Button = view.findViewById(R.id.btnNext)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.onboarding_item, parent, false)
//        return OnboardingViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
//        val item = items[position]
//        holder.image.setImageResource(item.imageRes)
//        holder.text.text = item.title
//
//        // Add fade and slide-in animation
//        val anim = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_slide_in)
//        holder.image.startAnimation(anim)
//
//        // Handle button click logic
//        holder.button.setOnClickListener {
//            if (position < items.size - 1) {
//                // Move to the next page
//                viewPager.currentItem = position + 1
//            } else {
//                // Trigger the onComplete callback when on the last page
//                onComplete()
//            }
//        }
//    }
//
//    override fun getItemCount(): Int = items.size
//}
