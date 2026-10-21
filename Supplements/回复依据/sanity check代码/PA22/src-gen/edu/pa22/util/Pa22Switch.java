/**
 */
package edu.pa22.util;

import edu.pa22.*;

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
 * @see edu.pa22.Pa22Package
 * @generated
 */
public class Pa22Switch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static Pa22Package modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Pa22Switch() {
		if (modelPackage == null) {
			modelPackage = Pa22Package.eINSTANCE;
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
			case Pa22Package.ACTION: {
				Action action = (Action)theEObject;
				T result = caseAction(action);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.ACTION_RESULT: {
				ActionResult actionResult = (ActionResult)theEObject;
				T result = caseActionResult(actionResult);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.SUCCESS: {
				Success success = (Success)theEObject;
				T result = caseSuccess(success);
				if (result == null) result = caseActionResult(success);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.FAILED: {
				Failed failed = (Failed)theEObject;
				T result = caseFailed(failed);
				if (result == null) result = caseActionResult(failed);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.EXIT: {
				Exit exit = (Exit)theEObject;
				T result = caseExit(exit);
				if (result == null) result = caseAction(exit);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.INVALID_INPUT: {
				InvalidInput invalidInput = (InvalidInput)theEObject;
				T result = caseInvalidInput(invalidInput);
				if (result == null) result = caseAction(invalidInput);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.UNDO: {
				Undo undo = (Undo)theEObject;
				T result = caseUndo(undo);
				if (result == null) result = caseAction(undo);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.ENTITY: {
				Entity entity = (Entity)theEObject;
				T result = caseEntity(entity);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.BOX: {
				Box box = (Box)theEObject;
				T result = caseBox(box);
				if (result == null) result = caseEntity(box);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.EMPTY: {
				Empty empty = (Empty)theEObject;
				T result = caseEmpty(empty);
				if (result == null) result = caseEntity(empty);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.PLAYER: {
				Player player = (Player)theEObject;
				T result = casePlayer(player);
				if (result == null) result = caseEntity(player);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.WALL: {
				Wall wall = (Wall)theEObject;
				T result = caseWall(wall);
				if (result == null) result = caseEntity(wall);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.ABSTRACT_SOKOBAN_GAME: {
				AbstractSokobanGame abstractSokobanGame = (AbstractSokobanGame)theEObject;
				T result = caseAbstractSokobanGame(abstractSokobanGame);
				if (result == null) result = caseSokobanGame(abstractSokobanGame);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.POSITION: {
				Position position = (Position)theEObject;
				T result = casePosition(position);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.GAME_MAP: {
				GameMap gameMap = (GameMap)theEObject;
				T result = caseGameMap(gameMap);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.GAME_STATE: {
				GameState gameState = (GameState)theEObject;
				T result = caseGameState(gameState);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.GAME_STATE_TRANSITION: {
				GameStateTransition gameStateTransition = (GameStateTransition)theEObject;
				T result = caseGameStateTransition(gameStateTransition);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.RENDERING_ENGINE: {
				RenderingEngine renderingEngine = (RenderingEngine)theEObject;
				T result = caseRenderingEngine(renderingEngine);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.TERMINAL_SOKOBAN_GAME: {
				TerminalSokobanGame terminalSokobanGame = (TerminalSokobanGame)theEObject;
				T result = caseTerminalSokobanGame(terminalSokobanGame);
				if (result == null) result = caseAbstractSokobanGame(terminalSokobanGame);
				if (result == null) result = caseSokobanGame(terminalSokobanGame);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.TERMINAL_INPUT_ENGINE: {
				TerminalInputEngine terminalInputEngine = (TerminalInputEngine)theEObject;
				T result = caseTerminalInputEngine(terminalInputEngine);
				if (result == null) result = caseInputEngine(terminalInputEngine);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.TERMINAL_RENDERING_ENGINE: {
				TerminalRenderingEngine terminalRenderingEngine = (TerminalRenderingEngine)theEObject;
				T result = caseTerminalRenderingEngine(terminalRenderingEngine);
				if (result == null) result = caseRenderingEngine(terminalRenderingEngine);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.MOVE: {
				Move move = (Move)theEObject;
				T result = caseMove(move);
				if (result == null) result = caseAction(move);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.UP: {
				Up up = (Up)theEObject;
				T result = caseUp(up);
				if (result == null) result = caseMove(up);
				if (result == null) result = caseAction(up);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.DOWN: {
				Down down = (Down)theEObject;
				T result = caseDown(down);
				if (result == null) result = caseMove(down);
				if (result == null) result = caseAction(down);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.LEFT: {
				Left left = (Left)theEObject;
				T result = caseLeft(left);
				if (result == null) result = caseMove(left);
				if (result == null) result = caseAction(left);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.RIGHT: {
				Right right = (Right)theEObject;
				T result = caseRight(right);
				if (result == null) result = caseMove(right);
				if (result == null) result = caseAction(right);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.PA22_HELPER: {
				PA22Helper pa22Helper = (PA22Helper)theEObject;
				T result = casePA22Helper(pa22Helper);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.INPUT_ENGINE: {
				InputEngine inputEngine = (InputEngine)theEObject;
				T result = caseInputEngine(inputEngine);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa22Package.SOKOBAN_GAME: {
				SokobanGame sokobanGame = (SokobanGame)theEObject;
				T result = caseSokobanGame(sokobanGame);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Action</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Action</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAction(Action object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Action Result</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Action Result</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseActionResult(ActionResult object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Success</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Success</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSuccess(Success object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Failed</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Failed</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseFailed(Failed object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Exit</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Exit</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseExit(Exit object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Invalid Input</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Invalid Input</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseInvalidInput(InvalidInput object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Undo</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Undo</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseUndo(Undo object) {
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
	 * Returns the result of interpreting the object as an instance of '<em>Box</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Box</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBox(Box object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Empty</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Empty</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEmpty(Empty object) {
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
	 * Returns the result of interpreting the object as an instance of '<em>Abstract Sokoban Game</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Abstract Sokoban Game</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAbstractSokobanGame(AbstractSokobanGame object) {
		return null;
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
	 * Returns the result of interpreting the object as an instance of '<em>Game Map</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Game Map</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseGameMap(GameMap object) {
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
	 * Returns the result of interpreting the object as an instance of '<em>Game State Transition</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Game State Transition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseGameStateTransition(GameStateTransition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Rendering Engine</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Rendering Engine</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRenderingEngine(RenderingEngine object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Terminal Sokoban Game</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Terminal Sokoban Game</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTerminalSokobanGame(TerminalSokobanGame object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Terminal Input Engine</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Terminal Input Engine</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTerminalInputEngine(TerminalInputEngine object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Terminal Rendering Engine</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Terminal Rendering Engine</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTerminalRenderingEngine(TerminalRenderingEngine object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Move</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Move</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMove(Move object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Up</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Up</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseUp(Up object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Down</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Down</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDown(Down object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Left</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Left</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseLeft(Left object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Right</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Right</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRight(Right object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>PA22 Helper</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>PA22 Helper</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePA22Helper(PA22Helper object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Input Engine</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Input Engine</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseInputEngine(InputEngine object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Sokoban Game</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Sokoban Game</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSokobanGame(SokobanGame object) {
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

} //Pa22Switch
