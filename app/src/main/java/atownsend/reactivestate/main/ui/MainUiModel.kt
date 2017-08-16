package atownsend.reactivestate.main.ui

import atownsend.reactivestate.api.model.Repo

data class MainUiModel(val inProgress: Boolean,
    val verified: Boolean,
    val repos: List<Repo>? = null,
    val errorMessage: String? = null)