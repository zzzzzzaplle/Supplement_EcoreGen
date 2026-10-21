/**
 */
package edu.pa20.util;

import edu.pa20.*;

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
 * @see edu.pa20.Pa20Package
 * @generated
 */
public class Pa20Switch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static Pa20Package modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Pa20Switch() {
		if (modelPackage == null) {
			modelPackage = Pa20Package.eINSTANCE;
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
			case Pa20Package.PLACE: {
				Place place = (Place)theEObject;
				T result = casePlace(place);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa20Package.MOVE: {
				Move move = (Move)theEObject;
				T result = caseMove(move);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa20Package.PLAYER: {
				Player player = (Player)theEObject;
				T result = casePlayer(player);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa20Package.PIECE: {
				Piece piece = (Piece)theEObject;
				T result = casePiece(piece);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa20Package.CONFIGURATION: {
				Configuration configuration = (Configuration)theEObject;
				T result = caseConfiguration(configuration);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa20Package.GAME: {
				Game game = (Game)theEObject;
				T result = caseGame(game);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa20Package.JESON_MOR: {
				JesonMor jesonMor = (JesonMor)theEObject;
				T result = caseJesonMor(jesonMor);
				if (result == null) result = caseGame(jesonMor);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa20Package.CONSOLE_PLAYER: {
				ConsolePlayer consolePlayer = (ConsolePlayer)theEObject;
				T result = caseConsolePlayer(consolePlayer);
				if (result == null) result = casePlayer(consolePlayer);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa20Package.RANDOM_PLAYER: {
				RandomPlayer randomPlayer = (RandomPlayer)theEObject;
				T result = caseRandomPlayer(randomPlayer);
				if (result == null) result = casePlayer(randomPlayer);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa20Package.KNIGHT: {
				Knight knight = (Knight)theEObject;
				T result = caseKnight(knight);
				if (result == null) result = casePiece(knight);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa20Package.ARCHER: {
				Archer archer = (Archer)theEObject;
				T result = caseArcher(archer);
				if (result == null) result = casePiece(archer);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa20Package.VACANT_RULE: {
				VacantRule vacantRule = (VacantRule)theEObject;
				T result = caseVacantRule(vacantRule);
				if (result == null) result = caseRule(vacantRule);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa20Package.OUT_OF_BOUNDARY_RULE: {
				OutOfBoundaryRule outOfBoundaryRule = (OutOfBoundaryRule)theEObject;
				T result = caseOutOfBoundaryRule(outOfBoundaryRule);
				if (result == null) result = caseRule(outOfBoundaryRule);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa20Package.OCCUPIED_RULE: {
				OccupiedRule occupiedRule = (OccupiedRule)theEObject;
				T result = caseOccupiedRule(occupiedRule);
				if (result == null) result = caseRule(occupiedRule);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa20Package.NIL_MOVE_RULE: {
				NilMoveRule nilMoveRule = (NilMoveRule)theEObject;
				T result = caseNilMoveRule(nilMoveRule);
				if (result == null) result = caseRule(nilMoveRule);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa20Package.KNIGHT_MOVE_RULE: {
				KnightMoveRule knightMoveRule = (KnightMoveRule)theEObject;
				T result = caseKnightMoveRule(knightMoveRule);
				if (result == null) result = caseRule(knightMoveRule);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa20Package.KNIGHT_BLOCK_RULE: {
				KnightBlockRule knightBlockRule = (KnightBlockRule)theEObject;
				T result = caseKnightBlockRule(knightBlockRule);
				if (result == null) result = caseRule(knightBlockRule);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa20Package.FIRST_NMOVES_PROTECTION_RULE: {
				FirstNMovesProtectionRule firstNMovesProtectionRule = (FirstNMovesProtectionRule)theEObject;
				T result = caseFirstNMovesProtectionRule(firstNMovesProtectionRule);
				if (result == null) result = caseRule(firstNMovesProtectionRule);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa20Package.ARCHER_MOVE_RULE: {
				ArcherMoveRule archerMoveRule = (ArcherMoveRule)theEObject;
				T result = caseArcherMoveRule(archerMoveRule);
				if (result == null) result = caseRule(archerMoveRule);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case Pa20Package.RULE: {
				Rule rule = (Rule)theEObject;
				T result = caseRule(rule);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Place</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Place</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePlace(Place object) {
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
	 * Returns the result of interpreting the object as an instance of '<em>Piece</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Piece</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePiece(Piece object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Configuration</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Configuration</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseConfiguration(Configuration object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Game</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Game</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseGame(Game object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Jeson Mor</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Jeson Mor</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseJesonMor(JesonMor object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Console Player</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Console Player</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseConsolePlayer(ConsolePlayer object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Random Player</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Random Player</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRandomPlayer(RandomPlayer object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Knight</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Knight</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseKnight(Knight object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Archer</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Archer</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseArcher(Archer object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Vacant Rule</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Vacant Rule</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseVacantRule(VacantRule object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Out Of Boundary Rule</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Out Of Boundary Rule</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseOutOfBoundaryRule(OutOfBoundaryRule object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Occupied Rule</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Occupied Rule</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseOccupiedRule(OccupiedRule object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Nil Move Rule</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Nil Move Rule</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNilMoveRule(NilMoveRule object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Knight Move Rule</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Knight Move Rule</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseKnightMoveRule(KnightMoveRule object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Knight Block Rule</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Knight Block Rule</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseKnightBlockRule(KnightBlockRule object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>First NMoves Protection Rule</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>First NMoves Protection Rule</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseFirstNMovesProtectionRule(FirstNMovesProtectionRule object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Archer Move Rule</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Archer Move Rule</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseArcherMoveRule(ArcherMoveRule object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Rule</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Rule</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRule(Rule object) {
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

} //Pa20Switch
