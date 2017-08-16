package atownsend.reactivestate.api.actions

sealed class ApiAction {

  data class CheckReposAction(val username: String) : ApiAction()
  data class CheckUsernameAction(val username: String) : ApiAction()
}