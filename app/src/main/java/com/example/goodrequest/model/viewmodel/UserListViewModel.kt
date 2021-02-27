package com.example.goodrequest.model.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.goodrequest.model.response.UserData
import com.example.goodrequest.model.response.UserListResponse
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class UserListViewModel: BaseViewModel() {

    private val userListMutableLiveData: MutableLiveData<MutableList<UserData>> = MutableLiveData()
    val usersLiveData: LiveData<MutableList<UserData>>
    get() {
        return userListMutableLiveData
    }
    var actualPage = 0
    private var totalPage = 1

    fun fetchUsers(isRefresing: Boolean = false, isDefault: Boolean = false) {
        if(isDefault && usersLiveData.value != null) //lifecycle of viewmodel is longer than activity's -> no need to fetch data
            return
        if(isRefresing)
            actualPage = 0
        if(actualPage == totalPage)
            return
        actualPage += 1
        compositeSubs.add(apiService.getUsers(actualPage,5)
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
            .subscribe (object : Subscriber<UserListResponse>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    throwableMutableLiveData.postValue(e)
                    loadingMutableLiveData.postValue(false)
                }

                override fun onNext(t: UserListResponse) {
                    t.users?.let {
                        if(!isRefresing) {
                            val actual = userListMutableLiveData.value ?: mutableListOf()
                            actual.addAll(t.users)
                            userListMutableLiveData.postValue(actual)
                        }
                        else
                            userListMutableLiveData.postValue(it.toMutableList())
                    }
                    loadingMutableLiveData.postValue(false)
                    totalPage = t.totalPages ?: 1
                }
            }))
    }
}