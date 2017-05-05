package core.zs.mpermissions.compiler;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 *
 */
final class Validator
{
    public static boolean isPublic(Element annotatedClass)
    {
        return annotatedClass.getModifiers().contains(PUBLIC);
    }

    public static boolean isPrivate(Element annotatedClass)
    {
        return annotatedClass.getModifiers().contains(PRIVATE);
    }

    public static boolean isAbstract(Element annotatedClass)
    {
        return annotatedClass.getModifiers().contains(ABSTRACT);
    }

    public static String getClassName(TypeElement type, String packageName)
    {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen)
                .replace('.', '$');
    }


    public  static boolean isMethodValid(Element annotatedElement)
    {
        if (annotatedElement.getKind() != ElementKind.METHOD)
        {
            return false;
        }
        if (Validator.isPrivate(annotatedElement) || Validator.isAbstract(annotatedElement))
        {
            return false;
        }
        return true;
    }
}
