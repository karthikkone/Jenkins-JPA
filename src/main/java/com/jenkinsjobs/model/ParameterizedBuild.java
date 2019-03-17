package com.jenkinsjobs.model;

import java.util.List;

public class ParameterizedBuild {
	private String buildName;
	private Long buildId;
	private String buildStatus;
	private List<JobParameter> buildParams;
	public String getBuildName() {
		return buildName;
	}
	public void setBuildName(String buildName) {
		this.buildName = buildName;
	}
	public Long getBuildId() {
		return buildId;
	}
	public void setBuildId(Long buildId) {
		this.buildId = buildId;
	}
	public String getBuildStatus() {
		return buildStatus;
	}
	public void setBuildStatus(String buildStatus) {
		this.buildStatus = buildStatus;
	}
	public List<JobParameter> getBuildParams() {
		return buildParams;
	}
	public void setBuildParams(List<JobParameter> buildParams) {
		this.buildParams = buildParams;
	}
	public ParameterizedBuild() {
		super();
	}
	@Override
	public String toString() {
		return "ParameterizedBuild [buildName=" + buildName + ", buildId=" + buildId + ", buildStatus=" + buildStatus
				+ ", buildParams=" + buildParams + "]";
	}
	
	

}
