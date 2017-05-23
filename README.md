# Android Lint
Android Lint 是有 Android SDK 提供的一种静态代码检测工具，用于检测 Android 的代码质量

Android Lint 的源码集成在 Android SDK Tools 16 及更高的版本中，我们可以在项目目录下通过 `./gradlew lint` 命令调用，也可以通过 Android Studio 的 `【Analyze】->【Inspect Code】`路径调用 Lint 检查

## 配置 Lint 规则

在 Android Studio 创建的 Android 项目中运行 `./gradlew lint` 可以获得 Lint 检测结果，生成详细的 xml 或者 html 报告文件，同时通过对 lint.xml、lintOptions 的配置可以实现符合自己项目的 Lint 检测规则

**lintOptions** 定义在 gradle 文件中，下面列举 lintOptions 可定义的选项

```gradle
android {
    lintOptions {
        // true--关闭lint报告的分析进度
        quiet true
        // true--错误发生后停止gradle构建
        abortOnError false
        // true--只报告error
        ignoreWarnings true
        // true--忽略有错误的文件的全/绝对路径(默认是true)
        //absolutePaths true
        // true--检查所有问题点，包含其他默认关闭项
        checkAllWarnings true
        // true--所有warning当做error
        warningsAsErrors true
        // 关闭指定问题检查
        disable 'TypographyFractions', 'TypographyQuotes'
        // 打开指定问题检查
        enable 'RtlHardcoded', 'RtlCompat', 'RtlEnabled'
        // 仅检查指定问题
        check 'NewApi', 'InlinedApi'
        // true--error输出文件不包含源码行号
        noLines true
        // true--显示错误的所有发生位置，不截取
        showAll true
        // 回退lint设置(默认规则)
        lintConfig file("default-lint.xml")
        // true--生成txt格式报告(默认false)
        textReport true
        // 重定向输出；可以是文件或'stdout'
        textOutput 'stdout'
        // true--生成XML格式报告
        xmlReport false
        // 指定xml报告文档(默认lint-results.xml)
        xmlOutput file("lint-report.xml")
        // true--生成HTML报告(带问题解释，源码位置，等)
        htmlReport true
        // html报告可选路径(构建器默认是lint-results.html )
        htmlOutput file("lint-report.html")
        //  true--所有正式版构建执行规则生成崩溃的lint检查，如果有崩溃问题将停止构建
        checkReleaseBuilds true
        // 在发布版本编译时检查(即使不包含lint目标)，指定问题的规则生成崩溃
        fatal 'NewApi', 'InlineApi'
        // 指定问题的规则生成错误
        error 'Wakelock', 'TextViewEdits'
        // 指定问题的规则生成警告
        warning 'ResourceAsColor'
        // 忽略指定问题的规则(同关闭检查)
        ignore 'TypographyQuotes'
    }
}
```

**lint.xml** 这个配置文件是用来指定你想禁用哪些lint检查功能，以及自定义问题严重度 (problem severity levels)，我们可以通过 lintOptions 中的 lintConfig file("lint.xml") 来指定配置文件的所在目录

lint.xml文件的组成结构是，最外面是一对闭合的标签，里面包含一个或多个子元素。每一个被唯一的id属性来标识，整体结构如下：

```
<?xml version="1.0" encoding="UTF-8"?>
<lint>
    <!-- list of issues to configure -->
    <!-- Disable the given check in this project -->
    <issue id="IconMissingDensityFolder" severity="ignore" />

    <!-- Ignore the ObsoleteLayoutParam issue in the specified files -->
    <issue id="ObsoleteLayoutParam">
        <ignore path="res/layout/activation.xml" />
        <ignore path="res/layout-xlarge/activation.xml" />
    </issue>

    <!-- Ignore the UselessLeaf issue in the specified file -->
    <issue id="UselessLeaf">
        <ignore path="res/layout/main.xml" />
    </issue>

    <!-- Change the severity of hardcoded strings to "error" -->
    <issue id="HardcodedText" severity="error" />
</lint>
```
id 的获取我们可以通过命令行 `lint --list` 获取。如果无法直接执行 lint 命令，我们可以在 `/.bash_profile` 中添加 `PATH="~/Library/Android/sdk/tools/bin:${PATH}"` 即可

## Android Studio 的 Lint 支持
Android Studio 2.0 以后，谷歌将 Lint 检查整合到了 IDE 之中，提供了方便的图形界面操作，检测结果也会在底部 Inspection Results 中展现，除了 Lint 规则，AS 也加入了一些其他的检测规则

## 自定义 Lint 规则
![自定义Lint流程图](http://oq546o6pk.bkt.clouddn.com/2671940175-57aec2cc55eb3_articlex.png)

在某些特殊情况下，系统定义的 Lint 并不能满足我们的需求，这时需要我们自己定义规则，然后利用 Android 的 Lint 工具帮助我们自动发现某些问题

谷歌官方的方案是依赖 lint-api 创建自己的 lint 规则，然后将自定义的 lint 规则打包成 jar (保存在 build/libs 中)，将 jar 包复制到 `~/.android/lint` 目录下，最后在 Android 工程源码目录下执行 `./gradlew lint` 即可。但是这种方案有一个缺点：它针对的是本机的所有项目，也就是会影响同一台机器其他项目的 Lint 检查

所以我们可以采用 LinkedIn 提出的 aar 方案，将 jar 包放到一个 aar 中，然后 Android 项目依赖这个 aar 完成自定义 lint 检查。利用这种方案我们就可以针对项目进行自定义 Lint 规则，且 lint.jar 只对当前项目有效，根据 [Android Lint工作原理剖析](http://carrotsight.com/2016/06/21/Android%20Lint%E5%B7%A5%E4%BD%9C%E5%8E%9F%E7%90%86%E5%89%96%E6%9E%90.html) 里分析可以得知，当系统执行 lint 时，会检查项目的构建目录下的 lint 目录下是否引用了一个名为lint.jar文件，有的话会添加到自定义的 lint 规则中，最终将会被执行

以下项目结构参考自美团的自定义 Lint 开源项目。首先需要创建一个存放自定义 Lint 代码的纯 Java module，最终目的是输出 jar 包

对于自定义 Lint 我们需要依赖两个库，目前的 lint_version 版本是 25.3.0

```
compile 'com.android.tools.lint:lint-api:' + lint_version
compile 'com.android.tools.lint:lint-checks:' + lint_version
```

lint-checks 中包含了所有的官方定义的 lint 规则，我们可以参考其中的 Detector 实现自定义的 Detector 来满足项目的特殊需要，但是关于这方面的资料和文档是十分稀少的，所以这些官方提供的实例非常值得我们深入研究

我们以一个检测项目中 Log 日志打印的自定义 Detector 为例来说明自定义 Detector 的结构构成

```
public class LogUtilDetector extends Detector implements Detector.JavaPsiScanner {

    public static final Issue ISSUE = Issue.create(
            "LogUtilUse",
            "避免使用Log/System.out.println",
            "使用LogUtil，LogUtil对系统的Log类进行了展示格式、逻辑等封装",
            Category.SECURITY, 5, Severity.WARNING,
            new Implementation(LogUtilDetector.class, Scope.JAVA_FILE_SCOPE));

    public List<String> getApplicableMethodNames() {
        return Arrays.asList("d", "e", "i", "v", "w");
    }

    public void visitMethod(JavaContext context, JavaElementVisitor visitor, PsiMethodCallExpression node, PsiMethod method) {
        JavaEvaluator evaluator = context.getEvaluator();
        if (evaluator.isMemberInClass(method, "android.util.Log")) {
            String message = "请使用LogUtil";
            context.report(ISSUE, node, context.getLocation(node), message);
        }
    }
}
```

### Detector

lint 规则实现类需要继承 Detector 并实现 Scanner 接口

* **Detector.JavaScanner**——扫描 Java 源码抽象语法树，**在25.2.0版本中该接口已被弃用，换成了 JavaPsiScanner**，对语法分析由 [Lombok AST API](https://github.com/tnorbye/lombok.ast) 转变 [IntelliJ IDEA's "PSI" API](https://github.com/joewalnes/idea-community/tree/master/java/openapi/src/com/intellij/psi)，功能更强大而且可以扩展到 kotlin 语言上（kotlin 是由 intellij 推出的与 Java 无缝兼容的全新语言）
* **Detector.ClassScanner**——扫描 class 文件
* **Detector.BinaryResourceScanner**——扫描二进制资源文件
* **Detector.ResourceFolderScanner**——扫描资源文件
* **Detector.XmlScanner**——扫描xml文件
* **Detector.GradleScanner**——扫描gradle文件
* **Detector.OtherFileScanner**——扫描其他类型文件

在 Detector 已经默认实现所有接口的所有方法了，只需要 override 需要的方法即可
以最复杂的 Detector.JavaPsiScanner 为例，其接口组成为

```Java
public interface JavaPsiScanner {
    
    List<Class<? extends PsiElement>> getApplicablePsiTypes();
    
    JavaElementVisitor createPsiVisitor(JavaContext var1);

    List<String> getApplicableMethodNames();

    void visitMethod(JavaContext var1, JavaElementVisitor var2, PsiMethodCallExpression var3, PsiMethod var4);

    List<String> getApplicableConstructorTypes();

    void visitConstructor(JavaContext var1, JavaElementVisitor var2, PsiNewExpression var3, PsiMethod var4);

    List<String> getApplicableReferenceNames();

    void visitReference(JavaContext var1, JavaElementVisitor var2, PsiJavaCodeReferenceElement var3, PsiElement var4);

    boolean appliesToResourceRefs();

    void visitResourceReference(JavaContext var1, JavaElementVisitor var2, PsiElement var3, ResourceType var4, String var5, boolean var6);

    List<String> applicableSuperClasses();

    void checkClass(JavaContext var1, PsiClass var2);
}
```

在自定义的 LogUtilDetector 中，继承了 JavaPsiScanner 用来检测源代码中的目标代码

```Java
public class LogUtilDetector extends Detector implements Detector.JavaPsiScanner
```
我们实现了 `List<String> getApplicableMethodNames() ` 方法用于返回我们需要查找的方法名称，因为打印日志会调用系统类 Log 对应的方法，我们通过简析方法名来达到替换的目的。系统找到对应的方法则会回调 `void visitMethod(JavaContext var1, JavaElementVisitor var2, PsiMethodCallExpression var3, PsiMethod var4);` 方法希望得到进一步的处理，例子中我们对方法的宿主类进行了检验，只有是 android.util.Log 下的方法才是我们最重要找的目标


### Issue
找到了目标代码，我们需要上报给系统以供展示，Issue 的作用就是在 Detector 发现并报告，由静态方法 create 创建

```Java
public static Issue create(String id, String briefDescription, String explanation, Category category, int priority, Severity severity, Implementation implementation)
```

* **id** 唯一值，应该能简短描述当前问题。利用Java注解或者XML属性进行屏蔽时，使用的就是这个id
* **summary** 简短的总结，通常5-6个字符，描述问题而不是修复措施
* **explanation** 完整的问题解释和修复建议
* **category** 问题类别。在 Category 类中定义
* **priority** 优先级。1-10的数字，10为最重要/最严重
* **severity** 严重级别：Fatal，Error，Warning，Informational，Ignore，是 Severity 枚举类
* **Implementation** 为 Issue 和 Detector 提供映射关系，Detector 就是当前 Detector。声明扫描检测的范围 Scope，Scope 用来描述 Detector 需要分析时需要考虑的文件集，包括：Resource 文件或目录、Java 文件、Class 文件

我们为 LogUtilDetector 定义了上报的 Issue 如下格式

```Java
public static final Issue ISSUE = Issue.create(
            "LogUtilUse",
            "避免使用Log/System.out.println",
            "使用LogUtil，LogUtil对系统的Log类进行了展示格式、逻辑等封装",
            Category.SECURITY, 5, Severity.WARNING,
            new Implementation(LogUtilDetector.class, Scope.JAVA_FILE_SCOPE));
```

最终通过 `context.report(ISSUE, node, context.getLocation(node), message);` 方法上报，其中 message 就是针对具体代码场景的描述

### IssueRegistry
自定义 lint 规则必须提供一个继承自 IssueRegistry 的类，实现抽象方法 `public abstract List<Issue> getIssues();` 将所有自定义 Detector 的 Issue 方法放入，最终执行 lint 命令时，通过注册的 IssueRegistry 可以获取所有的自定义探测器 detector（Issue 中存在与 Detector 的映射关系）
最终我们将所有自定义的 Detector 汇总，对外提供

```
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
```

### 工程 gradle 配置

提供包含自定义 lint 规则 jar 包的 :lintCoreLib 模块的 gradle 配置

```gradle
//注册MHCIssueRegistry，生成jar包
jar {
    manifest {
        attributes("Lint-Registry": "com.maihaoche.lint.core.MHCIssueRegistry")
    }
}

//为aar包提供jar包依赖配置
defaultTasks 'assemble'

configurations {
    lintJarOutput
}

dependencies {
    lintJarOutput files(jar)
}
```

上层提供给项目 aar 包的 :lint 模块的 gradle 配置

```gradle
//配置lint的jar包
configurations {
    lintJarImport
}

dependencies {
    lintJarImport project(path: ":lintCoreLib", configuration: "lintJarOutput")
}

//将jar复制到lint目录下的lint.jar，因为在 lint 命令执行时会检查该文件，存在的话会添加到 lint 检查的队列中
task copyLintJar(type: Copy) {
    from (configurations.lintJarImport) {
        rename {
            String fileName ->
                'lint.jar'
        }
    }
    into 'build/intermediates/lint/'
}

//当 Project 创建完所有任务的有向图后，通过 afterEvaluate 函数设置一个回调 Closure。将 copyLintJar 插入到 compileLint 之前执行
  Closure 里，我 disable 了所有 Debug 的 Task
project.afterEvaluate {
    def compileLintTask = project.tasks.find { it.name == 'compileLint' }
    compileLintTask.dependsOn(copyLintJar)
}
```

最后只要在主工程中依赖 :lint 模块即可

我把导出的 aar 包导入到了生产环境中，执行后就可以见到自定义的 Lint 的检测结果

![生产环境自定义lint截图](http://oq546o6pk.bkt.clouddn.com/%E8%87%AA%E5%AE%9A%E4%B9%89lint%E7%94%9F%E4%BA%A7%E7%8E%AF%E5%A2%83%E6%88%AA%E5%9B%BE.png)

## debug lint代码
调试 lint 的代码需要特殊配置

* 在 gradle.properties 文件中添加如下配置

```
org.gradle.jvmargs='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005'
```

* 创建一个 Remote 类型的 debug 配置文件 
* 终端中以 daemon 模式启动 gradle lint 任务
* 选择新建的配置文件快速点击 debug 按钮

按照这种方法我们就可以 debug 我们自定义的 lint 规则或者是系统定义的 lint 规则了 


## 参考文献
[How to find gradle lintOptions document for android?](http://stackoverflow.com/questions/31128770/how-to-find-gradle-lintoptions-document-for-android)
[Android Studio Project Site -- Suppressing Lint Warnings](http://tools.android.com/tips/lint/suppressing-lint-warnings)
[Android自定义Lint实践](http://tech.meituan.com/android_custom_lint.html)
[Android自定义Lint实践2——改进原生Detector](http://tech.meituan.com/android_custom_lint2.html)
[Lint Source Code](https://android.googlesource.com/platform/tools/base/+/studio-master-dev/lint/)

