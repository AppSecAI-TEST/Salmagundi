package org.caojun.salmagundi.bloodpressure.ormlite;

import android.os.Parcel;
import android.os.Parcelable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.io.Serializable;

/**
 * 血压记录数据
 * Created by fourm on 2017/5/9.
 */

@DatabaseTable
public class BloodPressure implements Serializable, Parcelable {

    public static final byte Type_BloodPressure = 0;
    public static final byte Type_Medicine = 1;
    public static final byte Type_Weight = 2;

    @DatabaseField(id = true)
    private long time;//记录时间
    @DatabaseField
    private int high;//高压（收缩压）
    @DatabaseField
    private int low;//低压（舒张压）
    @DatabaseField
    private int pulse;//脉搏
    @DatabaseField
    private boolean isLeft;//左右手
    @DatabaseField
    private int device;//设备信息（水银、品牌名称等）
    @DatabaseField
    private float weight;//体重（公斤）
    @DatabaseField
    private byte type;//数据类型（0血压，1服药，2体重）

    public BloodPressure() {}

    /**
     * 服药记录
     * @param time
     */
    public BloodPressure(long time) {
        this.setTime(time);
        this.setType(Type_Medicine);
    }

    /**
     * 体重记录
     * @param time
     * @param weight
     */
    public BloodPressure(long time, float weight) {
        this.setTime(time);
        this.setType(Type_Weight);
        this.setWeight(weight);
    }

    /**
     * 血压记录
     * @param time
     * @param high
     * @param low
     * @param pulse
     * @param isLeft
     * @param device
     */
    public BloodPressure(long time, int high, int low, int pulse, boolean isLeft, int device) {
        this.setTime(time);
        this.setType(Type_BloodPressure);
        this.setHigh(high);
        this.setLow(low);
        this.setPulse(pulse);
        this.setLeft(isLeft);
        this.setDevice(device);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }

    public boolean isLeft() {
        return isLeft;
    }

    public void setLeft(boolean left) {
        isLeft = left;
    }

    public int getDevice() {
        return device;
    }

    public void setDevice(int device) {
        this.device = device;
    }

    public int getPulse() {
        return pulse;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public BloodPressure(Parcel in) {
        time = in.readLong();
        high = in.readInt();
        low = in.readInt();
        pulse = in.readInt();
        isLeft = in.readByte() == 1;
        device = in.readInt();
        weight = in.readFloat();
        type = in.readByte();
    }

    public static final Creator<BloodPressure> CREATOR = new Creator<BloodPressure>() {
        @Override
        public BloodPressure createFromParcel(Parcel in) {
            return new BloodPressure(in);
        }

        @Override
        public BloodPressure[] newArray(int size) {
            return new BloodPressure[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(time);
        dest.writeInt(high);
        dest.writeInt(low);
        dest.writeInt(pulse);
        dest.writeByte((byte)(isLeft?1:0));
        dest.writeInt(device);
        dest.writeFloat(weight);
        dest.writeByte(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(time).append("，");
        sb.append(high).append("，");
        sb.append(low).append("，");
        sb.append(pulse).append("，");
        sb.append((byte)(isLeft?1:0)).append("，");
        sb.append(weight).append("，");
        sb.append(type).append("。");
        return sb.toString();
    }
}
