package dev.olog.presentation.fragment_detail

import android.content.Context
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.ApplicationContext
import dev.olog.shared.MediaIdHelper
import javax.inject.Inject

class DetailHeaders @Inject constructor(
        @ApplicationContext private val context: Context,
        mediaId: String
) {

    private val source = MediaIdHelper.mapCategoryToSource(mediaId)

    val mostPlayed = listOf(
            DisplayableItem(R.layout.item_header, "most played id", context.getString(R.string.detail_most_played)),
            DisplayableItem(R.layout.item_most_played_horizontal_list, "most played list", "")
    )

    val recent = listOf(
            DisplayableItem(R.layout.item_header, "recent id", context.getString(R.string.detail_recently_added)),
            DisplayableItem(R.layout.item_recent_horizontal_list, "recent list", "")
    )

    val recentWithSeeAll = listOf(
            DisplayableItem(R.layout.item_header, "recent id", context.getString(R.string.detail_recently_added), context.getString(R.string.detail_see_all)),
            DisplayableItem(R.layout.item_recent_horizontal_list, "recent list", "")
    )

    val albums : DisplayableItem = DisplayableItem(R.layout.item_header, "albums id",
            context.resources.getStringArray(R.array.detail_album_header)[source])

    val albumsWithSeeAll : DisplayableItem = DisplayableItem(R.layout.item_header, "albums id",
            context.resources.getStringArray(R.array.detail_album_header)[source],
            context.getString(R.string.detail_see_all))

    val songs = listOf(
            DisplayableItem(R.layout.item_header, "songs id", context.getString(R.string.detail_songs)),
            DisplayableItem(R.layout.item_shuffle_with_divider, "shuffle id", "")
    )

}