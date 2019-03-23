package com.jenkinsjobs.Jobs;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Optional;
//import java.awt.List;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import net.sf.json.*;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.jenkinsjobs.model.JobConfiguration;
import com.jenkinsjobs.model.JobParameter;
import com.jenkinsjobs.model.ParameterizedBuild;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueItem;
import com.offbytwo.jenkins.model.QueueReference;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueItem;
import com.offbytwo.jenkins.model.QueueReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
@RestController
public class JenkinsJobs {
	
	/*@Value("${jobs.url}")
    private String Url;
    @Value("${jobs.username}")
    private String Username;
    @Value("${jobs.password}")
    private String password;*/
    public JenkinsServer jenkins;
    //private final Long retryInterval;
    private static final Long DEFAULT_RETRY_INTERVAL = 200L;
    boolean flag=false;
    private static QueueReference queueRef;
    private static QueueItem queueItem;
    //private JobStatusRepo Repo;
    private static SessionFactory sessionFactory;
    private static Session session;
    /*@Autowired
    private JobStatusRepo jobsrepository;*/
     private JobStatusRepo jobsRepository;
     private final Logger logger = LoggerFactory.getLogger(JenkinsJobs.class);
     private SpringTemplateEngine templateEngine;
     
	@Autowired
	public JenkinsJobs(JobStatusRepo repository, SpringTemplateEngine templateEngine) {
		this.jobsRepository = repository;
		this.templateEngine = templateEngine;
	}
	
    /*@Autowired
    private JobStatusRepo jobsrepository;*/
	@RequestMapping(value="/jobs", method=RequestMethod.GET)
	public JSONObject getJobs() throws Exception 
	{

		 try {	         
		 jenkins = new JenkinsServer(new URI("https://kone.iagilepro.com"), "agile.pro@kone.com", "infy1234"); 	          
	         List<String> jobnames = new ArrayList<String>();    
	         Map<String, Job> jobs = jenkins.getJobs();
	         //System.out.println("new jobs... :"+jobs);
	         JSONObject jsonobj = new JSONObject();	         
	         for (String jobnm: jobs.keySet())
	         {
	             jobnames.add(jobnm);
	             
	         }
	         jsonobj.put("JobNames", jobnames);
	         return jsonobj;
	     } 
		 catch (Exception e) {
	         System.err.println(e.getMessage());
	         throw e;
	     }
		finally 
		{
		jenkins.close();
		}
	}
	
	@RequestMapping(value="/Startjobs",params={"buildname"},method=RequestMethod.GET)	
	public ParameterizedBuild StartJob(@RequestParam("buildname") String buildname) throws Exception 
	//public void StartJob(String buildname) throws Exception
	{
		//JobParameter jobParams = new JobParameter();
		ParameterizedBuild buildParams = new ParameterizedBuild();
		List<JobParameter> paramlist = new ArrayList<JobParameter>();
		try {
		JSONObject Jsonobj = new JSONObject();	 
		HashMap<String, String> Paramtypes = new HashMap<String, String>();
		HashMap<String, String>  Params = new HashMap<String, String>();		 
		jenkins = new JenkinsServer(new URI("https://kone.iagilepro.com"), "agile.pro@kone.com", "infy1234");
		JobWithDetails jobinfo = jenkins.getJob(buildname);
		String jobxml = jenkins.getJobXml(buildname);		
		System.out.println("XML :"+jobxml);	
		org.w3c.dom.Document doc = convertStringToXMLDocument(jobxml);	
		NodeList list = doc.getElementsByTagName("parameterDefinitions");
	    for (int i=0; i< list.getLength(); i++) {	    	
	    Node Param = list.item(i);
        System.out.println("list size :"+list.getLength());
	    if(Param.hasChildNodes()){	        	
	        	
	       for(int j=0; j< Param.getChildNodes().getLength(); j++)
	        	{
	    	   		Node ParamType = Param.getChildNodes().item(j).getNextSibling();
	    	   		if(ParamType != null && ParamType.getNodeType()==org.dom4j.Node.ELEMENT_NODE)
	    	   		{	       	
	    	   		
	            	if(ParamType != null && ParamType.hasChildNodes())
	            	{	
	            	 switch(ParamType.getNodeName())
	            	 {
	            	 case "hudson.model.StringParameterDefinition":
	            	 JobParameter jobParams = new JobParameter();
	            	 Node ParamName = ParamType.getChildNodes().item(0).getNextSibling();
	            	 Node ParamValue = ParamType.getChildNodes().item(0).getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling();
	            	 /*System.out.println("ParamName in paramtypes:"+ParamName.getNodeName());
	            	 System.out.println("ParamNameValues in paramtypes:"+ParamName.getChildNodes().item(0).getNodeValue());
	            	 System.out.println("ParamValues in paramtypes:"+ParamValue.getChildNodes().item(0).getNodeValue());*/
	            	 jobParams.setParamName(ParamName.getChildNodes().item(0).getNodeValue());
			 if(ParamValue.getChildNodes().getLength() == 0)	            	 
	            	 {
	            		 jobParams.setValue("NA");
	            	 }
	            	 else
	            	 { 
	            		 jobParams.setValue(ParamValue.getChildNodes().item(0).getNodeValue());
	            	 }
	            	 jobParams.setParamType(ParamType.getNodeName());
	            	 Params.put(ParamName.getChildNodes().item(0).getNodeValue(), ParamType.getNodeName());
	            	 //System.out.println("jobparams :"+jobParams);
	            	 paramlist.add(jobParams);
	            	 break;
	            	 
	            	 case "hudson.model.BooleanParameterDefinition":
	            		 JobParameter booleanJobParams = new JobParameter();
		            	 Node booleanParamName = ParamType.getChildNodes().item(0).getNextSibling();
		            	 Node booleanParamValue = ParamType.getChildNodes().item(0).getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling();
		            	 System.out.println("ParamName in paramtypes:"+booleanParamName.getNodeName());
		            	 System.out.println("ParamNameValues in paramtypes:"+booleanParamName.getChildNodes().item(0).getNodeValue());
		            	 System.out.println("ParamValues in paramtypes:"+booleanParamValue.getChildNodes().item(0).getNodeValue());
		            	 booleanJobParams.setParamName(booleanParamName.getChildNodes().item(0).getNodeValue());
				 if(booleanParamValue.getChildNodes().getLength() == 0)
				 {
	            		 	booleanJobParams.setValue("NA");
	            	 	 }
				 else
		            	 {
		            	 	booleanJobParams.setValue(booleanParamValue.getChildNodes().item(0).getNodeValue());
				 }
		            	 booleanJobParams.setParamType(ParamType.getNodeName());
		            	 Params.put(booleanParamName.getChildNodes().item(0).getNodeValue(), ParamType.getNodeName());
		            	 //System.out.println("jobparams :"+jobParams);
		            	 paramlist.add(booleanJobParams);
		            	 break;
	            	 case "hudson.model.ChoiceParameterDefinition":
	            		JobParameter choiceJobParams = new JobParameter();
	            		 Node choiceParamName = ParamType.getChildNodes().item(0).getNextSibling();	
	            		 choiceJobParams.setParamName(choiceParamName.getChildNodes().item(0).getNodeValue());		            	 
		            	 choiceJobParams.setParamType(ParamType.getNodeName());
	            		 Node temp = ParamType.getChildNodes().item(4).getNextSibling();	            		 
	            		 Node temp1 = temp.getFirstChild().getNextSibling();
	            		 List<String> choices = new ArrayList<String>();
	            		 System.out.println("choice childs 4:"+temp1.getFirstChild().getNextSibling().getNodeName());
	            		 if(temp1.hasChildNodes())
	            		 {
	            			 for(int k=0; k< temp1.getChildNodes().getLength(); k++)
	         	        	{	            				
	         	    	   		Node temp2 = temp1.getChildNodes().item(k).getNextSibling();
	         	    	   	if(temp2!=null && temp2.getNodeType()==org.dom4j.Node.ELEMENT_NODE)
	    	    	   		{	
					if(temp2.getChildNodes().item(0).getNodeValue() != null) {
	         	    	   	System.out.println("temp 2 :"+temp2.getNodeName()+" : "+temp2.getChildNodes().item(0).getNodeValue());
	         	    	   	choiceJobParams.setValue(/*temp2.getChildNodes().item(0).getNodeValue()*/"NA");
	         	    	   	choices.add(temp2.getChildNodes().item(0).getNodeValue());
					}
	    	    	   		}
	         	    	   
	    	    	   		}
	         	        }           
	            		 for(int c=0;c<choices.size();c++)
	            		 {System.out.println("choiceeessss :"+choices.get(c));}
	            		 choiceJobParams.setChoices(choices);
		            	 Params.put(choiceParamName.getChildNodes().item(0).getNodeValue(), ParamType.getNodeName());		            	
		            	 paramlist.add(choiceJobParams);
	            		 break;
			case "hudson.model.TextParameterDefinition":
	            		 JobParameter multilineParams = new JobParameter();
		            	 Node multilineParamName = ParamType.getChildNodes().item(0).getNextSibling();
		            	 Node multilineParamValue = ParamType.getChildNodes().item(0).getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling();
		            	 System.out.println("ParamName in paramtypes:"+multilineParamName.getNodeName());
		            	 System.out.println("ParamNameValues in paramtypes:"+multilineParamName.getChildNodes().item(0).getNodeValue());
		            	 System.out.println("ParamValues in paramtypes:"+multilineParamValue.getChildNodes().item(0).getNodeValue());
		            	 multilineParams.setParamName(multilineParamName.getChildNodes().item(0).getNodeValue());
		            	 if(multilineParamValue.getChildNodes().getLength() == 0)
		            	 {
		            		 multilineParams.setValue("NA");					 
		            	 }
		            	 else
		            	 {
		            		multilineParams.setValue(multilineParamValue.getChildNodes().item(0).getNodeValue());
		            	 }
		            	 multilineParams.setParamType(ParamType.getNodeName());
		            	 Params.put(multilineParamName.getChildNodes().item(0).getNodeValue(), ParamType.getNodeName());
		            	 //System.out.println("jobparams :"+jobParams);
		            	 paramlist.add(multilineParams);
	            		 break;
			case "hudson.model.PasswordParameterDefinition":
	            		 JobParameter passParams = new JobParameter();
		            	 Node passParamName = ParamType.getChildNodes().item(0).getNextSibling();
		            	 Node passParamValue = ParamType.getChildNodes().item(0).getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling();
		            	 System.out.println("ParamName in paramtypes:"+passParamName.getNodeName());
		            	 System.out.println("ParamNameValues in paramtypes:"+passParamName.getChildNodes().item(0).getNodeValue());
		            	 System.out.println("ParamValues in paramtypes:"+passParamValue.getChildNodes().item(0).getNodeValue());
		            	 passParams.setParamName(passParamName.getChildNodes().item(0).getNodeValue());
		            	/* if(passParamValue.getChildNodes().item(0).getNodeValue() != null)
		            	 {
		            		 passParams.setValue(passParamValue.getChildNodes().item(0).getNodeValue());
		            	 }
		            	 else
		            	 {*/
		            		 passParams.setValue("NA");
		            	 //}
		            	 passParams.setParamType(ParamType.getNodeName());
		            	 Params.put(passParamName.getChildNodes().item(0).getNodeValue(), ParamType.getNodeName());
		            	 //System.out.println("jobparams :"+jobParams);
		            	 paramlist.add(passParams);
	            		 break;
	            		 
	            	 }
	            	}	
	            	
	            	//}	            	
	            	else
	            	{
	            		break;
	            	}	             	            	
	        	}
	        	}
	         }
	   	}
		  
		System.out.println("After converting string to xml :"+doc.getFirstChild().getNodeName());	
		JobStatus jobStat = new JobStatus();
		jobStat.setBuildname(buildname);
		jobStat.setBuildstatus("In Progress");	
		JobStatus selectedJob = jobsRepository.saveAndFlush(jobStat);   
		Jsonobj.put("Buildid", selectedJob.getBuildid());
		Jsonobj.put("Buildname", selectedJob.getBuildname());
		Jsonobj.put("Buildstatus", selectedJob.getBuildstatus());		
		Jsonobj.put("Paramtype",Paramtypes);	
		Jsonobj.put("BuildParams",Params);
		buildParams.setBuildId(selectedJob.getBuildid());
		buildParams.setBuildName(selectedJob.getBuildname());
		buildParams.setBuildStatus(selectedJob.getBuildstatus());
		buildParams.setBuildParams(paramlist);
		if(list.getLength() == 0)
		{
		Thread b= new Thread(new BuildThread(selectedJob.getBuildid(),buildname,jobsRepository,Params));
		b.start();
		}
		//BuildThread b = new BuildThread(selectedJob.getBuildid(),buildname,jobsRepository);
		//b.startJob();
		return buildParams;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		/*jobStat.setBuildstatus("Request In Progress..");
		System.out.println("buildname :"+jobStat.getBuildname());
		JobStatus selectedJob = jobsRepository.saveAndFlush(jobStat);    
		Jsonobj.put("Buildid", selectedJob.getBuildid());
		Jsonobj.put("Buildname", selectedJob.getBuildname());
		Jsonobj.put("Buildstatus", selectedJob.getBuildstatus());
		Jsonobj.put("httpstatus", "307");
		Thread b= new Thread(new BuildThread(selectedJob.getBuildid(),buildname,jobsRepository));
		 b.start();*/
		//BuildThread b = new BuildThread(selectedJob.getBuildid(),buildname,jobsRepository);
		//b.startJob();
		//return Jsonobj;
	}
	@RequestMapping(value="/CheckStatus",params={"buildid"},method=RequestMethod.GET)	
	public JSONObject CheckStatus(@RequestParam("buildid") long buildid) throws Exception 
	//public JSONObject CheckStatus(long buildid)
	{
		try
		{
		jenkins = new JenkinsServer(new URI("https://kone.iagilepro.com"), "agile.pro@kone.com", "infy1234"); 		
		JSONObject Jsonobj = new JSONObject();
		//SessionFactory sessionFactory = s;
		
			//JobStatus job = service.getbuild(buildid);	
			//JobStatus job = jobsrepository.getOne(buildid);
			JobStatus job = jobsRepository.getOne(buildid);
			Jsonobj.put("Buildid", job.getBuildid());
			Jsonobj.put("Buildname", job.getBuildname());
			Jsonobj.put("Buildstatus", job.getBuildstatus());
			return Jsonobj;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}	

	@RequestMapping(value="/StartjobsWithParams",params={"buildid","buildname"},method=RequestMethod.POST)	
	//public JSONObject StartJobWithParams(@RequestParam("buildid") long buildid,@RequestParam("buildname") String buildname,@RequestParam("Params") HashMap<String, String> Params) throws Exception 
	public void StartjobsWithParams(long buildid,String buildname,@RequestBody Map<String, String> Params) throws Exception
	{
	//public void StartJob(String buildname) throws Exception
	
		try {
			System.out.println("Parametrs received from URL :"+Params);
			Thread build= new Thread(new BuildThread(buildid,buildname,jobsRepository,Params));
			build.start();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		//return null;
		
	}			
	@RequestMapping(value="/Stopjobs",method=RequestMethod.GET)
	public void StopJob() throws Exception 
	{
	        try{				
			
			BuildThread b = new BuildThread();
		        b.stopThread();
		}
		 catch (Exception e) {
	         System.err.println(e.getMessage());
	         throw e;
	     }
		
	
	
	}
	
	//create new job
	@RequestMapping(value="/createjob", method=RequestMethod.POST)
	public ResponseEntity createJob(@RequestBody JobConfiguration jobDetails) {
		String xml = "hello";
		HashMap<String,String> jobConfig = new HashMap<String,String>();
		Context context = new Context();
		context.setVariable("jobConfig", jobConfig);
		
		jobConfig.put("description", jobDetails.getDescription());
		jobConfig.put("github_project_url", jobDetails.getGithubProject());
		jobConfig.put("github_credential_id", jobDetails.getGithubCredentialId());
		jobConfig.put("git_branch", jobDetails.getGitBranch());
		jobConfig.put("batch_script", jobDetails.getBatchScript());
		jobConfig.put("targets", jobDetails.getBuildTargets());
		String xmlConfig = this.templateEngine.process("job-config", context);
		
		
		try {
			jenkins = new JenkinsServer(new URI("https://kone.iagilepro.com"), "agile.pro@kone.com", "infy1234"); 
			jenkins.createJob(jobDetails.getJobName(), xmlConfig, true);
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity(HttpStatus.OK);
	}
	
	public org.w3c.dom.Document convertStringToXMLDocument(String xmlString)
    {
        //Parser that produces DOM object trees from XML content
		
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         
        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try
        {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();             
            //Parse the content to Document object
            //String jobxml = jenkins.getJobXml(buildname);
            org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
