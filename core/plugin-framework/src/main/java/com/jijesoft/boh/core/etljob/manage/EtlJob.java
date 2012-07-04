package com.jijesoft.boh.core.etljob.manage;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.repository.JobRepository;

import com.jijesoft.boh.core.etljob.chunk.IProcess;
import com.jijesoft.boh.core.etljob.chunk.IReader;
import com.jijesoft.boh.core.etljob.chunk.IWriter;
import com.jijesoft.boh.core.etljob.descriptor.EtlJobModuleDescriptor;

public class EtlJob {
	
	private String key;
	private Job job; 
	private JobRepository jobRepository;

	private EtlJobModuleDescriptor descriptor;
	
	public EtlJob(EtlJobModuleDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public String getKey() {
		return descriptor.getKey();
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public JobRepository getJobRepository() {
		return jobRepository;
	}

	public void setJobRepository(JobRepository jobRepository) {
		this.jobRepository = jobRepository;
	}


}
