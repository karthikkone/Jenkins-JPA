package com.jenkinsjobs.Jobs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.management.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;
//import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueItem;
import com.offbytwo.jenkins.model.QueueReference;

import net.sf.json.JSONObject;

public class BuildThread implements Runnable {
	
	@Value("${jenkins.url}")
    private String Url;

    @Value("${jenkins.username}")
    private String Username;

    @Value("${jenkins.password}")
    private String Password;
    
	private String buildName;
	private Long buildId;
	private static final Long DEFAULT_RETRY_INTERVAL = 200L;
	private static QueueReference queueRef;
	private static QueueItem queueItem;	 
	private static Session session;
	
	private JobStatusRepo jobsRepository;
	public BuildThread()
	{
		
	}
	@Autowired
	public BuildThread(long buildId,String buildName, JobStatusRepo jobsRepository) {
		this.buildId = buildId;
		this.buildName = buildName;
		this.jobsRepository = jobsRepository;
	} 

	@Override
	public void run() {
		try {
			JenkinsServer jenkins = new JenkinsServer(new URI(System.getenv(Url)), Username, Password);
			JobWithDetails jobinfo = jenkins.getJob(this.buildName);
			queueRef=jobinfo.build(true);
			queueItem = jenkins.getQueueItem(queueRef);
		    JSONObject jsonobj = new JSONObject();				
			while (queueItem.getExecutable() == null) {		
			       Thread.sleep(DEFAULT_RETRY_INTERVAL);
			       queueItem = jenkins.getQueueItem(queueRef);
			      
			}
			Build build = jenkins.getBuild(queueItem);				
			while(build.details().isBuilding() == true)
			{						 
				continue;
			}

			//by now build has completed i.e succeded or failed

			// build success
			if(build.details().getResult() == build.details().getResult().SUCCESS) {
				Optional<JobStatus> currentBuildRecord = this.jobsRepository.findById(buildId);
				currentBuildRecord.ifPresent(currentBuild -> {
					currentBuild.setBuildstatus("SUCCESS");
					jobsRepository.saveAndFlush(currentBuild);
				});
			}

			//build fail
			if (build.details().getResult() == build.details().getResult().FAILURE) {
				Optional<JobStatus> currentBuildRecord = this.jobsRepository.findById(buildId);
				currentBuildRecord.ifPresent(currentBuild -> {
					currentBuild.setBuildstatus("FAILURE");
					jobsRepository.saveAndFlush(currentBuild);
				});
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
		
	}
	

