package com.swirlfist.simplepixel.data.repository

import com.swirlfist.simplepixel.domain.model.PixelImageEditorAction
import com.swirlfist.simplepixel.domain.model.PixelImageEditorActionContext
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertNull

class PixelImageEditorActionRepositoryImplTest {

    private lateinit var repository: PixelImageEditorActionRepository

    @Before
    fun setup() {
        repository = PixelImageEditorActionRepositoryImpl()
    }

    @Test
    fun `when repository is created actions are empty`() = runTest {
        // Given

        // When
        val actions = repository.getActions()

        // Then
        assertTrue { actions.isEmpty() }
    }

    @Test
    fun `when repository is created the current action is null`() = runTest {
        // Given

        // When
        val currentAction = repository.getCurrentAction()

        // Then
        assertNull(currentAction)
    }

    @Test
    fun `when we add the first action then actions just contain that action`() = runTest {
        // Given
        val pixelImage = mockk<PixelImageModel>()
        val action = mockk<PixelImageEditorAction>()
        every { action.actionContext }.returns(PixelImageEditorActionContext(pixelImage))

        // When
        repository.addAction(action, actionContextResult = PixelImageEditorActionContext(mockk()))
        val actions = repository.getActions()

        // Then
        assertEquals(1, actions.size)
        assertEquals(action, actions[0])
    }

    @Test
    fun `when we add an action then it gets added after the current index becoming the last action`() =
        runTest {
            // Given
            val action1 = mockk<PixelImageEditorAction>().also { action ->
                every { action.actionContext }.returns(PixelImageEditorActionContext(mockk()))
            }
            val action2 = mockk<PixelImageEditorAction>().also { action ->
                every { action.actionContext }.returns(PixelImageEditorActionContext(mockk()))
            }
            val action3 = mockk<PixelImageEditorAction>().also { action ->
                every { action.actionContext }.returns(PixelImageEditorActionContext(mockk()))
            }
            repository.addAction(action1, actionContextResult = PixelImageEditorActionContext(mockk()))
            repository.addAction(action2, actionContextResult = PixelImageEditorActionContext(mockk()))
            repository.addAction(action3, actionContextResult = PixelImageEditorActionContext(mockk()))
            repository.undoAction()
            repository.undoAction()
            val action4 = mockk<PixelImageEditorAction>()

            // When
            repository.addAction(action4, actionContextResult = PixelImageEditorActionContext(mockk()))
            val actions = repository.getActions()
            val currentAction = repository.getCurrentAction()

            // Then
            assertEquals(2, actions.size)
            assertEquals(action1, actions[0])
            assertEquals(action4, actions[1])
            assertEquals(action4, currentAction)
        }

    @Test
    fun `when we clear actions then actions are empty`() = runTest {
        // Given
        val action1 = mockk<PixelImageEditorAction>().also { action ->
            every { action.actionContext }.returns(PixelImageEditorActionContext(mockk()))
        }
        val action2 = mockk<PixelImageEditorAction>().also { action ->
            every { action.actionContext }.returns(PixelImageEditorActionContext(mockk()))
        }
        repository.addAction(action1, actionContextResult = PixelImageEditorActionContext(mockk()))
        repository.addAction(action2, actionContextResult = PixelImageEditorActionContext(mockk()))

        // When
        repository.clearActions()
        val actions = repository.getActions()

        // Then
        assertTrue(actions.isEmpty())
    }

    @Test
    fun `when we clear actions then current actions is null`() = runTest {
        // Given
        val action1 = mockk<PixelImageEditorAction>().also { action ->
            every { action.actionContext }.returns(PixelImageEditorActionContext(mockk()))
        }
        val action2 = mockk<PixelImageEditorAction>().also { action ->
            every { action.actionContext }.returns(PixelImageEditorActionContext(mockk()))
        }
        repository.addAction(action1, actionContextResult = PixelImageEditorActionContext(mockk()))
        repository.addAction(action2, actionContextResult = PixelImageEditorActionContext(mockk()))

        // When
        repository.clearActions()
        val currentAction = repository.getCurrentAction()

        // Then
        assertNull(currentAction)
    }

    @Test
    fun `when there is no action to undo null is returned`() = runTest {
        // Given
        val action = mockk<PixelImageEditorAction>().also { action ->
            every { action.actionContext }.returns(PixelImageEditorActionContext(mockk()))
        }
        repository.addAction(action, actionContextResult = PixelImageEditorActionContext(mockk()))
        repository.undoAction()

        // When
        val result = repository.undoAction()

        // Then
        assertNull(result)
    }

    @Test
    fun `when there is an action to undo that image is returned`() = runTest {
        // Given
        val pixelImage2 = mockk<PixelImageModel>()
        val action1 = mockk<PixelImageEditorAction>().also { action ->
            every { action.actionContext }.returns(PixelImageEditorActionContext(mockk()))
        }
        val action2 = mockk<PixelImageEditorAction>().also { action ->
            every { action.actionContext }.returns(PixelImageEditorActionContext(pixelImage2))
        }
        repository.addAction(action1, actionContextResult = PixelImageEditorActionContext(mockk()))
        repository.addAction(action2, actionContextResult = PixelImageEditorActionContext(mockk()))

        // When
        val result = repository.undoAction()

        // Then
        assertEquals(pixelImage2, result?.pixelImage)
    }

    @Test
    fun `when there is an action to undo the current action becomes the previous one`() = runTest {
        // Given
        val pixelImage2 = mockk<PixelImageModel>()
        val action1 = mockk<PixelImageEditorAction>().also { action ->
            every { action.actionContext }.returns(PixelImageEditorActionContext(mockk()))
        }
        val action2 = mockk<PixelImageEditorAction>().also { action ->
            every { action.actionContext }.returns(PixelImageEditorActionContext(pixelImage2))
        }
        repository.addAction(action1, actionContextResult = PixelImageEditorActionContext(mockk()))
        repository.addAction(action2, actionContextResult = PixelImageEditorActionContext(mockk()))

        // When
        repository.undoAction()

        // Then
        assertEquals(action1, repository.getCurrentAction())
    }

    @Test
    fun `when undoing the last action the current action becomes null`() = runTest {
        // Given
        val action = mockk<PixelImageEditorAction>().also { action ->
            every { action.actionContext }.returns(PixelImageEditorActionContext(mockk()))
        }
        repository.addAction(action, actionContextResult = PixelImageEditorActionContext(mockk()))

        // When
        repository.undoAction()

        // Then
        assertNull(repository.getCurrentAction())
    }

    @Test
    fun `when there is no action to redo null is returned`() = runTest {
        // Given
        val action = mockk<PixelImageEditorAction>().also { action ->
            every { action.actionContext }.returns(PixelImageEditorActionContext(mockk()))
        }
        repository.addAction(action, actionContextResult = PixelImageEditorActionContext(mockk()))

        // When
        val result = repository.redoAction()

        // Then
        assertNull(result)
    }

    @Test
    fun `when there is an action to redo the resulting image is returned`() = runTest {
        // Given
        val pixelImage = mockk<PixelImageModel>()
        val pixelImageResult = mockk<PixelImageModel>()
        val action = mockk<PixelImageEditorAction>().also { action ->
            every { action.actionContext }.returns(PixelImageEditorActionContext(pixelImage))
        }
        repository.addAction(action, actionContextResult = PixelImageEditorActionContext(pixelImageResult))
        repository.undoAction()

        // When
        val result = repository.redoAction()
        val currentAction = repository.getCurrentAction()

        // Then
        assertEquals(pixelImageResult, result?.pixelImage)
        assertEquals(action, currentAction)
    }

    @Test
    fun `when there is an action to redo the current action becomes the next one`() = runTest {
        // Given
        val pixelImage1 = mockk<PixelImageModel>()
        val action1 = mockk<PixelImageEditorAction>().also { action ->
            every { action.actionContext }.returns(PixelImageEditorActionContext(pixelImage1))
        }
        val pixelImage2 = mockk<PixelImageModel>()
        val action2 = mockk<PixelImageEditorAction>().also { action ->
            every { action.actionContext }.returns(PixelImageEditorActionContext(pixelImage2))
        }
        repository.addAction(action1, actionContextResult = PixelImageEditorActionContext(mockk()))
        repository.addAction(action2, actionContextResult = PixelImageEditorActionContext(mockk()))
        repository.undoAction()
        repository.undoAction()

        // When
        val result = repository.redoAction()

        // Then
        assertEquals(pixelImage2, result?.pixelImage)
        assertEquals(action1, repository.getCurrentAction())
    }

    @Test
    fun `when clearing actions then undo and redo are not available`() = runTest {
        // Given
        val pixelImage = mockk<PixelImageModel>()
        val action = mockk<PixelImageEditorAction>().also { action ->
            every { action.actionContext }.returns(PixelImageEditorActionContext(pixelImage))
        }
        repository.addAction(action, actionContextResult = PixelImageEditorActionContext(mockk()))

        // When
        repository.clearActions()

        // Then
        assertFalse(repository.isUndoAvailable().first())
        assertFalse(repository.isRedoAvailable().first())
    }

    @Test
    fun `when adding an action then undo is available`() = runTest {
        // Given
        val pixelImage = mockk<PixelImageModel>()
        val action = mockk<PixelImageEditorAction>().also { action ->
            every { action.actionContext }.returns(PixelImageEditorActionContext(pixelImage))
        }

        // When
        repository.addAction(action, actionContextResult = PixelImageEditorActionContext(mockk()))

        // Then
        assertTrue(repository.isUndoAvailable().first())
    }

    @Test
    fun `when undoing an action then redo is available`() = runTest {
        // Given
        val pixelImage = mockk<PixelImageModel>()
        val action = mockk<PixelImageEditorAction>().also { action ->
            every { action.actionContext }.returns(PixelImageEditorActionContext(pixelImage))
        }
        repository.addAction(action, actionContextResult = PixelImageEditorActionContext(mockk()))

        // When
        repository.undoAction()

        // Then
        assertTrue(repository.isRedoAvailable().first())
    }

    @Test
    fun `when redoing an action then undo is available`() = runTest {
        // Given
        val pixelImage = mockk<PixelImageModel>()
        val action = mockk<PixelImageEditorAction>().also { action ->
            every { action.actionContext }.returns(PixelImageEditorActionContext(pixelImage))
        }
        repository.addAction(action, actionContextResult = PixelImageEditorActionContext(mockk()))
        repository.undoAction()

        // When
        repository.redoAction()

        // Then
        assertTrue(repository.isUndoAvailable().first())
    }

    @Test
    fun `when undoing the first action then undo is not available`() = runTest {
        // Given
        val pixelImage = mockk<PixelImageModel>()
        val action = mockk<PixelImageEditorAction>().also { action ->
            every { action.actionContext }.returns(PixelImageEditorActionContext(pixelImage))
        }
        repository.addAction(action, actionContextResult = PixelImageEditorActionContext(mockk()))

        // When
        repository.undoAction()

        // Then
        assertFalse(repository.isUndoAvailable().first())
    }

    @Test
    fun `when redoing the last action then redo is not available`() = runTest {
        // Given
        val pixelImage = mockk<PixelImageModel>()
        val action = mockk<PixelImageEditorAction>().also { action ->
            every { action.actionContext }.returns(PixelImageEditorActionContext(pixelImage))
        }
        repository.addAction(action, actionContextResult = PixelImageEditorActionContext(mockk()))
        repository.undoAction()

        // When
        repository.redoAction()

        // Then
        assertFalse(repository.isRedoAvailable().first())
    }

    @Test
    fun `when adding an action if the max undo actions is reached then the first action is dropped`() =
        runTest {
            // Given
            val actions = mutableListOf<PixelImageEditorAction>().apply {
                repeat(MAX_UNDO_ACTIONS) {
                    add(mockk<PixelImageEditorAction>())
                }
            }
            actions.forEach { action ->
                repository.addAction(action, actionContextResult = PixelImageEditorActionContext(mockk()))
            }

            // When
            repository.addAction(mockk(), actionContextResult = PixelImageEditorActionContext(mockk()))

            // Then
            val actionsInRepository = repository.getActions()
            assertEquals(MAX_UNDO_ACTIONS, actionsInRepository.size)
            assertEquals(actions[1], actionsInRepository[0])
        }

    @Test
    fun `when updating the last current action then the number of actions does not increase`() =
        runTest {
            // Given
            val pixelImage1 = mockk<PixelImageModel>()
            val action1 = mockk<PixelImageEditorAction>().also { action ->
                every { action.actionContext }.returns(PixelImageEditorActionContext(pixelImage1))
            }
            repository.addAction(action1, actionContextResult = PixelImageEditorActionContext(mockk()))

            // When
            repository.updateLastAction(actionContextResult = PixelImageEditorActionContext(mockk()))

            // Then
            val actions = repository.getActions()
            assertEquals(1, actions.size)
            assertEquals(action1, actions[0])
        }

    @Test
    fun `when updating the last current action then an undo returns the original action image`() =
        runTest {
            // Given
            val pixelImage1 = mockk<PixelImageModel>()
            val action1 = mockk<PixelImageEditorAction>().also { action ->
                every { action.actionContext }.returns(PixelImageEditorActionContext(pixelImage1))
            }
            repository.addAction(action1, actionContextResult = PixelImageEditorActionContext(mockk()))

            // When
            repository.updateLastAction(actionContextResult = PixelImageEditorActionContext(mockk()))
            val result = repository.undoAction()

            // Then
            assertEquals(pixelImage1, result?.pixelImage)
        }

    @Test
    fun `when updating the last current action then a redo after an undo returns the final image`() =
        runTest {
            // Given
            val pixelImage1 = mockk<PixelImageModel>()
            val action1 = mockk<PixelImageEditorAction>().also { action ->
                every { action.actionContext }.returns(PixelImageEditorActionContext(pixelImage1))
            }
            repository.addAction(action1, actionContextResult = PixelImageEditorActionContext(mockk()))
            val finalPixelImage = mockk<PixelImageModel>()

            // When
            repository.updateLastAction(actionContextResult = PixelImageEditorActionContext(finalPixelImage))
            repository.undoAction()
            val result = repository.redoAction()

            // Then
            assertEquals(finalPixelImage, result?.pixelImage)
        }
}