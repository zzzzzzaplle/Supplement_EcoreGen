/**
 */
package edu.pa20;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Knight</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see edu.pa20.Pa20Package#getKnight()
 * @model kind="class"
 * @generated
 */
public class Knight extends Piece {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Knight() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Pa20Package.Literals.KNIGHT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 *        annotation="http://www.ustb.edu.cn/sei/mde/EnhancedCodeGen generated='manual'"
	 * @generated
	 */
	public char getLabel() {
		return 'K';
	}

} // Knight
