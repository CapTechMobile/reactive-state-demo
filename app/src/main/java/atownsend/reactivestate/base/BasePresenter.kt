package atownsend.reactivestate.base

import atownsend.reactivestate.util.plusAssign
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

abstract class BasePresenter<V, VS> where V : MvpView {

  private val viewStateSubject: BehaviorSubject<VS> = BehaviorSubject.create<VS>()

  private val viewObservableWrappers = ArrayList<ViewObservableWrapper<*>>(4)

  private var isFirstViewAttach = true

  private var viewStateDisposable: Disposable? = null

  private var viewRelayDisposable: Disposable? = null

  private val viewDisposables = CompositeDisposable()

  private var viewStateConsumer: ((V, VS) -> Unit)? = null

  private var viewStateSubscribed = false

  /**
   * Create the Observable chain and subscribe the View State Subject
   */
  abstract fun bindViewObservables()

  fun attachView(view: V) {
    if (isFirstViewAttach) bindViewObservables()

    // subscribes to the View State, passing it to the View Interface's given method
    viewRelayDisposable = viewStateSubject.subscribe({
      viewStateConsumer?.invoke(view, it)
    })

    viewObservableWrappers.forEach {
      bindViewObservable(it, view)
    }

    isFirstViewAttach = false
  }

  fun detachView() {
    // clear view disposables and view state disposable
    viewDisposables.clear()
    viewRelayDisposable?.dispose()
    viewRelayDisposable = null
  }

  fun destroy() {
    viewStateDisposable?.dispose()
    viewObservableWrappers.clear()
    isFirstViewAttach = true
    viewStateSubscribed = false
    viewStateDisposable = null
  }

  /**
   * Subscribes the View State Observable chain, adding the View's consumer method reference
   * @param viewStateObservable Observable chain that results in the View State
   * @param consumer View interface method that consumes the View State
   */
  protected fun subscribeViewState(viewStateObservable: Observable<VS>, consumer: (V, VS) -> Unit) {
    require(!viewStateSubscribed, { "subscribeViewState() can only be called once" })
    viewStateSubscribed = true

    viewStateConsumer = consumer
    viewStateDisposable = viewStateObservable.subscribe(viewStateSubject::onNext)
  }

  /**
   * Wraps a View method reference that returns Observable<I>
   * @param binder view method reference that returns Observable<I>
   * @return returns a wrapped Subject that emits the values of the View Observable
   */
  protected fun <I> wrap(binder: (V) -> Observable<I>): Observable<I> {
    val subject = PublishSubject.create<I>()
    viewObservableWrappers.add(ViewObservableWrapper<I>(subject, binder))
    return subject
  }

  /**
   * Take a ViewObservableWrapper and bind it to a view instance
   * @param wrapper the ViewObservableWrapper
   * @param view the view instance to be bound
   */
  private fun <I> bindViewObservable(wrapper: ViewObservableWrapper<I>, view: V): Unit {
    val subject = wrapper.subject
    // wrapper.binder.invoke(view) returns the desired View Observable
    viewDisposables += wrapper.binder.invoke(view).subscribe(subject::onNext)
  }

  internal inner class ViewObservableWrapper<I>(val subject: PublishSubject<I>,
      val binder: (V) -> Observable<I>)
}