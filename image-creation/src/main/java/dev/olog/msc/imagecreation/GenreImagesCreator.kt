package dev.olog.msc.imagecreation

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.track.Genre
import dev.olog.msc.imagecreation.impl.MergedImagesCreator
import dev.olog.msc.imageprovider.ImagesFolderUtils
import dev.olog.msc.shared.utils.assertBackgroundThread
import io.reactivex.Flowable
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI

internal class GenreImagesCreator @Inject constructor(
        @ApplicationContext private val ctx: Context,
        private val imagesThreadPool: ImagesThreadPool

) {


    fun execute(genres: List<Genre>) : Flowable<*> {
        return Flowable.fromIterable(genres)
                .observeOn(imagesThreadPool.scheduler)
                .parallel()
                .runOn(imagesThreadPool.scheduler)
                .map {
                    val uri = MediaStore.Audio.Genres.Members.getContentUri("external", it.id)
                    Pair(it, CommonQuery.extractAlbumIdsFromSongs(ctx.contentResolver, uri))
                }
                .map { (genre, albumsId) -> try {
                    makeImage(genre, albumsId)
                } catch (ex: Exception){ false }
                }
                .sequential()
                .buffer(10)
                .filter { it.reduce { acc, curr -> acc || curr } }
                .doOnNext {
                    ctx.contentResolver.notifyChange(MEDIA_STORE_URI, null)
                }
    }

    private fun makeImage(genre: Genre, albumsId: List<Long>) : Boolean {
        assertBackgroundThread()
        val folderName = ImagesFolderUtils.GENRE
        return MergedImagesCreator.makeImages(ctx, albumsId, folderName, "${genre.id}")
    }

}