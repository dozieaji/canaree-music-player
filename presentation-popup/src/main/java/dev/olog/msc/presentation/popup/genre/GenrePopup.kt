package dev.olog.msc.presentation.popup.genre

import android.view.View

import dev.olog.msc.core.entity.track.Genre
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.msc.presentation.popup.R
import dev.olog.msc.shared.TrackUtils

@Suppress("UNUSED_PARAMETER")
internal class GenrePopup(
        view: View,
        genre: Genre,
        song: Song?,
        listener: AbsPopupListener

) : AbsPopup(view) {

    init {
        if (song == null){
            inflate(R.menu.dialog_genre)
        } else {
            inflate(R.menu.dialog_song)
        }

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        if (song != null){
            if (song.artist == TrackUtils.UNKNOWN_ARTIST){
                menu.removeItem(R.id.viewArtist)
            }
            if (song.album == TrackUtils.UNKNOWN_ALBUM){
                menu.removeItem(R.id.viewAlbum)
            }
        }
    }

}