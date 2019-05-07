package dev.olog.msc.presentation.detail.domain

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.executors.ComputationScheduler
import dev.olog.msc.core.interactor.GetSongListByParamUseCase
import dev.olog.msc.core.interactor.base.SingleUseCaseWithParam
import io.reactivex.Single
import javax.inject.Inject

class GetTotalSongDurationUseCase @Inject constructor(
        scheduler: ComputationScheduler,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

): SingleUseCaseWithParam<Int, MediaId>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Single<Int> {
        return getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .map { it.sumBy { it.duration.toInt() } }
    }
}