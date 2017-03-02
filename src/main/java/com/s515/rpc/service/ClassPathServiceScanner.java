package com.s515.rpc.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import com.s515.rpc.service.annotation.Service;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import com.s515.rpc.invoker.Invoker;

/**
 * Created by Administrator on 8/25/2016.
 */
public class ClassPathServiceScanner extends ClassPathBeanDefinitionScanner {
	private Invoker invoker;

	private ServiceRegistry serviceRegistry;

	public ClassPathServiceScanner(BeanDefinitionRegistry registry) {
		super(registry);
	}

	public ClassPathServiceScanner(BeanDefinitionRegistry registry,
			boolean useDefaultFilters) {
		super(registry, useDefaultFilters);
	}

	public ClassPathServiceScanner(BeanDefinitionRegistry registry,
			boolean useDefaultFilters, Environment environment) {
		super(registry, useDefaultFilters, environment);
	}

	public Set<BeanDefinitionHolder> doScan(String... basePackages) {
		Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

		if (beanDefinitions.isEmpty()) {
			logger.warn("No service was found in '"
					+ Arrays.toString(basePackages)
					+ "' package, Please check your configuration.");
		} else {
			processBeanDefinition(beanDefinitions);
		}

		return beanDefinitions;
	}

	private void processBeanDefinition(Set<BeanDefinitionHolder> beanDefinitions) {
		GenericBeanDefinition definition;

		for (BeanDefinitionHolder holder : beanDefinitions) {
			definition = (GenericBeanDefinition) holder.getBeanDefinition();

			if (logger.isDebugEnabled()) {
				logger.debug("Creating ServiceFactoryBean with name "
						+ holder.getBeanName());
			}

			definition.getConstructorArgumentValues().addGenericArgumentValue(
					definition.getBeanClassName());
			definition.setBeanClass(ServiceFactoryBean.class);
			definition.getPropertyValues().add("invoker", new RuntimeBeanReference("requestInvoker"));

			definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

			// add service to serviceRegistry
//			serviceRegistry.addService(definition.getBeanClass());
		}
	}

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	/**
	 * Configures parent scanner to search for the right interfaces. It can
	 * search for all interfaces or just for those that extends a
	 * markerInterface or/and those annotated with the annotationClass
	 */
	public void registerFilters() {
		boolean acceptAllInterfaces = true;

		if (acceptAllInterfaces) {
			// default include filter that accepts all classes
			addIncludeFilter(new TypeFilter() {
				@Override
				public boolean match(MetadataReader metadataReader,
						MetadataReaderFactory metadataReaderFactory)
						throws IOException {
					return true;
				}
			});
		}

		// exclude package-info.java
		addExcludeFilter(new TypeFilter() {
			@Override
			public boolean match(MetadataReader metadataReader,
					MetadataReaderFactory metadataReaderFactory)
					throws IOException {
				String className = metadataReader.getClassMetadata()
						.getClassName();
				return className.endsWith("package-info");
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isCandidateComponent(
			AnnotatedBeanDefinition beanDefinition) {
		return beanDefinition.getMetadata().isInterface()
				&& beanDefinition.getMetadata().isIndependent()
				&& beanDefinition.getMetadata().hasAnnotation(Service.class.getName());
	}
}
