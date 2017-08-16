package atownsend.reactivestate.main.ui

import atownsend.reactivestate.api.UserModel
import atownsend.reactivestate.api.actions.ApiAction
import atownsend.reactivestate.api.actions.ApiAction.CheckReposAction
import atownsend.reactivestate.api.actions.ApiAction.CheckUsernameAction
import atownsend.reactivestate.api.results.ApiResult
import atownsend.reactivestate.api.results.ApiResult.CheckReposResult
import atownsend.reactivestate.api.results.ApiResult.CheckUsernameResult
import atownsend.reactivestate.base.BasePresenter
import atownsend.reactivestate.util.ofType
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import timber.log.Timber

class MainPresenter constructor(val model: UserModel) : BasePresenter<MainView, MainUiModel>() {

  override fun bindViewObservables() {
    // wrap view observables so we can un-subscribe from them without killing our streams, then
    // map the view events into API Actions
    val repoActions: Observable<CheckReposAction> = wrap(MainView::checkRepoEvents)
        .map { CheckReposAction(it.username) }
        .doOnNext { Timber.tag("ACTIONS").d("New repo event: $it") }

    val usernameActions: Observable<CheckUsernameAction> = wrap(MainView::checkUsernameEvents)
        .map { CheckUsernameAction(it.username) }
        .doOnNext { Timber.tag("ACTIONS").d("New username event: $it") }

    // merge the view observables into a single stream
    val events: Observable<ApiAction> = Observable.merge(repoActions, usernameActions)

    // takes a stream of actions, splits to their appropriate transformers
    val actionsTransformer: ObservableTransformer<ApiAction, ApiResult> = ObservableTransformer {
      it.publish {
        Observable.merge(
            it.ofType<CheckUsernameAction>().compose(model.checkUsername(repoActions)),
            it.ofType<CheckReposAction>().compose(model.retrieveRepos()))
      }
    }

    // apply our transformers to our stream of API actions
    val results: Observable<ApiResult> = events.compose(actionsTransformer)
        .doOnNext { Timber.tag("RESULTS").d("API Result is: $it") }
    // use Observable.scan() to apply each ApiResult to the previous UI model
    val uiModels: Observable<MainUiModel> = results
        .scan(MainUiModel(false, false), this::reduceState)
        .doOnNext { state -> Timber.tag("STATES").d("UI state is: $state") }

    subscribeViewState(uiModels.distinctUntilChanged(), MainView::setViewState)
  }

  fun reduceState(previousState: MainUiModel, result: ApiResult): MainUiModel =
      when (result) {
        is CheckUsernameResult.InFlight, is CheckReposResult.InFlight -> previousState.copy(
            inProgress = true, verified = false, repos = null, errorMessage = null)
        is CheckReposResult.Success -> previousState.copy(inProgress = false, verified = true,
            repos = result.repos, errorMessage = null)
        is CheckUsernameResult.Success -> previousState.copy(inProgress = false, verified = true,
            repos = null, errorMessage = null)
        is CheckReposResult.Failure -> previousState.copy(inProgress = false, verified = false,
            repos = null, errorMessage = result.errorMessage)
        is CheckUsernameResult.Failure -> previousState.copy(inProgress = false, verified = false,
            repos = null, errorMessage = result.errorMessage)
      }
}