package com.nextappsdigital.nextuiads.utils;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A class representing four corner radii as floating-point values.
 */
public class CornersF implements Parcelable {

    /** Parcelable.Creator implementation to create CornersF instances from a Parcel. */
    public static final Creator<CornersF> CREATOR = new Creator<CornersF>() {
        @Override
        public CornersF createFromParcel(Parcel parcel) {
            return new CornersF(parcel);
        }

        @Override
        public CornersF[] newArray(int size) {
            return new CornersF[size];
        }
    };

    public float bottomLeft;
    public float bottomRight;
    public float topLeft;
    public float topRight;

    /**
     * Default constructor initializing all corners to 0.
     */
    public CornersF() {
        this(0.0f, 0.0f, 0.0f, 0.0f);
    }

    /**
     * Constructor with individual corner values.
     *
     * @param topLeft the top-left corner radius
     * @param topRight the top-right corner radius
     * @param bottomLeft the bottom-left corner radius
     * @param bottomRight the bottom-right corner radius
     */
    public CornersF(float topLeft, float topRight, float bottomLeft, float bottomRight) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }

    /**
     * Copy constructor.
     *
     * @param cornersF another CornersF instance to copy values from, or null to initialize all corners to 0.
     */
    public CornersF(@Nullable CornersF cornersF) {
        if (cornersF != null) {
            this.topLeft = cornersF.topLeft;
            this.topRight = cornersF.topRight;
            this.bottomLeft = cornersF.bottomLeft;
            this.bottomRight = cornersF.bottomRight;
        } else {
            this.topLeft = 0.0f;
            this.topRight = 0.0f;
            this.bottomLeft = 0.0f;
            this.bottomRight = 0.0f;
        }
    }

    /**
     * Constructor to initialize from a Parcel.
     *
     * @param parcel the Parcel to read values from.
     */
    protected CornersF(Parcel parcel) {
        this.topLeft = parcel.readFloat();
        this.topRight = parcel.readFloat();
        this.bottomLeft = parcel.readFloat();
        this.bottomRight = parcel.readFloat();
    }

    /**
     * Sets the corner radii based on another CornersF instance.
     *
     * @param cornersF the CornersF instance to copy values from.
     */
    public void set(@Nullable CornersF cornersF) {
        if (cornersF != null) {
            this.topLeft = cornersF.topLeft;
            this.topRight = cornersF.topRight;
            this.bottomLeft = cornersF.bottomLeft;
            this.bottomRight = cornersF.bottomRight;
        }
    }

    /**
     * Sets the corner radii directly.
     *
     * @param topLeft the top-left corner radius
     * @param topRight the top-right corner radius
     * @param bottomLeft the bottom-left corner radius
     * @param bottomRight the bottom-right corner radius
     */
    public void set(float topLeft, float topRight, float bottomLeft, float bottomRight) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int flags) {
        parcel.writeFloat(this.topLeft);
        parcel.writeFloat(this.topRight);
        parcel.writeFloat(this.bottomLeft);
        parcel.writeFloat(this.bottomRight);
    }

    @NonNull
    @Override
    public String toString() {
        return "CornersF{" +
                "topLeft=" + topLeft +
                ", topRight=" + topRight +
                ", bottomLeft=" + bottomLeft +
                ", bottomRight=" + bottomRight +
                '}';
    }
}
