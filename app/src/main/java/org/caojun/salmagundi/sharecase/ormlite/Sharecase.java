package org.caojun.salmagundi.sharecase.ormlite;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.List;

/**
 * 共享箱
 * Created by CaoJun on 2017/6/14.
 */

public class Sharecase implements Serializable, Parcelable {

    public Sharecase(float commission) {
        this.setCommission(commission);
        this.setIdHost(-1);
//        this.setIdUser(-1);
    }

    public boolean isEmpty() {
        if (TextUtils.isEmpty(name) && rent == 0 && deposit == 0 && /*timeStart == 0 && timeEnd == 0 && */idHost < 0 && idOrder < 0) {
            return true;
        }
        return false;
    }

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private String name;//箱内物品名称（物品主人设置）
    @DatabaseField
    private float rent;//箱内物品租金（归物品主人所有，按天计算）
    @DatabaseField
    private float deposit;//箱内物品押金（归共享箱所有人，归还物品时返还）
    @DatabaseField
    private float commission;//服务费（归共享箱平台所有，向物品主人收取，相当于收入所得税，收入的百分比）
//    @DatabaseField
//    private long timeStart;//物品租用起始时间
//    @DatabaseField
//    private long timeEnd;//物品租用结束时间（物品归还时，计算租金、服务费，归还押金）
    @DatabaseField
    private Integer idHost;//物品主人ID
    @DatabaseField
    private Integer idOrder;//订单ID
    @DatabaseField
    private List<Integer> idOrders;//相关订单ID

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRent() {
        return rent;
    }

    public void setRent(float rent) {
        this.rent = rent;
    }

    public float getDeposit() {
        return deposit;
    }

    public void setDeposit(float deposit) {
        this.deposit = deposit;
    }

    public float getCommission() {
        return commission;
    }

    public void setCommission(float commission) {
        this.commission = commission;
    }

//    public long getTimeStart() {
//        return timeStart;
//    }
//
//    public void setTimeStart(long timeStart) {
//        this.timeStart = timeStart;
//    }

//    public long getTimeEnd() {
//        return timeEnd;
//    }
//
//    public void setTimeEnd(long timeEnd) {
//        this.timeEnd = timeEnd;
//    }

    public Integer getIdHost() {
        return idHost;
    }

    public void setIdHost(Integer idHost) {
        this.idHost = idHost;
    }

    public Integer getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(Integer idOrder) {
        this.idOrder = idOrder;
    }

    public List<Integer> getIdOrders() {
        return idOrders;
    }

    public void setIdOrders(List<Integer> idOrders) {
        this.idOrders = idOrders;
    }

    public Sharecase(Parcel in) {
        id = in.readInt();
        name = in.readString();
        rent = in.readFloat();
        deposit = in.readFloat();
        commission = in.readFloat();
//        timeStart = in.readLong();
//        timeEnd = in.readLong();
        idHost = in.readInt();
        idOrder = in.readInt();
        idOrders = in.readArrayList(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<Sharecase> CREATOR = new Parcelable.Creator<Sharecase>() {
        @Override
        public Sharecase createFromParcel(Parcel in) {
            return new Sharecase(in);
        }

        @Override
        public Sharecase[] newArray(int size) {
            return new Sharecase[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeFloat(rent);
        dest.writeFloat(deposit);
        dest.writeFloat(commission);
//        dest.writeLong(timeStart);
//        dest.writeLong(timeEnd);
        dest.writeInt(idHost);
        dest.writeInt(idOrder);
        dest.writeList(idOrders);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
