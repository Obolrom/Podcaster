package io.obolonsky.podcaster.di.components

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import io.obolonsky.core.di.depsproviders.ToolsProvider
import io.obolonsky.core.di.scopes.ApplicationScope
import io.obolonsky.podcaster.di.modules.ToolsModule

@ApplicationScope
@Component(
    modules = [ToolsModule::class]
)
interface ToolsComponent : ToolsProvider {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance appCtx: Context): ToolsComponent
    }

    companion object {

        fun create(appCtx: Context): ToolsComponent {
            return DaggerToolsComponent.factory()
                .create(appCtx)
        }
    }
}