package dev.olog.msc.presentation.dialogs.delete

import dev.olog.msc.core.MediaId
import io.reactivex.Completable
import javax.inject.Inject

class DeleteDialogPresenter @Inject constructor(
        private val mediaId: MediaId,
        private val deleteUseCase: DeleteUseCase
) {


    fun execute(): Completable {
        return deleteUseCase.execute(mediaId)
    }

}