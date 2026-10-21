/**
 */
package edu.pa21.util;

import edu.pa21.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see edu.pa21.Pa21Package
 * @generated
 */
public class Pa21AdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static Pa21Package modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Pa21AdapterFactory() {
		if (modelPackage == null) {
			modelPackage = Pa21Package.eINSTANCE;
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
	protected Pa21Switch<Adapter> modelSwitch =
		new Pa21Switch<Adapter>() {
			@Override
			public Adapter casePosition(Position object) {
				return createPositionAdapter();
			}
			@Override
			public Adapter casePositionOffset(PositionOffset object) {
				return createPositionOffsetAdapter();
			}
			@Override
			public Adapter caseCell(Cell object) {
				return createCellAdapter();
			}
			@Override
			public Adapter caseEntity(Entity object) {
				return createEntityAdapter();
			}
			@Override
			public Adapter casePlayer(Player object) {
				return createPlayerAdapter();
			}
			@Override
			public Adapter caseGem(Gem object) {
				return createGemAdapter();
			}
			@Override
			public Adapter caseMine(Mine object) {
				return createMineAdapter();
			}
			@Override
			public Adapter caseExtraLife(ExtraLife object) {
				return createExtraLifeAdapter();
			}
			@Override
			public Adapter caseWall(Wall object) {
				return createWallAdapter();
			}
			@Override
			public Adapter caseStopCell(StopCell object) {
				return createStopCellAdapter();
			}
			@Override
			public Adapter caseEntityCell(EntityCell object) {
				return createEntityCellAdapter();
			}
			@Override
			public Adapter caseGameBoard(GameBoard object) {
				return createGameBoardAdapter();
			}
			@Override
			public Adapter caseGameState(GameState object) {
				return createGameStateAdapter();
			}
			@Override
			public Adapter caseMoveStack(MoveStack object) {
				return createMoveStackAdapter();
			}
			@Override
			public Adapter caseMoveResult(MoveResult object) {
				return createMoveResultAdapter();
			}
			@Override
			public Adapter caseValid(Valid object) {
				return createValidAdapter();
			}
			@Override
			public Adapter caseAlive(Alive object) {
				return createAliveAdapter();
			}
			@Override
			public Adapter caseDead(Dead object) {
				return createDeadAdapter();
			}
			@Override
			public Adapter caseInvalid(Invalid object) {
				return createInvalidAdapter();
			}
			@Override
			public Adapter caseGameController(GameController object) {
				return createGameControllerAdapter();
			}
			@Override
			public Adapter caseGameBoardController(GameBoardController object) {
				return createGameBoardControllerAdapter();
			}
			@Override
			public Adapter caseGameBoardView(GameBoardView object) {
				return createGameBoardViewAdapter();
			}
			@Override
			public Adapter caseBoardElement(BoardElement object) {
				return createBoardElementAdapter();
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
	 * Creates a new adapter for an object of class '{@link edu.pa21.Position <em>Position</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa21.Position
	 * @generated
	 */
	public Adapter createPositionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa21.PositionOffset <em>Position Offset</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa21.PositionOffset
	 * @generated
	 */
	public Adapter createPositionOffsetAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa21.Cell <em>Cell</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa21.Cell
	 * @generated
	 */
	public Adapter createCellAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa21.Entity <em>Entity</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa21.Entity
	 * @generated
	 */
	public Adapter createEntityAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa21.Player <em>Player</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa21.Player
	 * @generated
	 */
	public Adapter createPlayerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa21.Gem <em>Gem</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa21.Gem
	 * @generated
	 */
	public Adapter createGemAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa21.Mine <em>Mine</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa21.Mine
	 * @generated
	 */
	public Adapter createMineAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa21.ExtraLife <em>Extra Life</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa21.ExtraLife
	 * @generated
	 */
	public Adapter createExtraLifeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa21.Wall <em>Wall</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa21.Wall
	 * @generated
	 */
	public Adapter createWallAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa21.StopCell <em>Stop Cell</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa21.StopCell
	 * @generated
	 */
	public Adapter createStopCellAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa21.EntityCell <em>Entity Cell</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa21.EntityCell
	 * @generated
	 */
	public Adapter createEntityCellAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa21.GameBoard <em>Game Board</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa21.GameBoard
	 * @generated
	 */
	public Adapter createGameBoardAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa21.GameState <em>Game State</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa21.GameState
	 * @generated
	 */
	public Adapter createGameStateAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa21.MoveStack <em>Move Stack</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa21.MoveStack
	 * @generated
	 */
	public Adapter createMoveStackAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa21.MoveResult <em>Move Result</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa21.MoveResult
	 * @generated
	 */
	public Adapter createMoveResultAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa21.Valid <em>Valid</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa21.Valid
	 * @generated
	 */
	public Adapter createValidAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa21.Alive <em>Alive</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa21.Alive
	 * @generated
	 */
	public Adapter createAliveAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa21.Dead <em>Dead</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa21.Dead
	 * @generated
	 */
	public Adapter createDeadAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa21.Invalid <em>Invalid</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa21.Invalid
	 * @generated
	 */
	public Adapter createInvalidAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa21.GameController <em>Game Controller</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa21.GameController
	 * @generated
	 */
	public Adapter createGameControllerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa21.GameBoardController <em>Game Board Controller</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa21.GameBoardController
	 * @generated
	 */
	public Adapter createGameBoardControllerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa21.GameBoardView <em>Game Board View</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa21.GameBoardView
	 * @generated
	 */
	public Adapter createGameBoardViewAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa21.BoardElement <em>Board Element</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa21.BoardElement
	 * @generated
	 */
	public Adapter createBoardElementAdapter() {
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

} //Pa21AdapterFactory
