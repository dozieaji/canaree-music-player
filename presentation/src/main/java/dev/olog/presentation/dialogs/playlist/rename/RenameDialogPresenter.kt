package dev.olog.presentation.dialogs.playlist.rename

import dev.olog.core.MediaId
import dev.olog.core.entity.PlaylistType
import dev.olog.core.interactor.playlist.GetPlaylistsUseCase
import dev.olog.msc.domain.interactor.dialog.RenameUseCase
import javax.inject.Inject

class RenameDialogPresenter @Inject constructor(
    private val mediaId: MediaId,
    getPlaylistSiblingsUseCase: GetPlaylistsUseCase,
    private val renameUseCase: RenameUseCase

) {

    private val existingPlaylists = getPlaylistSiblingsUseCase
        .execute(if (mediaId.isPodcast) PlaylistType.PODCAST else PlaylistType.TRACK)
        .map { it.title }
        .map { it.toLowerCase() }

    fun execute(newTitle: String) {
        TODO()
//        return renameUseCase(mediaId, newTitle)
    }

    /**
     * returns false if is invalid
     */
    fun checkData(playlistTitle: String): Boolean {
        return when {
            mediaId.isPlaylist || mediaId.isPodcastPlaylist -> !existingPlaylists.contains(
                playlistTitle.toLowerCase()
            )
            else -> throw IllegalArgumentException("invalid media id category $mediaId")
        }
    }

}