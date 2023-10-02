package com.rubylichtenstein.ui.favorites

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.rubylichtenstein.domain.images.DogImageEntity
import com.rubylichtenstein.ui.common.AsyncResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FavoritesPresenterTest {

    @Test
    fun `FavoritesPresenter starts with loading state`() = runTest {
        val eventFlow = MutableSharedFlow<Event>()
        val favoriteImagesFlow = flowOf<List<DogImageEntity>>()  // An empty flow

        moleculeFlow(RecompositionMode.Immediate) {
            FavoritesPresenter(eventFlow, favoriteImagesFlow)
        }.test {
            assertTrue(awaitItem() is AsyncResult.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `FavoritesPresenter handles error state`() = runTest {
        val eventFlow = MutableSharedFlow<Event>()
        val favoriteImagesFlow = flow<List<DogImageEntity>> {
            throw RuntimeException("Error loading images")
        }

        moleculeFlow(RecompositionMode.Immediate) {
            FavoritesPresenter(eventFlow, favoriteImagesFlow)
        }.test {
            assertTrue(awaitItem() is AsyncResult.Loading)
            val result = awaitItem()
            assertTrue(result is AsyncResult.Error)
            assertEquals("Error loading images", (result as AsyncResult.Error).exception?.message)
            cancel()
        }
    }

//    @Test
//    fun `FavoritesPresenter returns empty dog images on non-existent breed selection`() = runTest {
//        val breedToSelect = ChipInfo("Husky1", selected = true)
//        val eventFlow = MutableSharedFlow<Event>()
//        val favoriteImagesFlow = flowOf(
//            listOf(
//                DogImageEntity(
//                    "Husky1",
//                    "Husky",
//                    true,
//                    "husky",
//                )
//            )
//        )
//
//        moleculeFlow(RecompositionMode.Immediate) {
//            FavoritesPresenter(eventFlow, favoriteImagesFlow)
//        }.test {
//            eventFlow.emit(Event.ToggleSelectedBreed(breedToSelect))
//            assertTrue(awaitItem() is AsyncResult.Loading)
//            val result = awaitItem()
//            println(result)
//            assertTrue(result is AsyncResult.Success)
//            assertTrue((result as AsyncResult.Success).data.dogImages.isEmpty())
//            cancelAndIgnoreRemainingEvents()
//        }
//    }

    @Test
    fun `FavoritesPresenter returns success state with dog images`() = runTest {
        val eventFlow = MutableSharedFlow<Event>()
        val favoriteImagesFlow = flowOf(
            listOf(
                DogImageEntity(
                    "Husky1",
                    "Husky",
                    true,
                    "husky",
                )
            )
        )

        moleculeFlow(RecompositionMode.Immediate) {
            FavoritesPresenter(eventFlow, favoriteImagesFlow)
        }.test {
            assertTrue(awaitItem() is AsyncResult.Loading)
            assertTrue(awaitItem() is AsyncResult.Success)
            cancel()
        }
    }

//    @Test
//    fun `FavoritesPresenter filters dog images correctly`() = runTest {
//        val initialBreed = ChipInfo("Husky", selected = false)
//        val eventFlow = MutableSharedFlow<Event>()
//        val favoriteImagesFlow = flowOf(
//            listOf(
//                DogImageEntity(
//                    "Husky1",
//                    "Husky",
//                    true,
//                    "husky",
//                )
//            )
//        )
//
//        moleculeFlow(RecompositionMode.Immediate) {
//            FavoritesPresenter(eventFlow, favoriteImagesFlow)
//        }.test {
//            eventFlow.emit(Event.ToggleSelectedBreed(initialBreed))
//            assertTrue(awaitItem() is AsyncResult.Loading)
//            val result = awaitItem() as AsyncResult.Success
//            assertTrue(result.data.dogImages.isNotEmpty())
//            cancel()
//        }
//    }

    @Test
    fun `FavoritesPresenter returns error state if flow errors`() = runTest {
        val eventFlow = MutableSharedFlow<Event>()
        val favoriteImagesFlow = flow<List<DogImageEntity>> {
            throw RuntimeException("Error fetching images")
        }

        moleculeFlow(RecompositionMode.Immediate) {
            FavoritesPresenter(eventFlow, favoriteImagesFlow)
        }.test {
            assertTrue(awaitItem() is AsyncResult.Loading)
            assertTrue(awaitItem() is AsyncResult.Error)
            cancel()
        }
    }

    @Test
    fun `FavoritesPresenter returns success state with no images`() = runTest {
        val eventFlow = MutableSharedFlow<Event>()
        val favoriteImagesFlow = flowOf(emptyList<DogImageEntity>())

        moleculeFlow(RecompositionMode.Immediate) {
            FavoritesPresenter(eventFlow, favoriteImagesFlow)
        }.test {
            assertTrue(awaitItem() is AsyncResult.Loading)
            val result = awaitItem() as AsyncResult.Success
            assertTrue(result.data.dogImages.isEmpty())
            cancel()
        }
    }
}