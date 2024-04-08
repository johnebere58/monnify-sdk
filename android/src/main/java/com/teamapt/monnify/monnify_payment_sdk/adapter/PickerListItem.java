package com.teamapt.monnify.monnify_payment_sdk.adapter;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class PickerListItem implements Parcelable {
    public static final Creator<PickerListItem> CREATOR = new Creator<PickerListItem>() {
        @Override
        public PickerListItem createFromParcel(Parcel source) {
            return new PickerListItem(source);
        }

        @Override
        public PickerListItem[] newArray(int size) {
            return new PickerListItem[size];
        }
    };
    private final String title;
    private final int position;

    public PickerListItem(String title, int position) {
        this.title = title;
        this.position = position;
    }

    protected PickerListItem(Parcel in) {
        this.title = in.readString();
        this.position = in.readInt();
    }

    public String getTitle() {
        return title;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeInt(this.position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PickerListItem listItem = (PickerListItem) o;
        return position == listItem.position &&
                Objects.equals(title, listItem.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, position);
    }
}
