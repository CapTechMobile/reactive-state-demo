package atownsend.reactivestate.api

import atownsend.reactivestate.api.actions.ApiAction.CheckReposAction
import atownsend.reactivestate.api.actions.ApiAction.CheckUsernameAction
import atownsend.reactivestate.api.results.ApiResult.CheckReposResult
import atownsend.reactivestate.api.results.ApiResult.CheckUsernameResult
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

interface UserModel {
  fun checkUsername(cancellationSignal: Observable<*> = Observable.never<Any>()):
      ObservableTransformer<CheckUsernameAction, CheckUsernameResult>

  fun retrieveRepos(): ObservableTransformer<CheckReposAction, CheckReposResult>
}