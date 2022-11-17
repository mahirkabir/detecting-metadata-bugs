## beanExists
---------------
Reports if a bean is called using `ApplicationContext.getBean` but the bean does not exist the project.

## constructorArgumentField
---------------
Reports if a constructor argument mentioned in the bean does not correspond to any *field* in the corresponding class mentioned in the bean.

## constructorArgumentFieldType
---------------
Reports if a constructor argument mentioned in the bean does not correspond to any *field's type* in the corresponding class mentioned in the bean.

## fieldProperty
---------------
Reports if a property mentioned in a bean is not available in the class mentioned in the bean

## methodExist
---------------
Reports if a method mentioned in a bean is not available in the class mentioned in the bean

## setterMethod
---------------
Reports if a property mentioned in a bean does not have a setter method available in the class mentioned in the bean

## importAnnotationIntoXML
---------------
Reports if `<context:annotation-config>` is mentioned in the XML configuration file, but no entity exists in the project with annotations such as - `@Autowired`, `@Qualifier`, `@PostConstruct`, `@PreDestroy`, or `@Resource`.

## importXMLIntoAnnotation
---------------
Reports if `@ImportResource` annotation is used in a class but the mentioned resource location does not exist.

## xmlPathCheck
---------------
Reports if `ApplicationContext.ClassPathXmlApplicationContext` is called in a class but the argument path does not exist.

## Notes
---------------
1. Verified: `constructorArgumentField`, `constructorArgumentFieldType`, `setterMethod`, `xmlPathCheck`, `importAnnotationIntoXML` (Throws exception only if the autowired method is invoked), `beanExists` (Need to modify the method call part in the rule), `importXMLIntoAnnotation`, `methodExists`
1. Unverified: `fieldProperty`
1. **NEW**: Can we do something similar to `ApplicationContext` for `BeanFactory` container? (Do after verification) (**Verified**: For `ClassPathResource`. But the problem is, unlike `ApplicationContext`, for `BeanFactory`, the path is usually formed in a separate `Resource` variable. Need static analysis for accessing the value of the variable. Might do it in future works)
1. **NEW**: Can we do something similar to `ApplicationContext.ClassPathXmlApplicationContext` for `ApplicationContext.FileSystemXmlApplicationContext` and `ApplicationContext.WebXmlApplicationContext`? (Do after verification. **Finding**: Only 750~ applications use hardcoded string path for `ApplicationContext.FileSystemXmlApplicationContext`. Is it worth it?)
1. Need to remove `fieldProperty`. Doesn't throw exception.
1. **NEW**: If only constructors with param(s) are there in the java file, but no `<construct-arg>` is there in the bean, an exception is thrown. Do we need to write rule for that? (We can do it. But chances are low for this to happen. Will do it later.)
1. **NEW**: `<constuct-arg>` can have formats other than `<constuct-arg name=".."/>`. Can we write rules to handle them as well? (Check if `index` is used but number of index is less than the `index` value - 1)

1. **Note**: We need to implement something to check the argument parameters instead of class fields when it comes to checking `<construct-arg>`