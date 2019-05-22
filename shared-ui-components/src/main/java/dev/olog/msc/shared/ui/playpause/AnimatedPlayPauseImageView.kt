package dev.olog.msc.shared.ui.playpause

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton

class AnimatedPlayPauseImageView : AppCompatImageButton, IPlayPauseBehavior {

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, androidx.appcompat.R.attr.imageButtonStyle)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize()
    }

    private val behavior = PlayPauseBehaviorImpl(this)

    private fun initialize() {
//        when {
//            context.isPortrait && (context.applicationContext as HasPlayerTheme).isClean() && !(context.applicationContext as HasDarkMode).isDark() -> 0xFF_8d91a6.toInt()
//            (context.applicationContext as HasPlayerTheme).isFullscreen() || (context.applicationContext as HasDarkMode).isDark() -> Color.WHITE
//            else -> context.textColorTertiary()
//        } TODO
//        TODO use light color (0xFF_F5F5F5) when fullscreen, do the same in animated image view
    }

    override fun animationPlay(animate: Boolean) {
        behavior.animationPlay(animate)
    }

    override fun animationPause(animate: Boolean) {
        behavior.animationPause(animate)
    }

}