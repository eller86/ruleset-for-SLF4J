package jp.skypencil.pmd.slf4j;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;

public final class DontPublishLogger extends AbstractSlf4jRule {
	@Override
	public Object visit(ASTFieldDeclaration node, Object data) {
		ASTClassOrInterfaceType field = node.getFirstChildOfType(ASTClassOrInterfaceType.class);
		if (field != null && !node.isPrivate() && fieldIsLogger(field)) {
			addViolation(data, node);
		}

		return super.visit(node, data);
	}
}
