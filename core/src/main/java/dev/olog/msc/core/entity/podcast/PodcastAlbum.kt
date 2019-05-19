package dev.olog.msc.core.entity.podcast

data class PodcastAlbum(
    val id: Long,
    val artistId: Long,
    val title: String,
    val artist: String,
    val albumArtist: String,
    val songs: Int
)