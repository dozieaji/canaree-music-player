package dev.olog.msc.core.interactor.item

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.data.request.ItemRequest
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.gateway.track.SongGateway
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetSongUseCase @Inject internal constructor(
    private val schedulers: ComputationDispatcher,
    private val gateway: SongGateway

) {

    suspend fun execute(mediaId: MediaId): ItemRequest<Song> = withContext(schedulers.worker) {
        gateway.getByParam(mediaId.leaf!!)
    }
}