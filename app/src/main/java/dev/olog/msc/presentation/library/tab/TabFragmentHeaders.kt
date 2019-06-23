package dev.olog.msc.presentation.library.tab

import android.content.res.Resources
import dev.olog.msc.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.core.MediaId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TabFragmentHeaders @Inject constructor(
        resources: Resources
) {

    val allPlaylistHeader = DisplayableItem(
        R.layout.item_tab_header,
        MediaId.headerId("all playlist"), resources.getString(R.string.tab_all_playlists)
    )

    val autoPlaylistHeader = DisplayableItem(
        R.layout.item_tab_header,
        MediaId.headerId("auto playlist"), resources.getString(R.string.tab_auto_playlists)
    )

    val shuffleHeader = DisplayableItem(
        R.layout.item_tab_shuffle,
        MediaId.headerId("tab shuffle"), ""
    )

    val allAlbumsHeader = listOf(
        DisplayableItem(
            R.layout.item_tab_header,
            MediaId.headerId("all albums"),
            resources.getString(R.string.tab_all_albums)
        )
    )

    val allArtistsHeader = listOf(
        DisplayableItem(
            R.layout.item_tab_header,
            MediaId.headerId("all artists"),
            resources.getString(R.string.tab_all_artists)
        )
    )

    val lastPlayedAlbumHeaders = listOf(
        DisplayableItem(
            R.layout.item_tab_header,
            MediaId.headerId("recent albums"),
            resources.getString(R.string.tab_recent_played)
        ),
        DisplayableItem(
            R.layout.item_tab_last_played_album_horizontal_list,
            MediaId.headerId("horiz list album"),
            ""
        )
    )

    val lastPlayedArtistHeaders = listOf(
        DisplayableItem(
            R.layout.item_tab_header,
            MediaId.headerId("recent artists"),
            resources.getString(R.string.tab_recent_played)
        ),
        DisplayableItem(
            R.layout.item_tab_last_played_artist_horizontal_list,
            MediaId.headerId("horiz list artist"),
            ""
        )
    )

    val recentlyAddedAlbumsHeaders = listOf(
        DisplayableItem(
            R.layout.item_tab_header, MediaId.headerId("new albums"),
            resources.getStringArray(R.array.tab_new_items)[0]
        ),
        DisplayableItem(
            R.layout.item_tab_new_album_horizontal_list,
            MediaId.headerId("horiz list new albums"),
            ""
        )
    )

    val recentlyAddedArtistsHeaders = listOf(
        DisplayableItem(
            R.layout.item_tab_header, MediaId.headerId("new artists"),
            resources.getStringArray(R.array.tab_new_items)[1]
        ),
        DisplayableItem(
            R.layout.item_tab_new_artist_horizontal_list,
            MediaId.headerId("horiz list new artists"),
            ""
        )
    )

}