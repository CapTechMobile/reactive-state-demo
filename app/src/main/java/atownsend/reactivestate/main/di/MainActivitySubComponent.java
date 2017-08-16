package atownsend.reactivestate.main.di;

import atownsend.reactivestate.di.scope.ActivityScope;
import atownsend.reactivestate.main.ui.MainActivity;
import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@ActivityScope @Subcomponent(modules = MainActivityModule.class)
public interface MainActivitySubComponent extends AndroidInjector<MainActivity> {
  @Subcomponent.Builder abstract class Builder extends AndroidInjector.Builder<MainActivity> {

  }
}
