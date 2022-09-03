package io.obolonsky.network.di.providers

import io.obolonsky.network.apihelpers.GetLaunchNextApiHelper
import io.obolonsky.network.apihelpers.GetRocketDetailsApiHelper

interface SpaceXHelpersProvider {

    val getLaunchNextApiHelper: GetLaunchNextApiHelper

    val getRocketDetailsApiHelper: GetRocketDetailsApiHelper
}