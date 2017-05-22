package com.maihaoche.lint.core.detectors;

import com.android.resources.ResourceFolderType;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.ResourceXmlDetector;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.XmlContext;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import java.util.Collection;
import java.util.Collections;

/**
 * Created with Android Studio.
 * User: dashu
 * Date: 2017/5/13
 * Time: 上午10:15
 * Desc: 如果vector中pathData包含"e-"形式的科学计数法会导致5.x系列手机崩溃
 */

public class VectorDrawableIllegalDetector extends ResourceXmlDetector {

    public static final Issue ISSUE = Issue.create("VectorDrawableAvoidScientificNotation",
            "VectorDrawable的pathData避免使用科学计数法e-形式",
            "可以使用小数的形式，或者小于0.01并且无精度要求直接为0",
            Category.SECURITY, 8, Severity.FATAL,
            new Implementation(VectorDrawableIllegalDetector.class, Scope.RESOURCE_FILE_SCOPE));

    @Override
    public boolean appliesTo(ResourceFolderType folderType) {
        return folderType == ResourceFolderType.DRAWABLE;
    }

    @Override
    public Collection<String> getApplicableElements() {
        return Collections.singletonList("path");
    }

    @Override
    public void visitElement(XmlContext context, Element element) {
        Attr node = element.getAttributeNodeNS("http://schemas.android.com/apk/res/android", "pathData");
        if (node.getValue().contains("e-")) {
            String message = "VectorDrawable的pathData避免使用科学计数法e-形式";
            context.report(ISSUE, node, context.getLocation(node), message);
        }
    }
}
