package dev.olog.presentation._base

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.support.annotation.CallSuper
import android.support.v7.util.DiffUtil
import dev.olog.presentation.Diff
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.Header
import dev.olog.presentation.model.toDisplayableItem
import dev.olog.shared.cleanThenAdd
import dev.olog.shared.unsubscribe
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers

class BaseAdapterController (
        private val adapter: BaseAdapter<*>

) : DefaultLifecycleObserver, IAdapterController<List<DisplayableItem>> {

    private var dataSetDisposable: Disposable? = null

    private val publisher = PublishProcessor.create<AdapterData<MutableList<DisplayableItem>>>()

    private val originalList = mutableListOf<DisplayableItem>()
    private val dataSet = mutableListOf<DisplayableItem>()

    private var dataVersion = 0

    init {
        dataSet.addAll(0, createActualHeaders())
    }

    override operator fun get(position: Int): DisplayableItem = dataSet[position]

    override fun getSize() : Int = dataSet.size

    private val onDataChanged = publisher
            .toSerialized()
            .observeOn(Schedulers.computation())
            .onBackpressureLatest()
//            .distinctUntilChanged { data -> data.list }
            .replay(1)
            .refCount()

    override fun onStart(owner: LifecycleOwner) {
        dataSetDisposable = onDataChanged
                .observeOn(Schedulers.computation())
                .map {
                    it.list.addAll(0, createActualHeaders())
                    it
                }
                .filter { it.dataVersion == dataVersion }
                .map { it.to(DiffUtil.calculateDiff(Diff(dataSet, it.list))) }
                .filter { it.first.dataVersion == dataVersion }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ (newAdapterData, callback) ->

                    val wasEmpty = this.dataSet.isEmpty()

                    this.dataSet.cleanThenAdd(newAdapterData.list)

                    if (wasEmpty || !adapter.hasGranularUpdate()) {
                        adapter.notifyDataSetChanged()
                    } else {
                        callback.dispatchUpdatesTo(adapter)
                    }

                }, Throwable::printStackTrace)
    }


    override fun onDataChanged(): Flowable<List<DisplayableItem>> {
        return onDataChanged.map { it.list }
    }

    @CallSuper
    override fun onStop(owner: LifecycleOwner) {
        dataSetDisposable.unsubscribe()
    }

    private fun createActualHeaders(): List<DisplayableItem> {
        return adapter.provideHeaders().mapIndexed { index: Int, header: Header ->
            header.toDisplayableItem(index)
        }
    }

    override fun onNext(data: List<DisplayableItem>) {
        dataVersion++
        this.originalList.cleanThenAdd(data)
        publisher.onNext(AdapterData(originalList.toMutableList(), dataVersion))
    }

    override fun getDataSet(): List<DisplayableItem> = dataSet
}