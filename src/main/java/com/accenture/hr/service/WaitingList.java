package com.accenture.hr.service;

import java.util.ArrayList;

public class WaitingList<E> extends ArrayList<E> {

    @Override
    public boolean add(E e) {
        boolean result = super.add(e);
        callBack();
        return result;
    }

    @Override
    public E remove(int index) {
        E object = super.remove(index);
        callBack();
        return object;
    }

    public void callBack() {
        System.out.println("in callback");
    }
}
