package com.example.behindsystembar

import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.core.view.isGone
import com.example.behindsystembar.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var startX = 0f
    private var startY = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        binding.image.alpha = 0f
        window.decorView.setOnApplyWindowInsetsListener { view, insets ->
            val margins = binding.toolbar.layoutParams as ViewGroup.MarginLayoutParams
            margins.setMargins(
                margins.leftMargin,
                insets.stableInsetTop,
                margins.rightMargin,
                margins.bottomMargin
            )

            binding.toolbar.requestLayout()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowInsets.CONSUMED
            } else {
                insets.consumeSystemWindowInsets()
            }

        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Sup"
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        with(binding) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    helpText.isGone = true
                    image.alpha = 0f
                    toolbar.alpha = 1f
                    toolbarParent.alpha = 1f
                    startX = event.rawX
                    startY = event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    val alphaValue = maxOf((event.rawY - startY) / 1000, 0f).coerceAtMost(1f)
                    text.text =
                        "X: ${event.rawX - startX} Y: ${(event.rawY - startY) / 100} alpha: $alphaValue"
                    image.alpha = (alphaValue * 1.2f).coerceAtMost(1f)
                    toolbarParent.alpha = 1f - alphaValue
                    setSystemBarTextColor(toolbarParent.alpha)
                }
                else -> {}
            }
        }
        return true
    }

    private fun setSystemBarTextColor(alpha: Float) {
        window?.decorView?.apply {
            systemUiVisibility = if((alpha <= 0.5 && isNightMode()) || (alpha > 0.5 && !isNightMode())) {
                systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            } else {
                systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    private fun isNightMode(): Boolean {
        return applicationContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

}