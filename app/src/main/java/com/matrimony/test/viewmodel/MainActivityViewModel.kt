package com.matrimony.test.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.matrimony.test.model.ImageDetails

class MainActivityViewModel: ViewModel() {
    var lst = MutableLiveData<ArrayList<ImageDetails>>()
    var newlist = arrayListOf<ImageDetails>()


    fun add(imagedetails: ImageDetails){
        newlist.add(imagedetails)

        lst.value=newlist
    }

    fun remove(imagedetails: ImageDetails){
        newlist.remove(imagedetails)

        lst.value=newlist

    }


}