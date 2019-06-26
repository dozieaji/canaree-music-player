package dev.olog.msc.presentation.detail

import android.content.Context
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.sort.SortType
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.*
import dev.olog.msc.R
import dev.olog.msc.domain.interactor.all.most.played.ObserveMostPlayedSongsUseCase
import dev.olog.msc.domain.interactor.all.recently.added.ObserveRecentlyAddedUseCase
import dev.olog.msc.domain.interactor.all.related.artists.ObserveRelatedArtistsUseCase
import dev.olog.msc.domain.interactor.all.sorted.util.ObserveDetailSortOrderUseCase
import dev.olog.msc.presentation.detail.DetailFragmentViewModel.Companion.VISIBLE_RECENTLY_ADDED_PAGES
import dev.olog.msc.presentation.detail.mapper.*
import dev.olog.msc.presentation.detail.mapper.toHeaderItem
import dev.olog.msc.presentation.detail.mapper.toMostPlayedDetailDisplayableItem
import dev.olog.msc.presentation.detail.mapper.toRecentDetailDisplayableItem
import dev.olog.msc.presentation.detail.mapper.toRelatedArtist
import dev.olog.msc.utils.TimeUtils
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.extensions.combineLatest
import dev.olog.shared.extensions.mapListItem
import dev.olog.shared.extensions.startWith
import dev.olog.shared.utils.TextUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import java.util.function.BiFunction
import javax.inject.Inject

internal class DetailDataProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val headers: DetailFragmentHeaders,
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway2,
    private val songGateway: SongGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,
    // podcast
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val podcastGateway: PodcastGateway,
    private val podcastAlbumGateway: PodcastAlbumGateway,
    private val podcastArtistGateway: PodcastArtistGateway,

    private val recentlyAddedUseCase: ObserveRecentlyAddedUseCase,
    private val mostPlayedUseCase: ObserveMostPlayedSongsUseCase,
    private val relatedArtistsUseCase: ObserveRelatedArtistsUseCase,
    private val sortOrderUseCase: ObserveDetailSortOrderUseCase
) {

    private val resources = context.resources


    fun observeHeader(mediaId: MediaId): Flow<DisplayableItem> = when (mediaId.category) {
        MediaIdCategory.FOLDERS -> folderGateway.observeByParam(mediaId.categoryValue).mapNotNull {
            it?.toHeaderItem(
                resources
            )
        }
        MediaIdCategory.PLAYLISTS -> playlistGateway.observeByParam(mediaId.categoryId).mapNotNull {
            it?.toHeaderItem(
                resources
            )
        }
        MediaIdCategory.ALBUMS -> albumGateway.observeByParam(mediaId.categoryId).mapNotNull { it?.toHeaderItem() }
        MediaIdCategory.ARTISTS -> artistGateway.observeByParam(mediaId.categoryId).mapNotNull {
            it?.toHeaderItem(
                resources
            )
        }
        MediaIdCategory.GENRES -> genreGateway.observeByParam(mediaId.categoryId).mapNotNull {
            it?.toHeaderItem(
                resources
            )
        }
        MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.observeByParam(mediaId.categoryId).mapNotNull {
            it?.toHeaderItem(
                resources
            )
        }
        MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumGateway.observeByParam(mediaId.categoryId).mapNotNull { it?.toHeaderItem() }
        MediaIdCategory.PODCASTS_ARTISTS -> podcastArtistGateway.observeByParam(mediaId.categoryId).mapNotNull {
            it?.toHeaderItem(
                resources
            )
        }
        MediaIdCategory.HEADER,
        MediaIdCategory.PLAYING_QUEUE,
        MediaIdCategory.SONGS,
        MediaIdCategory.PODCASTS -> throw IllegalArgumentException("invalid category=$mediaId")
    }

    fun observe(mediaId: MediaId): Flow<List<DisplayableItem>> {
        val songListFlow = if (mediaId.isAnyPodcast) {
            podcastGateway.observeAll()
        } else {
            songGateway.observeAll()
        }.combineLatest(sortOrderUseCase(mediaId)) { songList: List<Song>, order: SortType ->
            val result = songList.map { it.toDetailDisplayableItem(mediaId, order) }.toMutableList()
            val duration = songList.sumBy { it.duration.toInt() }
            if (result.isNotEmpty()){
                result.addAll(0, headers.songs)
                result.add(createDurationFooter(result.size, duration))
            } else {
                result.add(headers.no_songs)
            }

            result
        }
        return observeHeader(mediaId).combineLatest(
            observeSiblings(mediaId).map { if(it.isNotEmpty()) headers.albums() else listOf() },
            observeMostPlayed(mediaId).map { if(it.isNotEmpty()) headers.mostPlayed else listOf() },
            observeRecentlyAdded(mediaId).map { if(it.isNotEmpty()) headers.recent(it.size, it.size > VISIBLE_RECENTLY_ADDED_PAGES) else listOf() },
            songListFlow,
            observeRelatedArtists(mediaId).map { if(it.isNotEmpty()) headers.relatedArtists(it.size > 10) else listOf() }
        ) { header, siblings, mostPlayed, recentlyAdded, songList, relatedArtists ->
            if (mediaId.isArtist){
                (siblings + mostPlayed + recentlyAdded + songList + recentlyAdded).startWith(header)
            } else {
                (mostPlayed + recentlyAdded + songList + recentlyAdded + siblings).startWith(header)
            }
        }
    }

    fun observeMostPlayed(mediaId: MediaId): Flow<List<DisplayableItem>> {
        return mostPlayedUseCase(mediaId).mapListItem { it.toMostPlayedDetailDisplayableItem(mediaId) }
    }

    fun observeRecentlyAdded(mediaId: MediaId): Flow<List<DisplayableItem>> {
        return recentlyAddedUseCase(mediaId).mapListItem { it.toRecentDetailDisplayableItem(mediaId) }
    }

    fun observeRelatedArtists(mediaId: MediaId): Flow<List<DisplayableItem>> {
        return relatedArtistsUseCase(mediaId).mapListItem { it.toRelatedArtist(resources) }
    }

    fun observeSiblings(mediaId: MediaId): Flow<List<DisplayableItem>> = when (mediaId.category){
        MediaIdCategory.FOLDERS -> folderGateway.observeSiblings(mediaId.categoryValue).mapListItem { it.toDetailDisplayableItem(resources) }
        MediaIdCategory.PLAYLISTS -> playlistGateway.observeSiblings(mediaId.categoryId).mapListItem { it.toDetailDisplayableItem(resources) }
        MediaIdCategory.ALBUMS -> albumGateway.observeSiblings(mediaId.categoryId).mapListItem { it.toDetailDisplayableItem(resources) }
        MediaIdCategory.ARTISTS -> albumGateway.observeArtistsAlbums(mediaId.categoryId).mapListItem { it.toDetailDisplayableItem(resources) }
        MediaIdCategory.GENRES -> genreGateway.observeSiblings(mediaId.categoryId).mapListItem { it.toDetailDisplayableItem(resources) }
        // podcasts
        MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistGateway.observeSiblings(mediaId.categoryId).mapListItem { it.toDetailDisplayableItem(resources) }
        MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumGateway.observeSiblings(mediaId.categoryId).mapListItem { it.toDetailDisplayableItem(resources) }
        MediaIdCategory.PODCASTS_ARTISTS -> podcastAlbumGateway.observeArtistsAlbums(mediaId.categoryId).mapListItem { it.toDetailDisplayableItem(resources) }
        else -> throw IllegalArgumentException("invalid category=$mediaId")
    }

    private fun createDurationFooter(songCount: Int, duration: Int): DisplayableItem {
        val songs = DisplayableItem.handleSongListSize(resources, songCount)
        val time = TimeUtils.formatMillis(context, duration)

        return DisplayableItem(
            R.layout.item_detail_footer, MediaId.headerId("duration footer"),
            songs + TextUtils.MIDDLE_DOT_SPACED + time
        )
    }

}