package org.vaulture.project.features.home.presentation.viewmodel

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import org.vaulture.project.features.home.domain.model.FarmerVideoGuide
import org.vaulture.project.features.home.domain.model.RhythmTrack
import org.vaulture.project.features.home.domain.repository.RhythmRepository
import org.vaulture.project.features.home.domain.repository.VideoGuideRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private class FakeVideoGuideRepository : VideoGuideRepository {
    val guides = mutableListOf<FarmerVideoGuide>()
    private val flow = MutableStateFlow<List<FarmerVideoGuide>>(emptyList())

    override fun getVideoGuidesStream(): Flow<List<FarmerVideoGuide>> = flow.asStateFlow()

    override suspend fun addVideoGuide(guide: FarmerVideoGuide) {
        guides.add(guide)
        flow.value = guides.toList()
    }
}

private class FakeRhythmRepository : RhythmRepository {
    val tracks = mutableListOf<RhythmTrack>()
    private val flow = MutableStateFlow<List<RhythmTrack>>(emptyList())

    override fun getTracksStream(): Flow<List<RhythmTrack>> = flow.asStateFlow()

    override suspend fun addTrack(track: RhythmTrack) {
        tracks.add(track)
        flow.value = tracks.toList()
    }
}

class CreateMediaGuideViewModelTest {

    @Test
    fun initial_state_has_sensible_agronomic_defaults() {
        val fakeVideoRepo = FakeVideoGuideRepository()
        val fakeRhythmRepo = FakeRhythmRepository()
        val viewModel = CreateMediaGuideViewModel(fakeVideoRepo, fakeRhythmRepo)

        val state = viewModel.uiState.value
        assertEquals(GuideMediaType.VIDEO, state.mediaType)
        assertEquals("Pest & Disease Control", state.category)
        assertEquals("English", state.language)
        assertFalse(state.isPublishing)
        assertFalse(state.publishSuccess)
    }

    @Test
    fun set_media_type_updates_type_and_default_thumbnail() {
        val fakeVideoRepo = FakeVideoGuideRepository()
        val fakeRhythmRepo = FakeRhythmRepository()
        val viewModel = CreateMediaGuideViewModel(fakeVideoRepo, fakeRhythmRepo)

        viewModel.setMediaType(GuideMediaType.AUDIO)
        assertEquals(GuideMediaType.AUDIO, viewModel.uiState.value.mediaType)

        viewModel.setMediaType(GuideMediaType.VIDEO)
        assertEquals(GuideMediaType.VIDEO, viewModel.uiState.value.mediaType)
    }

    @Test
    fun add_and_remove_key_takeaways() {
        val fakeVideoRepo = FakeVideoGuideRepository()
        val fakeRhythmRepo = FakeRhythmRepository()
        val viewModel = CreateMediaGuideViewModel(fakeVideoRepo, fakeRhythmRepo)

        val initialCount = viewModel.uiState.value.keyTakeaways.size
        viewModel.addKeyTakeaway("Dig Zai pits spaced 75cm apart")
        assertEquals(initialCount + 1, viewModel.uiState.value.keyTakeaways.size)
        assertTrue(viewModel.uiState.value.keyTakeaways.contains("Dig Zai pits spaced 75cm apart"))

        viewModel.removeKeyTakeaway("Dig Zai pits spaced 75cm apart")
        assertEquals(initialCount, viewModel.uiState.value.keyTakeaways.size)
    }

    @Test
    fun toggle_tags_adds_and_removes_cleanly() {
        val fakeVideoRepo = FakeVideoGuideRepository()
        val fakeRhythmRepo = FakeRhythmRepository()
        val viewModel = CreateMediaGuideViewModel(fakeVideoRepo, fakeRhythmRepo)

        viewModel.toggleTag("#drip_irrigation")
        assertTrue(viewModel.uiState.value.tags.contains("drip_irrigation"))

        viewModel.toggleTag("drip_irrigation")
        assertFalse(viewModel.uiState.value.tags.contains("drip_irrigation"))
    }

    @Test
    fun publishing_with_blank_title_fails_validation() = runTest {
        val fakeVideoRepo = FakeVideoGuideRepository()
        val fakeRhythmRepo = FakeRhythmRepository()
        val viewModel = CreateMediaGuideViewModel(fakeVideoRepo, fakeRhythmRepo)

        var successCalled = false
        viewModel.publishGuideInternal { successCalled = true }

        assertFalse(successCalled)
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertEquals(0, fakeVideoRepo.guides.size)
    }

    @Test
    fun publish_video_guide_saves_to_video_repository() = runTest {
        val fakeVideoRepo = FakeVideoGuideRepository()
        val fakeRhythmRepo = FakeRhythmRepository()
        val viewModel = CreateMediaGuideViewModel(fakeVideoRepo, fakeRhythmRepo)

        viewModel.setMediaType(GuideMediaType.VIDEO)
        viewModel.updateTitle("Biological Neem Spray for Armyworm")
        viewModel.updateInstructor("Dr. Auma (NARO)")
        viewModel.updateCategory("Pest & Disease Control")
        viewModel.updateLanguage("Kiswahili")

        var successCalled = false
        viewModel.publishGuideInternal { successCalled = true }

        assertTrue(successCalled)
        assertTrue(viewModel.uiState.value.publishSuccess)
        assertFalse(viewModel.uiState.value.isPublishing)
        assertEquals(1, fakeVideoRepo.guides.size)
        val published = fakeVideoRepo.guides.first()
        assertEquals("Biological Neem Spray for Armyworm", published.title)
        assertEquals("Dr. Auma (NARO)", published.instructor)
        assertEquals("Kiswahili", published.language)
    }

    @Test
    fun publish_audio_lesson_saves_to_rhythm_repository() = runTest {
        val fakeVideoRepo = FakeVideoGuideRepository()
        val fakeRhythmRepo = FakeRhythmRepository()
        val viewModel = CreateMediaGuideViewModel(fakeVideoRepo, fakeRhythmRepo)

        viewModel.setMediaType(GuideMediaType.AUDIO)
        viewModel.updateTitle("Radio Extension: Drought Planting Calendar")
        viewModel.updateInstructor("Radio Simba Agri Desk")
        viewModel.updateCategory("Crop Yield & Harvesting")
        viewModel.updateLanguage("Luganda")

        var successCalled = false
        viewModel.publishGuideInternal { successCalled = true }

        assertTrue(successCalled)
        assertTrue(viewModel.uiState.value.publishSuccess)
        assertEquals(1, fakeRhythmRepo.tracks.size)
        val published = fakeRhythmRepo.tracks.first()
        assertEquals("Radio Extension: Drought Planting Calendar", published.title)
        assertEquals("Radio Simba Agri Desk", published.artist)
    }
}
