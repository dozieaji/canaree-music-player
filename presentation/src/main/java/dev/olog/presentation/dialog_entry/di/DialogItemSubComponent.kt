package dev.olog.presentation.dialog_entry.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.dialog_entry.DialogItemFragment

@Subcomponent(modules = arrayOf(
        DialogItemFragmentModule::class,
        ItemModule::class
))
@PerFragment
interface DialogItemSubComponent : AndroidInjector<DialogItemFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<DialogItemFragment>() {

        abstract fun module(module: DialogItemFragmentModule): Builder

        override fun seedInstance(instance: DialogItemFragment) {
            module(DialogItemFragmentModule(instance))
        }
    }

}