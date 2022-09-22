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