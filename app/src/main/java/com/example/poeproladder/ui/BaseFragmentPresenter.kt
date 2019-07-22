package com.example.poeproladder.ui

import com.example.poeproladder.BaseContract
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragmentPresenter<T:BaseContract.BaseView>(view: T) : BaseContract.BasePresenter<BaseContract.BaseView>{
    var view:T = view

    val compositeDisposable = CompositeDisposable()



    override fun onStop() {
        compositeDisposable.clear()
    }

    fun showError() {
        view?.showError()
    }
}