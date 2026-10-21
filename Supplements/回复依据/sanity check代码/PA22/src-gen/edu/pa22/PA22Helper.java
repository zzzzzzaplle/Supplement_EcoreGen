/**
 */
package edu.pa22;

import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>PA22 Helper</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see edu.pa22.Pa22Package#getPA22Helper()
 * @model kind="class"
 * @generated
 */
public class PA22Helper extends EObjectImpl implements EObject {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PA22Helper() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Pa22Package.Literals.PA22_HELPER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model annotation="http://www.ustb.edu.cn/sei/mde/EnhancedCodeGen generated='manual'"
	 * @generated
	 */
	public GameMap parseGameMap(final String mapText) {
		// TODO: implement this method
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model destinationsMany="true"
	 *        annotation="http://www.ustb.edu.cn/sei/mde/EnhancedCodeGen generated='manual'"
	 * @generated
	 */
	public GameMap createGameMap1(final int maxWidth, final int maxHeight, final EList<Position> destinations, final int undoLimit) {
		// TODO: implement this method
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model destinationsMany="true" mapDataType="edu.pa22.EntityMap&lt;edu.pa22.Position, edu.pa22.Entity&gt;"
	 *        annotation="http://www.ustb.edu.cn/sei/mde/EnhancedCodeGen generated='manual'"
	 * @generated
	 */
	public GameMap createGameMap2(final EList<Position> destinations, final int undoLimit, final Map<Position, Entity> map) {
		// TODO: implement this method
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

} // PA22Helper
