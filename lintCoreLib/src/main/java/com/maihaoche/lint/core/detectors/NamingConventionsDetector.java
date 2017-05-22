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
import com.intellij.psi.PsiField;

import java.util.Collections;
import java.util.List;

/**
 * Created with Android Studio.
 * User: dashu
 * Date: 2017/5/15
 * Time: 下午7:38
 * Desc: 命名规范的检测器
 */

public class NamingConventionsDetector extends Detector implements Detector.JavaPsiScanner {

    public static final Issue ISSUE = Issue.create(
            "NamingConventionsIllegal",
            "变量命名不正确",
            "请遵守开发规范",
            Category.SECURITY, 5, Severity.WARNING,
            new Implementation(NamingConventionsDetector.class, Scope.JAVA_FILE_SCOPE));


    @Override
    public List<Class<? extends PsiElement>> getApplicablePsiTypes() {
        return Collections.singletonList(PsiField.class);
    }

    public JavaElementVisitor createPsiVisitor(JavaContext context) {
        return new NamingConventionsDetector.FieldChecker(context);
    }

    private static class FieldChecker extends JavaElementVisitor {
        private final JavaContext mContext;

        public FieldChecker(JavaContext context) {
            this.mContext = context;
        }

        @Override
        public void visitField(PsiField field) {
            field.getNameIdentifier();
            // TODO: 2017/5/16 做一个格式校验
        }
    }
}
