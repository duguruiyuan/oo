package com.slfinance.modules.test.spring;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.slfinance.annotation.ImportData;
import com.slfinance.modules.test.ext.Function4Oracle;

/**
 * Spring的支持数据库访问, 事务控制和依赖注入的JUnit4 集成测试基类. 相比Spring原基类名字更短并保存了dataSource变量.
 * 
 * 子类需要定义applicationContext文件的位置, 如:
 * 
 * @ContextConfiguration(locations = { "/applicationContext.xml" })
 * 
 */
@ActiveProfiles(Profiles.UNIT_TEST)
@ContextConfiguration(locations = { "/application-jpa.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public abstract class SpringTransactionalTestCase extends
		AbstractTransactionalJUnit4SpringContextTests {

	// private static Class<?> fastLoad;
	@Autowired
	DataSourceInitializer dataSourceInitializer;

	/**
	 * Title 测试之前初始化自己的表数据
	 */
	@Before
	public void loadData() {
		// if (fastLoad == getClass())
		// return;
		ImportData importData = this.getClass().getAnnotation(ImportData.class);
		// 清空后 加载自己的数据
		dataSourceInitializer.destroy();
		ResourceDatabasePopulator resourceDateDatabasePopulator = new ResourceDatabasePopulator();
		resourceDateDatabasePopulator.setSqlScriptEncoding("UTF-8");
		Resource resPath = new ClassPathResource("sql/h2/schema.sql");
		resourceDateDatabasePopulator.addScript(resPath);
		if (importData == null) {
			String sqlPath = "data/h2/import-data.sql";
			resPath = new ClassPathResource(sqlPath);
			resourceDateDatabasePopulator.addScript(resPath);
		} else {
			String[] paths = importData.value();
			for (String str : paths) {
				if (str == null || str.trim().length() == 0)
					continue;
				if (!str.endsWith(".sql"))
					str += ".sql";
				resPath = new ClassPathResource("data/h2/" + str);
				resourceDateDatabasePopulator.addScript(resPath);
			}
		}
		if ((resPath = registerOracleFuncs(Function4Oracle.class)) != null)
			resourceDateDatabasePopulator.addScript(resPath);
		dataSourceInitializer
				.setDatabasePopulator(resourceDateDatabasePopulator);
		dataSourceInitializer.afterPropertiesSet();
		// fastLoad = getClass();
	}

	/**
	 * 注入oracle函数
	 * 
	 * @return
	 */
	final static Resource registerOracleFuncs(Class<?>... classes) {
		requireNonNull(classes);
		if (classes.length == 0)
			return null;
		int modifiers;
		StringBuilder sqlBuf = new StringBuilder();
		for (Class<?> klass : classes) {
			String className = klass.getName();
			Method[] methods = klass.getMethods();
			for (Method m : methods) {
				if (Modifier.isPublic(modifiers = m.getModifiers())
						&& Modifier.isStatic(modifiers))
					sqlBuf.append("CREATE ALIAS IF NOT EXISTS ")
							.append(m.getName().toUpperCase())
							.append(" FOR \"").append(className).append('.')
							.append(m.getName()).append("\";\n");
			}
		}
		if (sqlBuf.length() > 0)
			return new ByteArrayResource(sqlBuf.toString().getBytes());
		return null;
	}
}
