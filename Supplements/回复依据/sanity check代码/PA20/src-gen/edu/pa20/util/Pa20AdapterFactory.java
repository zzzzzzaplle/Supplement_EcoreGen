/**
 */
package edu.pa20.util;

import edu.pa20.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see edu.pa20.Pa20Package
 * @generated
 */
public class Pa20AdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static Pa20Package modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Pa20AdapterFactory() {
		if (modelPackage == null) {
			modelPackage = Pa20Package.eINSTANCE;
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
	protected Pa20Switch<Adapter> modelSwitch =
		new Pa20Switch<Adapter>() {
			@Override
			public Adapter casePlace(Place object) {
				return createPlaceAdapter();
			}
			@Override
			public Adapter caseMove(Move object) {
				return createMoveAdapter();
			}
			@Override
			public Adapter casePlayer(Player object) {
				return createPlayerAdapter();
			}
			@Override
			public Adapter casePiece(Piece object) {
				return createPieceAdapter();
			}
			@Override
			public Adapter caseConfiguration(Configuration object) {
				return createConfigurationAdapter();
			}
			@Override
			public Adapter caseGame(Game object) {
				return createGameAdapter();
			}
			@Override
			public Adapter caseJesonMor(JesonMor object) {
				return createJesonMorAdapter();
			}
			@Override
			public Adapter caseConsolePlayer(ConsolePlayer object) {
				return createConsolePlayerAdapter();
			}
			@Override
			public Adapter caseRandomPlayer(RandomPlayer object) {
				return createRandomPlayerAdapter();
			}
			@Override
			public Adapter caseKnight(Knight object) {
				return createKnightAdapter();
			}
			@Override
			public Adapter caseArcher(Archer object) {
				return createArcherAdapter();
			}
			@Override
			public Adapter caseVacantRule(VacantRule object) {
				return createVacantRuleAdapter();
			}
			@Override
			public Adapter caseOutOfBoundaryRule(OutOfBoundaryRule object) {
				return createOutOfBoundaryRuleAdapter();
			}
			@Override
			public Adapter caseOccupiedRule(OccupiedRule object) {
				return createOccupiedRuleAdapter();
			}
			@Override
			public Adapter caseNilMoveRule(NilMoveRule object) {
				return createNilMoveRuleAdapter();
			}
			@Override
			public Adapter caseKnightMoveRule(KnightMoveRule object) {
				return createKnightMoveRuleAdapter();
			}
			@Override
			public Adapter caseKnightBlockRule(KnightBlockRule object) {
				return createKnightBlockRuleAdapter();
			}
			@Override
			public Adapter caseFirstNMovesProtectionRule(FirstNMovesProtectionRule object) {
				return createFirstNMovesProtectionRuleAdapter();
			}
			@Override
			public Adapter caseArcherMoveRule(ArcherMoveRule object) {
				return createArcherMoveRuleAdapter();
			}
			@Override
			public Adapter caseRule(Rule object) {
				return createRuleAdapter();
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
	 * Creates a new adapter for an object of class '{@link edu.pa20.Place <em>Place</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa20.Place
	 * @generated
	 */
	public Adapter createPlaceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa20.Move <em>Move</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa20.Move
	 * @generated
	 */
	public Adapter createMoveAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa20.Player <em>Player</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa20.Player
	 * @generated
	 */
	public Adapter createPlayerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa20.Piece <em>Piece</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa20.Piece
	 * @generated
	 */
	public Adapter createPieceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa20.Configuration <em>Configuration</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa20.Configuration
	 * @generated
	 */
	public Adapter createConfigurationAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa20.Game <em>Game</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa20.Game
	 * @generated
	 */
	public Adapter createGameAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa20.JesonMor <em>Jeson Mor</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa20.JesonMor
	 * @generated
	 */
	public Adapter createJesonMorAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa20.ConsolePlayer <em>Console Player</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa20.ConsolePlayer
	 * @generated
	 */
	public Adapter createConsolePlayerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa20.RandomPlayer <em>Random Player</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa20.RandomPlayer
	 * @generated
	 */
	public Adapter createRandomPlayerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa20.Knight <em>Knight</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa20.Knight
	 * @generated
	 */
	public Adapter createKnightAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa20.Archer <em>Archer</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa20.Archer
	 * @generated
	 */
	public Adapter createArcherAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa20.VacantRule <em>Vacant Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa20.VacantRule
	 * @generated
	 */
	public Adapter createVacantRuleAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa20.OutOfBoundaryRule <em>Out Of Boundary Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa20.OutOfBoundaryRule
	 * @generated
	 */
	public Adapter createOutOfBoundaryRuleAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa20.OccupiedRule <em>Occupied Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa20.OccupiedRule
	 * @generated
	 */
	public Adapter createOccupiedRuleAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa20.NilMoveRule <em>Nil Move Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa20.NilMoveRule
	 * @generated
	 */
	public Adapter createNilMoveRuleAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa20.KnightMoveRule <em>Knight Move Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa20.KnightMoveRule
	 * @generated
	 */
	public Adapter createKnightMoveRuleAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa20.KnightBlockRule <em>Knight Block Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa20.KnightBlockRule
	 * @generated
	 */
	public Adapter createKnightBlockRuleAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa20.FirstNMovesProtectionRule <em>First NMoves Protection Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa20.FirstNMovesProtectionRule
	 * @generated
	 */
	public Adapter createFirstNMovesProtectionRuleAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa20.ArcherMoveRule <em>Archer Move Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa20.ArcherMoveRule
	 * @generated
	 */
	public Adapter createArcherMoveRuleAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link edu.pa20.Rule <em>Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see edu.pa20.Rule
	 * @generated
	 */
	public Adapter createRuleAdapter() {
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

} //Pa20AdapterFactory
