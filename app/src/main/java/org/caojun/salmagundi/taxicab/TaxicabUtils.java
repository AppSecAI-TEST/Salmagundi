package org.caojun.salmagundi.taxicab;

import android.content.Context;
import org.caojun.salmagundi.taxicab.ormlite.Taxicab;
import org.caojun.salmagundi.taxicab.ormlite.TaxicabDatabase;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by fourm on 2017/5/2.
 */

public class TaxicabUtils {

    /**
     * 立方
     * @param number
     * @return
     */
    private static BigInteger getCube(BigInteger number) {
        return number.pow(3);
    }

    /**
     * 立方和
     * @param a
     * @param b
     * @return
     */
    public static BigInteger getTaxicab(BigInteger a, BigInteger b) {
        BigInteger aCube = getCube(a);
        BigInteger bCube = getCube(b);
        return aCube.add(bCube);
    }

    public static List<Taxicab> getList(Context context, BigInteger max, boolean isTaxicab) {
        int n = 6;
        if (isTaxicab) {
            //正整数
            for (BigInteger a = BigInteger.ONE; a.compareTo(max) <= 0; a = a.add(BigInteger.ONE)) {
                for (BigInteger b = BigInteger.ONE; b.compareTo(a) <= 0; b = b.add(BigInteger.ONE)) {
                    TaxicabDatabase.getInstance(context).insert(a, b);
                }
            }
        } else {
            for (BigInteger a = BigInteger.ONE; a.compareTo(max) <= 0; a = a.add(BigInteger.ONE)) {
                BigInteger na = BigInteger.ZERO.subtract(a);
                for (BigInteger b = na; b.compareTo(a) <= 0; b = b.add(BigInteger.ONE)) {
                    TaxicabDatabase.getInstance(context).insert(a, b);
                }
            }
            n = 10;
        }

        List<Taxicab> list = TaxicabDatabase.getInstance(context).query();
        //排序
        Collections.sort(list, new Comparator<Taxicab>() {
            @Override
            public int compare(Taxicab t0, Taxicab t1) {
                return t0.getTaxicab().compareTo(t1.getTaxicab());
            }
        });

        //过滤
        List<Taxicab> result = new ArrayList<>();
        for (int i = 1;i <= n;i ++) {
            result.addAll(getTaxicab(isTaxicab, i, max, list));
        }
        return result;
    }

    /**
     *
     * @param n
     * @param source 已升序排序的所有立方和数据
     * @return
     */
    private static List<Taxicab> getTaxicab(boolean isTaxicab, int n, BigInteger max, List<Taxicab> source) {
        if (n < 1 || source == null || source.isEmpty()) {
            return null;
        }
        List<Taxicab> list = new ArrayList<>();
        if (n == 1) {
            list.add(source.get(0));
            return list;
        }

        for (int i = 0;i < source.size() - n - 1;i ++) {
            Taxicab first = source.get(i);
            boolean found = true;
            for (int j = 1;j < n;j ++) {
                Taxicab taxicab = source.get(i + j);
                if (isTaxicab && (taxicab.getA().compareTo(BigInteger.ZERO) <= 0 || taxicab.getB().compareTo(BigInteger.ZERO) <= 0)) {
                    found = false;
                    break;
                }
                if (taxicab.getA().compareTo(max) > 0 || taxicab.getB().compareTo(max) > 0) {
                    found = false;
                    break;
                }
                if (first.getTaxicab().compareTo(taxicab.getTaxicab()) != 0) {
                    found = false;
                    break;
                }
            }
            if (found) {
                for (int j = 0;j < n;j ++) {
                    Taxicab taxicab = source.get(i + j);
                    list.add(taxicab);
                }
                break;
            }
        }
        return list;
    }
}
