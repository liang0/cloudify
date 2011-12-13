package com.gigaspaces.cloudify.dsl.context.kvstorage;

import groovy.lang.GroovyObjectSupport;

import java.io.Serializable;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import com.gigaspaces.cloudify.dsl.context.Service;
import com.gigaspaces.cloudify.dsl.context.ServiceContext;
import com.gigaspaces.cloudify.dsl.context.kvstorage.spaceentries.ServiceCloudifyAttribute;

public class ServiceAttributesAccessor extends AbstractAttributesAccessor {

	private final String serviceName;
	private final InstancesFacade instancesFacade;

	public ServiceAttributesAccessor(AttributesFacade attributesFacade,
			String applicationName, String serviceName, ServiceContext serviceContext) {
		super(attributesFacade, applicationName);
		this.serviceName = serviceName;
		this.instancesFacade = new InstancesFacade(attributesFacade, applicationName, serviceName, serviceContext);
	}

	@Override
	protected ServiceCloudifyAttribute prepareAttributeTemplate() {
		ServiceCloudifyAttribute attribute = new ServiceCloudifyAttribute();
		attribute.setServiceName(serviceName);
		return attribute;
	}
	
	public InstancesFacade getInstances(){
		return instancesFacade ;
	}
	
	//This is serializable just because groovy .each method returns the iterator as a result, if the user
	//will write each method as a last command in a closue with no other return value he will get serialization error
	public static class InstancesFacade extends GroovyObjectSupport implements Iterable<InstanceAttributesAccessor>, Serializable {

		private static final long serialVersionUID = 1L;
		
		private static final int WAIT_FOR_SERVICE_TIMEOUT = 10;
		private final transient ServiceContext serviceContext;
		private final transient AttributesFacade attributesFacade;
		private final transient String applicationName;
		private final transient String serviceName;

		public InstancesFacade(AttributesFacade attributesFacade, String applicationName, String serviceName, ServiceContext serviceContext) {
			this.attributesFacade = attributesFacade;
			this.applicationName = applicationName;
			this.serviceName = serviceName;
			this.serviceContext = serviceContext;
		}

		@Override
		public Iterator<InstanceAttributesAccessor> iterator() {
			return new InstanceFacadeIterator();
		}
		
		public Object getAt(Object key){
			if (!(key instanceof Integer)){
				throw new IllegalArgumentException("key must be integer and represent service instance id");
			}
			
			Integer instanceId = (Integer) key;
			return new InstanceAttributesAccessor(attributesFacade, applicationName, serviceName, instanceId);
		}
		
		public class InstanceFacadeIterator implements
				Iterator<InstanceAttributesAccessor>, Serializable {

			private static final long serialVersionUID = 1L;

			private final transient int instancesCount;
			private transient int currentInstanceIndex = 0;
			
			public InstanceFacadeIterator() {
				final Service service = serviceContext.waitForService(serviceContext.getServiceName(), WAIT_FOR_SERVICE_TIMEOUT, TimeUnit.SECONDS);
				instancesCount = service != null? service.getNumberOfPlannedInstances() : 0;
			}

			@Override
			public boolean hasNext() {
				return currentInstanceIndex < instancesCount;
			}

			@Override
			public InstanceAttributesAccessor next() {
				int instanceId = currentInstanceIndex + 1;
				currentInstanceIndex++;
				return new InstanceAttributesAccessor(attributesFacade, applicationName, serviceName, instanceId);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

}
	}
	
}
