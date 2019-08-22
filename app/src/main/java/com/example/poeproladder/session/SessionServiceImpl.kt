package com.example.poeproladder.session

import android.content.Context
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import com.example.poeproladder.domain.CharacterRequest
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class SessionServiceImpl: SessionService {

    private val characterSubject = BehaviorSubject.create<CharacterRequest>()
    private var account:String = ""
    private var character:String = ""
    private val context: Context

    @Inject
    constructor(context: Context) {
        this.context = context
    }


    override fun saveAccount(accountName: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(ACCOUNT_KEY, accountName)
            .apply()

        account = accountName
        updateObservable()
    }

    override fun saveCharacter(characterName: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(CHARACTER_KEY, characterName)
            .apply()

        character = characterName
        updateObservable()
    }

    override fun getCharacter(): String? {
        val characterName = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(CHARACTER_KEY, "default")
        if (characterName != "default") character = characterName
        updateObservable()
        return characterName
    }

    override fun getAccount(): String? {
        val accountName = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(ACCOUNT_KEY, "default")

        if (accountName != "default") account = accountName
        updateObservable()
        return accountName
    }

    override fun getCharacterObservable(): Observable<CharacterRequest> {
        return characterSubject
    }

    override fun getNetworkStatus(): Boolean? {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo?.isConnected
    }

    private fun updateObservable() {
        if (account != "" && character != "") {
            val characterRequestInfo = CharacterRequest(account, character)
            characterSubject.onNext(characterRequestInfo)
        }
    }

    companion object {
        const val ACCOUNT_KEY = "Account"
        const val CHARACTER_KEY = "Character"
    }
}