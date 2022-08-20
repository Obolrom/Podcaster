package io.obolonsky.core.di

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.obolonsky.core.di.utils.ToastUtil
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface ToasterProperty<in R : Any, out T : Any> : ReadOnlyProperty<R, T>, ToastUtil

fun AppCompatActivity.toaster(
    eventToClean: Lifecycle.Event = Lifecycle.Event.ON_DESTROY,
): Lazy<ToastUtil> = lazy {
    Toaster.apply {
        eventToCleanToast = eventToClean
        this@toaster.lifecycle.addObserver(this)
    }
}

fun Fragment.toaster(
    eventToClean: Lifecycle.Event = Lifecycle.Event.ON_DESTROY,
): Lazy<ToastUtil> = lazy {
    Toaster.apply {
        eventToCleanToast = eventToClean
        this@toaster.viewLifecycleOwner.lifecycle.addObserver(this)
    }
}

private object Toaster : LifecycleEventObserver, ToasterProperty<Any, Toaster> {

    private var toast: Toast? = null
    var eventToCleanToast = Lifecycle.Event.ON_DESTROY

    override fun showToast(context: Context, message: String) {
        toast?.cancel()
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast?.show()
    }

    override fun clear() {
        toast?.cancel()
        toast = null
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == eventToCleanToast) {
            clear()
            source.lifecycle.removeObserver(this)
        }
    }

    override fun getValue(thisRef: Any, property: KProperty<*>) = this
}