package com.craftmaster.lds.fgc;

import static java.lang.String.format;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestUtil {
	private static final Logger logger = Logger.getLogger(TestUtil.class.getName());

	public static final Date toDate(LocalDate localDate) {
		return java.util.Date.from(localDate.atStartOfDay(ZoneId.of("America/Denver")).toInstant());
	}

	private TestUtil() {
		// exists to thwart instantiation
	}

	/**
	 * This method tests the associated read and/or write methods for all
	 * "properties" of the testObject, except the passed in field names
	 *
	 * @param testObject    Object on which to call the getters and/or setters
	 * @param propertyNames Array (varargs) of property names whose associated read
	 *                      and/or write SHOULD NOT be tested
	 * @throws throws a runtime exception if the test fails
	 */
	public static void testPropertiesExclude(Object testObject, String... propertyNames) {
		test(instantiateInstanceIfClass(testObject), propertyNames, false);
	}

	/**
	 * This method tests the associated read and/or write methods for the passed in
	 * "property" names, of the testObject
	 *
	 * @param testObject    Object on which to call the read and/or write methods
	 * @param propertyNames Array (varargs) of field names whose associated getters
	 *                      and/or setters SHOULD be tested
	 * @throws throws a runtime exception if the test fails
	 */
	public static void testPropertiesInclude(Object testObject, String... propertyNames) {
		test(instantiateInstanceIfClass(testObject), propertyNames, true);
	}

	/**
	 * This method tests the read and/or write methods for all "properties" on the
	 * given testObject. This only attempts to test getters and setters of the class
	 * or instance passed in, and not the getters and setters of any superclasses.
	 * However, if there is a getter but not an associated setter, or vice versa, it
	 * will search parent classes for an associated match to create a true property.
	 * <p/>
	 * The test is accomplished with the following rules: If there is a getter and a
	 * setter (a true property) it tests that the getter returns what was set on the
	 * setter If there is a getter and a field (no setter) it will test that what is
	 * set on the field is returned by the getter &#160;&#160;&#160;If the field is
	 * final the field value is tested against the getter If there is a setter and a
	 * field (no getter) it will make sure that value passed to the setter is the
	 * value of the field
	 * <p/>
	 * If there is a getter, but no setter, all parent classes will be searched for
	 * a matching setter. If there is a setter, but no getter, all parent classes
	 * will be searched for a matching getter.
	 *
	 * @param testObject Object on which to call the read and/or write methods
	 * @throws throws a runtime exception if the test fails
	 */
	public static void testProperties(Object testObject) {
		// pass in true for exclude, and since there are no fields passed in, nothing
		// will be excluded
		test(instantiateInstanceIfClass(testObject), null, null);
	}

	/**
	 * This method determines if the passed in object if of type Class, and if it
	 * is, it attempt to instantiate an instance of the class with whic to test the
	 * properties.
	 *
	 * @param testObject Object passed in by user which is an instance or from which
	 *                   we create an instance.
	 * @return The instantiated object if it is a class, or the object passed in if
	 *         not.
	 */
	protected static Object instantiateInstanceIfClass(Object testObject) {
		// if the object passed in is a class, try and instantiate an instance of it,
		// otherwise
		// just return the instance
		if (testObject instanceof Class<?>) {
			Class<?> testClass = (Class<?>) testObject;
			if (testClass.isInterface()) {
				throw new RuntimeException("There are no properties to test on an interface.");
			} else if (Modifier.isAbstract(testClass.getModifiers())) {
				return createCgLibProxy(testClass);
			} else {
				// if there is not a no arg constructor then an exception will be thrown
				try {
					return testClass.getDeclaredConstructor().newInstance();
				} catch (Exception e) {
					throw new RuntimeException(MessageFormat.format(
							"There was an exception attempting to instantiate an instance of class {0}." +
									" If this class does not contain a no argument constructor, that could have caused the problem." +
									" Try passing in an instance instead of the class.",
							testClass), e);
				}
			}

		} else {
			return testObject;
		}

	}

	/**
	 * Simple utility method that can simplify aquiring a private member. Useful in
	 * test assertions.
	 * 
	 * @param target    the object containing the member
	 * @param fieldName the String name of the field.
	 * @return The value of the specified field on the specified target.
	 */
	public static Object getField(Object target, String fieldName) {
		if (target == null || fieldName == null) {
			throw new IllegalArgumentException("Neither target or fieldName can be null.");
		}
		try {
			Field field = target.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(target);
		} catch (Exception e) {
			throw new IllegalArgumentException("Error attempting to get value of field '" + fieldName + "' on object of type'"
					+ target.getClass().getName() + "'", e);
		}

	}

	/**
	 * <p>
	 * Deep clone an <code>Object</code> using serialization.
	 * </p>
	 * <p/>
	 * <p>
	 * This is many times slower than writing clone methods by hand on all objects
	 * in your object graph. However, for complex object graphs, or for those that
	 * don't support deep cloning this can be a simple alternative implementation.
	 * Of course all the objects must be <code>Serializable</code>.
	 * </p>
	 *
	 * @param object the <code>Serializable</code> object to clone
	 * @return the cloned object
	 * @throws RuntimeException (runtime) if the serialization fails
	 */
	public static Object clone(Serializable object) {
		ObjectInputStream in = null;
		ObjectOutputStream out = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
			out = new ObjectOutputStream(baos);
			out.writeObject(object);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			in = new ObjectInputStream(bais);
			return in.readObject();
		} catch (Exception ex) {
			throw new RuntimeException("An exception was thrown cloning the object.", ex);
		} finally {

			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				// ignore close exception
				logger.log(Level.FINER, "IOException thrown in clone of TestUtil.");
			}
		}
	}

	/**
	 * This method performs basic litmus tests on equals methods
	 *
	 * @param testObject1 containing the equals method to be tested
	 * @param testObject2 used as the parameter to the call to the equals method
	 */
	public static void testEquals(Object testObject1, Object testObject2) {
		try {
			Method equalsMethod = testObject1.getClass().getMethod("equals", Object.class);

			// an object compared with itself should be equal
			if (!((Boolean) equalsMethod.invoke(testObject1, testObject1))) {
				throw new RuntimeException("The two instances of the same class should be equal, but they were not.");
			}
			// test that these are the same (so if the user passed in two objects
			// explicitly) they will be tested against each other
			if (!((Boolean) equalsMethod.invoke(testObject1, testObject2))) {
				throw new RuntimeException("The two instances of the same class should be equal, but they were not.");
			}
			if (((Boolean) equalsMethod.invoke(testObject1, testObject2))) {
				// if the objects are equal, then they must also have the same hashcode
				if (testObject1.hashCode() != testObject2.hashCode()) {
					throw new RuntimeException(
							"The the objects were equal but their hashcodes were not the same.  By definition, objects that are equal must have the same hashcode.");
				}
			}
			// an object compared against null should always return false
			if (((Boolean) equalsMethod.invoke(testObject1, new Object[] { null }))) {
				throw new RuntimeException(
						"An object compared with null should always return false, but the test returned true.");
			}
			// an object compared with an object of a different type should always be false
			if (((Boolean) equalsMethod.invoke(testObject1, "string"))) {
				throw new RuntimeException(
						"An object compared with an object of a different type should always return false, but the test returned true.");
			}
		} catch (Exception ex) {
			throw new RuntimeException("An exception was thrown testing object equals.", ex);
		}
	}

	protected static void test(Object testObject, String[] propertyNames, Boolean include) {
		Map<String, PropertyInfo> propertyMap = collectProperties(testObject,
				propertyNames == null ? new ArrayList<String>() : Arrays.asList(propertyNames),
				include == null ? false : include);
		Set<String> keys = propertyMap.keySet();
		for (String propertyName : keys) {
			PropertyInfo propertyInfo = propertyMap.get(propertyName);
			if (propertyInfo.getWriteMethods().size() <= 0) {
				testReadAndOrWriteMethods(testObject, propertyInfo.getReadMethod(), null, propertyInfo.getField(),
						propertyName);
			} else {
				for (Method writeMethod : propertyInfo.getWriteMethods()) {
					testReadAndOrWriteMethods(testObject, propertyInfo.getReadMethod(), writeMethod, propertyInfo.getField(),
							propertyName);
				}
			}
		}
	}

	/**
	 * This method collects all of the propeties (meaning readMethod, writeMethods,
	 * associated field) in the given test object and adds them to a map of
	 * PropertyInfo objects. This is done by looking for all methods that start with
	 * a get or is, and have a return type not equal to void and no parameters, or
	 * methods beginning with set that have one parameter and a return type of void.
	 *
	 * @param testObject
	 * @param propertyNames List of properties to include, or exclude from the test
	 * @param include       Whether to include, or exclude the names in
	 *                      <code>propertyNames</code>. If include is null, all
	 *                      found properties are tested.
	 * @return A map of PropertyInfo objects with the property as a key. For
	 *         example, if a method getSomething was found in the testObject, there
	 *         would be a PropertyInfo object with readMethod set to getSomething()
	 *         Method and the key for the propertyInfo Object would be "something".
	 */
	protected static Map<String, PropertyInfo> collectProperties(Object testObject, List<String> propertyNames,
			boolean include) {
		Map<String, PropertyInfo> propertyMap = new HashMap<String, PropertyInfo>();

		// if the object is a cglib proxy then just get the methods of the parent class
		Class<?> testClass = isCglibProxyClass(testObject.getClass()) ? testObject.getClass().getSuperclass()
				: testObject.getClass();

		for (Method method : testClass.getDeclaredMethods()) {
			String methodName = method.getName();
			// if the method is get and has no parameters and the return type is not void
			if ((methodName.startsWith("get") && method.getParameterTypes().length == 0
					&& method.getReturnType() != Void.TYPE)) {

				String propertyName = lowerCaseFirstLetter(methodName.substring(3));
				// if it does not already contain it, add the name
				if ((include && propertyNames.contains(propertyName)) || (!include && !propertyNames.contains(propertyName))) {
					PropertyInfo propertyInfo = propertyMap.get(propertyName);
					if (propertyInfo == null) {
						propertyInfo = new PropertyInfo();
					}
					propertyInfo.setReadMethod(method);
					propertyMap.put(propertyName, propertyInfo);
				}
			} else if (methodName.startsWith("set") && method.getParameterTypes().length == 1
					&& method.getReturnType() == Void.TYPE) {
				String propertyName = lowerCaseFirstLetter(methodName.substring(3));
				// if it does not already contain it, add the name
				if ((include && propertyNames.contains(propertyName)) || (!include && !propertyNames.contains(propertyName))) {
					PropertyInfo propertyInfo = propertyMap.get(propertyName);
					if (propertyInfo == null) {
						propertyInfo = new PropertyInfo();
					}
					propertyInfo.addWriteMethod(method);
					propertyMap.put(propertyName, propertyInfo);
				}
			} else if (methodName.startsWith("is") && method.getParameterTypes().length == 0
					&& method.getReturnType() != Void.TYPE) {
				String propertyName = lowerCaseFirstLetter(methodName.substring(2));
				// if it does not already contain it, add the name
				if ((include && propertyNames.contains(propertyName)) || (!include && !propertyNames.contains(propertyName))) {
					PropertyInfo propertyInfo = propertyMap.get(propertyName);
					if (propertyInfo == null) {
						propertyInfo = new PropertyInfo();
					}
					propertyInfo.setReadMethod(method);
					propertyMap.put(propertyName, propertyInfo);
				}
			}
		}
		return fillInHierarchicalInfo(propertyMap, testObject);
	}

	/**
	 * Check whether the specified class is a CGLIB-generated class.
	 * 
	 * @param clazz the class to check
	 */
	private static boolean isCglibProxyClass(Class<?> clazz) {
		return (clazz != null && clazz.getName().indexOf("$$") != -1);
	}

	/**
	 * This method goes through all of the properties collected (i.e. all methods
	 * for which either a getter and/or setter was found), and if there is a getter
	 * and no associated setter, or a setter and no associated getter is searches
	 * for one in the test objects superclasses
	 *
	 * @param propertyMap
	 * @param testObject
	 * @return
	 */
	protected static Map<String, PropertyInfo> fillInHierarchicalInfo(Map<String, PropertyInfo> propertyMap,
			Object testObject) {
		Set<String> keys = propertyMap.keySet();
		for (String propertyName : keys) {
			PropertyInfo propertyInfo = propertyMap.get(propertyName);

			// if we don't have a read method, search the hierarchy for one
			if (propertyInfo.getReadMethod() == null) {
				propertyInfo
						.setReadMethod(findDeclaredMethodInHierarchy(testObject, "get" + upperCaseFirstLetter(propertyName)));
				if (propertyInfo.getReadMethod() == null) {
					propertyInfo
							.setReadMethod(findDeclaredMethodInHierarchy(testObject, "is" + upperCaseFirstLetter(propertyName)));
				}
				// if we don't have a write method, search in the hierarchy for one
			} else if (propertyInfo.getWriteMethods().size() <= 0) {
				Method method = findDeclaredMethodInHierarchy(testObject,
						"set" + upperCaseFirstLetter(propertyName),
						propertyInfo.getReadMethod() != null ? propertyInfo.getReadMethod().getReturnType() : null);
				if (method != null) {
					propertyInfo.addWriteMethod(method);
				}
			}

			if (!propertyInfo.isProperty()) {
				propertyInfo.setField(findDeclaredFieldInHierarchy(testObject, propertyName));
			}
			// put the updated property info in the map
			propertyMap.put(propertyName, propertyInfo);
		}
		return propertyMap;
	}

	protected static void testReadAndOrWriteMethods(Object testObject, Method readMethod, Method writeMethod, Field field,
			String propertyName) {
		// if both getter and setter are null, there is nothing to test
		// so, throw an exception since they must have added a non-existant property
		if (readMethod == null && writeMethod == null) {
			throw new RuntimeException(MessageFormat.format("Could not find method for property named {0}.", propertyName));
		}

		// if we have a readMethod and a writeMethod (definition of a property)
		// call the writeMethod and the readMethod
		if (readMethod != null && writeMethod != null) {
			testProperty(testObject, readMethod, writeMethod, propertyName);
		} else if (readMethod != null) {
			testReadOnly(testObject, readMethod, field, propertyName);
		} else { // writeMethod != null
			testWriteOnly(testObject, writeMethod, field, propertyName);
		}
	}

	/**
	 * Test the read and write methods of the property.
	 *
	 * @param testObject
	 * @param readMethod
	 * @param writeMethod
	 * @param propertyName
	 * @throws Exception
	 */
	protected static void testProperty(Object testObject, Method readMethod, Method writeMethod, String propertyName) {
		readMethod.setAccessible(true);
		writeMethod.setAccessible(true);
		try {
			Object saveOffValue = readMethod.invoke(testObject);
			Object proxy = createProxy(writeMethod.getParameterTypes()[0]);

			// make sure the getter returns what the setter sets
			writeMethod.invoke(testObject, proxy);
			if (proxy.equals(readMethod.invoke(testObject))) {
				logger.log(Level.INFO, MessageFormat.format("TEST OF **** {0} AND {1} **** SUCCEEDED.", readMethod.getName(),
						writeMethod.getName()));
			} else {
				throw new RuntimeException(MessageFormat.format("Test for property {0} failed.", propertyName));
			}

			// set the original value back
			writeMethod.invoke(testObject, saveOffValue);
		} catch (InvocationTargetException e) {
			handleInvocationTargetException(propertyName, e);
		} catch (Exception e) {
			throw new RuntimeException(format("Error accessing property: %s", propertyName), e);
		}
	}

	private static void handleInvocationTargetException(String propertyName, InvocationTargetException e) {
		if (e.getTargetException() instanceof RuntimeException) {
			throw (RuntimeException) e.getTargetException();
		} else {
			throw new RuntimeException("Exception thrown while invoking method for property: " + propertyName,
					e.getTargetException());
		}
	}

	/**
	 * Tests the read method for the given "property".
	 *
	 * @param testObject
	 * @param readMethod
	 * @param propertyName
	 * @throws Exception
	 */
	protected static void testReadOnly(Object testObject, Method readMethod, Field field, String propertyName) {
		try {
			readMethod.setAccessible(true);

			// there is only a getter - if there is no associated member variable, then
			// return
			// Field field = //findDeclaredFieldInHierarchy(testObject, propertyName);
			if (field == null) {
				return;
			}

			// make the field accessible, even if it is private
			field.setAccessible(true);

			// if the field is not declared final, then set it explicitly
			if ((field.getModifiers() & Modifier.FINAL) != Modifier.FINAL) {

				Object saveOffValue = readMethod.invoke(testObject);
				Object proxy = createProxy(field.getType());

				// set the field explicitly, and then make sure the getter returns the correct
				// value
				field.set(testObject, proxy);
				if (proxy.equals(readMethod.invoke(testObject))) {
					logger.log(Level.INFO,
							MessageFormat.format("TEST OF **** direct set AND {0} **** SUCCEEDED.", readMethod.getName()));
				} else {
					field.set(testObject, saveOffValue);
					throw new RuntimeException(
							MessageFormat.format("Test for get{0} or is{1} on class {2} failed.", upperCaseFirstLetter(propertyName),
									upperCaseFirstLetter(propertyName), testObject));
				}
				field.set(testObject, saveOffValue);
			} else {
				// if it is final just test that the field value is the same as the getter
				Object getterValue = readMethod.invoke(testObject);
				Object fieldValue = field.get(testObject);
				if ((getterValue != null && getterValue.equals(fieldValue)) || (getterValue == null && fieldValue == null)) {
					logger.log(Level.INFO,
							MessageFormat.format("TEST OF **** final member variable AND {0} **** SUCCEEDED.", readMethod.getName()));
				} else {
					throw new RuntimeException(
							MessageFormat.format("Test for get{0} or is{1} and final member variable on class {2} failed.",
									upperCaseFirstLetter(propertyName),
									upperCaseFirstLetter(propertyName), testObject));
				}
			}
		} catch (InvocationTargetException e) {
			handleInvocationTargetException(propertyName, e);
		} catch (Exception e) {
			throw new RuntimeException(format("Error accessing property: %s to test", propertyName), e);
		}
	}

	/**
	 * Tests the write method for the given "property".
	 *
	 * @param testObject
	 * @param writeMethod
	 * @param propertyName
	 * @throws Exception
	 */
	protected static void testWriteOnly(Object testObject, Method writeMethod, Field field, String propertyName) {
		try {
			writeMethod.setAccessible(true);

			// there is only a setter - if there is no associated member variable, then
			// return
			// Field field = findDeclaredField(testObject, propertyName);
			// //findDeclaredFieldInHierarchy(testObject, propertyName);
			if (field == null) {
				return;
			}

			// make the field accessible, even if it is private or final
			field.setAccessible(true);

			Object saveOffValue = field.get(testObject);
			Object proxy = createProxy(writeMethod.getParameterTypes()[0]);

			// invoke the setter, and access the field directly to make sure the value was
			// set
			writeMethod.invoke(testObject, proxy);
			if (proxy.equals(field.get(testObject))) {
				logger.log(Level.INFO,
						MessageFormat.format("TEST OF **** {0} AND direct get **** SUCCEEDED.", writeMethod.getName()));
			} else {
				throw new RuntimeException(
						MessageFormat.format("Test for {0} on class {1} failed.", writeMethod.getName(), testObject));
			}
			field.set(testObject, saveOffValue);
		} catch (InvocationTargetException e) {
			handleInvocationTargetException(propertyName, e);
		} catch (Exception e) {
			throw new RuntimeException("Error accessing property to test", e);
		}
	}

	/**
	 * Compiles a list of fields found in the object passed in, or any superclasses
	 * of the object.
	 *
	 * @param testObject The object whose fields and fields of its superclass should
	 *                   be retrieved
	 * @return List<Field> of all the fields of the class and all of its
	 *         superclasses
	 */
	protected static Field findDeclaredFieldInHierarchy(Object testObject, String fieldName) {
		Field found = null;
		Class<?> objectClass = testObject.getClass();
		do {
			// catching the exception does not seem very performant, but it seems easier
			// than
			// comparing fields
			try {
				found = objectClass.getDeclaredField(fieldName);
				// if it is not found, it will throw an exception and we will continue with the
				// loop
				return found;
			} catch (NoSuchFieldException ex) {
				// we are looking for the field, so just ignore this exception
			}
			objectClass = objectClass.getSuperclass();
		} while (objectClass != null);

		return found;
	}

	protected static Method findDeclaredMethodInHierarchy(Object testObject, String methodName,
			Class<?>... parameterTypes) {
		Method found = null;
		Class<?> objectClass = testObject.getClass();
		do {
			// catching the exception does not seem very performant, but I am not sure how
			// else to do this - getDeclaredField (or method for that matter) only look in
			// the current class or interface
			try {
				found = objectClass.getDeclaredMethod(methodName, parameterTypes);
				// if it is not found, it will throw an exception and we will continue with the
				// loop
				return found;
			} catch (NoSuchMethodException ex) {
				// we are looking for the field, so just ignore this exception//no method with
				// the given name was found for this field
				logger.log(Level.FINE,
						MessageFormat.format("No accessor named {0} was found in class {1}.", methodName, testObject.getClass()));
			}
			objectClass = objectClass.getSuperclass();
		} while (objectClass != null);

		return found;
	}

	/**
	 * Converts the first letter of the given string to upper case. For example:
	 * "propertyName" should be returned as "PropertyName"
	 *
	 * @param string String to have first letter converted to upper case
	 * @return String with the first letter converted to upper case
	 */
	protected static String upperCaseFirstLetter(String string) {
		return (string.length() == 0) ? string : Character.toUpperCase(string.charAt(0)) + string.substring(1);
	}

	protected static String lowerCaseFirstLetter(String string) {
		return (string.length() == 0) ? string : Character.toLowerCase(string.charAt(0)) + string.substring(1);
	}

	/**
	 * This method creates a proxy object or primitive for the given type.
	 *
	 * @param type Class type of the object or primitive to be proxied
	 * @return The proxied object or wrapped primitive
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws ClassNotFoundException
	 */
	protected static Object createProxy(Class<?> type)
			throws IllegalAccessException, InstantiationException, NoSuchMethodException, NoSuchFieldException,
			InvocationTargetException, ClassNotFoundException {
		if (String.class.isAssignableFrom(type)) {
			return "";
		} else if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type)) {
			return Boolean.FALSE;
		} else if (Byte.class.isAssignableFrom(type) || byte.class.isAssignableFrom(type)) {
			return Byte.parseByte("1");
		} else if (Character.class.isAssignableFrom(type) || char.class.isAssignableFrom(type)) {
			return Character.valueOf('?');
		} else if (Short.class.isAssignableFrom(type) || short.class.isAssignableFrom(type)) {
			return Short.valueOf("1");
		} else if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
			return 0;
		} else if (Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type)) {
			return 0L;
		} else if (Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type)) {
			return 0F;
		} else if (Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type)) {
			return 0D;
		} else if (BigDecimal.class.isAssignableFrom(type)) {
			return BigDecimal.ZERO;
		} else if (BigInteger.class.isAssignableFrom(type)) {
			return BigInteger.ZERO;
		} else if (Class.class.isAssignableFrom(type)) {
			return Object.class;
		} else if (Method.class.isAssignableFrom(type)) {
			return Object.class.getMethod("toString", (Class[]) null);
		} else if (Field.class.isAssignableFrom(type)) {
			return Integer.class.getField("MAX_VALUE");
		} else if (type.isArray()) {
			return Array.newInstance(type.getComponentType(), 0);
		} else if (type.isEnum()) {
			Method m = type.getMethod("values", new Class[0]);
			try {
				Object[] enums = (Object[]) m.invoke(null, new Object[0]);
				if (enums.length > 0) {
					return enums[0];
				}
			} catch (Exception e) {
				// Just ignore
			}
			throw new RuntimeException("Unable to get instance from Enum.");
		} else if (Locale.class.isAssignableFrom(type)) {
			return Locale.getDefault();
		} else if (Timestamp.class.isAssignableFrom(type)) {
			return new Timestamp(System.currentTimeMillis());
		} else if (LocalDateTime.class.isAssignableFrom(type)) {
			return LocalDateTime.now();
		} else if (type.isInterface()) {
			if (List.class.isAssignableFrom(type)) {
				return new ArrayList<>();
			} else if (Set.class.isAssignableFrom(type)) {
				return new HashSet<>();
			} else if (Map.class.isAssignableFrom(type)) {
				return new HashMap<>();
			}
			return createCgLibProxy(type);
			// throw new RuntimeException("Unable to get instance for " + type.getName());
		} else {
			try {
				return type.getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				// Just ignore
			}
			return createCgLibProxy(type);
		}
	}

	/**
	 * This method creates a proxy object for the given type using a no-arg
	 * constructor.
	 *
	 * @param type Class type of the object or primitive to be proxied
	 * @return The proxied object or wrapped primitive
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	protected static Object createInstance(Class<?> type)
			throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
		Constructor<?> constructor;
		try {
			constructor = type.getConstructor((Class[]) null);
		} catch (NoSuchMethodException e) {
			constructor = type.getDeclaredConstructor((Class[]) null);
			constructor.setAccessible(true);
		}
		return constructor.newInstance((Object[]) null);
	}

	private static Object createCgLibProxy(Class<?> type) {
		throw new RuntimeException(
				format("We don't want to invoke cgLib for type: %s, lets figure this out", type.getCanonicalName()));
		/*
		 * Class<?> enhancer = null; Object noOp = null; Class<?> callback = null; try {
		 * enhancer =
		 * TestUtil.class.getClassLoader().loadClass("net.sf.cglib.proxy.Enhancer");
		 * noOp =
		 * TestUtil.class.getClassLoader().loadClass("net.sf.cglib.proxy.NoOp").getField
		 * ("INSTANCE").get(null); callback =
		 * TestUtil.class.getClassLoader().loadClass("net.sf.cglib.proxy.Callback");
		 * 
		 * } catch (Throwable e) { throw new
		 * RuntimeException("It appears the test you're executing requires cglib.", e);
		 * } try { return enhancer.getMethod("create", Class.class,
		 * callback).invoke(null, type, noOp); } catch (Throwable e) { throw new
		 * RuntimeException("Unexpected error trying to create instance of '" +
		 * type.getName() + "' using cglib.", e); }
		 */ }
}
