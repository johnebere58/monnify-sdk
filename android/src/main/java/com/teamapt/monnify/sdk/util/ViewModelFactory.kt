package com.teamapt.monnify.pos.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

object ViewModelFactory {

    fun build(viewModelConstructor: ViewModelConstructor): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return viewModelConstructor.create() as T
            }
        }
    }

}
