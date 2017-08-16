package atownsend.reactivestate.api

import atownsend.reactivestate.api.actions.ApiAction.CheckReposAction
import atownsend.reactivestate.api.actions.ApiAction.CheckUsernameAction
import atownsend.reactivestate.api.results.ApiResult.CheckReposResult
import atownsend.reactivestate.api.results.ApiResult.CheckUsernameResult
import atownsend.reactivestate.api.retrofit.GithubUserService
import atownsend.reactivestate.api.retrofit.RepoObservable
import atownsend.reactivestate.api.retrofit.UserObservable
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class UserModelImpl @Inject constructor(val githubUserService: GithubUserService) : UserModel {

  override fun checkUsername(
      cancellationSignal: Observable<*>): ObservableTransformer<CheckUsernameAction, CheckUsernameResult> {
    return ObservableTransformer {
      it.switchMap {
        checkUsernameOperation(it.username)
//            .delay(1500, MILLISECONDS)
            .map {
              if (it.isSuccessful) {
                CheckUsernameResult.Success(it.body())
              } else {
                CheckUsernameResult.Failure("User not found")
              }
            }
            .onErrorReturn { CheckUsernameResult.Failure("Exception retrieving user") }
            .observeOn(AndroidSchedulers.mainThread())
            .startWith(CheckUsernameResult.InFlight)
            .takeUntil(cancellationSignal)
      }
    }
  }

  override fun retrieveRepos(): ObservableTransformer<CheckReposAction, CheckReposResult> {
    return ObservableTransformer {
      it.switchMap {
        retrieveReposOperation(it.username)
            .map {
              if (it.isSuccessful) {
                CheckReposResult.Success(it.body())
              } else {
                CheckReposResult.Failure("Error retrieving repos")
              }
            }
            .onErrorReturn { CheckReposResult.Failure("Exception retrieving repos") }
            .observeOn(AndroidSchedulers.mainThread())
            .startWith(CheckReposResult.InFlight)
      }
    }
  }

  private fun checkUsernameOperation(username: String): UserObservable
      = githubUserService.checkUser(username)

  private fun retrieveReposOperation(username: String): RepoObservable = githubUserService.getRepos(
      username)
}