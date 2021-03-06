package jp.skypencil.pmd.slf4j;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTVariableInitializer;

public final class DontPassOtherClassToLoggerFactory extends AbstractSlf4jRule {
	// stack which contains class name
	private Deque<String> stack = new LinkedList<String>();

	private Map<String, String> classNameToFqcn = new HashMap<String, String>();

	private String packageName;

	@Override
	public Object visit(ASTImportDeclaration node, Object data) {
		String fqcn = node.getFirstChildOfType(ASTName.class).getImage();
		String className = fqcn.substring(fqcn.lastIndexOf('.'));
		classNameToFqcn.put(className, fqcn);
		return super.visit(node, data);
	}

	@Override
	public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
		packageName = node.getScope().getEnclosingSourceFileScope().getPackageName();
		String fqcn = packageName + "." + node.getImage();
		stack.push(fqcn);

		try {
			return super.visit(node, data);
		} finally {
			stack.pop();
		}
	}

	@Override
	public Object visit(ASTFieldDeclaration node, Object data) {
		ASTClassOrInterfaceType field = node.getFirstChildOfType(ASTClassOrInterfaceType.class);
		if (field != null && fieldIsLogger(field)) {
			ASTVariableInitializer initializer = node.getFirstChildOfType(ASTVariableInitializer.class);
			ASTClassOrInterfaceType givenClassToFactory = initializer.getFirstChildOfType(ASTClassOrInterfaceType.class);
			if (givenClassToFactory != null) {
				String givenClassName = givenClassToFactory.getImage();
				String fqcn;
				if (!givenClassName.contains(".")) {
					fqcn = packageName + "." + givenClassName;
				} else {
					fqcn = givenClassName;
				}

				if (!fqcn.equals(stack.peek())) {
					addViolation(data, node);
				}
			}
		}

		return super.visit(node, data);
	}
}
