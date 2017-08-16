package atownsend.reactivestate.main.di;

import atownsend.reactivestate.api.UserModel;
import atownsend.reactivestate.di.scope.ActivityScope;
import atownsend.reactivestate.main.ui.MainPresenter;
import dagger.Module;
import dagger.Provides;

@Module @ActivityScope
public abstract class MainActivityModule {

  // example injection for a presenter -- not used
  @Provides @ActivityScope static MainPresenter providePresenter(UserModel userModel) {
    return new MainPresenter(userModel);
  }
}
