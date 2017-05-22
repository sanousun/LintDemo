package com.maihaoche.lint

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.dsl.LintOptions
import com.android.build.gradle.tasks.Lint
import org.gradle.api.*
import org.gradle.api.tasks.TaskState

class LintPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        applyTask(project, getAndroidVariants(project))
    }

    private static
    final String sPluginMisConfiguredErrorMessage = "Plugin requires the 'android' or 'android-library' plugin to be configured.";
    /**
     * get android variant list of the project
     * @param project the compiling project
     * @return android variants
     */
    private static DomainObjectCollection<BaseVariant> getAndroidVariants(Project project) {

        if (project.getPlugins().hasPlugin(AppPlugin)) {
            return project.getPlugins().getPlugin(AppPlugin).extension.applicationVariants
        }

        if (project.getPlugins().hasPlugin(LibraryPlugin)) {
            return project.getPlugins().getPlugin(LibraryPlugin).extension.libraryVariants
        }

        throw new ProjectConfigurationException(sPluginMisConfiguredErrorMessage, null)
    }

    private void applyTask(Project project, DomainObjectCollection<BaseVariant> variants) {
        project.dependencies {

//            if (project.getPlugins().hasPlugin('com.android.application')) {
//                compile('com.meituan.android.lint:lint:latest.release') {
//                    force = true;
//                }
//            }
//            else {
//                provided('com.meituan.android.lint:lint:latest.release') {
//                    force = true;
//                }
//            }

        }
//        project.configurations.all {
//            resolutionStrategy.cacheDynamicVersionsFor 0, 'seconds'
//        }

        def archonTaskExists = false

        variants.all { variant ->
            def variantName = variant.name.capitalize()
            Lint lintTask = project.tasks.getByName("lint" + variantName) as Lint

            //Lint 会把project下的lint.xml和lintConfig指定的lint.xml进行合并，为了确保只执行插件中的规则，采取此策略
            File lintFile = project.file("lint.xml")
            File lintOldFile = null

            /*
            lintOptions {
               lintConfig file("lint.xml")
               warningsAsErrors true
               abortOnError true
               htmlReport true
               htmlOutput file("lint-report/lint-report.html")
               xmlReport false
            }
            */

            def newOptions = new LintOptions()
            newOptions.lintConfig = lintFile
            newOptions.warningsAsErrors = true
            newOptions.abortOnError = true
            newOptions.htmlReport = true
            //不放在build下，防止被clean掉
            newOptions.htmlOutput = project.file("${project.projectDir}/lint-report/lint-report.html")
            newOptions.xmlReport = false

            lintTask.lintOptions = newOptions

            lintTask.doFirst {

                if (lintFile.exists()) {
                    lintOldFile = project.file("lintOld.xml")
                    lintFile.renameTo(lintOldFile)
                }
                def isLintXmlReady = copyLintXml(project, lintFile)

                if (!isLintXmlReady) {
                    if (lintOldFile != null) {
                        lintOldFile.renameTo(lintFile)
                    }
                    throw new GradleException("lint.xml不存在")
                }

            }

            project.gradle.taskGraph.afterTask { task, TaskState state ->
                if (task == lintTask) {
                    lintFile.delete()
                    if (lintOldFile != null) {
                        lintOldFile.renameTo(lintFile)
                    }
                }
            }

            // For archon

            if (!archonTaskExists) {
                archonTaskExists = true
                project.task("lintForArchon").dependsOn lintTask
            }

        }
    }
    /**
     * copy lint xml
     * @return is lint xml ready
     */
    boolean copyLintXml(Project project, File targetFile) {

        targetFile.parentFile.mkdirs()

        InputStream lintIns = this.class.getResourceAsStream("/config/lint.xml")
        OutputStream outputStream = new FileOutputStream(targetFile)

        // 未使用 或 使用了不支持try with resource的版本
        IOUtils.copy(lintIns, outputStream)
        IOUtils.closeQuietly(outputStream)
        IOUtils.closeQuietly(lintIns)

        if (targetFile.exists()) {
            return true
        }

        return false
    }
}