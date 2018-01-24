package dev.olog.presentation.activity_neural_network

import android.app.AlertDialog
import android.arch.lifecycle.Lifecycle
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import dev.olog.presentation.GlideApp
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.activity_neural_network.image_chooser.NeuralNetworkImageChooser
import dev.olog.presentation.activity_neural_network.service.NeuralNetworkService
import dev.olog.presentation.activity_neural_network.style_chooser.NeuralNetworkStyleChooser
import dev.olog.presentation.utils.extension.makeDialog
import dev.olog.presentation.utils.extension.subscribe
import dev.olog.shared.unsubscribe
import dev.olog.shared_android.ImageUtils
import dev.olog.shared_android.extension.asLiveData
import dev.olog.shared_android.neural.NeuralImages
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_neural_network_result_chooser.view.*
import org.jetbrains.anko.toast
import java.lang.ref.WeakReference
import javax.inject.Inject

private const val HIGHLIGHT_STYLIZE_ALL = "HIGHLIGHT_STYLIZE_ALL"

class NeuralNetworkFragment : BaseFragment() {

    @Inject lateinit var viewModel: NeuralNetworkActivityViewModel
    private var stylezedImageDisposable: Disposable? = null
    private var toastRef : WeakReference<Toast>? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.currentNeuralImage.subscribe(this, {

            view!!.chooseImage.visibility = View.GONE

            GlideApp.with(this)
                    .load(Uri.parse(it))
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .override(300)
                    .priority(Priority.IMMEDIATE)
                    .into(view!!.cover)

        })

        viewModel.currentNeuralStyle.subscribe(this, {

            val uri = NeuralImages.getThumbnail(it)

            view!!.chooseStyle.visibility = View.GONE

            GlideApp.with(this)
                    .load(uri)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .override(300)
                    .priority(Priority.IMMEDIATE)
                    .into(view!!.style)
        })

        viewModel.observeImageLoadedSuccesfully
                .asLiveData()
                .subscribe(this, { pair ->
                    val (image, style) = pair

            stylezedImageDisposable.unsubscribe()
            stylezedImageDisposable = Single.create<Bitmap> { emitter ->

                val bitmap = NeuralImages.stylizeTensorFlow(activity!!,
                        ImageUtils.getBitmapFromUri(activity!!, Uri.parse(image))!!, size = 768)
                emitter.onSuccess(bitmap)

            }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { view!!.progressBar.visibility = View.VISIBLE }
                    .doOnEvent {  _,_ -> view!!.progressBar.visibility = View.GONE }
                    .subscribe({ bitmap ->

                        GlideApp.with(this)
                                .load(bitmap)
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .priority(Priority.IMMEDIATE)
                                .listener(object : RequestListener<Drawable>{
                                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                                        return false
                                    }

                                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                        highlightStylizeAll()
                                        return false
                                    }
                                })
                                .into(view!!.preview)

                    }, Throwable::printStackTrace)

        })
    }

    private fun highlightStylizeAll(){
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)){
            return
        }
        val preferences = PreferenceManager.getDefaultSharedPreferences(activity!!)
        val hightlight = preferences.getBoolean(HIGHLIGHT_STYLIZE_ALL, true)
        if (!hightlight){
            return
        }

        val text = getString(R.string.neural_stylize_all_description)
        val tapTarget = TapTarget.forView(view!!.stylize, text)

        TapTargetView.showFor(activity, tapTarget, object : TapTargetView.Listener(){
            override fun onTargetLongClick(view: TapTargetView?) {
                super.onTargetLongClick(view)
                updateHighlightPrefs()
            }

            override fun onOuterCircleClick(view: TapTargetView?) {
                super.onOuterCircleClick(view)
                updateHighlightPrefs()
            }

            override fun onTargetCancel(view: TapTargetView?) {
                super.onTargetCancel(view)
                updateHighlightPrefs()
            }

            override fun onTargetDismissed(view: TapTargetView?, userInitiated: Boolean) {
                super.onTargetDismissed(view, userInitiated)
                updateHighlightPrefs()
            }

            override fun onTargetClick(view: TapTargetView?) {
                super.onTargetClick(view)
                updateHighlightPrefs()
            }
        })
    }

    private fun updateHighlightPrefs(){
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)){
            return
        }
        PreferenceManager.getDefaultSharedPreferences(activity!!)
                .edit()
                .putBoolean(HIGHLIGHT_STYLIZE_ALL, false)
                .apply()
    }

    override fun onResume() {
        super.onResume()
        view!!.style.setOnClickListener {
            NeuralNetworkStyleChooser.newInstance().show(activity!!.supportFragmentManager,
                            NeuralNetworkStyleChooser.TAG)
        }
        view!!.cover.setOnClickListener {
            NeuralNetworkImageChooser.newInstance().show(activity!!.supportFragmentManager,
                            NeuralNetworkImageChooser.TAG)
        }
        view!!.stylize.setOnClickListener {
            if (viewModel.currentNeuralStyle.value != null){
                createNeuralStartServiceRequestDialog()
            } else {
                toastRef?.get()?.cancel() // delete previous
                toastRef = WeakReference(activity!!.toast(R.string.neural_stylize_all_missing_style))
            }
        }
    }

    override fun onPause() {
        super.onPause()
        view!!.style.setOnClickListener(null)
        view!!.cover.setOnClickListener(null)
        view!!.stylize.setOnClickListener(null)
    }

    override fun onStop() {
        super.onStop()
        stylezedImageDisposable.unsubscribe()
    }

    private fun createNeuralStartServiceRequestDialog(){
        AlertDialog.Builder(activity)
                .setTitle(R.string.neural_stylize_all)
                .setMessage(R.string.neural_stylize_all_message)
                .setPositiveButton(R.string.popup_positive_ok, { _, _ ->
                    val intent = Intent(activity, NeuralNetworkService::class.java)
                    intent.action = NeuralNetworkService.ACTION_START
                    intent.putExtra(NeuralNetworkService.EXTRA_STYLE, NeuralImages.getCurrentStyle())
                    ContextCompat.startForegroundService(activity!!, intent)
                    activity!!.onBackPressed()
                })
                .setNegativeButton(R.string.popup_negative_no, null)
                .makeDialog()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_neural_network_result_chooser
}