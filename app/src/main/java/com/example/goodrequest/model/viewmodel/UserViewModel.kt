package com.example.goodrequest.model.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.goodrequest.model.response.UserData
import com.example.goodrequest.model.response.UserResponse
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class UserViewModel: BaseViewModel() {

    private val userMutableLiveData: MutableLiveData<UserData> = MutableLiveData()
    val userLiveData: LiveData<UserData>
        get() {
            return userMutableLiveData
        }

    fun fetchUser(userId: Int, isDefault: Boolean = false) {
        if(isDefault && userMutableLiveData.value != null)
            return
        compositeSubs.add(apiService.getUser(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                loadingMutableLiveData.postValue(true)
            }
            .retryWhen{ observable ->
                observable.flatMap { e ->
                    throwableMutableLiveData.postValue(e)
                    loadingMutableLiveData.postValue(false)
                    retrySubject.asObservable()
                }
            }
            .subscribe (object : Subscriber<UserResponse>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    throwableMutableLiveData.postValue(e)
                    loadingMutableLiveData.postValue(false)
                }

                override fun onNext(t: UserResponse) {
                    t.user?.let {
                        userMutableLiveData.postValue(it)
                    }
                    loadingMutableLiveData.postValue(false)
                }
            }))
    }
}