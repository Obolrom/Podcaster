package io.obolonsky.network.di.providers

import io.obolonsky.network.apihelpers.GetLaunchNextApiHelper

interface SpaceXHelpersProvider {

    val getLaunchNextApiHelper: GetLaunchNextApiHelper
}