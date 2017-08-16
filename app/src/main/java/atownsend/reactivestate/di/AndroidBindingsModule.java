package atownsend.reactivestate.di;

import android.app.Activity;
import atownsend.reactivestate.main.di.MainActivitySubComponent;
import atownsend.reactivestate.main.ui.MainActivity;
import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

@Module(subcomponents = {
    MainActivitySubComponent.class
})
public abstract class AndroidBindingsModule {

  @Binds
  @IntoMap
  @ActivityKey(MainActivity.class)
  abstract AndroidInjector.Factory<? extends Activity> mainActivityInjectorFactory(
      MainActivitySubComponent.Builder builder);
}