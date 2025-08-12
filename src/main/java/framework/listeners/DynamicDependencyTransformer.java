package framework.listeners;

import framework.config.PropertyReader;
import framework.utils.ExcelUtil;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class DynamicDependencyTransformer implements IAnnotationTransformer {
    private static Map<String, List<String>> dependencyMap;
    static String path = PropertyReader.readProperty("testNGPath");

    static {
        dependencyMap = ExcelUtil.getDependencies(path);
    }

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {

        annotation.setRetryAnalyzer(framework.listeners.RetryAnalyzer.class);

        List<String> dependsOn = dependencyMap.get(testMethod.getName());
        if (dependsOn != null) {
            annotation.setDependsOnMethods(dependsOn.toArray(new String[0]));
        }
    }
}

