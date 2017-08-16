package atownsend.reactivestate.api.results

import atownsend.reactivestate.api.model.Repo
import atownsend.reactivestate.api.model.User

sealed class ApiResult {
  sealed class CheckReposResult : ApiResult() {
    object InFlight : CheckReposResult()
    data class Success(val repos: List<Repo>?) : CheckReposResult()
    data class Failure(val errorMessage: String?) : CheckReposResult()
  }

  sealed class CheckUsernameResult : ApiResult() {
    object InFlight : CheckUsernameResult()
    data class Success(val body: User?) : CheckUsernameResult()
    data class Failure(val errorMessage: String?) : CheckUsernameResult()
  }
}