/*
 * Copyright (C) 2021 The Android Open Source Project
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
package com.android.server.stats.bootstrap;

import android.content.Context;
import android.os.IStatsBootstrapAtomService;
import android.os.StatsBootstrapAtom;
import android.os.StatsBootstrapAtomValue;
import android.util.Slog;
import android.util.StatsEvent;
import android.util.StatsLog;

import com.android.server.SystemService;

/**
 * Proxy service for logging pushed atoms to statsd
 *
 * @hide
 */
public class StatsBootstrapAtomService extends IStatsBootstrapAtomService.Stub {

    private static final String TAG = "StatsBootstrapAtomService";
    private static final boolean DEBUG = false;

    @Override
    public void reportBootstrapAtom(StatsBootstrapAtom atom) {
        if (atom.atomId < 1 || atom.atomId >= 10000) {
            Slog.e(TAG, "Atom ID " + atom.atomId + " is not a valid atom ID");
            return;
        }
        StatsEvent.Builder builder = StatsEvent.newBuilder().setAtomId(atom.atomId);
        for (StatsBootstrapAtomValue atomValue : atom.values) {
            StatsBootstrapAtomValue.Primitive value = atomValue.value;
            switch (value.getTag()) {
                case StatsBootstrapAtomValue.Primitive.boolValue:
                    builder.writeBoolean(value.getBoolValue());
                    break;
                case StatsBootstrapAtomValue.Primitive.intValue:
                    builder.writeInt(value.getIntValue());
                    break;
                case StatsBootstrapAtomValue.Primitive.longValue:
                    builder.writeLong(value.getLongValue());
                    break;
                case StatsBootstrapAtomValue.Primitive.floatValue:
                    builder.writeFloat(value.getFloatValue());
                    break;
                case StatsBootstrapAtomValue.Primitive.stringValue:
                    builder.writeString(value.getStringValue());
                    break;
                case StatsBootstrapAtomValue.Primitive.bytesValue:
                    builder.writeByteArray(value.getBytesValue());
                    break;
                case StatsBootstrapAtomValue.Primitive.stringArrayValue:
                    builder.writeStringArray(value.getStringArrayValue());
                    break;
                default:
                    Slog.e(TAG, "Unexpected value type " + value.getTag()
                            + " when logging atom " + atom.atomId);
                    return;

            }
            StatsBootstrapAtomValue.Annotation[] annotations = atomValue.annotations;
            for (StatsBootstrapAtomValue.Annotation annotation : atomValue.annotations) {
                if (annotation.id != StatsBootstrapAtomValue.Annotation.Id.IS_UID) {
                    Slog.e(TAG, "Unexpected annotation ID: " + annotation.id
                            + ", for atom " + atom.atomId + ": only UIDs are supported!");
                    return;
                }

                switch (annotation.value.getTag()) {
                    case StatsBootstrapAtomValue.Annotation.Primitive.boolValue:
                        builder.addBooleanAnnotation(
                                annotation.id, annotation.value.getBoolValue());
                        break;
                    default:
                        Slog.e(TAG, "Unexpected value type " + annotation.value.getTag()
                                + " when logging UID for atom " + atom.atomId);
                        return;
                }
            }
        }
        StatsLog.write(builder.usePooledBuffer().build());
    }

    /**
     * Lifecycle and related code
     */
    public static final class Lifecycle extends SystemService {
        private StatsBootstrapAtomService mStatsBootstrapAtomService;

        public Lifecycle(Context context) {
            super(context);
        }

        @Override
        public void onStart() {
            mStatsBootstrapAtomService = new StatsBootstrapAtomService();
            try {
                publishBinderService(Context.STATS_BOOTSTRAP_ATOM_SERVICE,
                        mStatsBootstrapAtomService);
                if (DEBUG) Slog.d(TAG, "Published " + Context.STATS_BOOTSTRAP_ATOM_SERVICE);
            } catch (Exception e) {
                Slog.e(TAG, "Failed to publishBinderService", e);
            }
        }
    }

}
