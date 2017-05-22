package com.maihaoche.lint.core.detectors;

import com.android.tools.lint.client.api.JavaEvaluator;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.ConstantEvaluator;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;

import java.util.Collections;
import java.util.List;

/**
 * Created with Android Studio.
 * User: dashu
 * Date: 2017/5/13
 * Time: 下午8:30
 * Desc:
 */

public class RequestCodeForV4FragmentDetector extends Detector implements Detector.JavaPsiScanner {

    public static final Issue ISSUE = Issue.create(
            "V4FragmentRequestCode",
            "当fragment为supportV4时，调用startActivityForResult的requestCode超16位会crash",
            "requestCode需要小于0xffff",
            Category.SECURITY, 8, Severity.FATAL,
            new Implementation(RequestCodeForV4FragmentDetector.class, Scope.JAVA_FILE_SCOPE));

    @Override
    public List<String> getApplicableMethodNames() {
        return Collections.singletonList("startActivityForResult");
    }

    @Override
    public void visitMethod(JavaContext context, JavaElementVisitor visitor, PsiMethodCallExpression call, PsiMethod method) {
        JavaEvaluator javaEvaluator = context.getEvaluator();
        if (javaEvaluator.isMemberInClass(method, "android.support.v4.app.Fragment")) {
            //获取参数值
            PsiExpression argument = call.getArgumentList().getExpressions()[1];
            int requestCode = getIntValue(context, argument);
            if (requestCode > 0xff){
                String message = "REQUEST_CODE需要不大于16位即0xffff";
                context.report(ISSUE, call, context.getLocation(call), message);
            }
        }
    }

    private int getIntValue(JavaContext context, PsiExpression argument) {
        Object value = ConstantEvaluator.evaluate(context, argument);
        return value instanceof Number ? ((Number) value).intValue() : 1000000;
    }
}
