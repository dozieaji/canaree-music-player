package dev.olog.msc.presentation.create.playlist.model

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.create.playlist.R
import dev.olog.msc.shared.TrackUtils

internal fun Podcast.toDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_choose_track,
        MediaId.podcastId(this.id),
        this.title,
        TrackUtils.adjustArtist(this.artist),
        this.image,
        true
    )
}

internal fun Song.toDisplayableItem(): DisplayableItem {
    return DisplayableItem(
        R.layout.item_choose_track,
        MediaId.songId(this.id),
        this.title,
        TrackUtils.adjustArtist(this.artist),
        this.image,
        true
    )
}