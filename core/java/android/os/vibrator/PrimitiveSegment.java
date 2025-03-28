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

package android.os.vibrator;

import android.annotation.NonNull;
import android.annotation.Nullable;
import android.annotation.TestApi;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.VibrationEffect;
import android.os.VibratorInfo;

import com.android.internal.util.Preconditions;

import java.util.Locale;
import java.util.Objects;

/**
 * Representation of {@link VibrationEffectSegment} that plays a primitive vibration effect after a
 * specified delay and applying a given scale.
 *
 * @hide
 */
@TestApi
public final class PrimitiveSegment extends VibrationEffectSegment {

    /** @hide */
    public static final float DEFAULT_SCALE = 1f;

    /** @hide */
    public static final int DEFAULT_DELAY_MILLIS = 0;

    /** @hide */
    public static final int DEFAULT_DELAY_TYPE = VibrationEffect.Composition.DELAY_TYPE_PAUSE;

    private final int mPrimitiveId;
    private final float mScale;
    private final int mDelay;
    private final int mDelayType;

    PrimitiveSegment(@NonNull Parcel in) {
        this(in.readInt(), in.readFloat(), in.readInt(), in.readInt());
    }

    /** @hide */
    public PrimitiveSegment(int id, float scale, int delay) {
        this(id, scale, delay, DEFAULT_DELAY_TYPE);
    }

    /** @hide */
    public PrimitiveSegment(int id, float scale, int delay, int delayType) {
        mPrimitiveId = id;
        mScale = scale;
        mDelay = delay;
        mDelayType = delayType;
    }

    public int getPrimitiveId() {
        return mPrimitiveId;
    }

    public float getScale() {
        return mScale;
    }

    public int getDelay() {
        return mDelay;
    }

    /** @hide */
    public int getDelayType() {
        return mDelayType;
    }

    @Override
    public long getDuration() {
        return -1;
    }

    /** @hide */
    @Override
    public long getDuration(@Nullable VibratorInfo vibratorInfo) {
        if (vibratorInfo == null) {
            return getDuration();
        }
        int duration = vibratorInfo.getPrimitiveDuration(mPrimitiveId);
        return duration > 0 ? duration + mDelay : getDuration();
    }

    /** @hide */
    @Override
    public boolean areVibrationFeaturesSupported(@NonNull VibratorInfo vibratorInfo) {
        return vibratorInfo.isPrimitiveSupported(mPrimitiveId);
    }

    /** @hide */
    @Override
    public boolean isHapticFeedbackCandidate() {
        return true;
    }

    /** @hide */
    @NonNull
    @Override
    public PrimitiveSegment resolve(int defaultAmplitude) {
        return this;
    }

    /** @hide */
    @NonNull
    @Override
    public PrimitiveSegment scale(float scaleFactor) {
        float newScale = VibrationEffect.scale(mScale, scaleFactor);
        if (Float.compare(mScale, newScale) == 0) {
            return this;
        }
        return new PrimitiveSegment(mPrimitiveId, newScale, mDelay, mDelayType);
    }

    /** @hide */
    @NonNull
    @Override
    public PrimitiveSegment scaleLinearly(float scaleFactor) {
        float newScale = VibrationEffect.scaleLinearly(mScale, scaleFactor);
        if (Float.compare(mScale, newScale) == 0) {
            return this;
        }
        return new PrimitiveSegment(mPrimitiveId, newScale, mDelay, mDelayType);
    }

    /** @hide */
    @NonNull
    @Override
    public PrimitiveSegment applyEffectStrength(int effectStrength) {
        return this;
    }

    /** @hide */
    @Override
    public void validate() {
        Preconditions.checkArgumentInRange(mPrimitiveId, VibrationEffect.Composition.PRIMITIVE_NOOP,
                VibrationEffect.Composition.PRIMITIVE_LOW_TICK, "primitiveId");
        Preconditions.checkArgumentInRange(mScale, 0f, 1f, "scale");
        VibrationEffectSegment.checkDurationArgument(mDelay, "delay");
        Preconditions.checkArgument(isValidDelayType(mDelayType), "delayType");
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(PARCEL_TOKEN_PRIMITIVE);
        dest.writeInt(mPrimitiveId);
        dest.writeFloat(mScale);
        dest.writeInt(mDelay);
        dest.writeInt(mDelayType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Primitive{"
                + "primitive=" + VibrationEffect.Composition.primitiveToString(mPrimitiveId)
                + ", scale=" + mScale
                + ", delay=" + mDelay
                + ", delayType=" + VibrationEffect.Composition.delayTypeToString(mDelayType)
                + '}';
    }

    /** @hide */
    @Override
    public String toDebugString() {
        return String.format(Locale.ROOT, "Primitive=%s(scale=%.2f, %s=%dms)",
                VibrationEffect.Composition.primitiveToString(mPrimitiveId), mScale,
                toDelayTypeDebugString(mDelayType), mDelay);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrimitiveSegment that = (PrimitiveSegment) o;
        return mPrimitiveId == that.mPrimitiveId
                && Float.compare(that.mScale, mScale) == 0
                && mDelay == that.mDelay
                && mDelayType == that.mDelayType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mPrimitiveId, mScale, mDelay, mDelayType);
    }

    private static boolean isValidDelayType(int delayType) {
        return switch (delayType) {
            case VibrationEffect.Composition.DELAY_TYPE_PAUSE,
                 VibrationEffect.Composition.DELAY_TYPE_RELATIVE_START_OFFSET -> true;
            default -> false;
        };
    }

    private static String toDelayTypeDebugString(int delayType) {
        return switch (delayType) {
            case VibrationEffect.Composition.DELAY_TYPE_RELATIVE_START_OFFSET -> "startOffset";
            default -> "pause";
        };
    }

    @NonNull
    public static final Parcelable.Creator<PrimitiveSegment> CREATOR =
            new Parcelable.Creator<PrimitiveSegment>() {
                @Override
                public PrimitiveSegment createFromParcel(Parcel in) {
                    // Skip the type token
                    in.readInt();
                    return new PrimitiveSegment(in);
                }

                @Override
                public PrimitiveSegment[] newArray(int size) {
                    return new PrimitiveSegment[size];
                }
            };
}
