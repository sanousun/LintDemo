package com.maihaoche.lint.core.detectors;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiLiteral;
import com.intellij.psi.PsiSwitchLabelStatement;

import java.util.Collections;
import java.util.List;

/**
 * Created with Android Studio.
 * User: dashu
 * Date: 2017/5/16
 * Time: 下午3:51
 * Desc:
 */

public class CaseLiteralIllegalDetector extends Detector implements Detector.JavaPsiScanner {

    public static final Issue ISSUE = Issue.create(
            "CaseLiteralIllegal",
            "避免在switch-case中使用硬编码",
            "使用static final变量代替",
            Category.SECURITY, 6, Severity.WARNING,
            new Implementation(CaseLiteralIllegalDetector.class, Scope.JAVA_FILE_SCOPE));

    @Override
    public List<Class<? extends PsiElement>> getApplicablePsiTypes() {
        return Collections.singletonList(PsiSwitchLabelStatement.class);
    }

    @Override
    public JavaElementVisitor createPsiVisitor(JavaContext context) {
        return new SwitchLabelChecker(context);
    }

    private class SwitchLabelChecker extends JavaElementVisitor {
        private JavaContext mContext;

        SwitchLabelChecker(JavaContext context) {
            mContext = context;
        }

        @Override
        public void visitSwitchLabelStatement(PsiSwitchLabelStatement statement) {
            PsiExpression caseValue = statement.getCaseValue();
            if (caseValue != null && caseValue instanceof PsiLiteral) {
                mContext.report(ISSUE, statement, mContext.getLocation(statement), "避免使用硬编码");
            }
        }
    }
}
