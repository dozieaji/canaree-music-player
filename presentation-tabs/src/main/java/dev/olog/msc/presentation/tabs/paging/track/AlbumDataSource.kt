package dev.olog.msc.presentation.tabs.paging.track

import androidx.lifecycle.Lifecycle
import androidx.paging.DataSource
import dev.olog.msc.core.coroutines.merge
import dev.olog.msc.core.dagger.qualifier.ActivityLifecycle
import dev.olog.msc.core.entity.Page
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.track.AlbumGateway
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.base.paging.BaseDataSource
import dev.olog.msc.presentation.tabs.TabFragmentHeaders
import dev.olog.msc.presentation.tabs.mapper.toTabDisplayableItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider

internal class AlbumDataSource @Inject constructor(
    @ActivityLifecycle lifecycle: Lifecycle,
    private val gateway: AlbumGateway,
    prefsGateway: AppPreferencesGateway,
    private val displayableHeaders: TabFragmentHeaders
) : BaseDataSource<DisplayableItem>() {

    private val page = gateway.getAll()

    init {
        launch {
            withContext(Dispatchers.Main) { lifecycle.addObserver(this@AlbumDataSource) }
            page.observeNotification()
                .merge(prefsGateway.observeAllAlbumsSortOrder().drop(1))
                .take(1)
                .collect {
                    invalidate()
                }
        }
    }

    override fun getMainDataSize(): Int {
        return page.getCount()
    }

    override fun getHeaders(mainListSize: Int): List<DisplayableItem> {
        val headers = mutableListOf<DisplayableItem>()
        if (gateway.canShowRecentlyAdded()) {
            headers.addAll(displayableHeaders.recentlyAddedAlbumsHeaders)
        }
        if (gateway.canShowLastPlayed()) {
            headers.addAll(displayableHeaders.lastPlayedAlbumHeaders)
        }
        if (headers.isNotEmpty()) {
            headers.addAll(displayableHeaders.allAlbumsHeader)
        }
        return headers
    }

    override fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(page: Page): List<DisplayableItem> {
        return this.page.getPage(page)
            .map { it.toTabDisplayableItem() }
    }

}

internal class AlbumDataSourceFactory @Inject constructor(
    private val dataSource: Provider<AlbumDataSource>
) : DataSource.Factory<Int, DisplayableItem>() {

    override fun create(): DataSource<Int, DisplayableItem> {
        return dataSource.get()
    }
}