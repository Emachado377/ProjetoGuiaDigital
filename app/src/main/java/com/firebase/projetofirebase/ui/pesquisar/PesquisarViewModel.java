package com.firebase.projetofirebase.ui.pesquisar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PesquisarViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PesquisarViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is pesquisar fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}