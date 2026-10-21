/**
 */
package edu.pa22.util;

import edu.pa22.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see edu.pa22.Pa22Package
 * @generated
 */
public class Pa22AdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static Pa22Package modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Pa22AdapterFactory() {
		if (modelPackage == null) {
			modelPackage = Pa22Package.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Pa22Switch<Adapter> modelSwitch =
		new Pa22Switch<Adapter>() {
			@Override
			public Adapter caseAction(Action object) {
				return createActionAdapter();
			}
			@Override
			public Adapter caseActionResult(ActionResult object) {
				return createActionResultAdapter();
			}
			@Override
			public Adapter caseSuccess(Success object) {
				return createSuccessAdapter();
			}
			@Override
			public Adapter caseFailed(Failed object) {
				return createFailedAdapter();
			}
			@Override
			public Adapter caseExit(Exit object) {
				return createExitAdapter();
			}
			@Override
			public Adapter caseInvalidInput(InvalidInput object) {
				return createInvalidInputAdapter();
			}
			@Override
			public Adapter caseUndo(Undo object) {
				return createUndoAdapter();
			}
			@Override
			public Adapter caseEntity(Entity object) {
				return createEntityAdapter();
			}
			@Override
			public Adapter caseBox(Box object) {
				return createBoxAdapter();
			}
			@Override
			public Adapter caseEmpty(Empty object) {
				return createEmptyAdapter();
			}
			@Override
			public Adapter casePlayer(Player object) {
				return createPlayerAdapter();
			}
			@Override
			public Adapter caseWall(Wall object) {
				return createWallAdapter();
			}
			@Override
			public Adapter caseAbstractSokobanGame(AbstractSokobanGame object) {
				return createAbstractSokobanGameAdapter();
			}
			@Override
			public Adapter casePosition(Position object) {
				return createPositionAdapter();
			}
			@Override
			public Adapter caseGameMap(GameMap object) {
				return createGameMapAdapter();
			}
			@Override
			public Adapter caseGameState(GameState object) {
				return createGameStateAdapter();
			}
			@Override
			public Adapter caseGameStateTransition(GameStateTransition object) {
				return createGameStateTransitionAdapter();
			}
			@Override
			public Adapter caseRenderingEngine(RenderingEngine object) {
				return createRenderingEngineAdapter();
			}
			@Override
			public Adapter caseTerminalSokobanGame(TerminalSokobanGame object) {
				return createTerminalSokobanGameAdapter();
			}
			@Override
			public Adapter caseTerminalInputEngine(TerminalInputEngine object) {
				return createTerminalInputEngineAdapter();
			}
			@Override
			public Adapter caseTerminalRenderingEngine(TerminalRenderingEngine object) {
				return createTerminalRenderingEngineAdapter();
			}
			@Override
			public Adapter caseMove(Move object) {
				return createMoveAdapter();
			}
			@Override
			public Adapter caseUp(Up object) {
				return createUpAdapter();
			}
			@Override
			public Adapter caseDown(Down object) {
				return createDownAdapter();
			}
			@Override
			public Adapter caseLeft(Left object) {
				return createLeftAdapter();
			}
			@Override
			public Adapter caseRight(Right object) {
				return createRightAdapter();
			}
			@Override
			public Adapter casePA22Helper(PA22Helper object) {
				return createPA22HelperAdapter();
			}
			@Override
			public Adapter caseInputEngine(InputEngine object) {
				return createInputEngineAdapter();
			}
			@Override
			public Adapter caseSokobanGame(SokobanGame object) {
				return createSokobanGameAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.Action <em>Action</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.Action
	 * @generated
	 */
	public Adapter createActionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.ActionResult <em>Action Result</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.ActionResult
	 * @generated
	 */
	public Adapter createActionResultAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.Success <em>Success</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.Success
	 * @generated
	 */
	public Adapter createSuccessAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.Failed <em>Failed</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.Failed
	 * @generated
	 */
	public Adapter createFailedAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.Exit <em>Exit</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.Exit
	 * @generated
	 */
	public Adapter createExitAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.InvalidInput <em>Invalid Input</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.InvalidInput
	 * @generated
	 */
	public Adapter createInvalidInputAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.Undo <em>Undo</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.Undo
	 * @generated
	 */
	public Adapter createUndoAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.Entity <em>Entity</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.Entity
	 * @generated
	 */
	public Adapter createEntityAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.Box <em>Box</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.Box
	 * @generated
	 */
	public Adapter createBoxAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.Empty <em>Empty</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.Empty
	 * @generated
	 */
	public Adapter createEmptyAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.Player <em>Player</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.Player
	 * @generated
	 */
	public Adapter createPlayerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.Wall <em>Wall</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.Wall
	 * @generated
	 */
	public Adapter createWallAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.AbstractSokobanGame <em>Abstract Sokoban Game</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.AbstractSokobanGame
	 * @generated
	 */
	public Adapter createAbstractSokobanGameAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.Position <em>Position</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.Position
	 * @generated
	 */
	public Adapter createPositionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.GameMap <em>Game Map</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.GameMap
	 * @generated
	 */
	public Adapter createGameMapAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.GameState <em>Game State</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.GameState
	 * @generated
	 */
	public Adapter createGameStateAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.GameStateTransition <em>Game State Transition</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.GameStateTransition
	 * @generated
	 */
	public Adapter createGameStateTransitionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.RenderingEngine <em>Rendering Engine</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.RenderingEngine
	 * @generated
	 */
	public Adapter createRenderingEngineAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.TerminalSokobanGame <em>Terminal Sokoban Game</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.TerminalSokobanGame
	 * @generated
	 */
	public Adapter createTerminalSokobanGameAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.TerminalInputEngine <em>Terminal Input Engine</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.TerminalInputEngine
	 * @generated
	 */
	public Adapter createTerminalInputEngineAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.TerminalRenderingEngine <em>Terminal Rendering Engine</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.TerminalRenderingEngine
	 * @generated
	 */
	public Adapter createTerminalRenderingEngineAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.Move <em>Move</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.Move
	 * @generated
	 */
	public Adapter createMoveAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.Up <em>Up</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.Up
	 * @generated
	 */
	public Adapter createUpAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.Down <em>Down</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.Down
	 * @generated
	 */
	public Adapter createDownAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.Left <em>Left</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.Left
	 * @generated
	 */
	public Adapter createLeftAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.Right <em>Right</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.Right
	 * @generated
	 */
	public Adapter createRightAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.PA22Helper <em>PA22 Helper</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.PA22Helper
	 * @generated
	 */
	public Adapter createPA22HelperAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.InputEngine <em>Input Engine</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.InputEngine
	 * @generated
	 */
	public Adapter createInputEngineAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa22.SokobanGame <em>Sokoban Game</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa22.SokobanGame
	 * @generated
	 */
	public Adapter createSokobanGameAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //Pa22AdapterFactory
