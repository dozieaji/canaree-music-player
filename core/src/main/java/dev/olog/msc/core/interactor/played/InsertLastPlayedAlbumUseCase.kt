package dev.olog.msc.core.interactor.played

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.AlbumGateway
import dev.olog.msc.core.gateway.PodcastAlbumGateway
import dev.olog.msc.core.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class InsertLastPlayedAlbumUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val albumGateway: AlbumGateway,
        private val podcastGateway: PodcastAlbumGateway

): CompletableUseCaseWithParam<MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Completable {
        if (mediaId.isPodcastAlbum){
            return podcastGateway.addLastPlayed(mediaId.categoryValue.toLong())
        }
        return albumGateway.addLastPlayed(mediaId.categoryValue.toLong())
    }
}