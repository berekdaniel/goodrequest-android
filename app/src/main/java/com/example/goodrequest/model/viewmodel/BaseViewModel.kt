package com.example.goodrequest.model.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.goodrequest.rest.ApiManager
import rx.subjects.PublishSubject

import rx.subscriptions.CompositeSubscription

open class BaseViewModel: ViewModel() {
    var compositeSubs: CompositeSubscription = CompositeSubscription()
    val apiService = ApiManager.instance

    val retrySubject: PublishSubject<Void> = PublishSubject.create()
    override fun onCleared() {

        if (!compositeSubs.isUnsubscribed) {
            compositeSubs.unsubscribe()
        }
        super.onCleared()
    }

    protected val throwableMutableLiveData: MutableLiveData<Throwable?> = MutableLiveData()
    val throwableLiveData: LiveData<Throwable?>
        get() {
            return throwableMutableLiveData
        }

    fun resetThrowable() {
        throwableMutableLiveData.value = null
    }

    protected val loadingMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val loadingLiveData: LiveData<Boolean>
        get() {
            return loadingMutableLiveData
        }
}