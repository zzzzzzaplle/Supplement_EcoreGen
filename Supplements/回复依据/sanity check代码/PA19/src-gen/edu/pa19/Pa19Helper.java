/**
 */
package edu.pa19;

import java.util.List;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Helper</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see edu.pa19.Pa19Package#getPa19Helper()
 * @model kind="class"
 * @generated
 */
public class Pa19Helper extends EObjectImpl implements EObject {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Pa19Helper() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Pa19Package.Literals.PA19_HELPER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model cellsDataType="edu.pa19.cellarray" pipesMany="true"
	 *        annotation="http://www.ustb.edu.cn/sei/mde/EnhancedCodeGen generated='manual'"
	 * @generated
	 */
	public Game createGame(final int rows, final int cols, final int delay, final Cell[][] cells, final EList<Pipe> pipes) {
		// TODO: implement this method
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model pipesMany="true"
	 *        annotation="http://www.ustb.edu.cn/sei/mde/EnhancedCodeGen generated='manual'"
	 * @generated
	 */
	public Game gameFromString(final int rows, final int cols, final int delay, final String cellsRep, final EList<Pipe> pipes) {
		// TODO: implement this method
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model cellsDataType="edu.pa19.cellarray"
	 *        annotation="http://www.ustb.edu.cn/sei/mde/EnhancedCodeGen generated='manual'"
	 * @generated
	 */
	public Map createMap(final int rows, final int cols, final Cell[][] cells) {
		// TODO: implement this method
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model annotation="http://www.ustb.edu.cn/sei/mde/EnhancedCodeGen generated='manual'"
	 * @generated
	 */
	public Map mapFromString(final int rows, final int cols, final String cellsRep) {
		// TODO: implement this method
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model annotation="http://www.ustb.edu.cn/sei/mde/EnhancedCodeGen generated='manual'"
	 * @generated
	 */
	public Cell cellFromChar(final char c, final Coordinate coord, final TerminationType terminationType) {
		// TODO: implement this method
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model annotation="http://www.ustb.edu.cn/sei/mde/EnhancedCodeGen generated='manual'"
	 * @generated
	 */
	public Wall createWall(final Coordinate coord) {
		// TODO: implement this method
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model annotation="http://www.ustb.edu.cn/sei/mde/EnhancedCodeGen generated='manual'"
	 * @generated
	 */
	public FillableCell createFillableCell(final Coordinate coord, final Pipe pipes) {
		// TODO: implement this method
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model annotation="http://www.ustb.edu.cn/sei/mde/EnhancedCodeGen generated='manual'"
	 * @generated
	 */
	public TerminationCell createTerminationCell(final Coordinate coord, final TerminationType type, final Direction dir) {
		// TODO: implement this method
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model annotation="http://www.ustb.edu.cn/sei/mde/EnhancedCodeGen generated='manual'"
	 * @generated
	 */
	public Pipe pipeFromString(final String rep) {
		// TODO: implement this method
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model annotation="http://www.ustb.edu.cn/sei/mde/EnhancedCodeGen generated='manual'"
	 * @generated
	 */
	public Direction getOpposite(final Direction dir) {
		// TODO: implement this method
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model annotation="http://www.ustb.edu.cn/sei/mde/EnhancedCodeGen generated='manual'"
	 * @generated
	 */
	public Coordinate getOffset(final Direction dir) {
		// TODO: implement this method
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

} // Pa19Helper
