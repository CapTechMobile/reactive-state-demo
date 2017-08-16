package atownsend.reactivestate.api

import atownsend.reactivestate.api.actions.ApiAction.CheckReposAction
import atownsend.reactivestate.api.actions.ApiAction.CheckUsernameAction
import atownsend.reactivestate.api.model.Repo
import atownsend.reactivestate.api.model.User
import atownsend.reactivestate.api.results.ApiResult.CheckReposResult
import atownsend.reactivestate.api.results.ApiResult.CheckUsernameResult
import atownsend.reactivestate.api.retrofit.GithubUserService
import atownsend.reactivestate.runner.RxJavaJUnit4Runner
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response

@RunWith(RxJavaJUnit4Runner::class) class UserModelImplTest {

  private val checkUsernameActions = PublishSubject.create<CheckUsernameAction>()
  private val checkRepoActions = PublishSubject.create<CheckReposAction>()

  private val user = User("test")
  private val repos: List<Repo> = listOf(Repo("repo_one"), Repo("repo_two"))

  private val serviceMock: GithubUserService = mock {
    // check username mocks
    on { checkUser("success") } doReturn Observable.just(Response.success(user))
    on { checkUser("exception") } doReturn Observable.error(Exception("exception"))
    on { checkUser("failure") } doReturn Observable.just(Response.error(404, ResponseBody.create(
        MediaType.parse("application/json"), "")))

    on { getRepos("success") } doReturn Observable.just(Response.success(repos))
    on { getRepos("exception") } doReturn Observable.error(Exception("exception"))
    on { getRepos("failure") } doReturn Observable.just(Response.error(404, ResponseBody.create(
        MediaType.parse("application/json"), "")))

  }

  private lateinit var userModel: UserModel

  @Before fun setUp() {
    userModel = UserModelImpl(serviceMock)
  }

  @Test fun testCheckUserSuccess() {
    val obs = checkUsernameActions.compose(userModel.checkUsername())
        .test()

    obs.assertEmpty()
    checkUsernameActions.onNext(CheckUsernameAction("success"))

    obs.assertValueAt(0, { it is CheckUsernameResult.InFlight })
        .assertValueAt(1, { it == CheckUsernameResult.Success(user) })
        .dispose()
  }

  @Test fun testCheckUsernameFailure() {
    val obs = checkUsernameActions.compose(userModel.checkUsername())
        .test()

    obs.assertEmpty()
    checkUsernameActions.onNext(CheckUsernameAction("failure"))

    obs.assertValueAt(0, { it is CheckUsernameResult.InFlight })
        .assertValueAt(1, {
          it is CheckUsernameResult.Failure
              && it.errorMessage == "User not found"
        })
        .dispose()
  }

  @Test fun testCheckUsernameException() {
    val obs = checkUsernameActions.compose(userModel.checkUsername())
        .test()

    obs.assertEmpty()
    checkUsernameActions.onNext(CheckUsernameAction("exception"))

    obs.assertValueAt(0, { it is CheckUsernameResult.InFlight })
        .assertValueAt(1, {
          it is CheckUsernameResult.Failure
              && it.errorMessage == "Exception retrieving user"
        })
        .dispose()
  }

  @Test fun testCheckReposSuccess() {
    val obs = checkRepoActions.compose(userModel.retrieveRepos())
        .test()

    obs.assertEmpty()
    checkRepoActions.onNext(CheckReposAction("success"))

    obs.assertValueAt(0, { it is CheckReposResult.InFlight })
        .assertValueAt(1, {
          it is CheckReposResult.Success
              && it.repos?.size == 2
        })
        .dispose()
  }

  @Test fun testCheckReposFailure() {
    val obs = checkRepoActions.compose(userModel.retrieveRepos())
        .test()

    obs.assertEmpty()
    checkRepoActions.onNext(CheckReposAction("failure"))

    obs.assertValueAt(0, { it is CheckReposResult.InFlight })
        .assertValueAt(1, {
          it is CheckReposResult.Failure
              && it.errorMessage == "Error retrieving repos"
        })
        .dispose()
  }

  @Test fun testCheckReposException() {
    val obs = checkRepoActions.compose(userModel.retrieveRepos())
        .test()

    obs.assertEmpty()
    checkRepoActions.onNext(CheckReposAction("exception"))

    obs.assertValueAt(0, { it is CheckReposResult.InFlight })
        .assertValueAt(1, {
          it is CheckReposResult.Failure
              && it.errorMessage == "Exception retrieving repos"
        })
        .dispose()
  }
}