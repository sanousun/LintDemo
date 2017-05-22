package com.maihaoche.lint.core.detectors;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.LayoutDetector;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.XmlContext;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Collection;
import java.util.Collections;

/**
 * Created with Android Studio.
 * User: dashu
 * Date: 2017/5/13
 * Time: 上午10:29
 * Desc: 避免在ScrollView中使用自适应高度的RecyclerView
 */

public class NestRecyclerViewDetector extends LayoutDetector {

    public static final Issue ISSUE = Issue.create(
            "NestRecyclerView",
            "避免在ScrollView中使用自适应高度的RecyclerView",
            "使用NestScrollView可以避免自使用RecyclerView初始化未加载item无法显示的bug",
            Category.CORRECTNESS, 8, Severity.FATAL,
            new Implementation(NestRecyclerViewDetector.class, Scope.RESOURCE_FILE_SCOPE));

    @Override
    public Collection<String> getApplicableElements() {
        return Collections.singletonList("android.support.v7.widget.RecyclerView");
    }

    @Override
    public void visitElement(XmlContext context, Element element) {
        String width = element.getAttributeNS("http://schemas.android.com/apk/res/android", "layout_height");
        if ("wrap_content".equals(width)) {
            Element parent = this.findOuterScrollingWidget(element.getParentNode());
            if (parent != null) {
                String msg = "使用NestScrollView代替ScrollView";
                context.report(ISSUE, parent, context.getLocation(parent), msg);
            }
        }
    }

    private Element findOuterScrollingWidget(Node node) {
        for (String applicableElements = "ScrollView"; node != null; node = node.getParentNode()) {
            if (node instanceof Element) {
                Element element = (Element) node;
                String tagName = element.getTagName();
                if (applicableElements.equals(tagName)) {
                    return element;
                }
            }
        }
        return null;
    }
}
