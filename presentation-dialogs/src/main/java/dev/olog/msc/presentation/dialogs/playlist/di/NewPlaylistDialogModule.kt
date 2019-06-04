package dev.olog.msc.presentation.dialogs.playlist.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.dialogs.playlist.NewPlaylistDialogViewModel
import dev.olog.msc.shared.dagger.ViewModelKey


@Module
abstract class NewPlaylistDialogModule {

    @Binds
    @IntoMap
    @ViewModelKey(NewPlaylistDialogViewModel::class)
    abstract fun provideViewModel(viewModel: NewPlaylistDialogViewModel): ViewModel


}
