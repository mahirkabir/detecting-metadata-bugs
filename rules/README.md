# Rules
Rules are categorized in 4 categories</br>
1. Beans - Rules to check beans related bugs
2. Annotations - Rules to check annotations (but not beans) related bugs
3. JUnits - Rules to check JUnits related bugs
4. General - All other rules

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

## runwithNoParameters
---------------
Reports if class has `@RunWith(Parameterized.class)` annotation but no method has `@Parameters` annotation

## runwithNoSuiteclasses
---------------
Reports if class has `@RunWith(Suite.class)` annotation but does not have `@SuiteClasses` annotation

## runwithNoTest
---------------
Reports if class has `@RunWith(Parameterized.class)` but no method has `@Test` annotation

## suiteclassesNoRunwith
---------------
Reports if class has `@SuiteClasses` annotation but does not have `@RunWith` annotations

## suiteclassesNoTest
---------------
Reports if class has `@SuiteClasses(CLASS1.class, CLASS2.class ...)` but any of the suite classes does not have a `@Test` method. **Note:** This rule only works if the class short name is unique for any class.

## testParamsNotIterable
---------------
Reports if the return type of the method with `@Parameters` annotation is not iterable.