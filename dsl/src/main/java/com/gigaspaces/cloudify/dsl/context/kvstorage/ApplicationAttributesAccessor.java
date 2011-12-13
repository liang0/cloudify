package com.gigaspaces.cloudify.dsl.context.kvstorage;

import com.gigaspaces.cloudify.dsl.context.kvstorage.spaceentries.ApplicationCloudifyAttribute;


/**
 * @author eitany
 * @since 2.0
 */
public class ApplicationAttributesAccessor extends AbstractAttributesAccessor {

	
	public ApplicationAttributesAccessor(AttributesFacade attributesFacade, String applicationName) {
		super(attributesFacade, applicationName);
	}
	
	@Override
	protected ApplicationCloudifyAttribute prepareAttributeTemplate() {
		return new ApplicationCloudifyAttribute();
	}

}
