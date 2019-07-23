/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.core

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asObservable

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */

/**
 * In order to use [CoroutineScope], [launch] and [suspend], we will take parameters and [CoroutineDispatcher].
 * Since controllers have too much interactions and coupled with lifecycle heavily,
 * this is an attempt to separate logics.
 *
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
interface Interactor<in P> {
    val dispatcher: CoroutineDispatcher
    suspend operator fun invoke(executeParams: P)
}

/**
 * An abstract interactor to work with [Channel].
 * [Channel] can be used like we use [BehaviorSubject].
 * Explanation and example will be updated after testing.
 *
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
abstract class ChannelInteractor<P, T : Any> : Interactor<P> {
    private val channel = Channel<T>()
    val errorSubject: BehaviorSubject<Throwable> = BehaviorSubject.create<Throwable>()

    final override suspend fun invoke(executeParams: P) {
        try {
            channel.offer(execute(executeParams))
        } catch (e: Throwable) {
            errorSubject.onNext(e)
        }
    }

    @UseExperimental(ObsoleteCoroutinesApi::class)
    fun observe(): Observable<T> = channel.asObservable(dispatcher)

    protected abstract suspend fun execute(executeParams: P): T

    fun clear() {
        channel.close()
    }
}

/**
 * An abstract interactor to work with [BehaviorSubject].
 * Steps to use:
 * 1) [SubjectInteractor.setParams] Any parameters for observing in a presenter.
 * 2) [SubjectInteractor.observe] Some object to observe when the presenter is coupled with ui.
 * 3) [launchInteractor] from the presenter. This will trigger [SubjectInteractor.execute]. So the subject will emit.
 *
 * eg.
 * 1) When a presenter is created, [SubjectInteractor.setParams].
 * 2) When the presenter is attached with a view, [SubjectInteractor.observe].
 * 3) When we want to fetch or refresh data, [SubjectInteractor.invoke]. This will be called with [launchInteractor], so we can cancel the [Job] and [SubjectInteractor.clear].
 *
 * [SubjectInteractor.loading] will be used for showing a loading progress view or something.
 *
 *  @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
abstract class SubjectInteractor<P : Any, EP, T> : Interactor<EP> {
    private var disposable: Disposable? = null
    private val subject: BehaviorSubject<T> = BehaviorSubject.create()

    val loading = BehaviorSubject.createDefault(false)
    val errorSubject: BehaviorSubject<Throwable> = BehaviorSubject.create<Throwable>()

    private lateinit var params: P

    fun setParams(params: P) {
        this.params = params
        setSource(createObservable(params))
    }

    final override suspend fun invoke(executeParams: EP) {
        loading.onNext(true)
        try {
            execute(params, executeParams)
        } catch (t: Throwable) {
            errorSubject.onNext(t)
        }
        loading.onNext(false)
    }

    protected abstract fun createObservable(params: P): Observable<T>

    protected abstract suspend fun execute(params: P, executeParams: EP)

    fun clear() {
        disposable?.dispose()
        disposable = null
    }

    fun observe(): Observable<T> = subject

    private fun setSource(source: Observable<T>) {
        disposable?.dispose()
        disposable = source.subscribe(subject::onNext, subject::onError)
    }
}

/**
 * Extension function to [launch] the [Interactor] within the [CoroutineScope], which is presenter in our case. The presenter is aware of the lifecycle.
 *
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
fun <P> CoroutineScope.launchInteractor(interactor: Interactor<P>, param: P): Job {
    return launch(context = interactor.dispatcher, block = { interactor(param) })
}