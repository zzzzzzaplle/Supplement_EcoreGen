/**
 */
package edu.pa20;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Knight Block Rule</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see edu.pa20.Pa20Package#getKnightBlockRule()
 * @model kind="class"
 * @generated
 */
public class KnightBlockRule extends EObjectImpl implements Rule {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected KnightBlockRule() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Pa20Package.Literals.KNIGHT_BLOCK_RULE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	public boolean validate(Game game, Move move) {
		// TODO: implement this method
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 *        annotation="http://www.ustb.edu.cn/sei/mde/EnhancedCodeGen generated='manual'"
	 * @generated
	 */
	public String getDescription() {
		return "knight is blocked by another piece";
	}

} // KnightBlockRule
