package atownsend.reactivestate.main.ui

import atownsend.reactivestate.api.UserModel
import atownsend.reactivestate.api.model.Repo
import atownsend.reactivestate.api.model.User
import atownsend.reactivestate.api.results.ApiResult.CheckReposResult
import atownsend.reactivestate.api.results.ApiResult.CheckUsernameResult
import atownsend.reactivestate.main.ui.events.MainUiEvents.CheckReposEvent
import atownsend.reactivestate.main.ui.events.MainUiEvents.CheckUsernameEvent
import atownsend.reactivestate.runner.RxJavaJUnit4Runner
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.subjects.PublishSubject
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.verify
import kotlin.test.assertEquals

@RunWith(RxJavaJUnit4Runner::class) class MainPresenterTest {

  // ui events
  val checkReposEvents: PublishSubject<CheckReposEvent> = PublishSubject.create()
  val usernameChangeEvents: PublishSubject<CheckUsernameEvent> = PublishSubject.create()

  private val view: MainView = mock {
    on { checkRepoEvents() } doReturn checkReposEvents
    on { checkUsernameEvents() } doReturn usernameChangeEvents
  }

  private val model: UserModel = mock {
    on { retrieveRepos() } doReturn ObservableTransformer {
      it.flatMap {
        val obs: Observable<CheckReposResult> = when (it.username) {
          "exception" -> Observable.just(CheckReposResult.Failure("failure"))
          else -> Observable.just(
              CheckReposResult.Success(listOf(Repo("repo_one"), Repo("repo_two"))))
        }
        obs.startWith(CheckReposResult.InFlight)
      }
    }

    on { checkUsername(any()) } doReturn ObservableTransformer {
      it.flatMap {
        val obs: Observable<CheckUsernameResult> = when (it.username) {
          "exception" -> Observable.just(CheckUsernameResult.Failure("failure"))
          else -> Observable.just(CheckUsernameResult.Success(User(it.username)))
        }
        obs.startWith(CheckUsernameResult.InFlight)
      }
    }
  }

  private var modelCaptor: KArgumentCaptor<MainUiModel> = argumentCaptor()

  @Test fun testUsernameChangesSuccess() {

    val presenter = MainPresenter(model)
    presenter.attachView(view)

    // cause a username change so we hook into the subject
    usernameChangeEvents.onNext(CheckUsernameEvent("test"))

    verify(view, atLeastOnce()).setViewState(modelCaptor.capture())

    // idle
    assertEquals(false, modelCaptor.firstValue.inProgress)
    assertEquals(false, modelCaptor.firstValue.verified)
    // in progress
    assertEquals(true, modelCaptor.secondValue.inProgress)
    assertEquals(false, modelCaptor.secondValue.verified)
    // success
    assertEquals(false, modelCaptor.thirdValue.inProgress)
    assertEquals(true, modelCaptor.thirdValue.verified)
  }

  @Test fun testUsernameChangesFailure() {
    val presenter = MainPresenter(model)
    presenter.attachView(view)

    usernameChangeEvents.onNext(CheckUsernameEvent("exception"))

    verify(view, atLeastOnce()).setViewState(modelCaptor.capture())

    // idle
    assertEquals(false, modelCaptor.firstValue.inProgress)
    assertEquals(false, modelCaptor.firstValue.verified)
    // in progress
    assertEquals(true, modelCaptor.secondValue.inProgress)
    assertEquals(false, modelCaptor.secondValue.verified)
    // failure
    assertEquals(false, modelCaptor.thirdValue.inProgress)
    assertEquals("failure", modelCaptor.thirdValue.errorMessage)
  }

  @Test fun testCheckReposSuccess() {
    val presenter = MainPresenter(model)
    presenter.attachView(view)

    checkReposEvents.onNext(CheckReposEvent("test"))

    verify(view, atLeastOnce()).setViewState(modelCaptor.capture())

    // idle
    assertEquals(false, modelCaptor.firstValue.inProgress)
    assertEquals(false, modelCaptor.firstValue.verified)
    // in progress
    assertEquals(true, modelCaptor.secondValue.inProgress)
    assertEquals(false, modelCaptor.secondValue.verified)
    // success
    assertEquals(false, modelCaptor.thirdValue.inProgress)
    assertEquals(true, modelCaptor.thirdValue.verified)
    assertEquals(2, modelCaptor.thirdValue.repos?.size)
  }

  @Test fun testCheckReposFailure() {
    val presenter = MainPresenter(model)
    presenter.attachView(view)

    checkReposEvents.onNext(CheckReposEvent("exception"))

    verify(view, atLeastOnce()).setViewState(modelCaptor.capture())

    // idle
    assertEquals(false, modelCaptor.firstValue.inProgress)
    assertEquals(false, modelCaptor.firstValue.verified)
    // in progress
    assertEquals(true, modelCaptor.secondValue.inProgress)
    assertEquals(false, modelCaptor.secondValue.verified)
    // failure
    assertEquals(false, modelCaptor.thirdValue.inProgress)
    assertEquals("failure", modelCaptor.thirdValue.errorMessage)
  }

}
