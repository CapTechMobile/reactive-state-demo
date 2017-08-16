package atownsend.reactivestate.main.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import atownsend.reactivestate.R
import atownsend.reactivestate.api.UserModel
import atownsend.reactivestate.base.BaseMvpActivity
import atownsend.reactivestate.di.ApplicationModule.DebounceProvider
import atownsend.reactivestate.main.ui.events.MainUiEvents
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.concurrent.TimeUnit.MILLISECONDS
import javax.inject.Inject

class MainActivity : BaseMvpActivity<MainView, MainPresenter>(), MainView {

  @Inject lateinit var debounceProvider: DebounceProvider
  @Inject lateinit var userModel: UserModel

  var instanceStateRestored: Boolean = false

  override fun createPresenter(): MainPresenter = MainPresenter(userModel)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    instanceStateRestored = savedInstanceState != null

    Timber.d("debounce value is: " + debounceProvider.provideDebounce())
  }

  override fun checkRepoEvents(): Observable<MainUiEvents.CheckReposEvent> =
      RxView.clicks(button)
          .throttleFirst(250, MILLISECONDS)
          .map { MainUiEvents.CheckReposEvent(editText.text.toString()) }

  override fun checkUsernameEvents(): Observable<MainUiEvents.CheckUsernameEvent> =
      RxTextView.textChanges(editText).map { it.toString() }
          .skip(if (instanceStateRestored) 2 else 1) // skip initial emission(s)
          .filter { it.isNotEmpty() }
          .map { MainUiEvents.CheckUsernameEvent(it) }
          .debounce(debounceProvider.provideDebounce(), MILLISECONDS)
          .observeOn(AndroidSchedulers.mainThread())

  @SuppressLint("SetTextI18n")
  override fun setViewState(state: MainUiModel) {
    progressBar.visibility = if (state.inProgress) View.VISIBLE else View.GONE
    helloText.visibility = if (state.inProgress) View.GONE else View.VISIBLE
    helloText.text = if (state.verified) "Username verified" else "Username not found"

    val repoString = state.repos?.take(5)?.map { it.name }?.joinToString("\n")
    if (repoString == null) {
      reposText.visibility = View.GONE
    } else {
      val username = editText.text
      reposText.visibility = View.VISIBLE
      reposText.text = "$username's repos include:\n$repoString"
    }

    state.errorMessage?.let {
      Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
    }
  }
}

