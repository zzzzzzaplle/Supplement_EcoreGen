/**
 */
package edu.pa21.util;

import edu.pa21.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see edu.pa21.Pa21Package
 * @generated
 */
public class Pa21Switch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static Pa21Package modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Pa21Switch() {
		if (modelPackage == null) {
			modelPackage = Pa21Package.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		return ePackage == modelPackage;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case Pa21Package.POSITION: {
				Position position = (Position)theEObject;
				T result = casePosition(position);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa21Package.POSITION_OFFSET: {
				PositionOffset positionOffset = (PositionOffset)theEObject;
				T result = casePositionOffset(positionOffset);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa21Package.CELL: {
				Cell cell = (Cell)theEObject;
				T result = caseCell(cell);
				if (result == null) result = caseBoardElement(cell);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa21Package.ENTITY: {
				Entity entity = (Entity)theEObject;
				T result = caseEntity(entity);
				if (result == null) result = caseBoardElement(entity);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa21Package.PLAYER: {
				Player player = (Player)theEObject;
				T result = casePlayer(player);
				if (result == null) result = caseEntity(player);
				if (result == null) result = caseBoardElement(player);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa21Package.GEM: {
				Gem gem = (Gem)theEObject;
				T result = caseGem(gem);
				if (result == null) result = caseEntity(gem);
				if (result == null) result = caseBoardElement(gem);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa21Package.MINE: {
				Mine mine = (Mine)theEObject;
				T result = caseMine(mine);
				if (result == null) result = caseEntity(mine);
				if (result == null) result = caseBoardElement(mine);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa21Package.EXTRA_LIFE: {
				ExtraLife extraLife = (ExtraLife)theEObject;
				T result = caseExtraLife(extraLife);
				if (result == null) result = caseEntity(extraLife);
				if (result == null) result = caseBoardElement(extraLife);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa21Package.WALL: {
				Wall wall = (Wall)theEObject;
				T result = caseWall(wall);
				if (result == null) result = caseCell(wall);
				if (result == null) result = caseBoardElement(wall);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa21Package.STOP_CELL: {
				StopCell stopCell = (StopCell)theEObject;
				T result = caseStopCell(stopCell);
				if (result == null) result = caseEntityCell(stopCell);
				if (result == null) result = caseCell(stopCell);
				if (result == null) result = caseBoardElement(stopCell);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa21Package.ENTITY_CELL: {
				EntityCell entityCell = (EntityCell)theEObject;
				T result = caseEntityCell(entityCell);
				if (result == null) result = caseCell(entityCell);
				if (result == null) result = caseBoardElement(entityCell);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa21Package.GAME_BOARD: {
				GameBoard gameBoard = (GameBoard)theEObject;
				T result = caseGameBoard(gameBoard);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa21Package.GAME_STATE: {
				GameState gameState = (GameState)theEObject;
				T result = caseGameState(gameState);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa21Package.MOVE_STACK: {
				MoveStack moveStack = (MoveStack)theEObject;
				T result = caseMoveStack(moveStack);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa21Package.MOVE_RESULT: {
				MoveResult moveResult = (MoveResult)theEObject;
				T result = caseMoveResult(moveResult);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa21Package.VALID: {
				Valid valid = (Valid)theEObject;
				T result = caseValid(valid);
				if (result == null) result = caseMoveResult(valid);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa21Package.ALIVE: {
				Alive alive = (Alive)theEObject;
				T result = caseAlive(alive);
				if (result == null) result = caseValid(alive);
				if (result == null) result = caseMoveResult(alive);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa21Package.DEAD: {
				Dead dead = (Dead)theEObject;
				T result = caseDead(dead);
				if (result == null) result = caseValid(dead);
				if (result == null) result = caseMoveResult(dead);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa21Package.INVALID: {
				Invalid invalid = (Invalid)theEObject;
				T result = caseInvalid(invalid);
				if (result == null) result = caseMoveResult(invalid);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa21Package.GAME_CONTROLLER: {
				GameController gameController = (GameController)theEObject;
				T result = caseGameController(gameController);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa21Package.GAME_BOARD_CONTROLLER: {
				GameBoardController gameBoardController = (GameBoardController)theEObject;
				T result = caseGameBoardController(gameBoardController);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa21Package.GAME_BOARD_VIEW: {
				GameBoardView gameBoardView = (GameBoardView)theEObject;
				T result = caseGameBoardView(gameBoardView);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa21Package.BOARD_ELEMENT: {
				BoardElement boardElement = (BoardElement)theEObject;
				T result = caseBoardElement(boardElement);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Position</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Position</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePosition(Position object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Position Offset</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Position Offset</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePositionOffset(PositionOffset object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Cell</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Cell</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCell(Cell object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Entity</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Entity</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEntity(Entity object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Player</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Player</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePlayer(Player object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Gem</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Gem</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseGem(Gem object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Mine</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Mine</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMine(Mine object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Extra Life</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Extra Life</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseExtraLife(ExtraLife object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Wall</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Wall</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseWall(Wall object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Stop Cell</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Stop Cell</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseStopCell(StopCell object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Entity Cell</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Entity Cell</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEntityCell(EntityCell object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Game Board</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Game Board</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseGameBoard(GameBoard object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Game State</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Game State</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseGameState(GameState object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Move Stack</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Move Stack</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMoveStack(MoveStack object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Move Result</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Move Result</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMoveResult(MoveResult object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Valid</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Valid</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseValid(Valid object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Alive</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Alive</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAlive(Alive object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Dead</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Dead</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDead(Dead object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Invalid</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Invalid</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseInvalid(Invalid object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Game Controller</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Game Controller</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseGameController(GameController object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Game Board Controller</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Game Board Controller</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseGameBoardController(GameBoardController object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Game Board View</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Game Board View</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseGameBoardView(GameBoardView object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Board Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Board Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBoardElement(BoardElement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T defaultCase(EObject object) {
		return null;
	}

} //Pa21Switch
