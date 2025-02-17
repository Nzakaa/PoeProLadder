package com.example.poeproladder.repository

import com.example.poeproladder.database.CharacterDb
import com.example.poeproladder.database.CharacterItemsDb
import com.example.poeproladder.network.CharacterWindowCharacterJson
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

interface CharactersRepository {
    fun getItemsObservable(): Observable<CharacterItemsDb>
    fun getItemsByName(accountName: String, characterName: String): Observable<CharacterItemsDb>
    fun getAccountData(accountName: String, networkIsActive: Boolean) : Single<List<CharacterDb>>
    fun saveAccountData(characters: List<CharacterWindowCharacterJson>, accountName: String)
}