/*
 * Copyright (C) 2009 The Android Open Source Project
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

package android.service.wallpaper;

import android.app.WallpaperInfo;
import android.app.wallpaper.WallpaperDescription;
import android.graphics.Rect;
import android.service.wallpaper.IWallpaperConnection;

/**
 * @hide
 */
oneway interface IWallpaperService {
    void attach(IWallpaperConnection connection,
            IBinder windowToken, int windowType, boolean isPreview,
            int reqWidth, int reqHeight, in Rect padding, int displayId, int which,
            in WallpaperInfo info, in WallpaperDescription description);
    void detach(IBinder windowToken);
}
