package atownsend.reactivestate.main.ui

import atownsend.reactivestate.base.MvpView
import atownsend.reactivestate.main.ui.events.MainUiEvents
import io.reactivex.Observable

interface MainView : MvpView {
  fun checkRepoEvents(): Observable<MainUiEvents.CheckReposEvent>

  fun checkUsernameEvents(): Observable<MainUiEvents.CheckUsernameEvent>

  fun setViewState(state: MainUiModel)
}