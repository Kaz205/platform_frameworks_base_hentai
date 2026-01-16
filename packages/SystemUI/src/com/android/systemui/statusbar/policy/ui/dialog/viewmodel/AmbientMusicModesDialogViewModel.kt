/*
 * Copyright (C) 2024 The Android Open Source Project
 * Copyright (C) 2026 StatiXOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.statusbar.policy.ui.dialog.viewmodel

import android.content.Context
import android.content.Intent
import com.android.systemui.common.shared.model.Icon
import com.android.systemui.dagger.SysUISingleton
import com.android.systemui.dagger.qualifiers.Background
import com.android.systemui.res.R
import com.android.systemui.qs.tiles.impl.ambientmusicmodes.domain.interactor.AmbientMusicModesTileDataInteractor
import com.android.systemui.statusbar.policy.ui.dialog.AmbientMusicModesDialogDelegate
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Viewmodel for the priority ("zen") modes dialog that can be opened from quick settings. It allows
 * the user to quickly toggle modes.
 */
@SysUISingleton
class AmbientMusicModesDialogViewModel
@Inject
constructor(
    val context: Context,
    @Background val bgDispatcher: CoroutineDispatcher,
    private val dialogDelegate: AmbientMusicModesDialogDelegate,
    private val interactor: AmbientMusicModesTileDataInteractor,
) {
    private val _activeMode = MutableStateFlow<String?>(null)

    val tiles: Flow<List<AmbientMusicModeTileViewModel>> =
        _activeMode.map { activeMode: String? ->
            listOf(
                AmbientMusicModeTileViewModel(
                    id = "calm",
                    icon = Icon.Resource(R.drawable.ic_calm, null),
                    text = "Calm",
                    subtext = if (activeMode == "calm") "On" else "Off",
                    subtextDescription = if (activeMode == "calm") "On" else "Off",
                    enabled = activeMode == "calm",
                    stateDescription = if (activeMode == "calm") "On" else "Off",
                    onClick = {
                        if (activeMode == "calm") {
                            startService(ACTION_TOGGLE_PLAYBACK_QS)
                            _activeMode.value = null
                            interactor.setActiveMode(null)
                        } else {
                            startService(ACTION_PLAY_GENRE_CALM)
                            _activeMode.value = "calm"
                            interactor.setActiveMode("calm")
                        }
                    },
                    onLongClick = {
                        launchApp()
                    },
                    onLongClickLabel = "Long press for settings",
                ),
                AmbientMusicModeTileViewModel(
                    id = "chill",
                    icon = Icon.Resource(R.drawable.ic_chill, null),
                    text = "Chill",
                    subtext = if (activeMode == "chill") "On" else "Off",
                    subtextDescription = if (activeMode == "chill") "On" else "Off",
                    enabled = activeMode == "chill",
                    stateDescription = if (activeMode == "chill") "On" else "Off",
                    onClick = {
                        if (activeMode == "chill") {
                            startService(ACTION_TOGGLE_PLAYBACK_QS)
                            _activeMode.value = null
                            interactor.setActiveMode(null)
                        } else {
                            startService(ACTION_PLAY_GENRE_CHILL)
                            _activeMode.value = "chill"
                            interactor.setActiveMode("chill")
                        }
                    },
                    onLongClick = {
                        launchApp()
                    },
                    onLongClickLabel = "Long press for settings",
                ),
                AmbientMusicModeTileViewModel(
                    id = "sleep",
                    icon = Icon.Resource(R.drawable.ic_sleep, null),
                    text = "Sleep",
                    subtext = if (activeMode == "sleep") "On" else "Off",
                    subtextDescription = if (activeMode == "sleep") "On" else "Off",
                    enabled = activeMode == "sleep",
                    stateDescription = if (activeMode == "sleep") "On" else "Off",
                    onClick = {
                        if (activeMode == "sleep") {
                            startService(ACTION_TOGGLE_PLAYBACK_QS)
                            _activeMode.value = null
                            interactor.setActiveMode(null)
                        } else {
                            startService(ACTION_PLAY_GENRE_SLEEP)
                            _activeMode.value = "sleep"
                            interactor.setActiveMode("sleep")
                        }
                    },
                    onLongClick = {
                        launchApp()
                    },
                    onLongClickLabel = "Long press for settings",
                ),
                AmbientMusicModeTileViewModel(
                    id = "focus",
                    icon = Icon.Resource(R.drawable.ic_focus, null),
                    text = "Focus",
                    subtext = if (activeMode == "focus") "On" else "Off",
                    subtextDescription = if (activeMode == "focus") "On" else "Off",
                    enabled = activeMode == "focus",
                    stateDescription = if (activeMode == "focus") "On" else "Off",
                    onClick = {
                        if (activeMode == "focus") {
                            startService(ACTION_TOGGLE_PLAYBACK_QS)
                            _activeMode.value = null
                            interactor.setActiveMode(null)
                        } else {
                            startService(ACTION_PLAY_GENRE_FOCUS)
                            _activeMode.value = "focus"
                            interactor.setActiveMode("focus")
                        }
                    },
                    onLongClick = {
                        launchApp()
                    },
                    onLongClickLabel = "Long press for settings",
                ),
            )
        }

    private fun startService(mode: String) {
        val intent = Intent(mode).apply{
            setPackage("com.sourajitk.ambient_music")
        }
        context.startForegroundService(intent)
    }

    private fun launchApp() {
        val intent = context.packageManager.getLaunchIntentForPackage("com.sourajitk.ambient_music")
        intent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(it)
        }
    }

    companion object {
        const val ACTION_TOGGLE_PLAYBACK_QS = "com.sourajitk.ambient_music.ACTION_TOGGLE_PLAYBACK_QS"
        const val ACTION_SKIP_TO_NEXT = "com.sourajitk.ambient_music.ACTION_SKIP_TO_NEXT"
        const val ACTION_STOP_SERVICE = "com.sourajitk.ambient_music.ACTION_STOP_SERVICE"
        const val ACTION_PLAY_GENRE_CHILL = "com.sourajitk.ambient_music.ACTION_PLAY_GENRE_CHILL"
        const val ACTION_PLAY_GENRE_CALM = "com.sourajitk.ambient_music.ACTION_PLAY_GENRE_CALM"
        const val ACTION_PLAY_GENRE_SLEEP = "com.sourajitk.ambient_music.ACTION_PLAY_GENRE_SLEEP"
        const val ACTION_PLAY_GENRE_FOCUS = "com.sourajitk.ambient_music.ACTION_PLAY_GENRE_PRODUCTIVITY"
        const val ACTION_PLAY_GENRE_SERENITY = "com.sourajitk.ambient_music.ACTION_PLAY_GENRE_SERENITY"
    }
}
