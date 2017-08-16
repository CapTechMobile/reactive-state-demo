package atownsend.reactivestate.di;

import atownsend.reactivestate.api.UserModel;
import atownsend.reactivestate.api.UserModelImpl;
import atownsend.reactivestate.api.retrofit.GithubUserService;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import retrofit2.Retrofit;

@Module @Singleton public abstract class ApplicationModule {

  @Binds @Singleton abstract UserModel bindUserService(UserModelImpl userService);

  @Provides @Singleton static GithubUserService provideGithubUserService(Retrofit retrofit) {
    return retrofit.create(GithubUserService.class);
  }

  public interface DebounceProvider {
    Long provideDebounce();
  }
}
