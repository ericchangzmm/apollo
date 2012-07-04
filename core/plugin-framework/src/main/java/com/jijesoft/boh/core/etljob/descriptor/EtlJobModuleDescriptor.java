package com.jijesoft.boh.core.etljob.descriptor;

import java.lang.reflect.Constructor;
import java.util.List;

import org.dom4j.Element;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.MapExecutionContextDao;
import org.springframework.batch.core.repository.dao.MapJobExecutionDao;
import org.springframework.batch.core.repository.dao.MapJobInstanceDao;
import org.springframework.batch.core.repository.dao.MapStepExecutionDao;
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.batch.core.step.item.SimpleStepFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.jijesoft.boh.core.etljob.chunk.IProcess;
import com.jijesoft.boh.core.etljob.chunk.IReader;
import com.jijesoft.boh.core.etljob.chunk.IWriter;
import com.jijesoft.boh.core.etljob.manage.EtlJob;
import com.jijesoft.boh.core.etljob.manage.EtlJobManager;
import com.jijesoft.boh.core.plugin.AutowireCapablePlugin;
import com.jijesoft.boh.core.plugin.Plugin;
import com.jijesoft.boh.core.plugin.PluginParseException;
import com.jijesoft.boh.core.plugin.descriptors.AbstractModuleDescriptor;
import com.jijesoft.boh.core.plugin.hostcontainer.HostContainer;

public class EtlJobModuleDescriptor extends AbstractModuleDescriptor<Void> {

	private final HostContainer hostContainer;
	private String commitInterval;
	private String readerClassName;
	private String processClassName;
	private String writerClassName;
	private Class<IReader> readerClass;
	private Class<IProcess> processClass;
	private Class<IWriter> writerClass;

	@Override
	public void init(Plugin plugin, Element element)
			throws PluginParseException {
		super.init(plugin, element);
		commitInterval = element.attributeValue("commit-interval");
		Element chunkreader = element.element("chunk-reader");
		readerClassName = chunkreader.attributeValue("class");
		Element chunkprocess = element.element("chunk-processor");
		processClassName = chunkprocess.attributeValue("class");
		Element chunkwriter = element.element("chunk-writer");
		writerClassName = chunkwriter.attributeValue("class");

	}

	@Override
	public void enabled() {
		super.enabled();
		this.readerClass = (Class<IReader>) loadChunkClass(plugin,
				readerClassName);
		this.processClass = (Class<IProcess>) loadChunkClass(plugin,
				processClassName);
		this.writerClass = (Class<IWriter>) loadChunkClass(plugin,
				writerClassName);

		initEtlJob();
	}

	private void initEtlJob() {
		EtlJob etlJob = new EtlJob(this);

		JobRepository jobRepository = this.createRepository();
		Step step = null;
		try {
			step = this.createStep(this.getReaderModule(),
					this.getWriterModule(), this.getProcessModule(),
					jobRepository);
		} catch (Exception e) {
			e.printStackTrace();
		}
		etlJob.setJobRepository(jobRepository);
		etlJob.setJob(createJob(jobRepository, step));
		EtlJobManager.getEtlJobInstance().put(etlJob);
	}

	private SimpleJob createJob(JobRepository jobRepository, Step step) {
		SimpleJob job = new SimpleJob();
		job.setName("Movie Ticket Recalculation");
		job.setJobRepository(jobRepository);
		job.addStep(step);
		return job;
	}

	private Step createStep(IReader itemReader, IWriter itemWriter,
			IProcess itemProcessor, JobRepository jobRepository)
			throws Exception {

		PlatformTransactionManager transactionManager = new ResourcelessTransactionManager();
		SimpleStepFactoryBean factory = new SimpleStepFactoryBean();
		factory.setTransactionManager(transactionManager);
		factory.setItemReader(itemReader);
		factory.setItemWriter(itemWriter);
		factory.setItemProcessor(itemProcessor);
		factory.setJobRepository(jobRepository);
		factory.setCommitInterval(Integer.valueOf(commitInterval));
		factory.setBeanName("simpleStepFactoryBean");

		Step step = (Step) factory.getObject();
		return step;
	}

	private SimpleJobRepository createRepository() {
		return new SimpleJobRepository(new MapJobInstanceDao(),
				new MapJobExecutionDao(), new MapStepExecutionDao(),
				new MapExecutionContextDao());
	}

	@Override
	public void disabled() {
		super.disabled();
		EtlJobManager.getEtlJobInstance().remove(this.getKey());
	}

	public EtlJobModuleDescriptor(final HostContainer hostContainer) {
		this.hostContainer = hostContainer;
	}

	private Class<?> loadChunkClass(final Plugin plugin, final String clazz) {
		Class<?> moduleClass = null;
		try {
			if (clazz != null) // not all plugins have to have a class
			{
				// First try and load the class, to make sure the class exists
				@SuppressWarnings("unchecked")
				final Class<?> loadedClass = (Class<?>) plugin.loadClass(clazz,
						getClass());
				moduleClass = loadedClass;

				// Then instantiate the class, so we can see if there are any
				// dependencies that aren't satisfied
				try {
					final Constructor<?> noargConstructor = moduleClass
							.getConstructor(new Class[] {});
					if (noargConstructor != null) {
						moduleClass.newInstance();
					}
				} catch (final NoSuchMethodException e) {
					// If there is no "noarg" constructor then don't do the
					// check
				}
			}
		} catch (final ClassNotFoundException e) {
			throw new PluginParseException("Could not load class: " + clazz, e);
		} catch (final NoClassDefFoundError e) {
			throw new PluginParseException(
					"Error retrieving dependency of class: " + clazz
							+ ". Missing class: " + e.getMessage(), e);
		} catch (final UnsupportedClassVersionError e) {
			throw new PluginParseException(
					"Class version is incompatible with current JVM: " + clazz,
					e);
		} catch (final Throwable t) {
			throw new PluginParseException(t);
		}
		return moduleClass;
	}

	public IReader getReaderModule() {
		if (plugin instanceof AutowireCapablePlugin) {
			return ((AutowireCapablePlugin) plugin).autowire(readerClass);
		}
		return hostContainer.create(readerClass);
	}

	public IProcess getProcessModule() {
		if (plugin instanceof AutowireCapablePlugin) {
			return ((AutowireCapablePlugin) plugin).autowire(processClass);
		}
		return hostContainer.create(processClass);
	}

	public IWriter getWriterModule() {
		if (plugin instanceof AutowireCapablePlugin) {
			return ((AutowireCapablePlugin) plugin).autowire(writerClass);
		}
		return hostContainer.create(writerClass);
	}

	@Override
	public Void getModule() {
		throw new UnsupportedOperationException();
	}
}
