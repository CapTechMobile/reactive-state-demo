package atownsend.reactivestate.di;

/**
 * Created by 90303 on 5/19/17.
 */

import atownsend.reactivestate.ReactiveStateApplication;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;
import javax.inject.Singleton;

@Singleton @Component(modules = {
    AndroidSupportInjectionModule.class, AndroidBindingsModule.class, MainApplicationModule.class
}) public interface AppComponent {

  void inject(ReactiveStateApplication application);

  @Component.Builder interface Builder {
    @BindsInstance Builder application(ReactiveStateApplication application);

    AppComponent build();
  }
}
