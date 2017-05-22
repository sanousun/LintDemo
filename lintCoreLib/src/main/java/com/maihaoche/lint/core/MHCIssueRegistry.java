package com.maihaoche.lint.core;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.Issue;
import com.maihaoche.lint.core.detectors.CaseLiteralIllegalDetector;
import com.maihaoche.lint.core.detectors.LogUtilDetector;
import com.maihaoche.lint.core.detectors.NamingConventionsDetector;
import com.maihaoche.lint.core.detectors.NestRecyclerViewDetector;
import com.maihaoche.lint.core.detectors.RequestCodeForV4FragmentDetector;
import com.maihaoche.lint.core.detectors.SubscriptionDetector;
import com.maihaoche.lint.core.detectors.VectorDrawableIllegalDetector;

import java.util.Arrays;
import java.util.List;

/**
 * Created with Android Studio.
 * User: dashu
 * Date: 2017/5/5
 * Time: 下午5:08
 * Desc: 收集自定义的Lint规则
 */

public class MHCIssueRegistry extends IssueRegistry {
    @Override
    public List<Issue> getIssues() {
        return Arrays.asList(
                LogUtilDetector.ISSUE,
                NestRecyclerViewDetector.ISSUE,
                RequestCodeForV4FragmentDetector.ISSUE,
                VectorDrawableIllegalDetector.ISSUE,
                SubscriptionDetector.ISSUE,
                NamingConventionsDetector.ISSUE,
                CaseLiteralIllegalDetector.ISSUE);
    }
}
