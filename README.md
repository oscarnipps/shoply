# Shoply

Shoply (I know the name is not as cool as `CoolBlue`'s but that was what i could think of) is an Android app for searching the products Coolblue sells. The app is built with a focus on a clear separation of concerns, predictable state, and testability.

---

# Built With 🛠	
* Kotlin
* Kotlin Coroutines
* Kotlin Flows
* Jetpack Compose
* ViewModel
* Retrofit
* OkHttp
* Jetpack Paging
* Coil
* Moshi
* Junit
* Mockk 
* Dagger Hilt

---

## App architecture

The app follows **Clean Architecture** principles with an **MVI (Model–View–Intent)** presentation pattern.

<img width="1920" height="1280" alt="image" src="https://github.com/user-attachments/assets/b9c74286-539b-4319-97ed-76fd12b18168" />


- **Dependencies point inwards.** The UI depends on the data layer's abstractions (`ProductRepository`), and both depend on the domain models. The domain depends on nothing.
- **Every layer talks through interfaces** (`ProductRepository`, `ProductSearchDataSource`), so each one can be replaced by a fake in tests.
- **Data flows one way.** User actions go up as events, and state comes down as a single `StateFlow`.

---

## Architecture layers

### Data layer

The data layer fetches products from the API, handles errors, maps the network models to domain models, and provides paging.

- Service : `ProductSearchService` makes use of retrofit to make the netwok call with the endpoint (get back a `ProductSearchResponse`)  and query parameter - `GET search?query=iphone&page=1`.
- Entity Mapper : `ProductMapper` | Converts DTOs into domain models (`ProductItem`, `ProductPage`)
- SearchDataSource : `ProductSearchDataSource` Calls the service, maps the result and wraps it in an `AppResult`. Exceptions are translated into domain errors: `IOException` → `NO_INTERNET`, `HttpException` / parsing errors → `TECHNICAL`, `totalResults == 0` → `NO_DATA`.

  The reason for a separate data source is because the paging source only knows about paging keys, while the data source only knows about the network and errors. Each one can be tested on its own i.e the data source with a mocked Retrofit service, and the paging source with a fake data source.

- Paging : Paging 3 source (`ProductPagingSource`) requests a page from the data source and turns the `AppResult` into a `LoadResult`. Errors are wrapped in an `AppException` so the `AppError` survives Paging's `Throwable`-only error API.
- Reposiotry : `ProductRepository` builds the `Pager` (`pageSize = 24`, `initialLoadSize = 24`, placeholders off) and exposes a cold `Flow<PagingData<ProductItem>>`. `searchProducts` is not a `suspend` function: it only returns a stream, and nothing happens until the stream is collected.



### Domain layer

The domain layer holds plain Kotlin models with no Android or framework dependencies:

- `ProductItem`: a product as the app understands it (name, price, rating, selling points, availability, delivery).
- `ProductPage`: one page of products plus the paging metadata (`currentPage`, `pageCount`).
- `AppResult<T>`: `Success(data)` or `Error(appError, message)`.
- `AppError`: `NO_INTERNET`, `NO_DATA`, `TECHNICAL`.
- `AppException`: carries an `AppError` through Paging.

There are no use cases. The screen has one flow (search → list → detail), and a use case would only forward calls to the repository. Because the models are pure Kotlin, they can move unchanged into a Kotlin Multiplatform / Compose Multiplatform `shared` module.

### UI layer

#### Unidirectional data flow (MVI)

Think of the screen as a **restaurant**:

- The **composables are the waiter**. They never cook as they only take **orders** (`ProductSearchScreenEvent`) and serve **plates** (`ProductSearchScreenState`).
- The **ViewModel is the kitchen**. It receives every order, decides what changes, and sends out a new plate.
- The waiter only ever shows what the kitchen sent, so what's on screen always matches what the ViewModel decided.

**Events** (orders) the screen can send:

| Event | Sent when | Effect |
|---|---|---|
| `SearchModeActive` | The search field gains focus | Enters search mode (Cancel appears) |
| `QueryChanged(query)` | The user types | Updates the query; a search starts after the debounce |
| `ClearSearchClicked` | The ✕ in the field, or "Clear search" on the empty state | Empties the query and stays in search mode |
| `CancelClicked` | The Cancel button or system back | Leaves search mode, back to the launch screen |
| `ProductClicked(product)` | A product card is tapped | Opens the bottom sheet |
| `BottomSheetDismissed` | Swipe down, scrim tap, back, or close | Closes the bottom sheet |
| `RetryClicked` | Retry on a full-screen error | Reloads the current search from page 1 |
| `PagingLoadStateChanged(refresh, itemCount)` | Paging's load state changes (sent by the UI) | Updates loading / error / empty content |


#### Paging Implementation Idea

Paging works like **a restaurant serving courses**. The first course (page 1, 24 products) is served when the search starts. When you're nearly finished with it (Paging prefetches roughly one page ahead), the next course is already on its way. When the API says there are no more pages (`pageCount`), the kitchen closes.

- **The first page** comes from Paging's `refresh` load. Its state drives the whole content area: skeleton, list, empty state, offline, or error.
- **Further pages** come from the `append` load. A failure here doesn't throw away what's already shown. The list keeps its items and shows a "Couldn't load more" row whose Retry calls `products.retry()`, which repeats only the page that failed.
- **Load states round trip through the ViewModel.** Paging only exposes load states to whoever collects the list (`LazyPagingItems` in the UI). The screen reports them back with `PagingLoadStateChanged(refresh, itemCount)` inside a `LaunchedEffect` keyed on those two values. The ViewModel stays the single place that decides what the screen shows.

---

## Testing

Unit tests cover every layer that contains logic. The domain layer only has plain models, so it has no tests.

Notes:
- **Fakes are used for our own abstractions**, and mocks only for third-party APIs (Retrofit).
- **Screen state is checked by reading `screenState.value`** straight after an event. The state updates synchronously, so Turbine isn't needed.
- **The UI mapping was moved out of the ViewModel** into `ProductUiMapper` so it can be tested without Paging. Snapshotting a debounced `cachedIn` flow inside the ViewModel test proved unreliable.
- **The search pipeline tests move virtual time explicitly** (`advanceTimeBy` + `runCurrent`). The `products` collector runs in `backgroundScope`, and `advanceUntilIdle()` can stop before background work such as the debounce has run.

---

## Limitations and considerations

- **No UI tests.** Compose UI and screenshot tests were left out because of time. The screens are built so they're easy to add later. `ProductSearchContent` is stateless and every state already has a preview.
- **The page size is fixed at 24.** The API always returns 24 items per page. I tried using `limit`, `size` and `pageSize` were all tried as query parameters and ignored but only `page` works so i saw no reason to ask Eleonora or the team any questions.
- **The availability mapping is an assumption.** `availabilityState` is mapped as `2` → in stock, `1` → low stock, `0` → out of stock. 
- **There's no offline cache.** There's a single remote source and no `RemoteMediator` or database, so results don't survive process death or work offline.
- **Formatting is fixed to the Netherlands.** Prices and ratings always use the `nl-NL` locale and `€`, whatever the device language. The UI text is in English while product content (names, selling points) comes from the API in Dutch.
- **The screen may briefly flicker on rotation.** Load states round-trip from the UI to the ViewModel. On a configuration change, a fresh `LazyPagingItems` can briefly report `Loading` before the cached data arrives, which on some Paging versions shows the skeleton for a frame or closes an open bottom sheet.
- **Moshi uses reflection.** `KotlinJsonAdapterFactory` pulls in `kotlin-reflect` and needs R8 keep rules for the DTOs. Switching to Moshi codegen (`@JsonClass(generateAdapter = true)`) would remove both.
- **Load-more retry is separate from full retry.** A failed next page retries only that page (`products.retry()`), while a full-screen error rebuilds the whole search (`RetryClicked`). This is intended: the first keeps what's already loaded, the second starts clean.

# App Screen Flow 🎨

Happy Flow:
https://github.com/user-attachments/assets/45cd156d-c0d1-430b-ada5-6d1b9e1f57dd

No Internet Flow:
https://github.com/user-attachments/assets/a16fccbf-49f9-48c0-b20c-0a5b20103fd6

Internal Error:
https://github.com/user-attachments/assets/f034b2f7-6f98-4499-a72d-2d10f9a55088
