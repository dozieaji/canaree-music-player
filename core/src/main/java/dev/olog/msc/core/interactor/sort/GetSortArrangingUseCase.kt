package dev.olog.msc.core.interactor.sort

import dev.olog.msc.core.entity.sort.SortArranging
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class GetSortArrangingUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: AppPreferencesGateway

) : ObservableUseCase<SortArranging>(scheduler) {

    override fun buildUseCaseObservable(): Observable<SortArranging> {
        return gateway.getSortArranging()
    }
}