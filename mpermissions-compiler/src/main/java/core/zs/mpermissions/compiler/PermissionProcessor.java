package core.zs.mpermissions.compiler;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import core.zs.mpermissions.annotations.OnPermissionDenied;
import core.zs.mpermissions.annotations.OnPermissionGranted;
import core.zs.mpermissions.annotations.ShowRequestPermissionRationale;

import static javax.lang.model.SourceVersion.latestSupported;


/**
 * Created by ZhangShuai on 2017/5/5.
 */
@AutoService(Processor.class)
public class PermissionProcessor extends AbstractProcessor {
    private Messager mMessager;
    private Elements mElementUtils;
    private Map<String, ProxyInfo> mProxyMap = new HashMap<String, ProxyInfo>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
        mElementUtils = processingEnv.getElementUtils();
    }

    /**
     * 添加支持的注解类型
     *
     * @return 注解类型类名集合
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(OnPermissionDenied.class.getCanonicalName());
        supportTypes.add(OnPermissionGranted.class.getCanonicalName());
        supportTypes.add(ShowRequestPermissionRationale.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private boolean processAnnotations(RoundEnvironment roundEnv, Class<? extends Annotation>
            clazz) {
        //获得被该注解声明的元素，并遍历处理
        for (Element element : roundEnv.getElementsAnnotatedWith(clazz)) {
            if (Validator.isMethodValid(element) == false) {
                return false;
            }

            //强制转换为Method类型
            ExecutableElement method = (ExecutableElement) element;
            //强制转换为类类型
            TypeElement classElement = (TypeElement) method.getEnclosingElement();
            //获取类的全名
            String fqClassName = classElement.getQualifiedName().toString();

            //主要实现缓存机制
            //获取类的代理信息
            ProxyInfo proxyInfo = mProxyMap.get(fqClassName);
            if (proxyInfo == null) {
                proxyInfo = new ProxyInfo(mElementUtils, classElement);
                mProxyMap.put(fqClassName, proxyInfo);
                proxyInfo.setTypeElement(classElement);
            }


            Annotation annotation = method.getAnnotation(clazz);
            if (annotation instanceof OnPermissionGranted) {
                int requestCode = ((OnPermissionGranted) annotation).requestCode();
                proxyInfo.grantMethodMap.put(requestCode, method.getSimpleName().toString());
            }
            else if (annotation instanceof OnPermissionDenied) {
                int requestCode = ((OnPermissionDenied) annotation).requestCode();
                proxyInfo.deniedMethodMap.put(requestCode, method.getSimpleName().toString());
            }
            else if (annotation instanceof ShowRequestPermissionRationale) {
                int value = ((ShowRequestPermissionRationale) annotation).value();
                proxyInfo.rationaleMethodMap.put(value, method.getSimpleName().toString());
            }

            else {
                error(method, "%s not support .", clazz.getSimpleName());
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mProxyMap.clear();
        mMessager.printMessage(Diagnostic.Kind.NOTE, "process...");

        if (!processAnnotations(roundEnv, OnPermissionGranted.class)) {
            return false;
        }
        if (!processAnnotations(roundEnv, OnPermissionDenied.class)) {
            return false;
        }

        if (!processAnnotations(roundEnv, ShowRequestPermissionRationale.class)) {
            return false;
        }

        for (String key : mProxyMap.keySet()) {
            ProxyInfo proxyInfo = mProxyMap.get(key);
            try {
                JavaFileObject jfo =
                        processingEnv.getFiler().createSourceFile(proxyInfo.getProxyClassFullName
                                (), proxyInfo.getTypeElement());
                Writer writer = jfo.openWriter();
                writer.write(proxyInfo.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                error(proxyInfo.getTypeElement(), "Unable to write injector for type %s: %s",
                        proxyInfo.getTypeElement(), e.getMessage());
            }

        }
        return true;
    }


    /**
     * 打印错误消息
     */
    public void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message, element);
    }

}
