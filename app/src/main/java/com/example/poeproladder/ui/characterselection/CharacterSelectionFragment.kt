package com.example.poeproladder.ui.characterselection

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.poeproladder.BaseApp

import com.example.poeproladder.R
import com.example.poeproladder.activities.HostingActivity
import com.example.poeproladder.database.*
import com.example.poeproladder.interactors.Database.CharacterDatabaseInteractor
import com.example.poeproladder.interactors.Database.CharacterDatabaseInteractorImpl
import com.example.poeproladder.interactors.Network.CharacterNetworkInteractor
import com.example.poeproladder.interactors.Network.CharacterNetworkInteractorImpl
import com.example.poeproladder.network.Network
import com.example.poeproladder.repository.CharactersRepository
import com.example.poeproladder.repository.CharactersRepositoryImpl
import com.example.poeproladder.session.SessionService
import com.example.poeproladder.session.SessionServiceImpl
import kotlinx.android.synthetic.main.fragment_character_selection.*


class CharacterSelectionFragment : Fragment(), CharacterSelectionContract.MyAccountView {
    //dependency injection
    private lateinit var database: CharacterDatabase
    private lateinit var repository: CharactersRepository
    private lateinit var session: SessionService
    private lateinit var databaseInteractor: CharacterDatabaseInteractor
    private lateinit var networkInteractor: CharacterNetworkInteractor

    private lateinit var accountNameTextView: TextView
    private lateinit var defaultMessageTextView: TextView
    private lateinit var accountNameEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var searchButton: Button


    private lateinit var hostingActivity: HostingActivity

    private lateinit var presenter: CharacterSelectionPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dependencyInjection()
        presenter = CharacterSelectionPresenter(this, repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_character_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

        presenter.onBind()

        accountNameEditText.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchButton.isEnabled = !s.toString().trim().isEmpty()
            }
        })

        searchButton.setOnClickListener {
            if (accountNameEditText.text.toString() != "") {
                val requestedAccountName = accountNameEditText.text.toString()
                accountNameEditText.text = null
                accountNameEditText.onEditorAction(EditorInfo.IME_ACTION_DONE)
                defaultMessageTextView.visibility = View.GONE
                presenter.getCharacters(requestedAccountName)
            } else {
                showError("Please insert account name")
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        hostingActivity = activity as HostingActivity
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun showProgressBar(show: Boolean) {
        if (show)
            progressBar.visibility = View.VISIBLE
        else
            progressBar.visibility = View.GONE
    }

    override fun showCharacterList(characters: List<CharacterDb>) {
        val adapter = CharacterListAdapter(characters, {character: CharacterDb -> characterClicked(character)})
        defaultMessageTextView.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        recyclerView.adapter = adapter
    }

    override fun showDefaultScreen() {
        defaultMessageTextView.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    override fun showCharacterWindow(items: CharacterItemsDb) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(error: String) {
        AlertDialog.Builder(this.context!!)
            .setTitle("Error")
            .setMessage(error)
            .setPositiveButton("Ok", null)
            .show()
    }

    override fun navigateToInventory() {
        hostingActivity.navigateToPage(HostingActivity.INVENTORYPAGE)
    }

    private fun characterClicked(character: CharacterDb) {
        presenter.onCharacterPicked(character.characterName)
    }

    private fun initViews() {
        accountNameTextView = textView_account_name
        accountNameEditText = editText_account
        defaultMessageTextView = textView_default_message
        searchButton = button_get_account
        progressBar = progressBar_account

        recyclerView = recyclerView_character_list
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
    }

    override fun showCurrentAccount(accountName: String) {
//        defaultMessageTextView.visibility = View.GONE
//        recyclerView.visibility = View.VISIBLE
        accountNameTextView.text = "${accountName.toUpperCase()}"
    }

    private fun dependencyInjection() {
        database = getDatabase(activity!!.application)
        session = SessionServiceImpl.getInstance(activity!!.application)
        databaseInteractor = CharacterDatabaseInteractorImpl
            .getInstance(database, session)
        networkInteractor = CharacterNetworkInteractorImpl
            .getInstance(database, Network, databaseInteractor)
        repository = CharactersRepositoryImpl
            .getInstance(session, databaseInteractor, networkInteractor)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CharacterSelectionFragment()
    }
}