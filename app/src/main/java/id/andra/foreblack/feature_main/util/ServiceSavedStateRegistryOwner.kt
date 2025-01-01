package id.andra.foreblack.feature_main.util

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

class ServiceSavedStateRegistryOwner : SavedStateRegistryOwner {

    private val _lifecycle: LifecycleRegistry = LifecycleRegistry(this)
    private val _savedStateRegistryController: SavedStateRegistryController =
        SavedStateRegistryController.create(this)

    init {
        _savedStateRegistryController.performAttach()
    }

    override val lifecycle: Lifecycle
        get() = _lifecycle

    override val savedStateRegistry: SavedStateRegistry
        get() = _savedStateRegistryController.savedStateRegistry

    fun performSave(outState: Bundle) {
        _savedStateRegistryController.performSave(outState)
    }

    fun performRestore(savedState: Bundle?) {
        _savedStateRegistryController.performRestore(savedState)
    }

    fun start() {
        _lifecycle.currentState = Lifecycle.State.STARTED
    }

    fun stop() {
        _lifecycle.currentState = Lifecycle.State.DESTROYED
    }

}