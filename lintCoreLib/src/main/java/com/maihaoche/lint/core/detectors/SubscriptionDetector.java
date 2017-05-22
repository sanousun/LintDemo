package com.maihaoche.lint.core.detectors;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.LintUtils;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiType;

import java.util.Arrays;
import java.util.List;

/**
 * Created with Android Studio.
 * User: dashu
 * Date: 2017/5/14
 * Time: 下午3:35
 * Desc:
 */

public class SubscriptionDetector extends Detector implements Detector.JavaPsiScanner {

    public static final Issue ISSUE = Issue
            .create("RxLeakedSubscription",
                    "Subscription需要考虑到取消订阅的情况",
                    "Subscription需要被引用加入到一个CompositeSubscription中，然后在生命周期结束后取消订阅，防止内存泄露",
                    Category.CORRECTNESS, 7, Severity.ERROR,
                    new Implementation(SubscriptionDetector.class, Scope.JAVA_FILE_SCOPE));

    private static final List<String> OBSERVABLE_TYPES = Arrays.asList(
            "rx.Observable", "rx.Single", "rx.Completable",
            "io.reactivex.Observable", "io.reactivex.Flowable",
            "io.reactivex.Single", "io.reactivex.Maybe", "io.reactivex.Completable");

    @Override
    public List<String> getApplicableMethodNames() {
        return Arrays.asList("subscribe", "subscribeWith");
    }

    @Override
    public void visitMethod(JavaContext context, JavaElementVisitor visitor, PsiMethodCallExpression call, PsiMethod method) {
        super.visitMethod(context, visitor, call, method);
        if (isRxSubscribeableClass(method.getContainingClass()) && method.getReturnType() != PsiType.VOID) {
            PsiElement element = LintUtils.skipParentheses(call.getParent());
            if (element instanceof PsiExpressionStatement) {
                String message;
                if (isRx2(method.getContainingClass())) {
                    message = "disposable没有被引用，缺少取消订阅";
                } else {
                    message = "subscription没有被引用，缺少取消订阅";
                }
                context.report(ISSUE, call, context.getLocation(call), message);
            }
        }
    }

    private boolean isRx2(PsiClass clz) {
        return clz.getQualifiedName().startsWith("io.reactivex.");
    }

    private boolean isRxSubscribeableClass(PsiClass clz) {
        return OBSERVABLE_TYPES.contains(clz.getQualifiedName());
    }
}
