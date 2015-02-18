/**
 * 
 */
package org.lambdamatic;

import org.lambdamatic.analyzer.ast.node.Expression;

/**
 * The AST form of the user-defined Lambda Expression and its relevant data in a form that can be further manipulated.
 * 
 * @author xcoulon
 *
 */
public class LambdaExpression {

	/** The AST form of the user-defined Lambda Expression AST with captured arguments. */
	private final Expression expression;
	
	/** The type of the element being evaluated in the AST form of the user-defined Lambda Expression. */
	private final Class<?> argumentType;
	
	/**
	 * Constructor
	 * @param expression The AST form of the user-defined Lambda Expression with its captured arguments.
	 * @param argumentType The type of the element being evaluated in the AST form of the user-defined Lambda Expression.
	 */
	public LambdaExpression(final Expression expression, final Class<?> argumentType) {
		this.expression = expression;
		this.argumentType = argumentType;
	}

	/**
	 * @return The AST form of the user-defined Lambda Expression AST with captured arguments.
	 */
	public Expression getExpression() {
		return expression;
	}

	/**
	 * @return The type of the element being evaluated in the AST form of the user-defined Lambda Expression.
	 */
	public Class<?> getArgumentType() {
		return argumentType;
	}

	@Override
	public String toString() {
		return this.expression.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((argumentType == null) ? 0 : argumentType.hashCode());
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final LambdaExpression other = (LambdaExpression) obj;
		if (argumentType == null) {
			if (other.argumentType != null)
				return false;
		} else if (!argumentType.getName().equals(other.argumentType.getName()))
			return false;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		return true;
	}
	
	

}
