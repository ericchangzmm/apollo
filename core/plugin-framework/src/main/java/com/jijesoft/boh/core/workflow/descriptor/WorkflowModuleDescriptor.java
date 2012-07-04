package com.jijesoft.boh.core.workflow.descriptor;

import org.dom4j.Element;

import com.jijesoft.boh.core.plugin.AutowireCapablePlugin;
import com.jijesoft.boh.core.plugin.Plugin;
import com.jijesoft.boh.core.plugin.PluginParseException;
import com.jijesoft.boh.core.plugin.StateAware;
import com.jijesoft.boh.core.plugin.descriptors.AbstractModuleDescriptor;
import com.jijesoft.boh.core.plugin.hostcontainer.HostContainer;
import com.jijesoft.boh.core.report.manage.ReportManager;
import com.jijesoft.boh.core.workflow.manage.Workflow;
import com.jijesoft.boh.core.workflow.manage.WorkflowManager;
import com.jijesoft.boh.core.workflow.workflowable.Workflowable;

public class WorkflowModuleDescriptor extends
		AbstractModuleDescriptor<Workflowable> implements StateAware {

	private final HostContainer hostContainer;

	private String key;
	private String diagramFilePath;

	public WorkflowModuleDescriptor(HostContainer hostContainer) {
		this.hostContainer = hostContainer;
	}

	public void init(Plugin plugin, Element element)
			throws PluginParseException {
		super.init(plugin, element);
		key = element.attributeValue("key");
		diagramFilePath = element.attributeValue("diagram");
	}

	@Override
	public void enabled() {
		super.enabled();
		initWorkflow();
	}

	private void initWorkflow() {
		Workflow workflow = new Workflow(this);
		workflow.setDiagramFilePath(diagramFilePath);
		workflow.setKey(key);
		workflow.setWorkflowable(getModule());

		WorkflowManager.add(workflow);
	}

	@Override
	public void disabled() {
		super.disabled();
		ReportManager.getReportInstance().remove(key);
	}

	@Override
	public Workflowable getModule() {
		if (plugin instanceof AutowireCapablePlugin) {
			return ((AutowireCapablePlugin) plugin).autowire(getModuleClass());
		}
		return hostContainer.create(getModuleClass());
	}

}
