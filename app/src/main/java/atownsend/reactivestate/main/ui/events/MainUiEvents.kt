package atownsend.reactivestate.main.ui.events

sealed class MainUiEvents {
  data class CheckReposEvent(val username: String) : MainUiEvents()
  data class CheckUsernameEvent(val username: String) : MainUiEvents()
}