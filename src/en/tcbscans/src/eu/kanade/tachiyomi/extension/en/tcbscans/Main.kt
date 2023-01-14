package eu.kanade.tachiyomi.extension.en.tcbscans

import rx.Observable
import rx.Subscriber
import tachijs.*
import kotlin.js.Promise

// todo: make some sort of factory class in tachijs instead of having to write these for every source

val source = TCBScans()

@OptIn(ExperimentalJsExport::class)
@JsExport
fun name() = source.name

@OptIn(ExperimentalJsExport::class)
@JsExport
fun fetchPopularManga(page: Int): Promise<JsMangasPage> {
    return source.fetchPopularManga(page).map { response ->
        JsMangasPage.from(response)
    }.toPromise()
}

@OptIn(ExperimentalJsExport::class)
@JsExport
fun fetchLatestUpdates(page: Int): Promise<JsMangasPage> {
    return source.fetchLatestUpdates(page).map { response ->
        JsMangasPage.from(response)
    }.toPromise()
}

@OptIn(ExperimentalJsExport::class)
@JsExport
fun fetchMangaDetails(manga: JsManga): Promise<JsManga> {
    val smanga = manga.toManga()
    return source.fetchMangaDetails(smanga).map { response ->
        smanga.copyFrom(response)
        JsManga.from(smanga)
    }.toPromise()
}

@OptIn(ExperimentalJsExport::class)
@JsExport
fun fetchChapterList(manga: JsManga): Promise<Array<JsChapter>> {
    return source.fetchChapterList(manga.toManga()).map { response ->
        response.map { JsChapter.from(it) }.toTypedArray()
    }.toPromise()
}

@OptIn(ExperimentalJsExport::class)
@JsExport
fun fetchPageList(chapter: JsChapter): Promise<Array<JsPage>> {
    return source.fetchPageList(chapter.toChapter()).map { response ->
        response.map { JsPage.from(it) }.toTypedArray()
    }.toPromise()
}

fun <T> Observable<T>.toPromise(): Promise<T> {
    return Promise { resolve, reject ->
        val subscriber = object : Subscriber<T>() {
            override fun onCompleted() {}
            override fun onError(e: Throwable) {
                reject(e)
            }
            override fun onNext(t: T) {
                resolve(t)
            }
        }
        unsafeSubscribe(subscriber)
    }
}
