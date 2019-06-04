package dev.olog.msc.presentation.edititem.artist

import dev.olog.msc.core.entity.podcast.PodcastArtist
import dev.olog.msc.core.entity.track.Artist
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.interactor.GetSongListChunkByParamUseCase
import dev.olog.msc.core.interactor.item.GetArtistUseCase
import dev.olog.msc.core.interactor.item.GetPodcastArtistUseCase
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class EditArtistFragmentPresenter @Inject constructor(
    private val getArtistUseCase: GetArtistUseCase,
    private val getPodcastArtistUseCase: GetPodcastArtistUseCase,
    private val getSongListByParamUseCase: GetSongListChunkByParamUseCase

) {

    private lateinit var originalArtist: DisplayableArtist
    lateinit var songList: List<Song>

    fun observeArtist(): DisplayableArtist {
//        if (mediaId.isPodcastArtist) {
//            return getPodcastArtistInternal()
//        }
        return getArtistInternal()
    }

    private fun getArtistInternal(): DisplayableArtist = runBlocking {
        TODO()
//        getArtistUseCase.execute(mediaId).asObservable()
//                .firstOrError()
//                .map { it.toDisplayableArtist() }
//                .doOnSuccess { originalArtist = it }
    }

    private fun getPodcastArtistInternal(): DisplayableArtist = runBlocking {
        TODO()
//        getPodcastArtistUseCase.execute(mediaId).asObservable()
//                .firstOrError()
//                .map { it.toDisplayableArtist() }
//                .doOnSuccess { originalArtist = it }
    }

    fun getSongListSingle(): List<Song> {
        TODO()
//        return getSongListByParamUseCase.execute(mediaId)
//                .firstOrError()
//                .doOnSuccess { songList = it }
    }

    fun getArtist(): DisplayableArtist = originalArtist

    private fun Artist.toDisplayableArtist(): DisplayableArtist {
        return DisplayableArtist(
            this.id,
            this.name,
            this.albumArtist
        )
    }

    private fun PodcastArtist.toDisplayableArtist(): DisplayableArtist {
        return DisplayableArtist(
            this.id,
            this.name,
            this.albumArtist
        )
    }

}