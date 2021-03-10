package com.cc.nonstickylivedata;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

/**
 * Created by guoshichao on 2021/3/10
 * 非粘性LiveData事件
 */
public class NonStickyLiveData<T> extends MutableLiveData<T> {

    static final int START_VERSION = -1;

    private int mVersion = START_VERSION;

    @Override
    @MainThread
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        ObserverProxy observerProxy = new ObserverProxy<>(observer);
        observerProxy.preventNextEvent = mVersion > START_VERSION;
        super.observe(owner, observerProxy);
    }

    @Override
    public void setValue(T value) {
        mVersion++;
        super.setValue(value);
    }

    private class ObserverProxy<T> implements Observer<T> {

        @NonNull
        private final Observer<T> observer;
        private boolean preventNextEvent = false;

        ObserverProxy(@NonNull Observer<T> observer) {
            this.observer = observer;
        }

        @Override
        public void onChanged(@Nullable T t) {
            if (preventNextEvent) {
                preventNextEvent = false;
                return;
            }
            observer.onChanged(t);
        }
    }

}
