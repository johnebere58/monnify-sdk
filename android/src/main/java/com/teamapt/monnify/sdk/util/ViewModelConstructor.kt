package com.teamapt.monnify.pos.util

import androidx.lifecycle.ViewModel

interface ViewModelConstructor {
    fun create(): ViewModel
}
