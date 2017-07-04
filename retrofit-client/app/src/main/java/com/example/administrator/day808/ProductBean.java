package com.example.administrator.day808;

import java.util.List;

/**
 * Created by Administrator on 2017/6/21 0021.
 */

public class ProductBean {
    List<ProductChild> result;

    public List<ProductChild> getResult() {
        return result;
    }

    public void setResult(List<ProductChild> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ProductBean{" +
                "result=" + result +
                '}';
    }
}
