package com.jijesoft.boh.core.plugin.osgi.factory.descriptor;

import static com.jijesoft.boh.core.plugin.util.validation.ValidationPattern.test;

import com.jijesoft.boh.core.plugin.descriptors.AbstractModuleDescriptor;
import com.jijesoft.boh.core.plugin.util.validation.ValidationPattern;

public class ComponentScanDescriptor extends AbstractModuleDescriptor<Void>{

	@Override
	public Void getModule() {
		 throw new UnsupportedOperationException();
	}

	@Override
	protected void provideValidationRules(ValidationPattern pattern) {
		 pattern.rule(test("@base-package").withError("The base-package is required"));
	}
	

}
